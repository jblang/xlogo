/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo programming language
 *
 * @author Loïc Le Coq
 */
package xlogo.gui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import xlogo.Config;
import xlogo.Logo;
import xlogo.gui.preferences.PreferencesDialog;
import xlogo.gui.translation.UiTranslator;
import xlogo.kernel.*;
import xlogo.kernel.network.NetworkServer;
import xlogo.kernel.perspective.Viewer3D;
import xlogo.resources.ResourceLoader;
import xlogo.utils.ExtensionFilter;
import xlogo.utils.ImageWriter;
import xlogo.utils.Utils;

import javax.help.CSH;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import static xlogo.utils.Utils.createButton;

public class Application extends JFrame {
    public static final Font[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
    private static final double ZOOM_FACTOR = 1.25;
    private static final Stack<String> historyStack = new Stack<>();
    public static String path = null;
    public static int fontId = getFontId(Logo.config.getFont());
    private final Kernel kernel;
    private final DrawPanel drawPanel;
    private final RSyntaxTextArea commandLine = new RSyntaxTextArea(1, 1);
    private final HistoryPanel historyPanel = new HistoryPanel(this);
    private final SoundPlayer soundPlayer = new SoundPlayer(this);
    private final CommandKeyAdapter commandKeyAdapter = new CommandKeyAdapter();
    private final ArrayList<JMenuItem> menuItems = new ArrayList<>();
    private final JLabel commandLabel = new JLabel();
    public String tempPath = null; // When opening a file
    public boolean error = false;
    public Editor editor;
    public Animation animation = null;
    public JScrollPane scrollPane = new JScrollPane();
    public JSplitPane splitPane = new JSplitPane();
    boolean stop = false;
    private int historyIndex = 0;
    private JButton runButton;
    private JButton stopButton;
    private JButton zoomInButton;
    private JButton zoomOutButton;
    private StartupFileDialog startupDialog;
    private ProcedureEraser procedureEraser;
    private CodeTranslator codeTranslator;
    private UiTranslator uiTranslator;
    private Viewer3D viewer3D = null;

    /**
     * Builds the main frame
     */
    public Application() {
        kernel = new Kernel(this);
        drawPanel = new DrawPanel(this);
        resizeDrawingZone();
        kernel.initInterprete();
        editor = new Editor(this);
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        setTitle("XLogo");
        setIconImage(ResourceLoader.getAppIcon().getImage());
        createMenuBar();
        createContent();
        updateLocalization();
        changeLookAndFeel();
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        initTurtle();
        runStartupCommands();
    }

    private void runStartupCommands() {
        setCommandEnabled(false);
        generatePrimitives();
        if (Logo.isAutoLaunchEnabled()) {
            startAnimation(Utils.formatCode(Logo.getMainCommand()));
            getHistoryPanel().setText("normal", Logo.getMainCommand() + "\n");
        } else if (!Logo.config.getStartupCommand().equals("")) {
            animation = new Animation(this, Utils.formatCode(Logo.config.getStartupCommand()));
            animation.start();
        } else {
            setCommandEnabled(true);
            focusCommandLine();
        }
    }

    static public int getFontId(Font font) {
        for (int i = 0; i < fonts.length; i++) {
            if (fonts[i].getFontName().equals(font.getFontName())) return i;
        }
        return 0;
    }

    private JMenu createMenu(JMenuItem parent, String key) {
        var menu = new JMenu();
        menu.setActionCommand(key);
        menuItems.add(menu);
        if (parent != null) parent.add(menu);
        return menu;
    }

    private JMenu createMenu(String key) {
        return createMenu(null, key);
    }

    private JMenuItem createMenuItem(JMenu parent, String key, ActionListener listener) {
        var item = new JMenuItem();
        item.setActionCommand(key);
        menuItems.add(item);
        item.addActionListener(listener);
        parent.add(item);
        return item;
    }

    private void createMenuBar() {
        var menuBar = new JMenuBar();

        var fileMenu = createMenu("menu.file");
        menuBar.add(fileMenu);
        createMenuItem(fileMenu, "nouveau", e -> newFile());
        createMenuItem(fileMenu, "menu.file.open", e -> openWorkspace());
        createMenuItem(fileMenu, "menu.file.saveas", e -> saveWorkspace(true));
        createMenuItem(fileMenu, "menu.file.save", e -> saveWorkspace(false));

        var fileImageMenu = createMenu(fileMenu, "menu.file.captureimage");
        createMenuItem(fileImageMenu, "menu.file.captureimage.clipboard", e -> copyImage());
        createMenuItem(fileImageMenu, "menu.file.saveas", e -> saveImage());
        createMenuItem(fileImageMenu, "menu.file.captureimage.print", e -> printImage());

        var fileTextMenu = createMenu(fileMenu, "menu.file.textzone");
        createMenuItem(fileTextMenu, "menu.file.textzone.rtf", e -> historyPanel.saveHistory());
        createMenuItem(fileMenu, "menu.file.quit", e -> closeWindow());

        var editMenu = createMenu("menu.edition");
        menuBar.add(editMenu);
        createMenuItem(editMenu, "menu.edition.cut", e -> cut());
        createMenuItem(editMenu, "menu.edition.copy", e -> copy());
        createMenuItem(editMenu, "menu.edition.paste", e -> paste());
        createMenuItem(editMenu, "menu.edition.selectall", e -> selectAll());
        menuBar.add(editMenu);

        var toolsMenu = createMenu("menu.tools");
        menuBar.add(toolsMenu);
        createMenuItem(toolsMenu, "menu.tools.pencolor", e -> showColorChooser(true));
        createMenuItem(toolsMenu, "menu.tools.screencolor", e -> showColorChooser(false));
        createMenuItem(toolsMenu, "menu.tools.startup", e -> showStartupFiles());
        createMenuItem(toolsMenu, "menu.tools.translate", e -> showCodeTranslator());
        createMenuItem(toolsMenu, "menu.tools.eraser", e -> showProcedureEraser());
        createMenuItem(toolsMenu, "menu.tools.preferences", e -> showPreferences());

        var helpMenu = createMenu("menu.help");
        menuBar.add(helpMenu);
        createMenuItem(helpMenu, "menu.help.online", this::showHelp);
        createMenuItem(helpMenu, "menu.help.licence", e -> showLicence(true));
        createMenuItem(helpMenu, "menu.help.translatedlicence", e -> showLicence(false));
        createMenuItem(helpMenu, "menu.help.translatexlogo", e -> showUiTranslator());
        createMenuItem(helpMenu, "menu.help.about", e -> showAbout());

        setJMenuBar(menuBar);
    }

    private JToolBar createToolBar() {
        var toolBar = new JToolBar();
        createButton(toolBar, "editSource", e -> showEditor());
        toolBar.addSeparator();
        runButton = createButton(toolBar, "run", e -> runMainCommand());
        stopButton = createButton(toolBar, "stop", e -> stopAnimation());
        toolBar.addSeparator();
        zoomInButton = createButton(toolBar, "zoomIn", e -> zoomIn());
        zoomOutButton = createButton(toolBar, "zoomOut", e -> zoomOut());
        toolBar.addSeparator();
        createButton(toolBar, "cut", e -> cut());
        createButton(toolBar, "copy", e -> copy());
        createButton(toolBar, "paste", e -> paste());
        return toolBar;
    }

    private void createContent() {
        historyPanel.setMinimumSize(new Dimension(600, 40));

        var commandPanel = new JPanel();
        commandPanel.setLayout(new BorderLayout());
        commandPanel.add(commandLabel, BorderLayout.WEST);
        commandPanel.add(commandLine, BorderLayout.CENTER);

        var contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(createToolBar(), BorderLayout.NORTH);
        contentPane.add(commandPanel, BorderLayout.SOUTH);
        contentPane.add(splitPane, BorderLayout.CENTER);

        commandLine.setPreferredSize(new Dimension(300, 18 * Logo.config.getFont().getSize() / 10));
        commandLine.setAlignmentY(JComponent.CENTER_ALIGNMENT);
        commandLine.addKeyListener(commandKeyAdapter);

        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        splitPane.add(scrollPane, JSplitPane.LEFT);
        splitPane.add(historyPanel, JSplitPane.RIGHT);
        splitPane.setResizeWeight(0.8);

        drawPanel.setSize(new java.awt.Dimension((int) (Logo.config.getImageWidth() * DrawPanel.zoom), (int) (Logo.config.getImageHeight() * DrawPanel.zoom)));
        scrollPane.getViewport().add(drawPanel);
        scrollPane.getHorizontalScrollBar().setBlockIncrement(5);
        scrollPane.getVerticalScrollBar().setBlockIncrement(5);
    }

    /**
     * Called by the constructor or when language has been modified
     */
    public void updateLocalization() {
        // Internal text to use for JFileChooser and JColorChooser
        var pairs = new String[][]{
                {"FileChooser.cancelButtonText", "pref.cancel"},
                {"FileChooser.cancelButtonToolTipText", "pref.cancel"},
                {"FileChooser.fileNameLabelText", "nom_du_fichier"},
                {"FileChooser.upFolderToolTipText", "dossier_parent"},
                {"FileChooser.lookInLabelText", "rechercher_dans"},
                {"FileChooser.newFolderToolTipText", "nouveau_dossier"},
                {"FileChooser.homeFolderToolTipText", "repertoire_accueil"},
                {"FileChooser.filesOfTypeLabelText", "fichier_du_type"},
                {"FileChooser.helpButtonText", "menu.help"},
                {"ColorChooser.rgbNameText", "rgb"},
                {"ColorChooser.rgbBlueText", "bleu"},
                {"ColorChooser.rgbGreenText", "vert"},
                {"ColorChooser.rgbRedText", "rouge"},
                {"ColorChooser.swatchesNameText", "echantillon"},
                {"ColorChooser.hsbNameText", "hsb"},
                {"ColorChooser.hsbBlueText", "hsbbleu"},
                {"ColorChooser.hsbGreenText", "hsbvert"},
                {"ColorChooser.hsbRedText", "hsbrouge"},
                {"ColorChooser.swatchesRecentText", "dernier"},
                {"ColorChooser.previewText", "apercu"},
                {"ColorChooser.sampleText", "echantillon_texte"},
                {"ColorChooser.okText", "pref.ok"},
                {"ColorChooser.resetText", "restaurer"},
                {"ColorChooser.cancelText", "pref.cancel"},
                {"ColorChooser.previewText", "apercu"},
        };
        for (var p : pairs) {
            UIManager.put(p[0], Logo.messages.getString(p[1]));
        }
        commandLabel.setText(Logo.messages.getString("commande") + "  ");
        menuItems.forEach(i -> i.setText(Logo.messages.getString(i.getActionCommand())));
        historyPanel.updateText();
    }

    /**
     * Close the window
     */
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            closeWindow();
        } else {
            super.processWindowEvent(e);
        }
    }

    // What happens when validating the text box

    /**
     * When the frame is closed, show a Confirmation box
     */
    public void closeWindow() {
        setVisible(true);
        String message = Logo.messages.getString("quitter?");
        MessageTextArea jt = new MessageTextArea(message);
        String[] choice = {Logo.messages.getString("pref.ok"), Logo.messages.getString("pref.cancel")};
        int val = JOptionPane.showOptionDialog(this, jt, Logo.messages.getString("menu.file.quit"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, ResourceLoader.getAppIcon(), choice, choice[0]);
        if (val == JOptionPane.OK_OPTION) {
            try {
                Logo.config.write();
            } catch (IOException ignored) {}
            System.exit(0);
        }
    }

    private void newFile() {
        var wp = kernel.getWorkspace();
        String[] choice = {Logo.messages.getString("pref.ok"), Logo.messages.getString("pref.cancel")};
        MessageTextArea jt = new MessageTextArea(Logo.messages.getString("enregistrer_espace"));
        int val = JOptionPane.showOptionDialog(this, jt, Logo.messages.getString("enregistrer_espace"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, ResourceLoader.getAppIcon(), choice, choice[0]);

        if (val == JOptionPane.OK_OPTION) {
            wp.deleteAllProcedures();
            wp.deleteAllVariables();
            if (null != Application.path) {
                Application.path = null;
                setTitle("XLogo");
            }
            if (editor != null && editor.isVisible()) {
                editor.setVisible(false);
                editor.clearText();
            }
            Logo.setMainCommand("");

        }
    }

    private void showCloseEditor() {
        JOptionPane.showMessageDialog(this, Logo.messages.getString("ferme_editeur"), Logo.messages.getString("erreur"), JOptionPane.ERROR_MESSAGE);
    }

    private void openWorkspace() {
        JFileChooser jf = new JFileChooser(Utils.unescapeString(Logo.config.getDefaultFolder()));
        String[] ext = {".lgo"};
        jf.addChoosableFileFilter(new ExtensionFilter(Logo.messages.getString("fichiers_logo"), ext));
        int val = jf.showDialog(this, Logo.messages.getString("menu.file.open"));
        if (val == JFileChooser.APPROVE_OPTION) {
            String txt = "";
            String path = jf.getSelectedFile().getPath();
            try {
                txt = Utils.readLogoFile(path);
            } catch (IOException e1) {
                updateHistory("erreur", Logo.messages.getString("error.iolecture"));
            }
            if (!txt.equals("")) {
                if (!editor.isVisible()) editor.setVisible(true);
                editor.appendText(txt);
                editor.setMainCommand();
                editor.discardAllEdits();
            }
            tempPath = path;
        }
    }

    private void saveWorkspace(boolean promptName) {
        var wp = kernel.getWorkspace();
        if (null != editor && editor.isVisible()) {
            showCloseEditor();
            return;
        }
        String path = Application.path;
        if (promptName || null == path) {
            JFileChooser jf = new JFileChooser(Utils.unescapeString(Logo.config.getDefaultFolder()));
            String[] ext = {".lgo"};
            jf.addChoosableFileFilter(new ExtensionFilter(Logo.messages.getString("fichiers_logo"), ext));

            int val = jf.showDialog(this, Logo.messages.getString("menu.file.save"));
            if (val == JFileChooser.APPROVE_OPTION) {
                path = jf.getSelectedFile().getPath();
                String path2 = path.toLowerCase();  // on garde la casse du path pour les systemes d'exploitation faisant la diff�rence
                if (!path2.endsWith(".lgo")) path += ".lgo";
                Application.path = path;
                setTitle(path + " - XLogo");
                try {
                    File f = new File(path);
                    Logo.config.setDefaultFolder(Utils.escapeString(f.getParent()));
                } catch (NullPointerException ignored) {
                }
            }
        }
        try {
            var builder = new StringBuilder();
            try {
                for (int i = 0; i < wp.getNumberOfProcedure(); i++) {
                    Procedure procedure = wp.getProcedure(i);
                    builder.append(procedure.toString());
                }
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            } // If no procedure has been defined
            Utils.writeLogoFile(path, builder.toString());
        } catch (NullPointerException e3) {
            System.out.println("annulation");
        } // If the user cancels
        catch (IOException e2) {
            updateHistory("erreur", Logo.messages.getString("error.ioecriture"));
        }
    }

    private void saveImage() {
        ImageWriter imageWriter = new ImageWriter(this, getCanvas().getSelectionImage());
        int value = imageWriter.chooseFile();
        if (value == JFileChooser.APPROVE_OPTION) {
            imageWriter.start();
        }
    }

    private void printImage() {
        PrinterPanel panel = new PrinterPanel(getCanvas().getSelectionImage());
        Thread printer = new Thread(panel);
        printer.start();
    }

    void showColorChooser(boolean pen) {
        var title = pen ? "couleur_du_crayon" : "couleur_du_fond";
        Color color = JColorChooser.showDialog(this, Logo.messages.getString(title), getCanvas().getScreenColor());
        if (null != color) {
            var prim = Utils.primitiveName(pen ? "drawing.setpencolor" : "drawing.setscreencolor");
            updateHistory("commentaire", prim + " [" + color.getRed() + " " + color.getGreen() + " " + color.getBlue() + "]\n");
            if (pen) {
                kernel.fcc(color);
            } else {
                kernel.fcfg(color);
            }
        }
    }

    void showPreferences() {
        new PreferencesDialog(this);
    }

    private void showHelp(ActionEvent e) {
        new Thread(() -> {
            HelpSet hs = null;
            try {
                String url = "http://downloads.tuxfamily.org/xlogo/downloads-";
                url += Logo.getLocaleTwoLetters();
                url += "/javahelp/manual-";
                url += Logo.getLocaleTwoLetters();
                url += ".hs";
                java.net.URL hsURL = new java.net.URL(url);
                hs = new HelpSet(null, hsURL);
                HelpBroker hb = hs.createHelpBroker();
                new CSH.DisplayHelpFromSource(hb).actionPerformed(e);
            } catch (Exception ee) {
                // Say what the exception really is
                System.out.println("HelpSet " + ee.getMessage());
                if (null != hs) System.out.println("HelpSet " + hs.getHelpSetURL() + " not found");
            }
        }).start();
    }

    private void showAbout() {
        String message = Logo.messages.getString("message_a_propos1") + Logo.VERSION + "\n\n" + Logo.messages.getString("message_a_propos2") + " " + Logo.WEB_SITE;
        MessageTextArea jt = new MessageTextArea(message);
        JOptionPane.showMessageDialog(null, jt, Logo.messages.getString("menu.help.about"), JOptionPane.INFORMATION_MESSAGE, ResourceLoader.getAppIcon());
    }

    private void showLicence(boolean english) {
        JFrame frame = new JFrame(Logo.messages.getString("menu.help.licence"));
        frame.setIconImage(ResourceLoader.getAppIcon().getImage());
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setSize(500, 500);
        LicensePane editorPane = new LicensePane();
        editorPane.setEditable(false);
        String path = "resources/gpl/gpl-";
        if (english) {
            path += "en";
        } else {
            path += Logo.getLocaleTwoLetters();
        }
        path += ".html";
        java.net.URL helpURL = Logo.class.getResource(path);
        if (helpURL != null) {
            try {
                editorPane.setPage(helpURL);
            } catch (IOException e1) {
                System.err.println("Attempted to read a bad URL: " + helpURL);
            }
        } else {
            System.err.println("Couldn't find file: " + path);
        }

        JScrollPane editorScrollPane = new JScrollPane(editorPane);
        editorScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        editorScrollPane.setPreferredSize(new Dimension(250, 145));
        editorScrollPane.setMinimumSize(new Dimension(10, 10));
        frame.getContentPane().add(editorScrollPane);
        frame.setVisible(true);
    }

    private void showEditor() {
        editor.open();
    }

    private void runMainCommand() {
        startAnimation(Utils.formatCode(Logo.getMainCommand()));
        getHistoryPanel().setText("normal", Logo.getMainCommand() + "\n");
    }

    private void stopAnimation() {
        LogoException.lance = true;
        error = true;
        if (NetworkServer.isActive) {
            NetworkServer.stopServer();
        }
    }

    private void zoomIn() {
        getCanvas().zoom(ZOOM_FACTOR * DrawPanel.zoom, true);
    }

    private void zoomOut() {
        getCanvas().zoom(1 / ZOOM_FACTOR * DrawPanel.zoom, true);
    }

    /**
     * When the user types "Enter" in the Command Line
     */
    public void executeCommand() {
        if (stop) stop = false;
        String text = commandLine.getText();
        if (!text.equals("") && commandLine.isEditable()) {
            if (commandKeyAdapter.tape) {
                commandKeyAdapter.tape = false;
                historyStack.pop();
            }
            if (historyStack.size() > 49) historyStack.remove(0);
            historyStack.push(text); // Add text to the history
            historyIndex = historyStack.size(); // Readjust history index
            historyPanel.setText("normal", text + "\n");

            // Remove any comments
            int a = text.indexOf("#");

            while (a != -1) {
                if (a == 0) {
                    text = "";
                    break;
                } else if (text.charAt(a - 1) != '\\') {
                    text = text.substring(0, a);
                    break;
                }
                a = text.indexOf("#", a + 1);
            }
            Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
            startAnimation(Utils.formatCode(text));

            // Delete the command line
            SwingUtilities.invokeLater(() -> commandLine.setText(""));

        }
    }

    /**
     * Launch the animation thread with the instructions "st"
     *
     * @param st List of instructions
     */
    public void startAnimation(StringBuffer st) {
        animation = new Animation(this, st);
        animation.start();
    }

    /**
     * Get Method for Sound Player
     *
     * @return The Sound Player
     */
    public SoundPlayer getSoundPlayer() {
        return soundPlayer;
    }
    // change language for the interface
    // change la langue de l'interface

    public void closeUiTranslator() {
        uiTranslator = null;
    }

    /**
     * Modify the language for the interface
     *
     * @param id The integer representing the choosen language
     */

    public void changeLanguage(int id) {
        Logo.config.setLanguage(id);
        Logo.generateLanguage(id);
        updateLocalization();
        kernel.buildPrimitiveTreemap(id);
        editor = new Editor(this);
        historyPanel.changeLanguage();
        if (null != viewer3D) viewer3D.setText();
    }

    /**
     * Resize the dawing area
     */
    public void resizeDrawingZone() {
        if (null != animation) {
            animation.setPause(true);
        }
        // resize the drawing image
        SwingUtilities.invokeLater(() -> {

            MediaTracker tracker = new MediaTracker(drawPanel);
            try {
                tracker.waitForID(0);
            } catch (InterruptedException ignored) {
            }

            drawPanel.setPreferredSize(new Dimension(Logo.config.getImageWidth(), Logo.config.getImageHeight()));
            drawPanel.revalidate();
            drawPanel.initGraphics();
            kernel.initGraphics();
            //drawPanel.repaint();

            if (null != animation) animation.setPause(false);
        });

    }

    /**
     * Modify the Look&Feel for the Application
     */
    public void changeLookAndFeel() {
        try {
            switch (Logo.config.getLookAndFeel()) {
                case Config.LAF_NATIVE:
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    break;
                case Config.LAF_LIGHT:
                    UIManager.setLookAndFeel(new FlatLightLaf());
                    break;
                default:
                    UIManager.setLookAndFeel(new FlatDarkLaf());
                    break;
            }
            SwingUtilities.updateComponentTreeUI(this);
            editor.changeLookAndFeel();
            Logo.config.configureEditor(commandLine);
            commandLine.setHighlightCurrentLine(false);
            Logo.config.configureEditor(editor.textArea);
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Return the drawing area
     *
     * @return The drawing area
     */
    public DrawPanel getCanvas() {
        return drawPanel;
    }

    /**
     * Write in the History Panel
     *
     * @param sty This String represents the style for writing.<br>
     *            The possibilities are "normal", "erreur", "commentaire" or "perso"<br>
     *            - normal: classic style<br>
     *            - erreur: Red, when an error occured<br>
     *            - commentaire: blue, when the user leaves the editor<br>
     *            - perso: To write with the primtive "write" or "print"
     * @param txt The text to write
     */
    public void updateHistory(String sty, String txt) {
        historyPanel.setText(sty, txt);
    }

    /**
     * Set Focus on the command line
     */
    public void focusCommandLine() {
        commandLine.requestFocus();
        commandLine.getCaret().setVisible(true);
    }

    /**
     * Notice if the command line is editable.
     *
     * @return true if Command Line is editable, false otherwise
     */
    public boolean isCommandEditable() {
        return commandLine.isEditable();
    }

    /**
     * Set the text in the command Line
     *
     * @param txt The text to write
     */
    public void setCommandText(String txt) {
        commandLine.setText(txt);
    }

    /**
     * Get History panel
     *
     * @return The HistoryPanel
     */
    public HistoryPanel getHistoryPanel() {
        return historyPanel;
    }

    /**
     * Enable or disable the command line and the play button
     *
     * @param b The boolean true: enable command line, false: disable
     */
    public void setCommandEnabled(boolean b) {
        if (b) {
            if (SwingUtilities.isEventDispatchThread()) {
                commandLine.setEditable(true);
                runButton.setEnabled(true);
                stopButton.setEnabled(false);
            } else {
                SwingUtilities.invokeLater(() -> {
                    runButton.setEnabled(true);
                    stopButton.setEnabled(false);
                    commandLine.setEditable(true);
//						   commande.requestFocus();
                });
            }
        } else {
            runButton.setEnabled(false);
            stopButton.setEnabled(true);
            commandLine.setEditable(false);
        }
    }

    /**
     * This methos copy the selected Text in the command line
     */
    protected void copy() {
        commandLine.copy();
    }

    protected void copyImage() {
        Thread copie = new CopyImage();
        copie.start();
    }

    /**
     * This methos cut the selected Text in the command line
     */
    protected void cut() {
        commandLine.cut();
    }

    /**
     * This methos paste the selected Text into the command line
     */
    protected void paste() {
        commandLine.paste();
    }

    /**
     * This method selects all the text in the command line
     */
    protected void selectAll() {
        commandLine.selectAll();
    }

    /**
     * This method creates all primitives and loads the startup files
     */
    public void generatePrimitives() {
        Stack<String> stack = new Stack<>();
        editor.setEditable(false);
        int counter = Logo.config.getStartupFiles().size();

        for (int i = 0; i < counter; i++) {
            String txt = "";
            if (Logo.config.getStartupFiles().get(i).equals("#####"))
                editor.setEditable(true); //on a terminé avec les fichiers de démarrage
            else {
                try {
                    txt = Utils.readLogoFile(Logo.config.getStartupFiles().get(i));
                    if (!editor.isEditable()) stack.push(Logo.config.getStartupFiles().get(i));
                } catch (IOException e2) {
                    System.out.println("Problem reading file");
                }
                editor.appendText(txt);
                try {
                    editor.parseProcedures();
                } catch (Exception e3) {
                    e3.printStackTrace();
                }
                editor.clearText();
                editor.setVisible(false);
            }
        }

        Logo.config.setStartupFiles(new ArrayList<>(stack)); //On ne garde dans le path que les fichiers de démarrage
        editor.setEditable(true);
        editor.clearText();
    }

    /**
     * Returns an int that corresponds to the last key pressed.
     *
     * @return the int representing the last key pressed
     */
    public int getKey() {
        return commandKeyAdapter.getKey();
    }

    /**
     * Set the last key pressed to the key corresponding to integer i
     *
     * @param i The key code
     */
    public void setKey(int i) {
        commandKeyAdapter.setKey(i);
    }

    /**
     * Open the UiTranslatorFrame Dialog box
     */
    protected void showUiTranslator() {
        if (null == uiTranslator) {
            uiTranslator = new UiTranslator(this);
            uiTranslator.setBounds(100, 100, 600, 300);
            uiTranslator.setVisible(true);
        } else uiTranslator.requestFocus();
    }

    /**
     * Open the Startup Files Dialog box
     */
    protected void showStartupFiles() {
        if (null == startupDialog || !startupDialog.isVisible()) {
            startupDialog = new StartupFileDialog(this);
            startupDialog.setBounds(100, 100, 400, 250);
            startupDialog.setVisible(true);
        } else startupDialog.requestFocus();
    }

    /**
     * This boolean indicates if the viewer3D is visible
     *
     * @return true or false
     */
    public boolean viewer3DVisible() {
        if (null != viewer3D) return viewer3D.isVisible();
        return false;
    }

    /**
     * Initialize the 3D Viewer
     */
    public void initViewer3D() {
        if (null == viewer3D) {
            viewer3D = new Viewer3D(drawPanel.getWorld3D(), drawPanel.getScreenColor());
        }

    }

    public Viewer3D getViewer3D() {
        return viewer3D;
    }

    /**
     * Open the View3dFrame Frame
     */
    public void viewerOpen() {
        if (null == viewer3D) {
            viewer3D = new Viewer3D(drawPanel.getWorld3D(), drawPanel.getScreenColor());
        } else {
            viewer3D.setVisible(false);
        }
        viewer3D.insertBranch();
        viewer3D.setVisible(true);
        viewer3D.requestFocus();
    }

    /**
     * Open the Tranlator tool Dialog box
     */
    protected void showCodeTranslator() {
        if (null == codeTranslator || !codeTranslator.isVisible()) {
            codeTranslator = new CodeTranslator();
            codeTranslator.setVisible(true);
        } else {
            codeTranslator.setVisible(false);
            codeTranslator.setVisible(true);
        }
    }

    /**
     * Open the ProcedureEraserDialog Dialog box
     */
    protected void showProcedureEraser() {
        if (null == procedureEraser || !procedureEraser.isVisible()) {
            procedureEraser = new ProcedureEraser(this);
            procedureEraser.setBounds(100, 100, 300, 200);
            procedureEraser.setVisible(true);
        } else procedureEraser.requestFocus();
    }

    /**
     * Update the ProcedureEraserDialog Dialog box
     */
    public void updateProcedureEraser() {
        if (null != procedureEraser && procedureEraser.isVisible()) {
            procedureEraser.dispose();
            procedureEraser = new ProcedureEraser(this);
            procedureEraser.setBounds(100, 100, 300, 200);
            procedureEraser.setVisible(true);
        }
    }

    /**
     * Returns the current kernel
     *
     * @return The Kernel Object associated to main frame
     */
    public Kernel getKernel() {
        return kernel;
    }

    /**
     * Returns the current draw panel
     *
     * @return The DrawPanel Object associated to main frame
     */
    public DrawPanel getDrawPanel() {
        return drawPanel;
    }

    /**
     * Enables or disables the zoom buttons
     *
     * @param b The boolean
     */
    public void setZoomEnabled(boolean b) {
        zoomInButton.setEnabled(b);
        zoomOutButton.setEnabled(b);
    }

    /**
     * Initializes the main Frame
     */
    public void initTurtle() {
        // Centering turtle
        Dimension d = scrollPane.getViewport().getViewRect().getSize();
        Point p = new Point(Math.abs(Logo.config.getImageWidth() / 2 - d.width / 2), Math.abs(Logo.config.getImageHeight() / 2 - d.height / 2));
        scrollPane.getViewport().setViewPosition(p);

        // Displays turtle
        MediaTracker tracker = new MediaTracker(this);
        try {
            tracker.waitForID(0);
        } catch (InterruptedException ignored) {
        }
        scrollPane.validate();
    }

    /**
     * @author loic
     * This class is the Controller for the Command Line<br>
     * It looks for key event, Upper and Lower Arrow for History<br>
     * And all other Characters
     */
    class CommandKeyAdapter extends KeyAdapter {
        int key = -1;

        private boolean tape = false;

        public int getKey() {
            return key;
        }

        public void setKey(int i) {
            key = i;
        }

        public void keyPressed(KeyEvent e) {
            int ch = e.getKeyChar();
            int code = e.getKeyCode();
            if (commandLine.isEditable()) {
                if (code == KeyEvent.VK_UP) {
                    if (historyIndex > 0) {
                        if (historyIndex == historyStack.size()) {
                            tape = true;
                            historyStack.push(commandLine.getText());
                        }
                        historyIndex--;
                        commandLine.setText(historyStack.get(historyIndex));
                    } else historyIndex = 0;
                } else if (code == KeyEvent.VK_DOWN) {
                    if (historyIndex < historyStack.size() - 1) {
                        historyIndex++;
                        commandLine.setText(historyStack.get(historyIndex));
                    } else historyIndex = historyStack.size() - 1;
                } else if (code == KeyEvent.VK_ENTER) {
                    executeCommand();
                }
            } else {
                if (ch != 65535) key = ch;
                else key = -code;
            }
        }
    }

    class CopyImage extends Thread implements Transferable {
        private final BufferedImage image;
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();

        CopyImage() {
            image = getCanvas().getSelectionImage();
        }

        public void run() {
            clip.setContents(this, null);
        }

        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{DataFlavor.imageFlavor};
        }

        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return DataFlavor.imageFlavor.equals(flavor);
        }

        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
            if (!isDataFlavorSupported(flavor)) {
                throw new UnsupportedFlavorException(flavor);
            }
            return image;
        }
    }
}
/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo programming language
 *
 * @author Loïc Le Coq
 */
package xlogo.gui;

import xlogo.Config;
import xlogo.Logo;
import xlogo.gui.preferences.FontPanel;
import xlogo.gui.preferences.PreferencesDialog;
import xlogo.gui.translation.UiTranslator;
import xlogo.kernel.*;
import xlogo.kernel.network.NetworkServer;
import xlogo.kernel.perspective.Viewer3D;
import xlogo.utils.ExtensionFilter;
import xlogo.utils.ImageWriter;
import xlogo.utils.LogoException;
import xlogo.utils.Utils;

import javax.help.CSH;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import javax.swing.text.rtf.RTFEditorKit;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Stack;

public class Application extends JFrame {
    private static final double ZOOM_FACTOR = 1.25;
    public static String path = null;
    public static int fontId = FontPanel.police_id(Config.font);
    // Interpreter and drawPanel
    private final Kernel kernel;
    // UI Elements
    private final DrawPanel drawPanel;
    private final CommandLine commandLine = new CommandLine(this);
    private final HistoryPanel historyPanel = new HistoryPanel(this);
    private static final Stack<String> historyStack = new Stack<>();
    private int historyIndex = 0;
    private final SoundPlayer soundPlayer = new SoundPlayer(this);
    private final CommandKeyAdapter commandKeyAdapter = new CommandKeyAdapter();
    private final ArrayList<JMenuItem> menuItems = new ArrayList<>();
    private final JLabel commandLabel = new JLabel();
    private final EditorPopupMenu popupMenu = new EditorPopupMenu(commandLine);
    public String tempPath = null; // When opening a file
    public boolean error = false;
    boolean stop = false;
    public Editor editor;
    public Animation animation = null;
    public JScrollPane scrollPane = new JScrollPane();
    public JSplitPane splitPane = new JSplitPane();
    private JButton runButton;
    private JButton stopButton;
    private JButton zoomInButton;
    private JButton zoomOutButton;
    private JMenuItem fileNewMenuItem;
    private JMenuItem fileSaveMenuItem;
    // Dialog boxes available in menu
    private PreferencesDialog preferencesDialog = null;
    private StartupFileDialog startupDialog = null;
    private ProcedureEraser procedureEraser = null;
    private CodeTranslator codeTranslator = null;
    private UiTranslator uiTranslator = null;
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
        setIconImage(Toolkit.getDefaultToolkit().createImage(Utils.class.getResource("icone.png")));
        createMenuBar();
        createContent();
        updateLocalization();
        pack();
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
        fileNewMenuItem = createMenuItem(fileMenu, "nouveau", e -> newFile());
        createMenuItem(fileMenu, "menu.file.open", e -> openWorkspace());
        createMenuItem(fileMenu, "menu.file.saveas", e -> saveWorkspace(true));
        fileSaveMenuItem = createMenuItem(fileMenu, "menu.file.save", e -> saveWorkspace(false));

        var fileImageMenu = createMenu(fileMenu, "menu.file.captureimage");
        createMenuItem(fileImageMenu, "menu.file.captureimage.clipboard", e -> copyImage());
        createMenuItem(fileImageMenu, "menu.file.saveas", e -> saveImage());
        createMenuItem(fileImageMenu, "menu.file.captureimage.print", e -> printImage());

        var fileTextMenu = createMenu(fileMenu, "menu.file.textzone");
        createMenuItem(fileTextMenu, "menu.file.textzone.rtf", e -> saveHistory());
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
        createMenuItem(toolsMenu, "menu.tools.pencolor", e -> showFontChooser("fcc"));
        createMenuItem(toolsMenu, "menu.tools.screencolor", e -> showFontChooser("fcfg"));
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

    private JButton createButton(JToolBar parent, String iconName, ActionListener listener) {
        var button = new JButton(Logo.getIcon(iconName));
        button.addActionListener(listener);
        parent.add(button);
        return button;
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

        commandLine.setPreferredSize(new Dimension(300, 18 * Config.font.getSize() / 10));
        commandLine.setAlignmentY(JComponent.CENTER_ALIGNMENT);
        MouseListener popupListener = new PopupListener();
        commandLine.addMouseListener(popupListener);
        commandLine.addKeyListener(commandKeyAdapter);

        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        splitPane.add(scrollPane, JSplitPane.LEFT);
        splitPane.add(historyPanel, JSplitPane.RIGHT);
        splitPane.setResizeWeight(0.8);

        drawPanel.setSize(new java.awt.Dimension((int) (Config.imageWidth * DrawPanel.zoom), (int) (Config.imageHeight * DrawPanel.zoom)));
        scrollPane.getViewport().add(drawPanel);
        scrollPane.getHorizontalScrollBar().setBlockIncrement(5);
        scrollPane.getVerticalScrollBar().setBlockIncrement(5);
    }

    /**
     * Called by the constructor or when language has been modified
     */
    public void updateLocalization() {
        //		System.out.println(Config.police.getName());
        // Internal text to use for JFileChooser and JColorChooser
        UIManager.put("FileChooser.cancelButtonText", Logo.messages.getString("pref.cancel"));
        UIManager.put("FileChooser.cancelButtonToolTipText", Logo.messages.getString("pref.cancel"));
        UIManager.put("FileChooser.fileNameLabelText", Logo.messages.getString("nom_du_fichier"));
        UIManager.put("FileChooser.upFolderToolTipText", Logo.messages.getString("dossier_parent"));
        UIManager.put("FileChooser.lookInLabelText", Logo.messages.getString("rechercher_dans"));

        UIManager.put("FileChooser.newFolderToolTipText", Logo.messages.getString("nouveau_dossier"));
        UIManager.put("FileChooser.homeFolderToolTipText", Logo.messages.getString("repertoire_accueil"));
        UIManager.put("FileChooser.filesOfTypeLabelText", Logo.messages.getString("fichier_du_type"));
        UIManager.put("FileChooser.helpButtonText", Logo.messages.getString("menu.help"));

        UIManager.put("ColorChooser.rgbNameText", Logo.messages.getString("rgb"));
        UIManager.put("ColorChooser.rgbBlueText", Logo.messages.getString("bleu"));
        UIManager.put("ColorChooser.rgbGreenText", Logo.messages.getString("vert"));
        UIManager.put("ColorChooser.rgbRedText", Logo.messages.getString("rouge"));

        UIManager.put("ColorChooser.swatchesNameText", Logo.messages.getString("echantillon"));

        UIManager.put("ColorChooser.hsbNameText", Logo.messages.getString("hsb"));
        UIManager.put("ColorChooser.hsbBlueText", Logo.messages.getString("hsbbleu"));
        UIManager.put("ColorChooser.hsbGreenText", Logo.messages.getString("hsbvert"));
        UIManager.put("ColorChooser.hsbRedText", Logo.messages.getString("hsbrouge"));

        UIManager.put("ColorChooser.swatchesRecentText", Logo.messages.getString("dernier"));
        UIManager.put("ColorChooser.previewText", Logo.messages.getString("apercu"));
        UIManager.put("ColorChooser.sampleText", Logo.messages.getString("echantillon_texte"));
        UIManager.put("ColorChooser.okText", Logo.messages.getString("pref.ok"));
        UIManager.put("ColorChooser.resetText", Logo.messages.getString("restaurer"));
        UIManager.put("ColorChooser.cancelText", Logo.messages.getString("pref.cancel"));
        UIManager.put("ColorChooser.previewText", Logo.messages.getString("apercu"));

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
        ImageIcon icon = new ImageIcon(Utils.class.getResource("icone.png"));
        String[] choice = {Logo.messages.getString("pref.ok"), Logo.messages.getString("pref.cancel")};
        int val = JOptionPane.showOptionDialog(this, jt, Logo.messages.getString("menu.file.quit"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, icon, choice, choice[0]);
        if (val == JOptionPane.OK_OPTION) {
            writeConfig();
            System.exit(0);
        }
    }

    private void newFile() {
        var wp = kernel.getWorkspace();
        String[] choice = {Logo.messages.getString("pref.ok"), Logo.messages.getString("pref.cancel")};
        ImageIcon icon = new ImageIcon(Utils.class.getResource("icone.png"));
        MessageTextArea jt = new MessageTextArea(Logo.messages.getString("enregistrer_espace"));
        int val = JOptionPane.showOptionDialog(this, jt, Logo.messages.getString("enregistrer_espace"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, icon, choice, choice[0]);

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
            setNewEnabled(false);
            Config.mainCommand = "";

        } else setNewEnabled(true);
    }

    private void showCloseEditor() {
        JOptionPane.showMessageDialog(this, Logo.messages.getString("ferme_editeur"), Logo.messages.getString("erreur"), JOptionPane.ERROR_MESSAGE);
    }

    private void openWorkspace() {
        JFileChooser jf = new JFileChooser(Utils.SortieTexte(Config.defaultFolder));
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
                editor.setEditorStyledText(txt);
                editor.initMainCommand();
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
            JFileChooser jf = new JFileChooser(Utils.SortieTexte(Config.defaultFolder));
            String[] ext = {".lgo"};
            jf.addChoosableFileFilter(new ExtensionFilter(Logo.messages.getString("fichiers_logo"), ext));

            int val = jf.showDialog(this, Logo.messages.getString("menu.file.save"));
            if (val == JFileChooser.APPROVE_OPTION) {
                path = jf.getSelectedFile().getPath();
                String path2 = path.toLowerCase();  // on garde la casse du path pour les systemes d'exploitation faisant la diff�rence
                if (!path2.endsWith(".lgo")) path += ".lgo";
                Application.path = path;
                setSaveEnabled(true);
                setTitle(path + " - XLogo");
                try {
                    File f = new File(path);
                    Config.defaultFolder = Utils.rajoute_backslash(f.getParent());
                } catch (NullPointerException e2) {
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

    private void saveHistory() {
        RTFEditorKit myRTFEditorKit = new RTFEditorKit();
        StyledDocument myStyledDocument = getHistoryPanel().sd_Historique();
        try {
            JFileChooser jf = new JFileChooser(Utils.SortieTexte(Config.defaultFolder));
            String[] ext = {".rtf"};
            jf.addChoosableFileFilter(new ExtensionFilter(Logo.messages.getString("fichiers_rtf"), ext));
            int val = jf.showDialog(this, Logo.messages.getString("menu.file.save"));
            if (val == JFileChooser.APPROVE_OPTION) {
                String path = jf.getSelectedFile().getPath();
                String path2 = path.toLowerCase();  // on garde la casse du path pour les syst�mes d'exploitation faisant la diff�rence
                if (!path2.endsWith(".rtf")) path += ".rtf";
                FileOutputStream myFileOutputStream = new FileOutputStream(path);
                myRTFEditorKit.write(myFileOutputStream, myStyledDocument, 0, myStyledDocument.getLength() - 1);
                myFileOutputStream.close();
            }
        } catch (IOException e2) {
        } catch (BadLocationException e3) {
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

    void showFontChooser(String key) {
        String title = "";
        if (key.equals("fcfg")) {
            title = "couleur_du_fond";
        } else if (key.equals("fcc")) {
            title = "couleur_du_crayon";
        }
        Color color = JColorChooser.showDialog(this, Logo.messages.getString(title), getCanvas().getBackgroundColor());
        if (null != color) {
            Locale locale = Logo.getLocale(Config.language);
            java.util.ResourceBundle rs = java.util.ResourceBundle.getBundle("primitives", locale);
            var f = rs.getString(key);
            f = f.substring(0, f.indexOf(" "));
            updateHistory("commentaire", f + " [" + color.getRed() + " " + color.getGreen() + " " + color.getBlue() + "]\n");
            if (key.equals("fcfg")) {
                kernel.fcfg(color);
            } else if (key.equals("fcc")) {
                kernel.fcc(color);
            }
        }
    }

    void showPreferences() {
        if (editor.isVisible()) showCloseEditor();
        else if (null == preferencesDialog) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    preferencesDialog = new PreferencesDialog(getApp());
                    preferencesDialog.setBounds(100, 100, 600, 580);
                    preferencesDialog.setVisible(true);
                }
            });
        } else {
            preferencesDialog.requestFocus();
        }
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
        String message = Logo.messages.getString("message_a_propos1") + Config.VERSION + "\n\n" + Logo.messages.getString("message_a_propos2") + " " + Config.WEB_SITE;
        MessageTextArea jt = new MessageTextArea(message);
        ImageIcon icon = new ImageIcon(Utils.class.getResource("icone.png"));
        JOptionPane.showMessageDialog(null, jt, Logo.messages.getString("menu.help.about"), JOptionPane.INFORMATION_MESSAGE, icon);
    }

    private void showLicence(boolean english) {
        JFrame frame = new JFrame(Logo.messages.getString("menu.help.licence"));
        frame.setIconImage(Toolkit.getDefaultToolkit().createImage(Utils.class.getResource("icone.png")));
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setSize(500, 500);
        LicensePane editorPane = new LicensePane();
        editorPane.setEditable(false);
        String path = "gpl/gpl-";
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
        startAnimation(Utils.decoupe(Config.mainCommand));
        getHistoryPanel().ecris("normal", Config.mainCommand + "\n");
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
     * Write the Configuration file when the user quits XLogo
     */
    private void writeConfig() {
        try {
            FileOutputStream f = new FileOutputStream(System.getProperty("user.home") + File.separator + ".xlogo");
            BufferedOutputStream b = new BufferedOutputStream(f);
            OutputStreamWriter osw = new OutputStreamWriter(b, StandardCharsets.UTF_8);
            StringBuffer sb = new StringBuffer();
            int eff;
            if (Config.eraseImage) eff = 1;
            else eff = 0;
            int cle;
            if (Config.clearVariables) cle = 1;
            else cle = 0;
            sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
            sb.append("<xlogo>\n");
            sb.append("\t<lang value=\"");
            sb.append(Config.language);
            sb.append("\"/>\n");
            sb.append("\t<speed value=\"");
            sb.append(Config.turtleSpeed);
            sb.append("\"/>\n");
            sb.append("\t<tcp_port value=\"");
            sb.append(Config.tcpPort);
            sb.append("\"/>\n");
            sb.append("\t<turtle_shape value=\"");
            sb.append(Config.activeTurtle);
            sb.append("\"/>\n");
            sb.append("\t<max_number_turtle value=\"");
            sb.append(Config.maxTurtles);
            sb.append("\"/>\n");
            sb.append("\t<pen_shape value=\"");
            sb.append(Config.penShape);
            sb.append("\"/>\n");
            sb.append("\t<cleanscreen_leaving_editor value=\"");
            sb.append(eff);
            sb.append("\"/>\n");
            sb.append("\t<clear_variables_closing_editor value=\"");
            sb.append(cle);
            sb.append("\"/>\n");
            sb.append("\t<pen_width_max value=\"");
            sb.append(Config.maxPenWidth);
            sb.append("\"/>\n");
            sb.append("\t<pen_color value=\"");
            sb.append(Config.penColor.getRGB());
            sb.append("\"/>\n");
            sb.append("\t<screen_color value=\"");
            sb.append(Config.screenColor.getRGB());
            sb.append("\"/>\n");
            sb.append("\t<default_directory value=\"");
            sb.append(Utils.SortieTexte(Config.defaultFolder));
            sb.append("\"/>\n");
            sb.append("\t<start_command value=\"");
            sb.append(Utils.specialCharacterXML(Config.startupCommand));
            sb.append("\"/>\n");
            sb.append("\t<font name=\"");
            sb.append(Config.font.getName());
            sb.append("\" size=\"");
            sb.append(Config.font.getSize());
            sb.append("\"/>\n");
            sb.append("\t<width value=\"");
            sb.append(Config.imageWidth);
            sb.append("\"/>\n");
            sb.append("\t<height value=\"");
            sb.append(Config.imageHeight);
            sb.append("\"/>\n");
            sb.append("\t<memory value=\"");
            sb.append(Config.newMemoryLimit);
            sb.append("\"/>\n");
            sb.append("\t<quality value=\"");
            sb.append(Config.drawQuality);
            sb.append("\"/>\n");
            sb.append("\t<looknfeel value=\"");
            sb.append(Config.lookAndFeel);
            sb.append("\"/>\n");
            sb.append("\t<syntax_highlighting\n\t\tboolean=\"");
            sb.append(Config.syntaxHighlightingEnabled);
            sb.append("\"\n\t\tcolor_commentaire=\"");
            sb.append(Config.syntaxCommentColor);
            sb.append("\"\n\t\tcolor_operand=\"");
            sb.append(Config.syntaxOperandColor);
            sb.append("\"\n\t\tcolor_parenthesis=\"");
            sb.append(Config.syntaxBracketColor);
            sb.append("\"\n\t\tcolor_primitive=\"");
            sb.append(Config.syntaxPrimitiveColor);
            sb.append("\"\n\t\tstyle_commentaire=\"");
            sb.append(Config.syntaxCommentStyle);
            sb.append("\"\n\t\tstyle_operand=\"");
            sb.append(Config.syntaxOperandStyle);
            sb.append("\"\n\t\tstyle_parenthesis=\"");
            sb.append(Config.syntaxBracketStyle);
            sb.append("\"\n\t\tstyle_primitive=\"");
            sb.append(Config.syntaxPrimitiveStyle);
            sb.append("\">\n\t</syntax_highlighting>\n\t<border_image\n");
            for (int i = 0; i < Config.userBorderImages.size(); i++) {
                sb.append("\t\timage" + i + "=\"");
                sb.append(Config.userBorderImages.get(i));
                sb.append("\"\n");
            }
            if (null != Config.borderColor) {
                sb.append(">\n\t</border_image>\n\t<border_color\n\t\tvalue=\"");
                sb.append(Config.borderColor.getRGB());
                sb.append("\">\n\t</border_color>\n\t<border_image_selected\n\t\tvalue=\"");
            } else sb.append(">\n\t</border_image>\n\t<border_image_selected\n\t\tvalue=\"");
            sb.append(Config.borderImageSelected);
            sb.append("\">\n\t</border_image_selected>\n\t<grid\n\t\tboolean=\"");
            sb.append(Config.gridEnabled);
            sb.append("\"\n\t\txgrid=\"");
            sb.append(Config.xGridSpacing);
            sb.append("\"\n\t\tygrid=\"");
            sb.append(Config.yGridSpacing);
            sb.append("\"\n\t\tgridcolor=\"");
            sb.append(Config.gridColor);
            sb.append("\">\n\t</grid>\n\t<axis\n\t\tboolean_xaxis=\"");
            sb.append(Config.xAxisEnabled);
            sb.append("\"\n\t\tboolean_yaxis=\"");
            sb.append(Config.yAxisEnabled);
            sb.append("\"\n\t\txaxis=\"");
            sb.append(Config.xAxisSpacing);
            sb.append("\"\n\t\tyaxis=\"");
            sb.append(Config.yAxisSpacing);
            sb.append("\"\n\t\taxiscolor=\"");
            sb.append(Config.axisColor);
            sb.append("\">\n\t</axis>\n\t<startup_files\n");
            int i = 0;
            while (!Config.startupFiles.isEmpty()) {
                String att = "\t\tfile" + i + "=\"";
                sb.append(att);
                sb.append(Config.startupFiles.remove(Config.startupFiles.size() - 1));
                sb.append("\"\n");
                i++;
            }
            sb.append(">\n\t</startup_files>\n");
            sb.append("</xlogo>");
            osw.write(new String(sb));
            //	System.out.println(sb);
            osw.close();
            b.close();
            f.close();
        } catch (IOException e) {
            System.out.println("write error");
        }
    }

    /**
     * When the user types "Enter" in the Command Line
     */
    public void executeCommand() {
        //	System.out.println("commandeTotal :"+Runtime.getRuntime().totalMemory()/1024/1024+" Free "+Runtime.getRuntime().freeMemory()/1024/1024);
        // Si une parenthese était sélectionnée, on désactive la décoloration
        commandLine.setActive(false);
        //	System.out.println(commande.getCaret().isVisible());
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
            historyPanel.ecris("normal", text + "\n");

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
            startAnimation(Utils.decoupe(text));

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

    /**
     * Close the preference dialog box
     */
    public void closePreferences() {
        preferencesDialog = null;
    }
    // Change font for the interface
    // Change la police de l'interface

    public void closeUiTranslator() {
        uiTranslator = null;
    }

    /**
     * Modify the language for the interface
     *
     * @param id The integer representing the choosen language
     */

    public void changeLanguage(int id) {
        Config.language = id;
        Logo.generateLanguage(id);
        updateLocalization();
        kernel.buildPrimitiveTreemap(id);
        editor = new Editor(this);
        historyPanel.changeLanguage();
        if (null != viewer3D) viewer3D.setText();
    }

    /**
     * Modify the font for the main Interface
     *
     * @param font The choosen Font
     * @param size The font size
     */
    public void changeFont(Font font, int size) {
        Config.font = font;
        updateLocalization();
        historyPanel.getDsd().change_police_interface(font, size);
    }

    /**
     * Resize the dawing area
     */
    public void resizeDrawingZone() {
        if (null != animation) {
            animation.setPause(true);
        }
        // resize the drawing image
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                //DrawPanel.image = new BufferedImage((Config.imageWidth*3)/2, (Config.imageHeight*3)/2, BufferedImage.TYPE_INT_RGB);
                // 	System.out.println("Total :"+Runtime.getRuntime().totalMemory()/1024+" max "+Runtime.getRuntime().maxMemory()/1024+" Free "+Runtime.getRuntime().freeMemory()/1024);
                MediaTracker tracker = new MediaTracker(drawPanel);
                //tracker.addImage(DrawPanel.image, 0);
                try {
                    tracker.waitForID(0);
                } catch (InterruptedException e) {
                }

                drawPanel.setPreferredSize(new Dimension(Config.imageWidth, Config.imageHeight));
                drawPanel.revalidate();
                drawPanel.initGraphics();
                kernel.initGraphics();
                //drawPanel.repaint();

                if (null != animation) animation.setPause(false);
            }

        });

    }

    /**
     * Modify the Look&Feel for the Application
     *
     * @throws Exception
     */
    public void changeLookAndFeel() throws Exception {
        SwingUtilities.updateComponentTreeUI(this);
        SwingUtilities.updateComponentTreeUI(editor);
    }

    /**
     * Change Syntax Highlighting for the editor,
     * the command line and the History zone
     */
    public void changeSyntaxHighlightingStyle() {
        editor.initStyles(Config.syntaxCommentColor, Config.syntaxCommentStyle, Config.syntaxPrimitiveColor, Config.syntaxPrimitiveStyle, Config.syntaxBracketColor, Config.syntaxBracketStyle, Config.syntaxOperandColor, Config.syntaxOperandStyle);
        commandLine.initStyles(Config.syntaxCommentColor, Config.syntaxCommentStyle, Config.syntaxPrimitiveColor, Config.syntaxPrimitiveStyle, Config.syntaxBracketColor, Config.syntaxBracketStyle, Config.syntaxOperandColor, Config.syntaxOperandStyle);
        historyPanel.getDsd().initStyles(Config.syntaxCommentColor, Config.syntaxCommentStyle, Config.syntaxPrimitiveColor, Config.syntaxPrimitiveStyle, Config.syntaxBracketColor, Config.syntaxBracketStyle, Config.syntaxOperandColor, Config.syntaxOperandStyle);
    }

    /**
     * Enable or disable Syntax Highlighting
     */
    public void setSyntaxHighlightingEnabled(boolean b) {
        historyPanel.setColoration(b);
        commandLine.setColoration(b);
    }

    /**
     * Resize the Command line (height)
     */
    public void resizeCommandLine() {
        commandLine.setPreferredSize(new Dimension(300, Config.font.getSize() * 18 / 10));
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
        historyPanel.ecris(sty, txt);
    }

    /**
     * Set Focus on the command line
     */
    public void focusCommandLine() {
        commandLine.requestFocus();
        commandLine.getCaret().setVisible(true);
    }

    /**
     * Set the Menu "Save File" enable or disable
     *
     * @param b true or false
     */
    public void setSaveEnabled(boolean b) {
        fileSaveMenuItem.setEnabled(true);
    }

    /**
     * Notice if the menu File-New is enabled.
     *
     * @return true if Menu File-New is enabled, false otherwise
     */
    public boolean isNewEnabled() {
        return fileNewMenuItem.isEnabled();
    }

    /**
     * Set the Menu "File New" enable or disable
     *
     * @param b true or false
     */
    public void setNewEnabled(boolean b) {
        fileNewMenuItem.setEnabled(b);
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
        kernel.initPrimitive();
        Stack<String> stack = new Stack<>();
        editor.setEditable(false);
        int counter = Config.startupFiles.size();

        for (int i = 0; i < counter; i++) {
            String txt = "";
            if (Config.startupFiles.get(i).equals("#####"))
                editor.setEditable(true); //on a terminé avec les fichiers de démarrage
            else {
                try {
                    txt = Utils.readLogoFile(Config.startupFiles.get(i));
                    if (!editor.isEditable()) stack.push(Config.startupFiles.get(i));
                } catch (IOException e2) {
                    System.out.println("Problem reading file");
                }
                editor.setEditorStyledText(txt);
                try {
                    editor.analyzeProcedure();
                } catch (Exception e3) {
                    System.out.println(e3);
                }
                editor.clearText();
                editor.setVisible(false);
            }
        }

        Config.startupFiles = new ArrayList<>(stack); //On ne garde dans le path que les fichiers de démarrage
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

    private Application getApp() {
        return this;
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
            viewer3D = new Viewer3D(drawPanel.getWorld3D(), drawPanel.getBackgroundColor());
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
            viewer3D = new Viewer3D(drawPanel.getWorld3D(), drawPanel.getBackgroundColor());
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
                }
            } else {
                if (ch != 65535) key = ch;
                else key = -code;
            }
        }
    }

    /**
     * The Mouse popup Menu
     *
     * @author loic
     */

    class PopupListener extends MouseAdapter {

        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                popupMenu.show(e.getComponent(), e.getX(), e.getY());
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
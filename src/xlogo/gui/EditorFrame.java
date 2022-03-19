package xlogo.gui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import org.fife.rsta.ui.search.ReplaceDialog;
import org.fife.rsta.ui.search.SearchEvent;
import org.fife.rsta.ui.search.SearchListener;
import org.fife.ui.rsyntaxtextarea.*;
import org.fife.ui.rtextarea.*;
import xlogo.Config;
import xlogo.Logo;
import xlogo.gui.preferences.PreferencesDialog;
import xlogo.gui.translation.UiTranslator;
import xlogo.kernel.*;
import xlogo.kernel.network.NetworkServer;
import xlogo.utils.ExtensionFilter;
import xlogo.utils.LogoException;
import xlogo.utils.Utils;

import javax.help.CSH;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Stack;

import static xlogo.utils.Utils.createButton;

/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo
 * programming language
 *
 * @author Loïc Le Coq
 */

/* The main class for the EditorFrame windows
 *
 *  */
public class EditorFrame extends JFrame implements SearchListener {
    public final GraphFrame graphFrame;
    private final Stack<String> historyStack = new Stack<>();
    private final CommandKeyAdapter commandKeyAdapter = new CommandKeyAdapter();
    private final JLabel commandLabel = new JLabel();
    private final ArrayList<JMenuItem> menuItems = new ArrayList<>();
    private final HistoryPanel historyPanel = new HistoryPanel(this);
    private final Workspace workspace;
    public JSplitPane splitPane = new JSplitPane();
    private String path;
    private PreferencesDialog preferencesDialog;
    private StartupFileDialog startupDialog;
    private ProcedureEraser procedureEraser;
    private CodeTranslator codeTranslator;
    private UiTranslator uiTranslator;
    private JMenuItem fileNewMenuItem;
    private JMenuItem fileSaveMenuItem;
    private JButton runButton;
    private int historyIndex = 0;
    private boolean running = false;
    private boolean editable = true;
    private RSyntaxTextArea textArea;
    private RSyntaxTextArea commandLine;
    private ReplaceDialog replaceDialog;
    private JTextField mainCommand;
    private String tempPath;

    public EditorFrame() {
        this.graphFrame = new GraphFrame(this);
        this.workspace = Logo.kernel.getWorkspace();
        setIconImage(Logo.getAppIcon().getImage());
        setTitle("XLogo");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        initContent();
        initCommandLine();
        initSearchDialogs();
        initToolBar();
        initMenuBar();
        setRunning(false);
        updateLocalization();
        pack();
        setLocationRelativeTo(null);
        graphFrame.setVisible(true);
    }

    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            closeWindow();
        } else if (e.getID() == WindowEvent.WINDOW_ACTIVATED) {
            textArea.requestFocus();
        }
    }

    public void parseProcedures() throws SyntaxException {
        workspace.parseProcedures(textArea.getText(), editable);
    }

    private void initSearchDialogs() {
        replaceDialog = new ReplaceDialog(this, this);

        // This ties the properties of the two dialogs together (match case,
        // regex, etc.).
        SearchContext context = replaceDialog.getSearchContext();
        replaceDialog.setSearchContext(context);
    }

    private void initCommandLine() {
        commandLine = new RSyntaxTextArea(1, 1);
        commandLine.setSyntaxEditingStyle("text/logo");
        commandLine.setHighlightCurrentLine(false);
        InputStream in = getClass().
                getResourceAsStream("/org/fife/ui/rsyntaxtextarea/themes/monokai.xml");
        try {
            Theme theme = Theme.load(in);
            theme.apply(commandLine);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        //commandLine.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), null);
        var commandPanel = new JPanel();
        commandPanel.setLayout(new BorderLayout());
        commandPanel.add(commandLabel, BorderLayout.WEST);
        commandPanel.add(commandLine, BorderLayout.CENTER);

        getContentPane().add(commandPanel, BorderLayout.SOUTH);

        commandLine.setPreferredSize(new Dimension(300, 18 * Logo.config.getFont().getSize() / 10));
        commandLine.setAlignmentY(JComponent.CENTER_ALIGNMENT);
        commandLine.addKeyListener(commandKeyAdapter);

    }

    private void rstaButton(JToolBar parent, String iconName, String toolTip, int action) {
        var button = parent.add(RTextArea.getAction(action));
        button.setIcon(Logo.getIcon(iconName));
        button.setHideActionText(true);
        button.setToolTipText(Logo.messages.getString(toolTip));
    }

    private void initToolBar() {
        var toolBar = new JToolBar(JToolBar.HORIZONTAL);
        //createButton(toolBar, "save", "lire_editeur", e -> saveEdits()).setMnemonic('Q');
        //toolBar.addSeparator();
        createButton(toolBar, "print", "imprimer_editeur", e -> print());
        toolBar.addSeparator();
        rstaButton(toolBar, "cut", "menu.edition.cut", RTextArea.CUT_ACTION);
        rstaButton(toolBar, "copy", "menu.edition.copy", RTextArea.COPY_ACTION);
        rstaButton(toolBar, "paste", "menu.edition.paste", RTextArea.PASTE_ACTION);
        toolBar.addSeparator();
        rstaButton(toolBar, "undo", "editor.undo", RTextArea.UNDO_ACTION);
        rstaButton(toolBar, "redo", "editor.redo", RTextArea.REDO_ACTION);
        toolBar.addSeparator();
        createButton(toolBar, "search", "find", e -> showReplace());
        toolBar.addSeparator();
        runButton = createButton(toolBar, "run", e -> runOrStop());
        mainCommand = new JTextField();
        toolBar.add(mainCommand);
        getContentPane().add(toolBar, BorderLayout.NORTH);
    }

    private void initContent() {
        textArea = new RSyntaxTextArea(25, 80);
        // Register syntax highlighter
        AbstractTokenMakerFactory atmf = (AbstractTokenMakerFactory) TokenMakerFactory.getDefaultInstance();
        atmf.putMapping("text/logo", "xlogo.gui.LogoTokenMaker");
        textArea.setSyntaxEditingStyle("text/logo");
        textArea.setCodeFoldingEnabled(true);
        InputStream in = getClass().
                getResourceAsStream("/org/fife/ui/rsyntaxtextarea/themes/monokai.xml");
        try {
            Theme theme = Theme.load(in);
            theme.apply(textArea);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        var scrollPane = new RTextScrollPane(textArea);
        historyPanel.setMinimumSize(new Dimension(600, 40));
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        splitPane.add(scrollPane, JSplitPane.LEFT);
        splitPane.add(historyPanel, JSplitPane.RIGHT);
        splitPane.setResizeWeight(0.8);
        getContentPane().add(splitPane, BorderLayout.CENTER);
        //ErrorStrip errorStrip = new ErrorStrip(textArea);
        //getContentPane().add(errorStrip, BorderLayout.EAST);
    }

    private void print() {
        try {
            textArea.print();
        } catch (PrinterException ex) {
            ex.printStackTrace();
        }
    }

    private void showReplace() {
        replaceDialog.setVisible(true);
    }

    /**
     * This method displays the editorFrame (if necessary)
     * and adds all defined procedures
     */
    public void open() {
        if (!isVisible()) {
            setVisible(true);
            toFront();
            setTitle("XLogo");
            for (int i = 0; i < workspace.getNumberOfProcedure(); i++) {
                Procedure procedure = workspace.getProcedure(i);
                appendText(procedure.toString());
            }
            setMainCommand();
            textArea.requestFocus();
        } else {
            setVisible(false);
            setVisible(true);
            textArea.requestFocus();
        }
    }

    public void updateWorkspace() {
        try {
            parseProcedures();
            if (null != tempPath) {
                path = tempPath;
                setTitle(Paths.get(path).getFileName() + " - XLogo");
                try {
                    File f = new File(tempPath);
                    Logo.config.setDefaultFolder(Utils.escapeString(f.getParent()));
                } catch (NullPointerException ignored) {
                }
                tempPath = null;
                setSaveEnabled(true);
            }
            if (!isNewEnabled())
                setNewEnabled(true);
        } catch (SyntaxException ignored) {
        }
        focusCommandLine();
        if (Logo.config.isEraseImage()) { //Effacer la zone de dessin
            LogoException.lance = true;
            graphFrame.error = true;
            try {
                while (!isCommandEditable()) Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
            Logo.kernel.vide_ecran();
            focusCommandLine();
        }
        if (Logo.config.isClearVariables()) {
            // Interrupt any running programs
            LogoException.lance = true;
            graphFrame.error = true;
            try {
                while (!isCommandEditable()) Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
            Logo.kernel.getWorkspace().deleteAllVariables();
            Logo.kernel.getWorkspace().deleteAllPropertyLists();
            focusCommandLine();

        }
        Logo.setMainCommand(mainCommand.getText());
    }

    public void setMainCommand() {
        mainCommand.setText(Logo.getMainCommand());
        setRunning(false);
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean b) {
        editable = b;
    }

    public void clearText() {
        textArea.setText("");
    }

    public void appendText(String txt) { textArea.append(txt); }

    public void focusTextArea() {
        textArea.requestFocus();
    }

    @Override
    public void searchEvent(SearchEvent e) {
        SearchEvent.Type type = e.getType();
        SearchContext context = e.getSearchContext();
        SearchResult result;

        switch (type) {
            default: // Prevent FindBugs warning later
            case MARK_ALL:
                result = SearchEngine.markAll(textArea, context);
                break;
            case FIND:
                result = SearchEngine.find(textArea, context);
                if (!result.wasFound() || result.isWrapped()) {
                    UIManager.getLookAndFeel().provideErrorFeedback(textArea);
                }
                break;
            case REPLACE:
                result = SearchEngine.replace(textArea, context);
                if (!result.wasFound() || result.isWrapped()) {
                    UIManager.getLookAndFeel().provideErrorFeedback(textArea);
                }
                break;
            case REPLACE_ALL:
                result = SearchEngine.replaceAll(textArea, context);
                JOptionPane.showMessageDialog(null, result.getCount() +
                        " occurrences replaced.");
                break;
        }
    }

    @Override
    public String getSelectedText() {
        return textArea.getSelectedText();
    }

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
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        SwingUtilities.updateComponentTreeUI(this);
        graphFrame.changeLookAndFeel();
        replaceDialog.updateUI();
        pack();
    }

    void clearWorkspace() {
       var wp = Logo.kernel.getWorkspace();
       String[] choice = {Logo.messages.getString("pref.ok"), Logo.messages.getString("pref.cancel")};
       MessageTextArea jt = new MessageTextArea(Logo.messages.getString("enregistrer_espace"));
       int val = JOptionPane.showOptionDialog(this, jt, Logo.messages.getString("enregistrer_espace"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, Logo.getAppIcon(), choice, choice[0]);

       if (val == JOptionPane.OK_OPTION) {
           wp.deleteAllProcedures();
           wp.deleteAllVariables();
           if (null != path) {
               path = null;
               setTitle("XLogo");
           }
           clearText();
           setNewEnabled(false);
           Logo.setMainCommand("");

       } else setNewEnabled(true);
   }

    void openWorkspace() {
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
                appendText(txt);
                setMainCommand();
            }
            tempPath = path;
        }
    }

    void saveWorkspace(boolean promptName) {
        var wp = Logo.kernel.getWorkspace();
        String path = this.path;
        if (promptName || null == path) {
            JFileChooser jf = new JFileChooser(Utils.unescapeString(Logo.config.getDefaultFolder()));
            String[] ext = {".lgo"};
            jf.addChoosableFileFilter(new ExtensionFilter(Logo.messages.getString("fichiers_logo"), ext));

            int val = jf.showDialog(this, Logo.messages.getString("menu.file.save"));
            if (val == JFileChooser.APPROVE_OPTION) {
                path = jf.getSelectedFile().getPath();
                String path2 = path.toLowerCase();  // on garde la casse du path pour les systemes d'exploitation faisant la diff�rence
                if (!path2.endsWith(".lgo")) path += ".lgo";
                this.path = path;
                setSaveEnabled(true);
                setTitle(Paths.get(path).getFileName() + " - XLogo");
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

    /**
     * Change Syntax Highlighting for the editorFrame,
     * the command line and the History zone
     */
    public void changeSyntaxHighlightingStyle() {
    }

    /**
     * Enable or disable Syntax Highlighting
     * @param b true or false
     */
    public void setSyntaxHighlightingEnabled(boolean b) {
    }

    /**
     * Set the Menu "Save File" enable or disable
     *
     * @param b true or false
     */
    void setSaveEnabled(boolean b) {
        fileSaveMenuItem.setEnabled(b);
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

    private JMenuItem rstaMenuItem(JMenu parent, String iconName, String key, int action) {
        var item = parent.add(RTextArea.getAction(action));
        item.setIcon(Logo.getIcon(iconName));
        item.setActionCommand(key);
        menuItems.add(item);
        parent.add(item);
        return item;
    }

     private JMenuItem createMenuItem(JMenu parent, String key, ActionListener listener) {
        var item = new JMenuItem();
        item.setActionCommand(key);
        menuItems.add(item);
        item.addActionListener(listener);
        parent.add(item);
        return item;
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

     private void initMenuBar() {
        var menuBar = new JMenuBar();

        var fileMenu = createMenu("menu.file");
        menuBar.add(fileMenu);
        fileNewMenuItem = createMenuItem(fileMenu, "nouveau", e -> clearWorkspace());
        createMenuItem(fileMenu, "menu.file.open", e -> openWorkspace());
        createMenuItem(fileMenu, "menu.file.saveas", e -> saveWorkspace(true));
        fileSaveMenuItem = createMenuItem(fileMenu, "menu.file.save", e -> saveWorkspace(false));

        var fileImageMenu = createMenu(fileMenu, "menu.file.captureimage");
        createMenuItem(fileImageMenu, "menu.file.captureimage.clipboard", e -> graphFrame.copyImage());
        createMenuItem(fileImageMenu, "menu.file.saveas", e -> graphFrame.saveImage());
        createMenuItem(fileImageMenu, "menu.file.captureimage.print", e -> graphFrame.printImage());

        var fileTextMenu = createMenu(fileMenu, "menu.file.textzone");
        createMenuItem(fileTextMenu, "menu.file.textzone.rtf", e -> historyPanel.saveHistory());
        createMenuItem(fileMenu, "menu.file.quit", e -> closeWindow());

        var editMenu = createMenu("menu.edition");
        menuBar.add(editMenu);
        rstaMenuItem(editMenu, "cut", "menu.edition.cut", RTextArea.CUT_ACTION);
        rstaMenuItem(editMenu, "copy", "menu.edition.copy", RTextArea.COPY_ACTION);
        rstaMenuItem(editMenu, "paste", "menu.edition.paste", RTextArea.PASTE_ACTION);
        editMenu.addSeparator();
        rstaMenuItem(editMenu, "undo", "editor.undo", RTextArea.UNDO_ACTION);
        rstaMenuItem(editMenu, "redo", "editor.redo", RTextArea.REDO_ACTION);
         menuBar.add(editMenu);

        var toolsMenu = createMenu("menu.tools");
        menuBar.add(toolsMenu);
        createMenuItem(toolsMenu, "menu.tools.pencolor", e -> graphFrame.showColorChooser(true));
        createMenuItem(toolsMenu, "menu.tools.screencolor", e -> graphFrame.showColorChooser(false));
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

    /**
     * Called by the constructor or when language has been modified
     */
    private void updateLocalization() {
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

     private void showPreferences() {
        if (null == preferencesDialog) {
            SwingUtilities.invokeLater(() -> {
                preferencesDialog = new PreferencesDialog(this);
                preferencesDialog.setBounds(100, 100, 600, 580);
                preferencesDialog.setVisible(true);
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
        String message = Logo.messages.getString("message_a_propos1") + Logo.VERSION + "\n\n" + Logo.messages.getString("message_a_propos2") + " " + Logo.WEB_SITE;
        MessageTextArea jt = new MessageTextArea(message);
        JOptionPane.showMessageDialog(null, jt, Logo.messages.getString("menu.help.about"), JOptionPane.INFORMATION_MESSAGE, Logo.getAppIcon());
    }

     private void showLicence(boolean english) {
        JFrame frame = new JFrame(Logo.messages.getString("menu.help.licence"));
        frame.setIconImage(Logo.getAppIcon().getImage());
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

        var editorScrollPane = new JScrollPane(editorPane);
        editorScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        editorScrollPane.setPreferredSize(new Dimension(250, 145));
        editorScrollPane.setMinimumSize(new Dimension(10, 10));
        frame.getContentPane().add(editorScrollPane);
        frame.setVisible(true);
    }

    /**
     * Open the UiTranslatorFrame Dialog box
     */
    private void showUiTranslator() {
        if (null == uiTranslator) {
            uiTranslator = new UiTranslator(this);
            uiTranslator.setBounds(100, 100, 600, 300);
            uiTranslator.setVisible(true);
        } else uiTranslator.requestFocus();
    }

    /**
     * Open the Startup Files Dialog box
     */
    private void showStartupFiles() {
        if (null == startupDialog || !startupDialog.isVisible()) {
            startupDialog = new StartupFileDialog(this);
            startupDialog.setBounds(100, 100, 400, 250);
            startupDialog.setVisible(true);
        } else startupDialog.requestFocus();
    }

    /**
     * Open the Tranlator tool Dialog box
     */
    private void showCodeTranslator() {
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
    private void showProcedureEraser() {
        if (null == procedureEraser || !procedureEraser.isVisible()) {
            procedureEraser = new ProcedureEraser(this);
            procedureEraser.setBounds(100, 100, 300, 200);
            procedureEraser.setVisible(true);
        } else procedureEraser.requestFocus();
    }

    /**
     * Close the preference dialog box
     */
    public void closePreferences() {
        preferencesDialog = null;
    }

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
        Logo.kernel.buildPrimitiveTreemap(id);
        //this = new EditorFrame(graphFrame);
        historyPanel.changeLanguage();
        if (null != GraphFrame.viewer3D) GraphFrame.viewer3D.setText();
    }

    /**
     * Write in the History Panel
     *  @param sty This String represents the style for writing.<br>
     *            The possibilities are "normal", "erreur", "commentaire" or "perso"<br>
     *            - normal: classic style<br>
     *            - erreur: Red, when an error occured<br>
     *            - commentaire: blue, when the user leaves the editorFrame<br>
     *            - perso: To write with the primtive "write" or "print"
     * @param txt The text to write
     */
    public void updateHistory(String sty, String txt) {
        historyPanel.setText(sty, txt);
    }

    /**
     * Get Method for Sound Player
     *
     * @return The Sound Player
     */
    public SoundPlayer getSoundPlayer() {
        return graphFrame.soundPlayer;
    }

    /**
     * Modify the font for the main Interface
     *  @param font The choosen Font
     * @param size The font size
     */
    public void changeFont(Font font, int size) {
        Logo.config.setFont(font);
        updateLocalization();
        historyPanel.setFont(font, size);
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
     * When the frame is closed, show a Confirmation box
     */
    public void closeWindow() {
        this.setVisible(true);
        String message = Logo.messages.getString("quitter?");
        MessageTextArea jt = new MessageTextArea(message);
        String[] choice = {Logo.messages.getString("pref.ok"), Logo.messages.getString("pref.cancel")};
        int val = JOptionPane.showOptionDialog(this, jt, Logo.messages.getString("menu.file.quit"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, Logo.getAppIcon(), choice, choice[0]);
        if (val == JOptionPane.OK_OPTION) {
            Logo.writeConfig();
            System.exit(0);
        }
    }

    void runOrStop() {
        if (running) {
            stopAnimation();
        } else {
            startAnimation(Utils.formatCode(Logo.getMainCommand()));
            getHistoryPanel().setText("normal", Logo.getMainCommand() + "\n");
        }
    }

    void setRunning(boolean running) {
        this.running = running;
        if (running) {
            runButton.setIcon(Logo.getIcon("stop"));
            runButton.setEnabled(true);
        } else {
            runButton.setIcon(Logo.getIcon("run"));
            if (mainCommand.getText().strip() == "") {
                runButton.setEnabled(false);
            } else {
                runButton.setEnabled(true);
            }
        }
    }

    /**
     * When the user types "Enter" in the Command Line
     */
    void executeCommand() {
        if (graphFrame.stop) graphFrame.stop = false;
        String text = commandLine.getText();
        if (!text.equals("") && commandLine.isEditable()) {
            if (commandKeyAdapter.tape) {
                commandKeyAdapter.tape = false;
                historyStack.pop();
            }
            if (historyStack.size() > 49) historyStack.remove(0);
            historyStack.push(text); // Add text to the history
            historyIndex = historyStack.size(); // Readjust history index
            getHistoryPanel().setText("normal", text + "\n");

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
    void setCommandText(String txt) {
        commandLine.setText(txt);
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
                setRunning(false);
            } else {
                SwingUtilities.invokeLater(() -> {
                    commandLine.setEditable(true);
                    setRunning(false);
                });
            }
        } else {
            commandLine.setEditable(false);
            setRunning(true);
        }
    }

    /**
     * This methos copy the selected Text in the command line
     */
    void copyCommandLine() {
        commandLine.copy();
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
     * This methos cut the selected Text in the command line
     */
    void cutCommandLine() {
        commandLine.cut();
    }

    /**
     * This methos paste the selected Text into the command line
     */
    void pasteCommandLine() {
        commandLine.paste();
    }

    /**
     * This method selects all the text in the command line
     */
    void selectAllCommandLine() {
        commandLine.selectAll();
    }

    /**
     * This method creates all primitives and loads the startup files
     */
    public void generatePrimitives() {
        Logo.kernel.initPrimitive();
        Stack<String> stack = new Stack<>();
        setEditable(false);
        int counter = Logo.config.getStartupFiles().size();

        for (int i = 0; i < counter; i++) {
            String txt = "";
            if (Logo.config.getStartupFiles().get(i).equals("#####"))
                setEditable(true); //on a terminé avec les fichiers de démarrage
            else {
                try {
                    txt = Utils.readLogoFile(Logo.config.getStartupFiles().get(i));
                    if (!isEditable()) stack.push(Logo.config.getStartupFiles().get(i));
                } catch (IOException e2) {
                    System.out.println("Problem reading file");
                }
                appendText(txt);
                try {
                    parseProcedures();
                } catch (Exception e3) {
                    e3.printStackTrace();
                }
                clearText();
                setVisible(false);
            }
        }

        Logo.config.setStartupFiles(new ArrayList<>(stack)); //On ne garde dans le path que les fichiers de démarrage
        setEditable(true);
        clearText();
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

    void stopAnimation() {
        LogoException.lance = true;
        graphFrame.error = true;
        if (NetworkServer.isActive) {
            NetworkServer.stopServer();
        }
    }

    /**
     * Launch the animation thread with the instructions "st"
     *
     * @param st List of instructions
     */
    public void startAnimation(StringBuffer st) {
        updateWorkspace();
        graphFrame.animation = new Animation(graphFrame, st);
        graphFrame.animation.start();
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
}
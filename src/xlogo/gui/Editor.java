package xlogo.gui;

import org.fife.rsta.ui.search.ReplaceDialog;
import org.fife.rsta.ui.search.SearchEvent;
import org.fife.rsta.ui.search.SearchListener;
import org.fife.ui.rsyntaxtextarea.*;
import org.fife.ui.rtextarea.*;
import xlogo.Logo;
import xlogo.kernel.Procedure;
import xlogo.kernel.SyntaxException;
import xlogo.kernel.Workspace;
import xlogo.utils.LogoException;
import xlogo.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static xlogo.utils.Utils.createButton;

/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo
 * programming language
 *
 * @author Loïc Le Coq
 */

/* The main class for the Editor windows
 *
 *  */
public class Editor extends JFrame implements SearchListener {
    private boolean editable = true;
    private RSyntaxTextArea textArea;
    private ReplaceDialog replaceDialog;
    private JTextField mainCommand;

    private final Application app;
    private final Workspace workspace;

    public Editor(Application app) {
        this.app = app;
        this.workspace = app.getKernel().getWorkspace();
        setIconImage(Logo.getAppIcon().getImage());
        setTitle(Logo.messages.getString("editeur"));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        initTextArea();
        initSearchDialogs();
        initToolBar();
        initMainCommand();
        pack();
        setLocationRelativeTo(app);
    }

    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            cancelEdits();
        } else if (e.getID() == WindowEvent.WINDOW_ACTIVATED) {
            textArea.requestFocus();
        }
    }

    public void parseProcedures() throws SyntaxException {
        workspace.parseProcedures(textArea.getText(), editable);
    }

    private void rstaButton(JToolBar parent, String iconName, String toolTip, int action) {
        var button = parent.add(RTextArea.getAction(action));
        button.setIcon(Logo.getIcon(iconName));
        button.setHideActionText(true);
        button.setToolTipText(Logo.messages.getString(toolTip));
    }

    private void initSearchDialogs() {
        replaceDialog = new ReplaceDialog(this, this);

        // This ties the properties of the two dialogs together (match case,
        // regex, etc.).
        SearchContext context = replaceDialog.getSearchContext();
        replaceDialog.setSearchContext(context);
    }

    private void initMainCommand() {
        mainCommand = new JTextField();
        setMainCommand();
        var label = new JLabel(Logo.messages.getString("mainCommand"), Logo.getIcon("run"), JLabel.LEFT);
        var panel = new JPanel();
        if (Logo.getMainCommand().length() < 30) mainCommand.setPreferredSize(new Dimension(150, 20));
        panel.add(label);
        panel.add(mainCommand);
        getContentPane().add(panel, BorderLayout.SOUTH);
    }

    private void initToolBar() {
        var toolBar = new JToolBar(JToolBar.HORIZONTAL);
        createButton(toolBar, "save", "lire_editeur", e -> saveEdits()).setMnemonic('Q');
        createButton(toolBar, "cancel", "quit_editeur", e -> cancelEdits()).setMnemonic('C');
        toolBar.addSeparator();
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
        getContentPane().add(toolBar, BorderLayout.NORTH);
    }

    private void initTextArea() {
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
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        ErrorStrip errorStrip = new ErrorStrip(textArea);
        getContentPane().add(errorStrip, BorderLayout.EAST);
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
     * This method displays the editor (if necessary)
     * and adds all defined procedures
     */
    public void open() {
        if (!app.editor.isVisible()) {
            setVisible(true);
            toFront();
            setTitle(Logo.messages.getString("editeur"));
            for (int i = 0; i < workspace.getNumberOfProcedure(); i++) {
                Procedure procedure = workspace.getProcedure(i);
                appendText(procedure.toString());
            }
            setMainCommand();
            discardAllEdits();
            textArea.requestFocus();
        } else {
            app.editor.setVisible(false);
            app.editor.setVisible(true);
            textArea.requestFocus();
        }
    }

    private void cancelEdits() {
        clearText();
        setVisible(false);
        if (Logo.config.isEraseImage()) {
            LogoException.lance = true;
            app.error = true;
            while (!app.isCommandEditable()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }
            }
            app.getKernel().vide_ecran();
        }
        if (null != app.tempPath) {
            app.tempPath = null;
        }
        app.focusCommandLine();
    }

    private void saveEdits() {
        boolean visible = false;
        try {
            parseProcedures();
            clearText();
            if (null != app.tempPath) {
                Application.path = app.tempPath;
                app.setTitle(Application.path + " - XLogo");
                try {
                    File f = new File(app.tempPath);
                    Logo.config.setDefaultFolder(Utils.escapeString(f.getParent()));
                } catch (NullPointerException ignored) {
                }
                app.tempPath = null;
                app.setSaveEnabled(true);
            }
            if (!app.isNewEnabled())
                app.setNewEnabled(true);
        } catch (SyntaxException ex) {
            visible = true;
        }
        setVisible(visible);
        app.focusCommandLine();
        if (Logo.config.isEraseImage()) { //Effacer la zone de dessin
            LogoException.lance = true;
            app.error = true;
            try {
                while (!app.isCommandEditable()) Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
            app.getKernel().vide_ecran();
            app.focusCommandLine();
        }
        if (Logo.config.isClearVariables()) {
            // Interrupt any running programs
            LogoException.lance = true;
            app.error = true;
            try {
                while (!app.isCommandEditable()) Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
            app.getKernel().getWorkspace().deleteAllVariables();
            app.getKernel().getWorkspace().deleteAllPropertyLists();
            app.focusCommandLine();

        }
        Logo.setMainCommand(mainCommand.getText());
    }

    public void setMainCommand() {
        mainCommand.setText(Logo.getMainCommand());
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

    public void discardAllEdits() { textArea.discardAllEdits(); }

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
        SwingUtilities.updateComponentTreeUI(this);
        replaceDialog.updateUI();
        pack();
    }
}
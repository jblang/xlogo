package xlogo.gui;

import org.fife.rsta.ui.search.ReplaceDialog;
import org.fife.rsta.ui.search.SearchEvent;
import org.fife.rsta.ui.search.SearchListener;
import org.fife.ui.rsyntaxtextarea.ErrorStrip;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.*;
import xlogo.Logo;
import xlogo.kernel.LogoException;
import xlogo.kernel.Procedure;
import xlogo.kernel.SyntaxException;
import xlogo.kernel.Workspace;
import xlogo.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.print.PrinterException;
import java.io.File;

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
    RSyntaxTextArea textArea;
    private boolean editable = true;
    private ReplaceDialog replaceDialog;
    private JTextField mainCommand;

    private final Application app;
    private final Workspace workspace;

    public Editor(Application app) {
        this.app = app;
        this.workspace = app.getKernel().getWorkspace();
        setIconImage(Logo.getAppIcon().getImage());
        setTitle(Logo.getString("editor"));
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
        button.setToolTipText(Logo.getString(toolTip));
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
        var label = new JLabel(Logo.getString("editor.mainCommand"), Logo.getIcon("run"), JLabel.LEFT);
        var panel = new JPanel();
        if (Logo.getMainCommand().length() < 30) mainCommand.setPreferredSize(new Dimension(150, 20));
        panel.add(label);
        panel.add(mainCommand);
        getContentPane().add(panel, BorderLayout.SOUTH);
    }

    private void initToolBar() {
        var toolBar = new JToolBar(JToolBar.HORIZONTAL);
        createButton(toolBar, "save", "editor.tooltip.save", e -> saveEdits()).setMnemonic('Q');
        createButton(toolBar, "cancel", "editor.tooltip.cancel", e -> cancelEdits()).setMnemonic('C');
        toolBar.addSeparator();
        createButton(toolBar, "print", "editor.tooltip.print", e -> print());
        toolBar.addSeparator();
        rstaButton(toolBar, "cut", "menu.edit.cut", RTextArea.CUT_ACTION);
        rstaButton(toolBar, "copy", "menu.edit.copy", RTextArea.COPY_ACTION);
        rstaButton(toolBar, "paste", "menu.edit.paste", RTextArea.PASTE_ACTION);
        toolBar.addSeparator();
        rstaButton(toolBar, "undo", "editor.undo", RTextArea.UNDO_ACTION);
        rstaButton(toolBar, "redo", "editor.redo", RTextArea.REDO_ACTION);
        toolBar.addSeparator();
        createButton(toolBar, "search", "search.find", e -> showReplace());
        getContentPane().add(toolBar, BorderLayout.NORTH);
    }

    private void initTextArea() {
        textArea = new RSyntaxTextArea(25, 80);
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
            setTitle(Logo.getString("editor"));
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
        if (Logo.config.getEraseImage()) {
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
            }
        } catch (SyntaxException ex) {
            visible = true;
        }
        setVisible(visible);
        app.focusCommandLine();
        if (Logo.config.getEraseImage()) { //Effacer la zone de dessin
            LogoException.lance = true;
            app.error = true;
            try {
                while (!app.isCommandEditable()) Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
            app.getKernel().vide_ecran();
            app.focusCommandLine();
        }
        if (Logo.config.getClearVariables()) {
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
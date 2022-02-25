package xlogo.gui;

import xlogo.Logo;
import xlogo.kernel.Procedure;
import xlogo.kernel.SyntaxException;
import xlogo.kernel.Workspace;
import xlogo.utils.LogoException;
import xlogo.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

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
public class Editor extends JFrame {
    private JButton undoButton;
    private JButton redoButton;
    private boolean editable = true;
    private JScrollPane scrollPane;
    private EditorTextFacade textZone;
    private JTextField mainCommand;

    private Application app;
    private Workspace workspace;
    private ReplaceFrame replace;

    public Editor(Application app) {
        this.app = app;
        this.workspace = app.getKernel().getWorkspace();

        try {
            initGui();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Editor() {
    }

    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            this.toFront();
        } else if (e.getID() == WindowEvent.WINDOW_ACTIVATED) {
            textZone.requestFocus();
        }
    }

    public void analyzeProcedure() throws SyntaxException {
        workspace.parseProcedures(textZone.getText(), editable);
    }

    private void initGui() {
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // Init Toolbar button
        var toolBar = new JToolBar(JToolBar.HORIZONTAL);
        createButton(toolBar, "save", "lire_editeur", e -> saveEdits()).setMnemonic('Q');
        createButton(toolBar, "cancel", "quit_editeur", e -> cancelEdits()).setMnemonic('C');
        toolBar.addSeparator();
        createButton(toolBar, "print", "imprimer_editeur", e -> textZone.print());
        toolBar.addSeparator();
        createButton(toolBar, "cut", "menu.edition.cut", e -> textZone.cut());
        createButton(toolBar, "copy", "menu.edition.copy", e -> textZone.copy());
        createButton(toolBar, "paste", "menu.edition.paste", e -> paste());
        toolBar.addSeparator();
        undoButton = createButton(toolBar, "undo", "editor.undo", e -> undo());
        redoButton = createButton(toolBar, "redo", "editor.redo", e-> redo());
        toolBar.addSeparator();
        createButton(toolBar, "search", "find", e -> showFind());

        undoButton.setEnabled(false);
        redoButton.setEnabled(false);

        // Init all other components
        JLabel labelCommand = new JLabel(Logo.messages.getString("mainCommand"), Logo.getIcon("run"), JLabel.LEFT);
        scrollPane = new JScrollPane();
        if (Logo.config.isSyntaxHighlightingEnabled()) {
            textZone = new EditorTextPane(this);
        } else textZone = new EditorTextArea(this);

        mainCommand = new JTextField();
        JPanel panelCommand = new JPanel();

        replace = new ReplaceFrame(this, textZone);

        initMainCommand();
        if (Logo.getMainCommand().length() < 30) mainCommand.setPreferredSize(new Dimension(150, 20));
        panelCommand.add(labelCommand);
        panelCommand.add(mainCommand);

        setIconImage(Logo.getAppIcon().getImage());

        scrollPane.setPreferredSize(new Dimension(500, 500));
        this.setTitle(Logo.messages.getString("editeur"));
        this.getContentPane().add(toolBar, BorderLayout.NORTH);
        this.getContentPane().add(scrollPane, BorderLayout.CENTER);
        this.getContentPane().add(panelCommand, BorderLayout.SOUTH);
        scrollPane.getViewport().add(textZone.getTextComponent(), null);
        replace = new ReplaceFrame(this, textZone);
        pack();
    }

    private void paste() {
        // Test if there are too many characters to paste
        Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        String text = null;
        try {
            if (t != null && t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                text = (String) t.getTransferData(DataFlavor.stringFlavor);
            }
        } catch (UnsupportedFlavorException | IOException ignored) {
        }
        if (null != text && text.length() > 100000) {
            if (textZone instanceof EditorTextPane) {
                Logo.config.setSyntaxHighlightingEnabled(false);
                toTextArea();
            }
        }
        textZone.paste();
    }

    private void redo() {
        textZone.getUndoManager().redo();
        updateUndoRedoButtons();
    }

    private void undo() {
        textZone.getUndoManager().undo();
        updateUndoRedoButtons();
    }

    private void showFind() {
        if (!replace.isVisible()) {
            replace.setSize(350, 350);
            replace.setVisible(true);
        }
    }

    private void cancelEdits() {
        textZone.setActive(false);
        textZone.setText("");
        setVisible(false);
        if (Logo.config.isEraseImage()) { //Effacer la zone de dessin
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
        textZone.setActive(false);
        boolean visible = false;
        try {
            analyzeProcedure();
            this.textZone.setText("");
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
                app.setNewEnabled(true); //Si c'est la première fois qu'on enregistre, on active le menu nouveau
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

    public void initMainCommand() {
        mainCommand.setText(Logo.getMainCommand());
    }

    // Change Syntax Highlighting for the editor
    public void initStyles() {
        if (textZone.supportHighlighting()) {
            ((EditorTextPane) textZone).getDsd().initStyles(Logo.config.getSyntaxCommentColor(), Logo.config.getSyntaxCommentStyle(), Logo.config.getSyntaxPrimitiveColor(), Logo.config.getSyntaxPrimitiveStyle(),
                    Logo.config.getSyntaxBracketColor(), Logo.config.getSyntaxBracketStyle(), Logo.config.getSyntaxOperandColor(), Logo.config.getSyntaxOperandStyle());
        }
    }

    // Enable or disable Syntax Highlighting

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean b) {
        editable = b;
    }

    /**
     * Erase all text
     */
    public void clearText() {
        textZone.clearText();
    }

    /**
     * Convert the textZone from a JTextArea to a JTextPane
     * To allow Syntax Highlighting
     */
    public void toTextPane() {
        scrollPane.getViewport().removeAll();
        String s = textZone.getText();
        textZone = new EditorTextPane(this);
        replace = new ReplaceFrame(this, textZone);
        textZone.ecris(s);
        scrollPane.getViewport().add(textZone.getTextComponent());
        scrollPane.revalidate();
    }

    /**
     * Convert the textZone from a JTextPane to a JTextArea
     * Cause could be that:
     * - Syntax Highlighting is disabled
     * - Large text to display in the editor
     */
    public void toTextArea() {
        String s = textZone.getText();
        scrollPane.getViewport().removeAll();
        textZone = new EditorTextArea(this);
        replace = new ReplaceFrame(this, textZone);
        textZone.ecris(s);
        scrollPane.getViewport().add(textZone.getTextComponent());
        scrollPane.revalidate();
    }

    public void setEditorStyledText(String txt) {
        if (txt.length() < 100000) {
            textZone.ecris(txt);
        } else {
            if (textZone instanceof EditorTextPane) {
                Logo.config.setSyntaxHighlightingEnabled(false);
                toTextArea();
                textZone.ecris(txt);
            } else textZone.ecris(txt);
        }
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
                setEditorStyledText(procedure.toString());
            }
            initMainCommand();
            discardAllEdits();
            textZone.requestFocus();
        } else {
            app.editor.setVisible(false);
            app.editor.setVisible(true);
            textZone.requestFocus();
        }
    }

    public void focusTextZone() {
        textZone.requestFocus();
    }

    public void discardAllEdits() {
        textZone.getUndoManager().discardAllEdits();
        updateUndoRedoButtons();
    }

    protected void updateUndoRedoButtons() {
        redoButton.setEnabled(textZone.getUndoManager().canRedo());
        undoButton.setEnabled(textZone.getUndoManager().canUndo());
    }
}
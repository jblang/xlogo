package xlogo.gui;

import xlogo.Logo;
import xlogo.kernel.Primitive;
import xlogo.kernel.Procedure;
import xlogo.kernel.Workspace;
import xlogo.utils.LogoException;
import xlogo.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Stack;
import java.util.StringTokenizer;

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

    public void analyzeProcedure() throws EditorException {
        String text = textZone.getText();
        text.replaceAll("\t", "  ");
        StringReader sr = new StringReader(text);
        BufferedReader br = new BufferedReader(sr);
        StringBuilder defineSentence = new StringBuilder();
        try {
            while (br.ready()) {
                StringBuilder comment = new StringBuilder();
                String line = "";
                String name = "";
                StringBuilder body = new StringBuilder();
                ArrayList<String> variables = new ArrayList<>();
                Stack<String> optVariables = new Stack<>();
                Stack<StringBuffer> optVariablesExp = new Stack<>();
                // Read and save the comments that appear before the procedure
                while (br.ready()) {
                    line = br.readLine();
                    if (null == line) break;
                    if (isComment(line)) comment.append(line).append("\n");
                    else {
                        if (!line.trim().equals("")) break;
                        else {
                            comment.append("\n");
                        }
                    }
                }
                if (null == line) break;
                // Read the first line
                if (!comment.toString().equals("") && line.trim().equals(""))
                    structureException();
                else {
                    StringTokenizer st = new StringTokenizer(line);
                    String token = st.nextToken();
                    // The first word must be "to" (or "pour" in French)
                    if (!token.equalsIgnoreCase(Logo.messages.getString("pour")))
                        structureException();
                    // The second word must be the name of the procedure
                    if (st.hasMoreTokens()) name = st.nextToken().toLowerCase();
                    else structureException();
                    // Then, We read the variables
                    // :a :b :c :d .....
                    while (st.hasMoreTokens()) {
                        token = st.nextToken();
                        if (token.startsWith(":")) {
                            String var = isValidVariable(token);
                            variables.add(var);
                        }
                        // And finally, optional variables if there are some.
                        // [:a 100] [:b 20] [:c 234] ...........
                        else {
                            StringBuffer sb = new StringBuffer();
                            sb.append(token);
                            while (st.hasMoreTokens()) {
                                sb.append(" ");
                                sb.append(st.nextToken());
                            }

                            while (sb.length() > 0) {
                                if (sb.indexOf("[") != 0) structureException();
                                else {
                                    sb.deleteCharAt(0);
                                    String[] arg = new String[2];
                                    extractList(sb, arg);
                                    optVariables.push(arg[0].toLowerCase());
                                    /* Bug Fixed: list as Optional arguments
                                     ** Eg:
                                     ** to a [:var [a b c]]
                                     * end
                                     * when the string is formatted, we check that a white space
                                     * is needed at the end of the argument
                                     */

                                    StringBuffer exp = Utils.formatCode(arg[1]);
                                    if (exp.charAt(exp.length() - 1) != ' ') exp.append(" ");
                                    optVariablesExp.push(exp);
                                }
                            }
                        }
                    }
                }
                // Then we read the body of the procedure until we find
                // the word "end" (or "fin" in French)
                boolean end = false;
                while (br.ready()) {
                    line = br.readLine();
                    if (null == line) break;
                    if (line.trim().equalsIgnoreCase(Logo.messages.getString("fin"))) {
                        end = true;
                        break;
                    } else {
                        body.append(line).append("\n");
                    }
                }
                if (!end) structureException();
                defineSentence.append(name).append(", ");
                int id = isProcedure(name);
                // If it's a new procedure
                Procedure proc;
                if (id == -1) {
                    proc = new Procedure(name, variables.size(), variables, optVariables, optVariablesExp, editable);
                    proc.instruction = body.toString();
                    proc.comment = comment.toString();
                    workspace.procedureListPush(proc);
                } else {          // Si on redéfinit une procédure existante
                    proc = workspace.getProcedure(id);
                    proc.instruction = body.toString();
                    proc.instr = null;
                    proc.comment = comment.toString();
                    proc.variable = variables;
                    proc.optVariables = optVariables;
                    proc.optVariablesExp = optVariablesExp;
                    proc.nbparametre = variables.size();
                    workspace.setProcedureList(id, proc);

                }
                //	System.out.println(proc.toString());
            }
            // On crée les chaînes d'instruction formatées pour chaque procédure et les sauvegardes
            for (int j = 0; j < workspace.getNumberOfProcedure(); j++) {
                Procedure pr = workspace.getProcedure(j);
                pr.decoupe();
                pr.instruction_sauve = pr.instruction;
                pr.instr_sauve = pr.instr;
                pr.variable_sauve = new ArrayList<>();
                pr.variable_sauve.addAll(pr.variable);
            }

            if (!defineSentence.toString().equals("") && editable) {
                app.updateHistory("commentaire", Logo.messages.getString("definir") + " " + defineSentence.substring(0, defineSentence.length() - 2) + ".\n");
                app.updateProcedureEraser();
            }
        } catch (IOException ignored) {
        }
    }

    private void structureException() throws EditorException {
        throw new EditorException(this, Logo.messages.getString("erreur_editeur"));
    }

    // Check if the String st is a comment (starts with "#")
    private boolean isComment(String st) {
        return st.trim().startsWith("#");
    }

    // Check if the String var is a number
    private void isNumber(String var) throws EditorException {
        try {
            Double.parseDouble(var);
            structureException();
        } catch (NumberFormatException ignored) {
        }
    }

    private void hasSpecialCharacter(String var) throws EditorException {
        StringTokenizer check = new StringTokenizer(var, ":+-*/() []=<>&|", true);
        String mess = Logo.messages.getString("caractere_special_variable") + "\n" + Logo.messages.getString("caractere_special2") + "\n" + Logo.messages.getString("caractere_special3") + " :" + var;
        if (check.countTokens() > 1) throw new EditorException(this, mess);
        if (":+-*/() []=<>&|".contains(check.nextToken())) throw new EditorException(this, mess);
    }

    // Check if token is a valid variable
    //  and returns its name
    private String isValidVariable(String token) throws EditorException {
        if (token.length() == 1) structureException();
        String var = token.substring(1);
        isNumber(var);
        hasSpecialCharacter(var);
        return var.toLowerCase();
    }

    private void extractList(StringBuffer sb, String[] args) throws EditorException {
        StringBuilder variable = new StringBuilder();
        String expression;
        int counter = 1;
        int id = 0;
        int id2 = 0;
        boolean space = false;
        for (int i = 0; i < sb.length(); i++) {
            char ch = sb.charAt(i);
            if (ch == '[') counter++;
            else if (ch == ']') {
                if (id == 0) {
                    structureException();
                }
                counter--;
            } else if (ch == ' ') {
                if (!variable.toString().equals("")) {
                    if (!space) id = i;
                    space = true;
                }
            } else {
                if (!space) variable.append(ch);
            }
            if (counter == 0) {
                id2 = i;
                break;
            }
        }
        if (variable.toString().startsWith(":")) {
            variable = new StringBuilder(isValidVariable(variable.toString()));
        } else structureException();
        if (counter != 0) structureException();
        expression = sb.substring(id + 1, id2).trim();
        if (expression.equals("")) structureException();
        sb.delete(0, id2 + 1);
        // delete unnecessary space
        while (sb.length() != 0 && sb.charAt(0) == ' ') sb.deleteCharAt(0);
        args[0] = variable.toString();
        args[1] = expression;
    }


    private int isProcedure(String mot) throws EditorException {   // vérifie si mot est une procédure
        // Vérifier si c'est le nom d'une procédure de démarrage
        for (int i = 0; i < workspace.getNumberOfProcedure(); i++) {
            Procedure procedure = workspace.getProcedure(i);
            if (procedure.name.equals(mot) && !procedure.affichable)
                throw new EditorException(this, mot + " " + Logo.messages.getString("existe_deja"));
        }
        // Vérifier si ce n'est pas un nombre:
        try {
            Double.parseDouble(mot);
            throw new EditorException(this, Logo.messages.getString("erreur_nom_nombre_procedure"));
        } catch (NumberFormatException ignored) {
        }
        // Vérifier tout d'abord si le mot n'est pas une primitive.
        if (Primitive.primitives.containsKey(mot))
            throw new EditorException(this, mot + " " + Logo.messages.getString("existe_deja"));
        else {
            //ensuite s'il ne contient pas de caractères spéciaux "\"
            StringTokenizer decoupe = new StringTokenizer("a" + mot + "a", ":\\+-*/() []=<>&|"); //on rajoute une lettre au mot au cas où le caractere spécial se trouve en début ou en fin de mot.
            if (decoupe.countTokens() > 1)
                throw new EditorException(this, Logo.messages.getString("caractere_special1") + "\n" + Logo.messages.getString("caractere_special2") + "\n" + Logo.messages.getString("caractere_special3") + " " + mot);
        }
        for (int i = 0; i < workspace.getNumberOfProcedure(); i++) {
            if (workspace.getProcedure(i).name.equals(mot)) return (i);
        }
        return (-1);
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
        } catch (EditorException ex) {
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

    public void discardAllEdits() {
        textZone.getUndoManager().discardAllEdits();
        updateUndoRedoButtons();
    }

    protected void updateUndoRedoButtons() {
        redoButton.setEnabled(textZone.getUndoManager().canRedo());
        undoButton.setEnabled(textZone.getUndoManager().canUndo());
    }

    class EditorException extends Exception {   // à générer en cas d'errreur dans la structure
        Editor editor;

        EditorException(Editor editor, String message) {        // et des variables
            this.editor = editor;
            MessageTextArea jt = new MessageTextArea(message);
            JOptionPane.showMessageDialog(this.editor, jt, Logo.messages.getString("erreur"), JOptionPane.ERROR_MESSAGE);
            for (int i = 0; i < workspace.getNumberOfProcedure(); i++) { // On remémorise les anciennes définitions de procédures
                Procedure pr = workspace.getProcedure(i);
                pr.variable = new ArrayList<>(pr.variable_sauve);
                pr.instr = pr.instr_sauve;
                pr.instruction = pr.instruction_sauve;
                pr.nbparametre = pr.variable.size();
            }
            editor.toFront();
            editor.textZone.requestFocus();
        }
    }
}
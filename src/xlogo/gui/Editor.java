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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Stack;
import java.util.StringTokenizer;

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
public class Editor extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;
    private final JToolBar menu = new JToolBar(JToolBar.HORIZONTAL);
    private JButton undoButton;
    private JButton redoButton;
    private boolean editable = true;
    private JScrollPane scroll;
    private EditorTextFacade textZone;
    private JTextField mainCommand;

    private Application cadre;
    private Workspace wp;
    private ReplaceFrame sf;

    public Editor(Application cadre) {
        this.cadre = cadre;
        this.wp = cadre.getKernel().getWorkspace();

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
        String defineSentence = "";
        try {
            while (br.ready()) {
                String comment = "";
                String line = "";
                String name = "";
                StringBuffer body = new StringBuffer();
                ArrayList<String> variables = new ArrayList<String>();
                Stack<String> optVariables = new Stack<String>();
                Stack<StringBuffer> optVariablesExp = new Stack<StringBuffer>();
                // Read and save the comments that appears before the procedure
                while (br.ready()) {
                    line = br.readLine();
                    if (null == line) break;
                    if (isComment(line)) comment += line + "\n";
                    else {
                        if (!line.trim().equals("")) break;
                        else {
                            comment += "\n";
                        }
                    }
                }
                if (null == line) break;
                // Read the first line
                if (!comment.equals("") && line.trim().equals(""))
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
                                    /* Bug Fixed: list as Optionnal arguments
                                     ** Eg:
                                     ** to a [:var [a b c]]
                                     * end
                                     * when the string is formatted, we check that a white space
                                     * is needed at the end of the argument
                                     */

                                    StringBuffer exp = Utils.decoupe(arg[1]);
                                    if (exp.charAt(exp.length() - 1) != ' ') exp.append(" ");
                                    optVariablesExp.push(exp);
                                }
                            }
                        }
                    }
                }
                // Then we read the body of the procedure until we find
                // the word "end" (or "fin" in French)
                boolean fin = false;
                while (br.ready()) {
                    line = br.readLine();
                    if (null == line) break;
                    if (line.trim().equalsIgnoreCase(Logo.messages.getString("fin"))) {
                        fin = true;
                        break;
                    } else {
                        body.append(line + "\n");
                    }
                }
                if (!fin) structureException();
                defineSentence += name + ", ";
                int id = isProcedure(name);
                // If it's a new procedure
                Procedure proc;
                if (id == -1) {
                    proc = new Procedure(name, variables.size(), variables, optVariables, optVariablesExp, editable);
                    proc.instruction = body.toString();
                    proc.comment = comment;
                    wp.procedureListPush(proc);
                } else {          // Si on redéfinit une procédure existante
                    proc = wp.getProcedure(id);
                    proc.instruction = body.toString();
                    proc.instr = null;
                    proc.comment = comment;
                    proc.variable = variables;
                    proc.optVariables = optVariables;
                    proc.optVariablesExp = optVariablesExp;
                    proc.nbparametre = variables.size();
                    wp.setProcedureList(id, proc);

                }
                //	System.out.println(proc.toString());
            }
            // On crée les chaînes d'instruction formatées pour chaque procédure et les sauvegardes
            for (int j = 0; j < wp.getNumberOfProcedure(); j++) {
                Procedure pr = wp.getProcedure(j);
                pr.decoupe();
                pr.instruction_sauve = pr.instruction;
                pr.instr_sauve = pr.instr;
                pr.variable_sauve = new ArrayList<String>();
                for (int k = 0; k < pr.variable.size(); k++) {
                    pr.variable_sauve.add(pr.variable.get(k));
                }
            }

            if (!defineSentence.equals("") && editable) {
                cadre.updateHistory("commentaire", Logo.messages.getString("definir") + " " + defineSentence.substring(0, defineSentence.length() - 2) + ".\n");
                cadre.updateProcedureEraser();
            }
        } catch (IOException e) {
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
        } catch (NumberFormatException e) {
        }
    }

    private void hasSpecialCharacter(String var) throws EditorException {
        StringTokenizer check = new StringTokenizer(var, ":+-*/() []=<>&|", true);
        String mess = Logo.messages.getString("caractere_special_variable") + "\n" + Logo.messages.getString("caractere_special2") + "\n" + Logo.messages.getString("caractere_special3") + " :" + var;
        if (check.countTokens() > 1) throw new EditorException(this, mess);
        if (":+-*/() []=<>&|".indexOf(check.nextToken()) > -1) throw new EditorException(this, mess);
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
        String variable = "";
        String expression = "";
        int compteur = 1;
        int id = 0;
        int id2 = 0;
        boolean espace = false;
        for (int i = 0; i < sb.length(); i++) {
            char ch = sb.charAt(i);
            if (ch == '[') compteur++;
            else if (ch == ']') {
                if (id == 0) {
                    structureException();
                }
                compteur--;
            } else if (ch == ' ') {
                if (!variable.equals("")) {
                    if (!espace) id = i;
                    espace = true;
                }
            } else {
                if (!espace) variable += ch;
            }
            if (compteur == 0) {
                id2 = i;
                break;
            }
        }
        if (variable.startsWith(":")) {
            variable = isValidVariable(variable);
        } else structureException();
        if (compteur != 0) structureException();
        expression = sb.substring(id + 1, id2).trim();
        if (expression.equals("")) structureException();
        sb.delete(0, id2 + 1);
        // delete unnecessary space
        while (sb.length() != 0 && sb.charAt(0) == ' ') sb.deleteCharAt(0);
        args[0] = variable;
        args[1] = expression;
    }


    private int isProcedure(String mot) throws EditorException {   // vérifie si mot est une procédure
// Vérifier si c'est le nom d'une procédure de démarrage
        for (int i = 0; i < wp.getNumberOfProcedure(); i++) {
            Procedure procedure = wp.getProcedure(i);
            if (procedure.name.equals(mot) && procedure.affichable == false)
                throw new EditorException(this, mot + " " + Logo.messages.getString("existe_deja"));
        }
// Vérifier si ce n'est pas un nombre:
        try {
            Double.parseDouble(mot);
            throw new EditorException(this, Logo.messages.getString("erreur_nom_nombre_procedure"));
        } catch (NumberFormatException e) {
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
        for (int i = 0; i < wp.getNumberOfProcedure(); i++) {
            if (wp.getProcedure(i).name.equals(mot)) return (i);
        }
        return (-1);
    }

    private void initGui() throws Exception {
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // Init Toolbar button
        JButton saveButton = new JButton(Logo.getIcon("save"));
        JButton cancelButton = new JButton(Logo.getIcon("cancel"));
        JButton cutButton = new JButton(Logo.getIcon("cut"));
        JButton copyButton = new JButton(Logo.getIcon("copy"));
        JButton pasteButton = new JButton(Logo.getIcon("paste"));
        JButton printButton = new JButton(Logo.getIcon("print"));
        JButton searchButton = new JButton(Logo.getIcon("search"));
        undoButton = new JButton(Logo.getIcon("undo"));
        redoButton = new JButton(Logo.getIcon("redo"));

        // Init All other components
        //  private HighlightedTextPane zonedition;
        JLabel labelCommand = new JLabel(Logo.messages.getString("mainCommand"), Logo.getIcon("run"), JLabel.LEFT);
        scroll = new JScrollPane();
        if (Logo.config.isSyntaxHighlightingEnabled()) {
            textZone = new EditorTextPane(this);
        } else textZone = new EditorTextArea(this);

        mainCommand = new JTextField();
        JPanel panelCommand = new JPanel();

        sf = new ReplaceFrame(this, textZone);

        copyButton.setToolTipText(Logo.messages.getString("menu.edition.copy"));
        cutButton.setToolTipText(Logo.messages.getString("menu.edition.cut"));
        pasteButton.setToolTipText(Logo.messages.getString("menu.edition.paste"));
        printButton.setToolTipText(Logo.messages.getString("imprimer_editeur"));
        saveButton.setToolTipText(Logo.messages.getString("lire_editeur"));
        cancelButton.setToolTipText(Logo.messages.getString("quit_editeur"));
        searchButton.setToolTipText(Logo.messages.getString("find"));
        undoButton.setToolTipText(Logo.messages.getString("editor.undo"));
        redoButton.setToolTipText(Logo.messages.getString("editor.redo"));

        copyButton.setActionCommand(Logo.messages.getString("menu.edition.copy"));
        cutButton.setActionCommand(Logo.messages.getString("menu.edition.cut"));
        pasteButton.setActionCommand(Logo.messages.getString("menu.edition.paste"));
        printButton.setActionCommand(Logo.messages.getString("imprimer_editeur"));
        saveButton.setActionCommand(Logo.messages.getString("lire_editeur"));
        cancelButton.setActionCommand(Logo.messages.getString("quit_editeur"));
        searchButton.setActionCommand(Logo.messages.getString("find"));
        undoButton.setActionCommand(Logo.messages.getString("editor.undo"));
        redoButton.setActionCommand(Logo.messages.getString("editor.redo"));

        undoButton.setEnabled(false);
        redoButton.setEnabled(false);

        saveButton.setMnemonic('Q');
        cancelButton.setMnemonic('C');

        menu.add(saveButton);
        menu.add(cancelButton);
        menu.addSeparator();
        menu.add(printButton);
        menu.addSeparator();
        menu.add(undoButton);
        menu.add(redoButton);
        menu.addSeparator();
        menu.add(cutButton);
        menu.add(copyButton);
        menu.add(pasteButton);
        menu.addSeparator();
        menu.add(searchButton);

        printButton.addActionListener(this);
        copyButton.addActionListener(this);
        cutButton.addActionListener(this);
        pasteButton.addActionListener(this);
        saveButton.addActionListener(this);
        cancelButton.addActionListener(this);
        searchButton.addActionListener(this);
        undoButton.addActionListener(this);
        redoButton.addActionListener(this);

        initMainCommand();
        if (Logo.getMainCommand().length() < 30) mainCommand.setPreferredSize(new Dimension(150, 20));
        panelCommand.add(labelCommand);
        panelCommand.add(mainCommand);

        setIconImage(Logo.getAppIcon().getImage());

        scroll.setPreferredSize(new Dimension(500, 500));
        this.setTitle(Logo.messages.getString("editeur"));
        this.getContentPane().add(menu, BorderLayout.NORTH);
        this.getContentPane().add(scroll, BorderLayout.CENTER);
        this.getContentPane().add(panelCommand, BorderLayout.SOUTH);
        scroll.getViewport().add(textZone.getTextComponent(), null);
        sf = new ReplaceFrame(this, textZone);
        pack();
    }

    public void actionPerformed(ActionEvent e) {

        String cmd = e.getActionCommand();
        if (cmd.equals(Logo.messages.getString("imprimer_editeur"))) {
            textZone.actionPrint();
        } else if (cmd.equals(Logo.messages.getString("menu.edition.copy"))) {
            textZone.copy();
        } else if (cmd.equals(Logo.messages.getString("menu.edition.cut"))) {
            textZone.cut();
        } else if (cmd.equals(Logo.messages.getString("menu.edition.paste"))) {
            // Test if there are too many characters to paste
            Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
            String text = null;
            try {
                if (t != null && t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                    text = (String) t.getTransferData(DataFlavor.stringFlavor);
                }
            } catch (UnsupportedFlavorException e1) {
            } catch (IOException e2) {
            }
            if (null != text && text.length() > 100000) {
                if (textZone instanceof EditorTextPane) {
                    Logo.config.setSyntaxHighlightingEnabled(false);
                    toTextArea();
                }
            }
            textZone.paste();
        } else if (cmd.equals(Logo.messages.getString("lire_editeur"))) {
            textZone.setActive(false);
            boolean visible = false;
            try {
                analyzeProcedure();
                this.textZone.setText("");
                if (null != cadre.tempPath) {
                    Application.path = cadre.tempPath;
                    cadre.setTitle("XLOGO        " + Application.path);
                    try {
                        File f = new File(cadre.tempPath);
                        Logo.config.setDefaultFolder(Utils.rajoute_backslash(f.getParent()));
                    } catch (NullPointerException e2) {
                    }
                    cadre.tempPath = null;
                    cadre.setSaveEnabled(true);
                }
                if (!cadre.isNewEnabled())
                    cadre.setNewEnabled(true); //Si c'est la première fois qu'on enregistre, on active le menu nouveau
            } catch (EditorException ex) {
                visible = true;
            }
            setVisible(visible);
            cadre.focusCommandLine();
            if (Logo.config.isEraseImage()) { //Effacer la zone de dessin
                LogoException.lance = true;
                cadre.error = true;
                try {
                    while (!cadre.isCommandEditable()) Thread.sleep(100);
                } catch (InterruptedException e1) {
                }
                cadre.getKernel().vide_ecran();
                cadre.focusCommandLine();
            }
            if (Logo.config.isClearVariables()) {
                // Interrupt any running programs
                LogoException.lance = true;
                cadre.error = true;
                try {
                    while (!cadre.isCommandEditable()) Thread.sleep(100);
                } catch (InterruptedException e1) {
                }
                cadre.getKernel().getWorkspace().deleteAllVariables();
                cadre.getKernel().getWorkspace().deleteAllPropertyLists();
                cadre.focusCommandLine();

            }
            Logo.setMainCommand(mainCommand.getText());

        }
        // Si on quitte sans enregistrer
        else if (cmd.equals(Logo.messages.getString("quit_editeur"))) {
            textZone.setActive(false);
            textZone.setText("");
            setVisible(false);
            if (Logo.config.isEraseImage()) { //Effacer la zone de dessin
                LogoException.lance = true;
                cadre.error = true;
                while (!cadre.isCommandEditable()) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e2) {
                    }
                }
                cadre.getKernel().vide_ecran();
            }
            if (null != cadre.tempPath) {
                cadre.tempPath = null;
            }
            cadre.focusCommandLine();
        } else if (cmd.equals(Logo.messages.getString("find"))) {
            if (!sf.isVisible()) {
                sf.setSize(350, 350);
                sf.setVisible(true);
            }
        }
        // Undo Action
        else if (cmd.equals(Logo.messages.getString("editor.undo"))) {
            textZone.getUndoManager().undo();
            updateUndoRedoButtons();
        }
        // Redo Action
        else if (cmd.equals(Logo.messages.getString("editor.redo"))) {
            textZone.getUndoManager().redo();
            updateUndoRedoButtons();
        }
    }

    public void initMainCommand() {
        mainCommand.setText(Logo.getMainCommand());
    }

    // Change Syntax Highlighting for the editor
    public void initStyles(int c_comment, int sty_comment, int c_primitive, int sty_primitive,
                           int c_parenthese, int sty_parenthese, int c_operande, int sty_operande) {
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
        scroll.getViewport().removeAll();
        String s = textZone.getText();
        textZone = new EditorTextPane(this);
        sf = new ReplaceFrame(this, textZone);
        textZone.ecris(s);
        scroll.getViewport().add(textZone.getTextComponent());
        scroll.revalidate();
    }

    /**
     * Convert the textZone from a JTextPane to a JTextArea
     * Cause could be that:
     * - Syntax Highlighting is disabled
     * - Large text to display in the editor
     */
    public void toTextArea() {
        String s = textZone.getText();
        scroll.getViewport().removeAll();
        textZone = new EditorTextArea(this);
        sf = new ReplaceFrame(this, textZone);
        textZone.ecris(s);
        scroll.getViewport().add(textZone.getTextComponent());
        scroll.revalidate();
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
        if (!cadre.editor.isVisible()) {
            setVisible(true);
            toFront();
            setTitle(Logo.messages.getString("editeur"));
            for (int i = 0; i < wp.getNumberOfProcedure(); i++) {
                Procedure procedure = wp.getProcedure(i);
                setEditorStyledText(procedure.toString());
            }
            initMainCommand();
            discardAllEdits();
            textZone.requestFocus();
        } else {
            cadre.editor.setVisible(false);
            cadre.editor.setVisible(true);
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
        private static final long serialVersionUID = 1L;
        String message;
        Editor editeur;

        EditorException() {
        }                      // des pour ... fin

        EditorException(Editor editeur, String message) {        // et des variables
            this.editeur = editeur;
            MessageTextArea jt = new MessageTextArea(message);
            JOptionPane.showMessageDialog(this.editeur, jt, Logo.messages.getString("erreur"), JOptionPane.ERROR_MESSAGE);
            for (int i = 0; i < wp.getNumberOfProcedure(); i++) { // On remémorise les anciennes définitions de procédures
                Procedure pr = wp.getProcedure(i);
                pr.variable = new ArrayList<String>(pr.variable_sauve);
                pr.instr = pr.instr_sauve;
                pr.instruction = pr.instruction_sauve;
                pr.nbparametre = pr.variable.size();
            }
            editeur.toFront();
            editeur.textZone.requestFocus();
        }
    }
}
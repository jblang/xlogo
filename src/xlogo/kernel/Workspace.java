/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo
 * programming language
 *
 * @author Loïc Le Coq
 */
package xlogo.kernel;

import xlogo.Logo;
import xlogo.gui.Application;
import xlogo.kernel.gui.GuiMap;
import xlogo.utils.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

/**
 * The Workspace class Contains:<br>
 * - All Defined Procedures <br>
 * - All Global variables <br>
 * - All Gui Objects
 * - All Property lists
 * @author loic
 *
 */

public class Workspace {


    protected HashMap<String, String> globals;  // HashMap with the name of all defined variables
    // and their values
    private Stack<Procedure> procedures; // stack with all procedures defined by the user

    // For all Gui Object (Buttons, ComboBoxes...)
    private final GuiMap guiMap;

    final Application app;

    /**
     *  For all Property Lists
     */
    private HashMap<String, HashMap<String, String>> propList;

    public Workspace(Application app) {
        this.app = app;
        globals = new HashMap<>();
        procedures = new Stack<>();
        propList = new HashMap<>();
        guiMap = new GuiMap(app);
    }

    /**
     * Delete all Variables from the workspace
     */
    public void deleteAllVariables() {
        globals = new HashMap<>();
    }

    /**
     * Delete all procedures from the workspace
     */
    public void deleteAllProcedures() {
        for (int i = procedures.size() - 1; i > -1; i--) {
            Procedure procedure = getProcedure(i);
            if (procedure.displayed)
                deleteProcedure(i);
        }
    }

    /**
     * Delete all property Lists from the workspace
     */
    public void deleteAllPropertyLists() {
        propList = new HashMap<>();
    }

    /**
     * This method adds in the property List called "name" a value for the corresponding key
     * @param name The property List 's name
     * @param key The key for the value to add
     * @param value The value to add
     */
    protected void addPropList(String name, String key, String value) {
        if (!propList.containsKey(name)) {
            propList.put(name, new HashMap<>());
        }
        propList.get(name).put(key, value);
    }

    /**
     * This method removes a Property List
     * @param name The property List 's name
     */
    protected void removePropList(String name) {
        propList.remove(name);
    }


    /**
     * This method removes a couple (key, value) from a Property List
     * @param name The property List 's name
     * @param key The key to delete
     */
    protected void removePropList(String name, String key) {
        if (propList.containsKey(name)) {
            propList.get(name).remove(key);
            if (propList.get(name).isEmpty()) propList.remove(name);
        }
    }

    /**
     * This method returns a list that contains all couple key value
     * @param name The Property List's name
     * @return A list with all keys-values
     */
    protected String displayPropList(String name) {
        if (propList.containsKey(name)) {
            StringBuilder sb = new StringBuilder();
            sb.append("[ ");
            Set<String> set = propList.get(name).keySet();
            for (String key : set) {
                sb.append(key);
                sb.append(" ");
                String value = propList.get(name).get(key);
                if (value.startsWith("\"")) value = value.substring(1);
                sb.append(value);
                sb.append(" ");
            }
            sb.append("] ");
            return sb.toString();
        } else return "[ ] ";
    }

    /**
     * This method return a value from a Property List
     * @param name The Property List's name
     * @param key The key for the chosen value
     * @return The value for this key
     */
    protected String getPropList(String name, String key) {
        if (!propList.containsKey(name)) {
            return "[ ]";
        }
        if (!propList.get(name).containsKey(key)) return "[ ]";
        return propList.get(name).get(key);
    }

    /**
     * Returns all defined Property List names
     * @return A list with all Property List names
     */
    protected Set<String> getPropListKeys() {
        return propList.keySet();
    }

    public void procedureListPush(Procedure pr) {
        procedures.push(pr);
    }

    public void setProcedureList(int id, Procedure pr) {
        procedures.set(id, pr);
    }

    public Procedure getProcedure(int index) {
        return procedures.get(index);
    }

    public Procedure getProcedure(String name) {
        for (var proc : procedures) {
            if (proc.name.equals(name))
                return proc;
        }
        return null;
    }

    public int getNumberOfProcedure() {
        return procedures.size();
    }

    public void deleteProcedure(int index) {
        procedures.remove(index);
    }

    public void deleteVariable(String st) {
        boolean b = globals.containsKey(st);
        if (b) {
            globals.remove(st);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        Set<String> set = globals.keySet();
        for (String key : set) {
            sb.append("-");
            sb.append(key);
            sb.append("\n");
            sb.append(globals.get(key));
            sb.append("\n");
        }
        for (int i = 0; i < getNumberOfProcedure(); i++) {
            Procedure procedure = getProcedure(i);
            sb.append(Logo.messages.getString("pour")).append(" ").append(procedure.name);
            for (int j = 0; j < procedure.arity; j++) {
                sb.append(" :").append(procedure.variables.get(j));
            }
            sb.append("\n");
            sb.append(procedure.body);
            sb.append(Logo.messages.getString("fin"));
            sb.append("\n\n");
        }
        return (new String(sb));
    }

    public void setWorkspace(Application app, String wp) {
        globals = new HashMap<>();
        procedures = new Stack<>();

        StringReader sr = new StringReader(wp);
        BufferedReader bfr = new BufferedReader(sr);
        try {
            String input;
            while ((input = bfr.readLine()) != null) {
                if (!input.startsWith(Logo.messages.getString("pour"))) {
                    globals.put(input.substring(1), bfr.readLine());
                } else break;
            }
            StringBuilder sb = new StringBuilder();
            if (null != input) {
                sb.append(input);
                sb.append("\n");
            }
            while ((input = bfr.readLine()) != null) {
                sb.append(input);
                sb.append("\n");
            }
            bfr.close();
            sr.close();
            try {
                app.editor.setEditable(false);
                app.editor.appendText(new String(sb));
                app.editor.parseProcedures();
            } catch (Exception ignored) {
            }

        } catch (IOException ignored) {
        }
    }

    public GuiMap getGuiMap() {
        return guiMap;
    }

    public void parseProcedures(String text, boolean editable) throws SyntaxException {
        text = text.replaceAll("\t", "  ");
        BufferedReader br = new BufferedReader(new StringReader(text));
        StringBuilder defineSentence = new StringBuilder();
        try {
            while (br.ready()) {
                ArrayList<String> variables = new ArrayList<>();
                Stack<String> optVariables = new Stack<>();
                Stack<StringBuffer> optVariablesExp = new Stack<>();
                // Read and save the comments that appear before the procedure
                var comment = new StringBuilder();
                var line = parseLeadingComments(br, comment);
                if (null == line) break;
                // Read the first line
                if (!comment.toString().equals("") && line.trim().equals(""))
                    structureException();
                var name = parseDeclaration(line, variables, optVariables, optVariablesExp);
                // Then we read the body of the procedure until we find
                // the word "end" (or "fin" in French)
                var body = new StringBuilder();
                parseBody(br, body);
                defineSentence.append(name).append(", ");
                int id = isProcedure(name);
                // If it's a new procedure
                Procedure proc;
                if (id == -1) {
                    proc = new Procedure(name, variables.size(), variables, optVariables, optVariablesExp, editable, app);
                    proc.body = body.toString();
                    proc.comment = comment.toString();
                    this.procedureListPush(proc);
                } else {          // If we redefine an existing procedure
                    proc = this.getProcedure(id);
                    proc.body = body.toString();
                    proc.formattedBody = null;
                    proc.comment = comment.toString();
                    proc.variables = variables;
                    proc.optVariables = optVariables;
                    proc.formattedOptVariables = optVariablesExp;
                    proc.arity = variables.size();
                    this.setProcedureList(id, proc);

                }
            }
            // We create the formatted instruction strings for each procedure and the backups
            for (int j = 0; j < this.getNumberOfProcedure(); j++) {
                Procedure pr = this.getProcedure(j);
                pr.format();
                pr.backupBody = pr.body;
                pr.backupFormattedBody = pr.formattedBody;
                pr.backupVariables = new ArrayList<>();
                pr.backupVariables.addAll(pr.variables);
            }

            if (!defineSentence.toString().equals("") && editable) {
                app.updateHistory("commentaire", Logo.messages.getString("definir") + " " + defineSentence.substring(0, defineSentence.length() - 2) + ".\n");
                app.updateProcedureEraser();
            }
        } catch (IOException ignored) {
        }
    }

    private void parseBody(BufferedReader br, StringBuilder body) throws IOException, SyntaxException {
        String line;
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
    }

    private String parseDeclaration(String line, ArrayList<String> variables, Stack<String> optVariables, Stack<StringBuffer> optVariablesExp) throws SyntaxException {
        String name = "";
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
        return name;
    }

    private String parseLeadingComments(BufferedReader br, StringBuilder comment) throws IOException {
        String line = "";
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
        return line;
    }

    private void structureException() throws SyntaxException {
        throw new SyntaxException(this, Logo.messages.getString("erreur_editeur"));
    }

    // Check if the String st is a comment (starts with "#")
    private boolean isComment(String st) {
        return st.trim().startsWith("#");
    }

    // Check if the String var is a number
    private void isNumber(String var) throws SyntaxException {
        try {
            Double.parseDouble(var);
            structureException();
        } catch (NumberFormatException ignored) {
        }
    }

    private void hasSpecialCharacter(String var) throws SyntaxException {
        StringTokenizer check = new StringTokenizer(var, ":+-*/() []=<>&|", true);
        String mess = Logo.messages.getString("caractere_special_variable") + "\n" + Logo.messages.getString("caractere_special2") + "\n" + Logo.messages.getString("caractere_special3") + " :" + var;
        if (check.countTokens() > 1) throw new SyntaxException(this, mess);
        if (":+-*/() []=<>&|".contains(check.nextToken())) throw new SyntaxException(this, mess);
    }

    // Check if token is a valid variable
    //  and returns its name
    private String isValidVariable(String token) throws SyntaxException {
        if (token.length() == 1) structureException();
        String var = token.substring(1);
        isNumber(var);
        hasSpecialCharacter(var);
        return var.toLowerCase();
    }

    private void extractList(StringBuffer sb, String[] args) throws SyntaxException {
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


    private int isProcedure(String mot) throws SyntaxException {   // vérifie si mot est une procédure
        // Vérifier si c'est le nom d'une procédure de démarrage
        for (int i = 0; i < this.getNumberOfProcedure(); i++) {
            Procedure procedure = this.getProcedure(i);
            if (procedure.name.equals(mot) && !procedure.displayed)
                throw new SyntaxException(this, mot + " " + Logo.messages.getString("existe_deja"));
        }
        // Vérifier si ce n'est pas un nombre:
        try {
            Double.parseDouble(mot);
            throw new SyntaxException(this, Logo.messages.getString("erreur_nom_nombre_procedure"));
        } catch (NumberFormatException ignored) {
        }
        // Vérifier tout d'abord si le mot n'est pas une primitive.
        if (Interpreter.primitiveMap.containsKey(mot))
            throw new SyntaxException(this, mot + " " + Logo.messages.getString("existe_deja"));
        else {
            //ensuite s'il ne contient pas de caractères spéciaux "\"
            StringTokenizer decoupe = new StringTokenizer("a" + mot + "a", ":\\+-*/() []=<>&|"); //on rajoute une lettre au mot au cas où le caractere spécial se trouve en début ou en fin de mot.
            if (decoupe.countTokens() > 1)
                throw new SyntaxException(this, Logo.messages.getString("caractere_special1") + "\n" + Logo.messages.getString("caractere_special2") + "\n" + Logo.messages.getString("caractere_special3") + " " + mot);
        }
        for (int i = 0; i < this.getNumberOfProcedure(); i++) {
            if (this.getProcedure(i).name.equals(mot)) return (i);
        }
        return (-1);
    }

}

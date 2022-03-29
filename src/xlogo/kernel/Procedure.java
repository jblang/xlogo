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
import xlogo.utils.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class Procedure implements AbstractProcedure {

    public boolean affichable; // false lorsque c'est une procédure d'un fichier de démarrage
    String comment; // Line of comment introducing the procedure
    int nbparametre;
    String name;
    ArrayList<String> variable = new ArrayList<String>();
    Stack<String> optVariables = new Stack<String>();
    Stack<StringBuffer> optVariablesExp = new Stack<StringBuffer>();
    String instruction = "";
    StringBuffer instr = null;
    String instruction_sauve = "";  // En cas de mauvaise écriture dans l'éditeur
    StringBuffer instr_sauve = null;          // Permet de revenir aux valeurs antérieures d'une
    ArrayList<String> variable_sauve = new ArrayList<String>(); // procédure avant modification
    Application app;

    public Procedure(String name, int arity, ArrayList<String> variable, Stack<String> optVariables, Stack<StringBuffer> optVariablesExp, boolean affichable, Application app) {
        this.app = app;
        this.name = name;
        this.nbparametre = arity;
        this.variable = variable;
        this.optVariables = optVariables;
        this.optVariablesExp = optVariablesExp;
        this.affichable = affichable;
    }

    public void decoupe() {
        // Only cut procedures which are visible in the editor
        if (null == instr) {
            instr = new StringBuffer();
            try {
                String line = "";
                StringReader sr = new StringReader(instruction);
                BufferedReader bfr = new BufferedReader(sr);
                int lineNumber = 0;
                // Append the number of the line
                instr.append("\\l");
                instr.append(lineNumber);
                instr.append(" ");
                while (bfr.ready()) {
                    lineNumber++;
                    // read the line
                    try {
                        line = bfr.readLine().trim();
                    } catch (NullPointerException e1) {
                        break;
                    }
                    // delete comments
                    line = deleteComments(line);
                    line = Utils.formatCode(line).toString().trim();
                    instr.append(line);
                    if (!line.equals("")) {
                        instr.append(" ");
                        // Append the number of the line
                        instr.append("\\l");
                        instr.append(lineNumber);
                        instr.append(" ");
                    }
                }
            } catch (IOException e) {
            }
            //  System.out.println("****************************"+name+"\n"+instr+"\n\n");
        }
    }

    private String deleteComments(String line) {
        int index = line.indexOf("#");
        while (index != -1) {
            if (index == 0) {
                return "";
            } else if (line.charAt(index - 1) != '\\') {
                return line.substring(0, index);
            } else index = line.indexOf("#", index + 1);
        }
        return line;
    }

    public String toString() {
//    return("nom "+name+" nombre paramètres "+nbparametre+" identifiant "+id+"\n"+variable.toString()+"\n"+instr+"\ninstrction_sauve"+instruction_sauve+"\ninstr_sauve\n"+instr_sauve);
        StringBuffer sb = new StringBuffer();
        if (affichable) {
            sb.append(comment);
            sb.append(Logo.messages.getString("pour") + " " + name);
            for (int j = 0; j < nbparametre; j++) {
                sb.append(" :");
                sb.append(variable.get(j));
            }
            for (int j = 0; j < optVariables.size(); j++) {
                sb.append(" [ :");
                sb.append(optVariables.get(j));
                sb.append(" ");
                sb.append(optVariablesExp.get(j));
                sb.append(" ]");
            }
            sb.append("\n");
            sb.append(instruction);
            sb.append(Logo.messages.getString("fin"));
            sb.append("\n");
        }
//      System.out.println("a"+sb+"a");
        return new String(sb);
    }

    public String getName() {
        return (name);
    }

    protected StringBuffer cutInList() {
        // Only cut procedures which are visible in the editor
        StringBuffer sb = new StringBuffer();
        try {
            String line = "";
            StringReader sr = new StringReader(instruction);
            BufferedReader bfr = new BufferedReader(sr);
            while (bfr.ready()) {
                try {
                    line = bfr.readLine().trim();
                } catch (NullPointerException e1) {
                    break;
                }
                // delete comments
                //       	line=deleteComments(line);
                line = Utils.formatCode(line).toString().trim();
                sb.append("[ ");
                sb.append(line);
                sb.append(" ] ");
            }
        } catch (IOException e) {
        }
        return sb;
    }

    @Override
    public boolean isGeneral() {
        return !optVariables.isEmpty();
    }

    @Override
    public int getArity() {
        return nbparametre;
    }

    @Override
    public void execute(Stack<String> param) {
        // procedure or primitive identifier parameter value
        Interpreter.stockvariable.push(Interpreter.locale);
        Interpreter.locale = new HashMap<>();
        // Read local Variable
        int optSize = optVariables.size();
        int normSize = variable.size();
        for (int j = 0; j < optSize + normSize; j++) {
            // Add local Variable
            if (j < normSize) {
                Interpreter.locale.put(variable.get(j), param.get(j));
            } else {
                // add optional variables
                String value;
                if (j < param.size()) value = param.get(j);
                else value = optVariablesExp.get(j - param.size()).toString();
                Interpreter.locale.put(optVariables.get(j - normSize), value);

            }
        }
        // Add optional variable
        if (Kernel.mode_trace) {
            StringBuilder buffer = new StringBuilder();
            buffer.append("  ".repeat(Interpreter.en_cours.size()));
            buffer.append(name);
            for (String s : param) buffer.append(" ").append(Utils.unescapeString(s));
            String msg = buffer + "\n";
            app.updateHistory("normal", msg);
        }
        Interpreter.en_cours.push(name);

        // Add Procedure code in Interpreter.instruction
        app.getKernel().getInstructionBuffer().insert("\n ");
        app.getKernel().getInstructionBuffer().insertCode(instr);
        Interpreter.nom.push("\n");
    }
}
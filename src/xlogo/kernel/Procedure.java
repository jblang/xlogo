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

    public boolean displayed; // false when it is a startup file procedure
    String comment; // Line of comment introducing the procedure
    int arity;
    String name;
    ArrayList<String> variables;
    Stack<String> optVariables;
    Stack<StringBuffer> formattedOptVariables;
    String body = "";
    StringBuffer formattedBody = null;
    String backupBody = "";  // In case of bad writing in the editor
    StringBuffer backupFormattedBody = null;          // allows you to return to the previous values of a
    ArrayList<String> backupVariables = new ArrayList<>(); // procedure before modification
    Application app;

    public Procedure(String name, int arity, ArrayList<String> variables, Stack<String> optVariables, Stack<StringBuffer> formattedOptVariables, boolean displayed, Application app) {
        this.app = app;
        this.name = name;
        this.arity = arity;
        this.variables = variables;
        this.optVariables = optVariables;
        this.formattedOptVariables = formattedOptVariables;
        this.displayed = displayed;
    }

    public void format() {
        // Only cut procedures which are visible in the editor
        if (null == formattedBody) {
            formattedBody = new StringBuffer();
            try {
                String line;
                StringReader sr = new StringReader(body);
                BufferedReader bfr = new BufferedReader(sr);
                int lineNumber = 0;
                // Append the number of the line
                formattedBody.append("\\l");
                formattedBody.append(lineNumber);
                formattedBody.append(" ");
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
                    formattedBody.append(line);
                    if (!line.equals("")) {
                        formattedBody.append(" ");
                        // Append the number of the line
                        formattedBody.append("\\l");
                        formattedBody.append(lineNumber);
                        formattedBody.append(" ");
                    }
                }
            } catch (IOException ignored) {
            }
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
        StringBuilder sb = new StringBuilder();
        if (displayed) {
            sb.append(comment);
            sb.append(Logo.getString("interpreter.keyword.to")).append(" ").append(name);
            for (int j = 0; j < arity; j++) {
                sb.append(" :");
                sb.append(variables.get(j));
            }
            for (int j = 0; j < optVariables.size(); j++) {
                sb.append(" [ :");
                sb.append(optVariables.get(j));
                sb.append(" ");
                sb.append(formattedOptVariables.get(j));
                sb.append(" ]");
            }
            sb.append("\n");
            sb.append(body);
            sb.append(Logo.getString("interpreter.keyword.end"));
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
            String line;
            StringReader sr = new StringReader(body);
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
        } catch (IOException ignored) {
        }
        return sb;
    }

    @Override
    public boolean isGeneral() {
        return !optVariables.isEmpty();
    }

    @Override
    public int getArity() {
        return arity;
    }

    @Override
    public void execute(Stack<String> param) {
        // procedure or primitive identifier parameter value
        Interpreter.scopes.push(Interpreter.locals);
        Interpreter.locals = new HashMap<>();
        // Read local Variable
        int optSize = optVariables.size();
        int normSize = variables.size();
        for (int j = 0; j < optSize + normSize; j++) {
            // Add local Variable
            if (j < normSize) {
                Interpreter.locals.put(variables.get(j), param.get(j));
            } else {
                // add optional variables
                String value;
                if (j < param.size()) value = param.get(j);
                else value = formattedOptVariables.get(j - param.size()).toString();
                Interpreter.locals.put(optVariables.get(j - normSize), value);

            }
        }
        // Add optional variable
        if (Kernel.traceMode) {
            StringBuilder buffer = new StringBuilder();
            buffer.append("  ".repeat(Interpreter.procedures.size()));
            buffer.append(name);
            for (String s : param) buffer.append(" ").append(Utils.unescapeString(s));
            String msg = buffer + "\n";
            app.updateHistory("misc.message.normal", msg);
        }
        Interpreter.procedures.push(name);

        // Add Procedure code in Interpreter.instruction
        app.getKernel().getInstructionBuffer().insert("\n ");
        app.getKernel().getInstructionBuffer().insertCode(formattedBody);
        Interpreter.consumers.push("\n");
    }
}
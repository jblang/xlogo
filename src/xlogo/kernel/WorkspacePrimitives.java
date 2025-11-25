/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo programming language
 */
package xlogo.kernel;

import xlogo.Logo;
import xlogo.utils.Utils;

import java.io.IOException;
import java.util.List;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Workspace management primitives: procedures, variables, property lists, tracing.
 */
public class WorkspacePrimitives extends PrimitiveGroup {

    public WorkspacePrimitives(Interpreter interpreter) {
        super(interpreter);
    }

    @Override
    public List<Primitive> getPrimitives() {
        return List.of(
            new Primitive("workspace.contents", 0, false, this::getContents),
            new Primitive("workspace.define", 2, false, this::define),
            new Primitive("workspace.edit", 1, false, this::edit),
            new Primitive("workspace.editall", 0, false, this::editAll),
            new Primitive("workspace.eraseall", 0, false, this::eraseAll),
            new Primitive("workspace.eraseprocedure", 1, false, this::eraseProcedure),
            new Primitive("workspace.erasepropertylist", 1, false, this::erasePropertyList),
            new Primitive("workspace.erasevariable", 1, false, this::eraseVariable),
            new Primitive("workspace.externalcommand", 1, false, this::runExternalCommand),
            new Primitive("workspace.getproperty", 2, false, this::getProperty),
            new Primitive("workspace.globalmake", 2, false, this::globalMakeWrap),
            new Primitive("workspace.local", 1, false, this::localWrap),
            new Primitive("workspace.localmake", 2, false, this::localMake),
            new Primitive("workspace.primitives", 0, false, this::listPrimitives),
            new Primitive("workspace.procedures", 0, false, this::procedures),
            new Primitive("workspace.propertylist", 1, false, this::listProperties),
            new Primitive("workspace.propertylists", 0, false, this::getPropertyLists),
            new Primitive("workspace.putproperty", 3, false, this::setProperty),
            new Primitive("workspace.removeproperty", 2, false, this::removeProperty),
            new Primitive("workspace.run", 1, false, this::run),
            new Primitive("workspace.stoptrace", 0, false, this::stopTrace),
            new Primitive("workspace.text", 1, false, this::getProcedureBody),
            new Primitive("workspace.thing", 1, false, this::getVariableValue),
            new Primitive("workspace.trace", 0, false, this::trace),
            new Primitive("workspace.variables", 0, false, this::listVariables)
        );
    }

    void runExternalCommand(Stack<String> param) {
        setReturnValue(false);
        try {
            String list = getFinalList(param.get(0));
            int index = numberOfElements(list);
            String[] cmd = new String[index];
            for (int i = 0; i < index; i++) {
                String liste1 = item(list, i + 1);
                cmd[i] = Utils.unescapeString(getFinalList(liste1).trim());
            }
            try {
                Runtime.getRuntime().exec(cmd);
            } catch (IOException ignored) {
            }
        } catch (LogoException ignored) {
        }
    }

    void getProcedureBody(Stack<String> param) {
        StringBuilder sb;
        try {
            String var = getWord(param.get(0));
            if (null == var) throw new LogoException(app, param.get(0) + " " + Logo.getString("interpreter.error.wordRequired"));
            int index = -1;
            Workspace workspace = getWorkspace();
            for (int i = 0; i < workspace.getNumberOfProcedure(); i++) {
                if (workspace.getProcedure(i).name.equals(var)) {
                    index = i;
                    break;
                }
            }
            if (index != -1) {
                Procedure proc = workspace.getProcedure(index);
                sb = new StringBuilder();
                sb.append("[ [ ");
                // Append variable names
                for (int j = 0; j < proc.arity; j++) {
                    sb.append(proc.variables.get(j));
                    sb.append(" ");
                }
                for (int j = 0; j < proc.optVariables.size(); j++) {
                    sb.append("[ ");
                    sb.append(proc.optVariables.get(j));
                    sb.append(" ");
                    sb.append(proc.formattedOptVariables.get(j).toString());
                    sb.append(" ] ");
                }
                sb.append("] ");
                // Append body procedure
                sb.append(proc.cutInList());
                sb.append("] ");
                setReturnValue(true);
                pushResult(sb.toString());
            } else throw new LogoException(app, var + " " + Logo.getString("interpreter.error.requireProcedure"));
        } catch (LogoException ignored) {
        }
    }

    void edit(Stack<String> param) {
        String mot;
        try {
            mot = getWord(param.get(0));
            if (null == mot) mot = getFinalList(param.get(0));
            StringTokenizer st = new StringTokenizer(mot);
            // Write all procedures names in a Vector
            Vector<String> names = new Vector<>();
            while (st.hasMoreTokens()) {
                names.add(st.nextToken());
            }
            app.editor.setTitle(Logo.getString("editor"));

            app.editor.setMainCommand();
            app.editor.setTitle(Logo.getString("editor"));
            app.editor.discardAllEdits();
            app.editor.setVisible(true);
            app.editor.toFront();
            app.editor.requestFocus();

            Workspace workspace = getWorkspace();
            for (int i = 0; i < workspace.getNumberOfProcedure(); i++) {
                Procedure procedure = workspace.getProcedure(i);
                if (names.contains(procedure.name) && procedure.displayed) {
                    app.editor.appendText(procedure.toString());
                }
            }
        } catch (LogoException ignored) {
        }
    }

    void editAll(Stack<String> param) {
        app.editor.open();
    }

    void erasePropertyList(Stack<String> param) {
        setReturnValue(false);
        erase(param.get(0), "propertylist");
    }

    void getContents(Stack<String> param) {
        StringBuilder sb;
        setReturnValue(true);
        sb = new StringBuilder("[ ");
        sb.append(getAllProcedures());
        sb.append(getAllVariables());
        sb.append(getAllpropertyLists());
        sb.append("] ");
        pushResult(new String(sb));
    }

    void getPropertyLists(Stack<String> param) {
        setReturnValue(true);
        pushResult(new String(getAllpropertyLists()));
    }

    void listPrimitives(Stack<String> param) {
        setReturnValue(true);
        pushResult(getAllPrimitives());
    }

    void listProperties(Stack<String> param) {
        String mot;
        try {
            setReturnValue(true);
            mot = getWord(param.get(0));
            if (null == mot) throw new LogoException(app, param.get(0) + " " + Logo.getString("interpreter.error.wordRequired"));
            pushResult(getWorkspace().displayPropList(mot));
        } catch (LogoException ignored) {
        }
    }

    void removeProperty(Stack<String> param) {
        String mot;
        String mot2;
        try {
            setReturnValue(false);
            mot = getWord(param.get(0));
            if (null == mot) throw new LogoException(app, param.get(0) + " " + Logo.getString("interpreter.error.wordRequired"));
            mot2 = getWord(param.get(1));
            if (null == mot2) throw new LogoException(app, param.get(1) + " " + Logo.getString("interpreter.error.wordRequired"));
            getWorkspace().removePropList(mot, mot2);
        } catch (LogoException ignored) {
        }
    }

    void getProperty(Stack<String> param) {
        String mot;
        String mot2;
        try {
            setReturnValue(true);
            mot = getWord(param.get(0));
            if (null == mot) throw new LogoException(app, param.get(0) + " " + Logo.getString("interpreter.error.wordRequired"));
            mot2 = getWord(param.get(1));
            if (null == mot2) throw new LogoException(app, param.get(1) + " " + Logo.getString("interpreter.error.wordRequired"));
            String value = getWorkspace().getPropList(mot, mot2);
            if (value.startsWith("[")) value += " ";
            pushResult(value);
        } catch (LogoException ignored) {
        }
    }

    void setProperty(Stack<String> param) {
        String mot;
        String mot2;
        setReturnValue(false);
        try {
            mot = getWord(param.get(0));
            if (null == mot) throw new LogoException(app, param.get(0) + " " + Logo.getString("interpreter.error.wordRequired"));
            mot2 = getWord(param.get(1));
            if (null == mot2) throw new LogoException(app, param.get(1) + " " + Logo.getString("interpreter.error.wordRequired"));
            getWorkspace().addPropList(mot, mot2, param.get(2));
        } catch (LogoException ignored) {
        }
    }

    void stopTrace(Stack<String> param) {
        Kernel.traceMode = false;
        setReturnValue(false);
    }

    void trace(Stack<String> param) {
        Kernel.traceMode = true;
        setReturnValue(false);
    }

    void getVariableValue(Stack<String> param) {
        String mot;
        mot = getWord(param.get(0));
        try {
            if (null == mot) {
                throw new LogoException(app, Logo.getString("interpreter.error.wordRequired"));
            } // if it's a list
            else if (getWordPrefix().equals("")) {
                throw new LogoException(app, Logo.getString("interpreter.error.variableInvalid"));
            } // if it's a number
            setReturnValue(true);
            String value;
            mot = mot.toLowerCase();
            Workspace workspace = getWorkspace();
            if (!Interpreter.locals.containsKey(mot)) {
                if (!workspace.globals.containsKey(mot))
                    throw new LogoException(app, mot + " " + Logo.getString("interpreter.error.variableInvalid"));
                else value = workspace.globals.get(mot);
            } else {
                value = Interpreter.locals.get(mot);
            }
            if (null == value) throw new LogoException(app, mot + "  " + Logo.getString("interpreter.error.variableInvalid"));
            pushResult(value);
        } catch (LogoException ignored) {
        }
    }

    void listVariables(Stack<String> param) {
        setReturnValue(true);
        pushResult(new String(getAllVariables()));
    }

    void eraseAll(Stack<String> param) {
        Workspace workspace = getWorkspace();
        workspace.deleteAllProcedures();
        workspace.deleteAllVariables();
        workspace.deleteAllPropertyLists();
    }

    void procedures(Stack<String> param) {
        setReturnValue(true);
        pushResult(new String(getAllProcedures()));
    }

    void eraseVariable(Stack<String> param) {
        erase(param.get(0), "variable");
    }

    void eraseProcedure(Stack<String> param) {
        erase(param.get(0), "procedure");
    }

    void localMake(Stack<String> param) {
        try {
            local(param);
            globalMake(param);
            setReturnValue(false);
        } catch (LogoException ignored) {
        }
    }

    void localWrap(Stack<String> param) {
        try {
            local(param);
            setReturnValue(false);
        } catch (LogoException ignored) {
        }
    }

    void globalMakeWrap(Stack<String> param) {
        try {
            globalMake(param);
            setReturnValue(false);
        } catch (LogoException ignored) {
        }
    }

    void define(Stack<String> param) {
        String mot;
        try {
            mot = getWord(param.get(0));
            if (null == mot) throw new LogoException(app, param.get(0) + " " + Logo.getString("interpreter.error.wordRequired"));
            if (mot.equals("")) throw new LogoException(app, Logo.getString("interpreter.error.emptyProcedureBody"));
            String list = getFinalList(param.get(1));
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i <= numberOfElements(list); i++) {
                String liste1 = item(list, i);
                liste1 = getFinalList(liste1);

                // First line
                if (i == 1) {
                    StringTokenizer st = new StringTokenizer(liste1);
                    sb.append(Logo.getString("interpreter.keyword.to"));
                    sb.append(" ");
                    sb.append(mot);
                    sb.append(" ");

                    while (st.hasMoreTokens()) {
                        // Optional variables
                        String token = st.nextToken();
                        if (token.equals("[")) {
                            sb.append("[ :");
                            while (st.hasMoreTokens()) {
                                token = st.nextToken();
                                if (token.equals("]")) {
                                    sb.append("] ");
                                    break;
                                } else {
                                    sb.append(token);
                                    sb.append(" ");
                                }
                            }
                        } else {
                            sb.append(":");
                            sb.append(token);
                            sb.append(" ");
                        }
                    }
                }
                // Body of the procedure
                else if (i > 1) {
                    sb.append("\n");
                    sb.append(liste1);
                }
            }
            sb.append("\n");
            sb.append(Logo.getString("interpreter.keyword.end"));
            app.editor.appendText(new String(sb));
        } catch (LogoException ignored) {
        }
        try {
            app.editor.parseProcedures();
            app.editor.clearText();
        } catch (Exception ignored) {
        }
    }

    void run(Stack<String> param) {
        String mot;
        try {
            mot = getWord(param.get(0));
            if (null == mot) {
                mot = getList(param.get(0).trim());
                mot = new String(Utils.formatCode(mot));
            } else mot = mot + " ";
            app.getKernel().getInstructionBuffer().insert(mot);
            Interpreter.evalReturnValue = true;
        } catch (LogoException ignored) {
        }
    }
}

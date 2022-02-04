/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo
 * programming language
 *
 * @author Loïc Le Coq
 */
package xlogo.kernel;

import xlogo.Application;
import xlogo.Logo;
import xlogo.kernel.gui.GuiMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

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


    protected HashMap<String, String> globale;  // HashMap with the name of all defined variables
    // and their values
    private Stack<Procedure> procedureList; // stack with all procedures defined by the user

    // For all Gui Object (Buttons, ComboBoxes...)
    private final GuiMap gm;

    /**
     *  For all Property Lists
     */
    private HashMap<String, HashMap<String, String>> propList;

    public Workspace(Application app) {
        globale = new HashMap<String, String>();
        procedureList = new Stack<Procedure>();
        propList = new HashMap<String, HashMap<String, String>>();
        gm = new GuiMap(app);
    }

    /**
     * Delete all Variables from the workspace
     */
    public void deleteAllVariables() {
        globale = new HashMap<String, String>();
    }

    /**
     * Delete all procedures from the workspace
     */
    public void deleteAllProcedures() {
        for (int i = procedureList.size() - 1; i > -1; i--) {
            Procedure procedure = getProcedure(i);
            if (procedure.affichable == true)
                deleteProcedure(i);
        }
    }

    /**
     * Delete all property Lists from the workspace
     */
    public void deleteAllPropertyLists() {
        propList = new HashMap<String, HashMap<String, String>>();
    }

    /**
     * This method adds in the property List called "name" a value for the corresponding key
     * @param name The property List 's name
     * @param key The key for the value to add
     * @param value The value to add
     */
    protected void addPropList(String name, String key, String value) {
        if (!propList.containsKey(name)) {
            propList.put(name, new HashMap<String, String>());
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
            StringBuffer sb = new StringBuffer();
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

    public Procedure procedureListPeek() {
        return procedureList.peek();
    }

    public void procedureListPop() {
        procedureList.pop();
    }

    public void procedureListPush(Procedure pr) {
        procedureList.push(pr);
    }

    public void setProcedureList(int id, Procedure pr) {
        procedureList.set(id, pr);
    }

    public Procedure getProcedure(int index) {
        return procedureList.get(index);
    }

    public int getNumberOfProcedure() {
        return procedureList.size();
    }

    public void deleteProcedure(int index) {
        procedureList.remove(index);
    }

    public void deleteVariable(String st) {
        boolean b = globale.containsKey(st);
        if (b) {
            globale.remove(st);
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        Set<String> set = globale.keySet();
        Iterator<String> it = set.iterator();
        while (it.hasNext()) {
            String key = it.next();
            sb.append("-");
            sb.append(key);
            sb.append("\n");
            sb.append(globale.get(key));
            sb.append("\n");
        }
        for (int i = 0; i < getNumberOfProcedure(); i++) {
            Procedure procedure = getProcedure(i);
            sb.append(Logo.messages.getString("pour") + " " + procedure.name);
            for (int j = 0; j < procedure.nbparametre; j++) {
                sb.append(" :" + procedure.variable.get(j));
            }
            sb.append("\n");
            sb.append(procedure.instruction);
            sb.append(Logo.messages.getString("fin"));
            sb.append("\n\n");
        }
        return (new String(sb));
    }

    public void setWorkspace(Application app, String wp) {
        globale = new HashMap<String, String>();
        procedureList = new Stack<Procedure>();

        StringReader sr = new StringReader(wp);
        BufferedReader bfr = new BufferedReader(sr);
        try {
            String input = "";
            while ((input = bfr.readLine()) != null) {
                if (!input.startsWith(Logo.messages.getString("pour"))) {
                    globale.put(input.substring(1), bfr.readLine());
                } else break;
            }
            StringBuffer sb = new StringBuffer();
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
                app.editeur.setAffichable(false);
                app.editeur.setEditorStyledText(new String(sb));
                app.editeur.analyseprocedure();
            } catch (Exception e) {
            }

        } catch (IOException e) {
        }
    }

    public GuiMap getGuiMap() {
        return gm;
    }
}

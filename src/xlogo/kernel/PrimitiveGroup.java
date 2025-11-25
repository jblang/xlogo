/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo programming language
 */
package xlogo.kernel;

import xlogo.Logo;
import xlogo.gui.Application;
import xlogo.utils.Utils;

import java.util.List;
import java.util.Stack;

/**
 * Base class for primitive groups. Provides shared dependencies and helper methods
 * that primitives need to interact with the interpreter, kernel, and application.
 */
public abstract class PrimitiveGroup {
    protected final Interpreter interpreter;
    protected final Application app;
    protected final Kernel kernel;

    protected PrimitiveGroup(Interpreter interpreter) {
        this.interpreter = interpreter;
        this.app = interpreter.app;
        this.kernel = interpreter.kernel;
    }

    /**
     * Returns the list of primitives provided by this group.
     */
    public abstract List<Primitive> getPrimitives();

    // Helper methods delegating to Interpreter

    protected Workspace getWorkspace() {
        return interpreter.workspace;
    }

    protected void setReturnValue(boolean value) {
        Interpreter.returnValue = value;
    }

    protected void pushResult(String value) {
        Interpreter.operands.push(value);
    }

    protected void delay() {
        interpreter.delay();
    }

    protected String getList(String s) throws LogoException {
        return interpreter.getList(s);
    }

    protected String getWord(String s) {
        return interpreter.getWord(s);
    }

    protected static boolean isList(String li) {
        return Interpreter.isList(li);
    }

    protected String formatList(String list) {
        return interpreter.formatList(list);
    }

    protected void primitive2D(String name) throws LogoException {
        interpreter.primitive2D(name);
    }

    protected void primitive3D(String name) throws LogoException {
        interpreter.primitive3D(name);
    }

    // List helper methods

    protected String getFinalList(String s) throws LogoException {
        return interpreter.getFinalList(s);
    }

    protected int numberOfElements(String list) {
        return interpreter.numberOfElements(list);
    }

    protected int getWordLength(String word) {
        return interpreter.getWordLength(word);
    }

    protected String item(String list, int index) throws LogoException {
        return interpreter.item(list, index);
    }

    protected String itemWord(int index, String word) throws LogoException {
        return interpreter.itemWord(index, word);
    }

    protected String getWordPrefix() {
        return interpreter.wordPrefix;
    }

    protected boolean getBoolean(String s) throws LogoException {
        return interpreter.getBoolean(s);
    }

    // List helper methods for ListPrimitives

    protected String extractList(java.util.StringTokenizer st) {
        return interpreter.extractList(st);
    }

    protected int extractList(String st, int deb) {
        return interpreter.extractList(st, deb);
    }

    protected void isMember(java.util.Stack<String> param, int id) throws LogoException {
        interpreter.isMember(param, id);
    }

    // Workspace helper methods

    protected StringBuffer getAllProcedures() {
        return interpreter.getAllProcedures();
    }

    protected StringBuffer getAllVariables() {
        return interpreter.getAllVariables();
    }

    protected StringBuffer getAllpropertyLists() {
        return interpreter.getAllpropertyLists();
    }

    protected String getAllPrimitives() {
        return interpreter.getAllPrimitives();
    }

    protected void erase(String name, String type) {
        interpreter.erase(name, type);
    }

    protected void local(java.util.Stack<String> param) throws LogoException {
        interpreter.local(param);
    }

    protected void globalMake(java.util.Stack<String> param) throws LogoException {
        interpreter.globalMake(param);
    }
}
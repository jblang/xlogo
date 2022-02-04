/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo
 * programming language
 * Licence : GPL
 *
 * @author Loïc Le Coq
 */

package xlogo.kernel.grammar;

public abstract class LogoType {

    /**
     * If this token is a word ?
     * @return true for a word, false otherwise
     */
    public boolean isWord() {
        return false;
    }

    /**
     * If this token is a list?
     * @return true for a list, false otherwise
     */
    public boolean isList() {
        return false;
    }

    /**
     * If this token is a number?
     * @return true for a number, false otherwise
     */
    public boolean isNumber() {
        return false;
    }

    /**
     * If this token is a variable?
     * @return true for a variable, false otherwise
     */
    public boolean isVariable() {
        return false;
    }

    /**
     * If this token is a primitive?
     * @return true for a primitive, false otherwise
     */
    public boolean isPrimitive() {
        return false;
    }

    /**
     * If this token is a procedure?
     * @return true for a procedure, false otherwise
     */
    public boolean isProcedure() {
        return false;
    }

    /**
     * If this token is an exception?
     * @return true for an exception, false otherwise
     */
    public boolean isException() {
        return false;
    }

    public boolean isNull() {
        return false;
    }

    public boolean isRightDelimiter() {
        return false;
    }

    /**
     * Util for debugging
     * @return the type and value for LogoType
     */
    abstract public String toDebug();
}

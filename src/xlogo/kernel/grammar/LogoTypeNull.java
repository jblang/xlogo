/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo
 * programming language
 * Licence : GPL
 *
 * @author Loïc Le Coq
 */
package xlogo.kernel.grammar;

/**
 * @author loic
 *
 */
public class LogoTypeNull extends LogoType {
    public boolean isNull() {
        return true;
    }


    @Override
    public String toDebug() {
        return "(LogoTypeNull)";
    }

}

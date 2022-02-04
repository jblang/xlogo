/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo
 * programming language
 * Licence : GPL
 *
 * @author Loïc Le Coq
 */
package xlogo.kernel.grammar;

public class LogoWord extends LogoType {
    private final String value;

    LogoWord(String value) {
        this.value = value;
    }

    public String getQuotedValue() {
        return "\"" + value;
    }

    public String toString() {
        return value;
    }

    public boolean isWord() {
        return true;
    }

    @Override
    public String toDebug() {
        return "(WORD) " + value;
    }
}

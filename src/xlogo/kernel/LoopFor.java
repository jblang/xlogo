package xlogo.kernel;

import java.math.BigDecimal;

public class LoopFor extends LoopProperties {
    /**
     * This boolean indicates
     */
    protected boolean conserver = false;

    /**
     * The variable name
     */

    String var = "";

    /**
     * Constructor Loop: For
     *
     * @param counter   The beginning integer
     * @param end       The end integer
     * @param increment The increment between two values
     * @param instr     The instruction to execute between two values
     * @param var       The name of the variable
     */

    LoopFor(BigDecimal counter, BigDecimal end, BigDecimal increment, String instr, String var) {
        super(counter, end, increment, instr);
        this.var = var;
    }

    protected boolean isFor() {
        return true;
    }

    protected boolean isForEver() {
        return false;
    }

    /**
     * This method affects the variable counter the correct value
     *
     * @param first boolean that indicates if it is the first affectation
     */
    protected void AffecteVar(boolean first) {
        String element = String.valueOf(super.getCounter());
        if (element.endsWith(".0")) element = element.substring(0, element.length() - 2);
        if (element.startsWith(".") || element.equals("")) element = "0" + element;

        if (Interprete.locale.containsKey(var)) {
            if (first) conserver = true;
            Interprete.locale.put(var, element);
        } else {
            Interprete.locale.put(var, element);
        }
    }

    /**
     * This method deletes the variable var from the local stack variable
     */
    void DeleteVar() {
        if (!conserver) {
            Interprete.locale.remove(var);

        }
    }

}

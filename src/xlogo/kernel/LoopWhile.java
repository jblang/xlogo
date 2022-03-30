package xlogo.kernel;

import java.math.BigDecimal;

public class LoopWhile extends Loop {

    LoopWhile(BigDecimal counter, BigDecimal end, BigDecimal increment, String instr) {
        super(counter, end, increment, instr);
    }

    protected boolean isForEver() {
        return false;
    }

    protected boolean isWhile() {
        return true;
    }
}

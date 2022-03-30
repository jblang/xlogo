package xlogo.kernel;

import java.math.BigDecimal;

public class LoopFillPolygon extends Loop {
    /**
     * The super constructor for Fill Polygon Loops
     */

    LoopFillPolygon() {
        super(BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, "");
    }

    protected boolean isForEver() {
        return false;
    }

    protected boolean isFillPolygon() {
        return true;
    }
}

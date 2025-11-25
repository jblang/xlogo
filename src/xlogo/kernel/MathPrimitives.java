/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo programming language
 */
package xlogo.kernel;

import java.util.List;
import java.util.Stack;

/**
 * Mathematical primitives: trigonometry, arithmetic, random numbers, etc.
 */
public class MathPrimitives extends PrimitiveGroup {

    public MathPrimitives(Interpreter interpreter) {
        super(interpreter);
    }

    @Override
    public List<Primitive> getPrimitives() {
        return List.of(
            new Primitive("math.abs", 1, false, this::abs),
            new Primitive("math.acos", 1, false, this::acos),
            new Primitive("math.asin", 1, false, this::asin),
            new Primitive("math.atan", 1, false, this::atan),
            new Primitive("math.cos", 1, false, this::cos),
            new Primitive("math.difference", 2, false, this::difference),
            new Primitive("math.digits", 0, false, this::getSignificantDigits),
            new Primitive("math.div", 2, false, this::divide),
            new Primitive("math.exp", 1, false, this::exp),
            new Primitive("math.greater", 2, false, this::isGreaterThan),
            new Primitive("math.greaterequal", 2, false, this::isGreaterThanOrEqual),
            new Primitive("math.integer", 1, false, this::truncate),
            new Primitive("math.less", 2, false, this::isLessThan),
            new Primitive("math.lessequal", 2, false, this::isLessThanOrEqual),
            new Primitive("math.log", 1, false, this::log),
            new Primitive("math.log10", 1, false, this::log10),
            new Primitive("math.minus", 1, false, this::minus),
            new Primitive("math.mod", 2, false, this::mod),
            new Primitive("math.pi", 0, false, this::pi),
            new Primitive("math.power", 2, false, this::power),
            new Primitive("math.product", 2, true, this::product),
            new Primitive("math.quotient", 2, false, this::quotient),
            new Primitive("math.random", 1, false, this::random),
            new Primitive("math.randomfrac", 0, false, this::randomZeroToOne),
            new Primitive("math.remainder", 2, false, this::remainder),
            new Primitive("math.round", 1, false, this::round),
            new Primitive("math.setdigits", 1, false, this::setSignificantDigits),
            new Primitive("math.sin", 1, false, this::sin),
            new Primitive("math.sqrt", 1, false, this::sqrt),
            new Primitive("math.sum", 2, true, this::sum),
            new Primitive("math.tan", 1, false, this::tan)
        );
    }

    void abs(Stack<String> param) {
        try {
            setReturnValue(true);
            pushResult(kernel.getCalculator().abs(param.get(0)));
        } catch (LogoException ignored) {
        }
    }

    void acos(Stack<String> param) {
        try {
            pushResult(kernel.getCalculator().acos(param.get(0)));
            setReturnValue(true);
        } catch (LogoException ignored) {
        }
    }

    void asin(Stack<String> param) {
        try {
            pushResult(kernel.getCalculator().asin(param.get(0)));
            setReturnValue(true);
        } catch (LogoException ignored) {
        }
    }

    void atan(Stack<String> param) {
        try {
            pushResult(kernel.getCalculator().atan(param.get(0)));
            setReturnValue(true);
        } catch (LogoException ignored) {
        }
    }

    void cos(Stack<String> param) {
        setReturnValue(true);
        try {
            pushResult(kernel.getCalculator().cos(param.get(0)));
        } catch (LogoException ignored) {
        }
    }

    void difference(Stack<String> param) {
        setReturnValue(true);
        try {
            pushResult(kernel.getCalculator().substract(param));
        } catch (LogoException ignored) {
        }
    }

    void divide(Stack<String> param) {
        setReturnValue(true);
        try {
            pushResult(kernel.getCalculator().divide(param));
        } catch (LogoException ignored) {
        }
    }

    void exp(Stack<String> param) {
        setReturnValue(true);
        try {
            pushResult(kernel.getCalculator().exp(param.get(0)));
        } catch (LogoException ignored) {
        }
    }

    void getSignificantDigits(Stack<String> param) {
        setReturnValue(true);
        pushResult(String.valueOf(kernel.getCalculator().getDigits()));
    }

    void isGreaterThan(Stack<String> param) {
        setReturnValue(true);
        pushResult(kernel.getCalculator().sup(param));
    }

    void isGreaterThanOrEqual(Stack<String> param) {
        setReturnValue(true);
        pushResult(kernel.getCalculator().supequal(param));
    }

    void isLessThan(Stack<String> param) {
        setReturnValue(true);
        pushResult(kernel.getCalculator().inf(param));
    }

    void isLessThanOrEqual(Stack<String> param) {
        setReturnValue(true);
        pushResult(kernel.getCalculator().infequal(param));
    }

    void log(Stack<String> param) {
        setReturnValue(true);
        try {
            pushResult(kernel.getCalculator().log(param.get(0)));
        } catch (LogoException ignored) {
        }
    }

    void log10(Stack<String> param) {
        setReturnValue(true);
        try {
            pushResult(kernel.getCalculator().log10(param.get(0)));
        } catch (LogoException ignored) {
        }
    }

    void minus(Stack<String> param) {
        try {
            pushResult(kernel.getCalculator().minus(param.get(0)));
            setReturnValue(true);
        } catch (LogoException ignored) {
        }
    }

    void mod(Stack<String> param) {
        setReturnValue(true);
        try {
            pushResult(kernel.getCalculator().modulo(param.get(0), param.get(1)));
        } catch (LogoException ignored) {
        }
    }

    void pi(Stack<String> param) {
        setReturnValue(true);
        pushResult(kernel.getCalculator().pi());
    }

    void power(Stack<String> param) {
        try {
            setReturnValue(true);
            pushResult(kernel.getCalculator().power(param.get(0), param.get(1)));
        } catch (LogoException ignored) {
        }
    }

    void product(Stack<String> param) {
        pushResult(kernel.getCalculator().multiply(param));
        setReturnValue(true);
    }

    void quotient(Stack<String> param) {
        try {
            setReturnValue(true);
            pushResult(kernel.getCalculator().quotient(param.get(0), param.get(1)));
        } catch (LogoException ignored) {
        }
    }

    void random(Stack<String> param) {
        setReturnValue(true);
        try {
            int i = kernel.getCalculator().getInteger(param.get(0));
            i = (int) Math.floor(Math.random() * i);
            pushResult(String.valueOf(i));
        } catch (LogoException ignored) {
        }
    }

    void randomZeroToOne(Stack<String> param) {
        setReturnValue(true);
        pushResult(Calculator.formatDouble(Math.random()));
    }

    void remainder(Stack<String> param) {
        setReturnValue(true);
        try {
            pushResult(kernel.getCalculator().remainder(param.get(0), param.get(1)));
        } catch (LogoException ignored) {
        }
    }

    void round(Stack<String> param) {
        setReturnValue(true);
        try {
            pushResult(String.valueOf(Math.round(kernel.getCalculator().numberDouble(param.get(0)))));
        } catch (LogoException ignored) {
        }
    }

    void setSignificantDigits(Stack<String> param) {
        setReturnValue(false);
        try {
            kernel.initCalculator(kernel.getCalculator().getInteger(param.get(0)));
        } catch (LogoException ignored) {
        }
    }

    void sin(Stack<String> param) {
        setReturnValue(true);
        try {
            pushResult(kernel.getCalculator().sin(param.get(0)));
        } catch (LogoException ignored) {
        }
    }

    void sqrt(Stack<String> param) {
        setReturnValue(true);
        try {
            pushResult(kernel.getCalculator().sqrt(param.get(0)));
        } catch (LogoException ignored) {
        }
    }

    void sum(Stack<String> param) {
        setReturnValue(true);
        pushResult(kernel.getCalculator().add(param));
    }

    void tan(Stack<String> param) {
        setReturnValue(true);
        try {
            pushResult(kernel.getCalculator().tan(param.get(0)));
        } catch (LogoException ignored) {
        }
    }

    void truncate(Stack<String> param) {
        setReturnValue(true);
        try {
            pushResult(kernel.getCalculator().truncate(param.get(0)));
        } catch (LogoException ignored) {
        }
    }
}

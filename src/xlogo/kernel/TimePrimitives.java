/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo programming language
 */
package xlogo.kernel;

import xlogo.Logo;
import xlogo.utils.Utils;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

/**
 * Time-related primitives: date, time, countdown, wait.
 */
public class TimePrimitives extends PrimitiveGroup {

    public TimePrimitives(Interpreter interpreter) {
        super(interpreter);
    }

    @Override
    public List<Primitive> getPrimitives() {
        return List.of(
            new Primitive("time.countdown", 1, false, this::countdown),
            new Primitive("time.date", 0, false, this::getDate),
            new Primitive("time.endcountdown?", 0, false, this::isCountdownEnded),
            new Primitive("time.pasttime", 0, false, this::getTimePassed),
            new Primitive("time.time", 0, false, this::getTime),
            new Primitive("time.wait", 1, false, this::wait)
        );
    }

    void isCountdownEnded(Stack<String> param) {
        setReturnValue(true);
        if (Calendar.getInstance().getTimeInMillis() > Kernel.chrono)
            pushResult(Logo.getString("interpreter.keyword.true"));
        else pushResult(Logo.getString("interpreter.keyword.false"));
    }

    void countdown(Stack<String> param) {
        try {
            int temps = kernel.getCalculator().getInteger(param.get(0));
            Kernel.chrono = Calendar.getInstance().getTimeInMillis() + 1000L * temps;
        } catch (LogoException ignored) {
        }
    }

    void getTimePassed(Stack<String> param) {
        setReturnValue(true);
        long currentTime = Calendar.getInstance().getTimeInMillis();
        pushResult(String.valueOf((currentTime - Logo.getStartupHour()) / 1000));
    }

    void getTime(Stack<String> param) {
        setReturnValue(true);
        Calendar cal = Calendar.getInstance(Objects.requireNonNull(Logo.config.getLanguage().locale));
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        pushResult("[ " + hour + " " + minute + " " + second + " ] ");
    }

    void getDate(Stack<String> param) {
        setReturnValue(true);
        Calendar cal = Calendar.getInstance(Objects.requireNonNull(Logo.config.getLanguage().locale));
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);
        pushResult("[ " + day + " " + month + " " + year + " ] ");
    }

    void wait(Stack<String> param) {
        try {
            int temps = kernel.getCalculator().getInteger(param.get(0));
            if (temps < 0) {
                String waitName = Utils.primitiveName("time.wait");
                throw new LogoException(app, waitName + " " + Logo.getString("interpreter.error.requirePositive"));
            } else {
                int nbSeconds = temps / 60;
                int remainder = temps % 60;
                for (int i = 0; i < nbSeconds; i++) {
                    Thread.sleep(1000);
                    if (app.error) break;
                }
                if (!app.error) Thread.sleep(remainder * 50 / 3);
            }
        } catch (LogoException | InterruptedException ignored) {
        }
    }
}

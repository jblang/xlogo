/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo programming language
 */
package xlogo.kernel;

import java.awt.Color;
import java.util.List;
import java.util.Stack;

/**
 * Color constant primitives that return RGB values for named colors.
 */
public class ColorPrimitives extends PrimitiveGroup {

    public ColorPrimitives(Interpreter interpreter) {
        super(interpreter);
    }

    @Override
    public List<Primitive> getPrimitives() {
        return List.of(
            new Primitive("color.black", 0, false, this::colorBlack),
            new Primitive("color.blue", 0, false, this::colorBlue),
            new Primitive("color.brown", 0, false, this::colorBrown),
            new Primitive("color.cyan", 0, false, this::colorCyan),
            new Primitive("color.darkblue", 0, false, this::colorDarkBlue),
            new Primitive("color.darkgreen", 0, false, this::colorDarkGreen),
            new Primitive("color.darkred", 0, false, this::colorDarkRed),
            new Primitive("color.gray", 0, false, this::colorGray),
            new Primitive("color.green", 0, false, this::colorGreen),
            new Primitive("color.lightgray", 0, false, this::colorLightGray),
            new Primitive("color.magenta", 0, false, this::colorMagenta),
            new Primitive("color.orange", 0, false, this::colorOrange),
            new Primitive("color.pink", 0, false, this::colorPink),
            new Primitive("color.purple", 0, false, this::colorPurple),
            new Primitive("color.red", 0, false, this::colorRed),
            new Primitive("color.white", 0, false, this::colorWhite),
            new Primitive("color.yellow", 0, false, this::colorYellow)
        );
    }

    private void colorCode(int i) {
        setReturnValue(true);
        Color co = DrawPanel.defaultColors[i];
        pushResult("[ " + co.getRed() + " " + co.getGreen() + " " + co.getBlue() + " ] ");
    }

    void colorBlack(Stack<String> param) {
        colorCode(0);
    }

    void colorRed(Stack<String> param) {
        colorCode(1);
    }

    void colorGreen(Stack<String> param) {
        colorCode(2);
    }

    void colorYellow(Stack<String> param) {
        colorCode(3);
    }

    void colorBlue(Stack<String> param) {
        colorCode(4);
    }

    void colorMagenta(Stack<String> param) {
        colorCode(5);
    }

    void colorCyan(Stack<String> param) {
        colorCode(6);
    }

    void colorWhite(Stack<String> param) {
        colorCode(7);
    }

    void colorGray(Stack<String> param) {
        colorCode(8);
    }

    void colorLightGray(Stack<String> param) {
        colorCode(9);
    }

    void colorDarkRed(Stack<String> param) {
        colorCode(10);
    }

    void colorDarkGreen(Stack<String> param) {
        colorCode(11);
    }

    void colorDarkBlue(Stack<String> param) {
        colorCode(12);
    }

    void colorOrange(Stack<String> param) {
        colorCode(13);
    }

    void colorPink(Stack<String> param) {
        colorCode(14);
    }

    void colorPurple(Stack<String> param) {
        colorCode(15);
    }

    void colorBrown(Stack<String> param) {
        colorCode(16);
    }
}

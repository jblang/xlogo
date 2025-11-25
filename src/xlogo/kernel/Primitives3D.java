/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo programming language
 */
package xlogo.kernel;

import xlogo.kernel.perspective.ElementLine;
import xlogo.kernel.perspective.ElementPoint;
import xlogo.kernel.perspective.ElementPolygon;
import org.scijava.vecmath.Point3d;

import java.util.List;
import java.util.Stack;

/**
 * 3D graphics primitives for perspective mode.
 */
public class Primitives3D extends PrimitiveGroup {

    public Primitives3D(Interpreter interpreter) {
        super(interpreter);
    }

    @Override
    public List<Primitive> getPrimitives() {
        return List.of(
            new Primitive("3d.downpitch", 1, false, this::pitchDown),
            new Primitive("3d.leftroll", 1, false, this::rollLeft),
            new Primitive("3d.lineend", 0, false, this::endLine),
            new Primitive("3d.linestart", 0, false, this::startLine),
            new Primitive("3d.orientation", 0, false, this::getOrientation),
            new Primitive("3d.perspective", 0, false, this::enable3D),
            new Primitive("3d.pitch", 0, false, this::getPitch),
            new Primitive("3d.pointend", 0, false, this::endPoint),
            new Primitive("3d.pointstart", 0, false, this::startPoint),
            new Primitive("3d.polyend", 0, false, this::endPolygon),
            new Primitive("3d.polystart", 0, false, this::startPolygon),
            new Primitive("3d.polyview", 0, false, this::view3D),
            new Primitive("3d.rightroll", 1, false, this::rollRight),
            new Primitive("3d.roll", 0, false, this::getRoll),
            new Primitive("3d.setorientation", 1, false, this::setOrientation),
            new Primitive("3d.setpitch", 1, false, this::setPitch),
            new Primitive("3d.setroll", 1, false, this::setRoll),
            new Primitive("3d.setxyz", 3, false, this::setXYZ),
            new Primitive("3d.setz", 1, false, this::setZ),
            new Primitive("3d.textend", 0, false, this::endText),
            new Primitive("3d.textstart", 0, false, this::startText),
            new Primitive("3d.uppitch", 1, false, this::pitchUp)
        );
    }

    void endPoint(Stack<String> param) {
        DrawPanel.record3D = DrawPanel.RECORD_3D_NONE;
        try {
            DrawPanel.poly.addToScene();
        } catch (LogoException ignored) {
        }
    }

    void endText(Stack<String> param) {
        DrawPanel.record3D = DrawPanel.RECORD_3D_NONE;
    }

    void startText(Stack<String> param) {
        DrawPanel.record3D = DrawPanel.RECORD_3D_TEXT;
        app.initViewer3D();
        DrawPanel.poly = null;
    }

    void endLine(Stack<String> param) {
        DrawPanel.record3D = DrawPanel.RECORD_3D_NONE;
        try {
            DrawPanel.poly.addToScene();
        } catch (LogoException ignored) {
        }
    }

    void startPoint(Stack<String> param) {
        DrawPanel.record3D = DrawPanel.RECORD_3D_POINT;
        app.initViewer3D();
        DrawPanel.poly = new ElementPoint(app);
    }

    void startLine(Stack<String> param) {
        DrawPanel.record3D = DrawPanel.RECORD_3D_LINE;
        app.initViewer3D();
        DrawPanel.poly = new ElementLine(app);
        DrawPanel.poly.addVertex(
            new Point3d(
                kernel.getActiveTurtle().X / 1000,
                kernel.getActiveTurtle().Y / 1000,
                kernel.getActiveTurtle().Z / 1000
            ),
            kernel.getActiveTurtle().penColor
        );
    }

    void view3D(Stack<String> param) {
        try {
            primitive3D("3d.polyview");
            app.viewerOpen();
        } catch (LogoException ignored) {
        }
    }

    void endPolygon(Stack<String> param) {
        DrawPanel.record3D = DrawPanel.RECORD_3D_NONE;
        try {
            DrawPanel.poly.addToScene();
        } catch (LogoException ignored) {
        }
    }

    void startPolygon(Stack<String> param) {
        DrawPanel.record3D = DrawPanel.RECORD_3D_POLYGON;
        app.initViewer3D();
        DrawPanel.poly = new ElementPolygon(app);
    }

    void setZ(Stack<String> param) {
        delay();
        try {
            primitive3D("3d.setz");
            app.getDrawPanel().setPosition(
                kernel.getActiveTurtle().X + " " +
                kernel.getActiveTurtle().Y + " " +
                kernel.getCalculator().numberDouble(param.get(0))
            );
        } catch (LogoException ignored) {
        }
    }

    void setXYZ(Stack<String> param) {
        try {
            primitive3D("3d.setxyz");
            app.getDrawPanel().setPosition(
                kernel.getCalculator().numberDouble(param.get(0)) + " " +
                kernel.getCalculator().numberDouble(param.get(1)) + " " +
                kernel.getCalculator().numberDouble(param.get(2))
            );
        } catch (LogoException ignored) {
        }
    }

    void getOrientation(Stack<String> param) {
        try {
            primitive3D("3d.orientation");
            setReturnValue(true);
            String pitch = Calculator.formatDouble(kernel.getActiveTurtle().pitch);
            String roll = Calculator.formatDouble(kernel.getActiveTurtle().roll);
            String heading = Calculator.formatDouble(kernel.getActiveTurtle().heading);
            pushResult("[ " + roll + " " + pitch + " " + heading + " ] ");
        } catch (LogoException ignored) {
        }
    }

    void setOrientation(Stack<String> param) {
        try {
            primitive3D("3d.setorientation");
            delay();
            app.getDrawPanel().setOrientation(getFinalList(param.pop()));
        } catch (LogoException ignored) {
        }
    }

    void setPitch(Stack<String> param) {
        try {
            primitive3D("3d.setpitch");
            delay();
            app.getDrawPanel().setPitch(kernel.getCalculator().numberDouble(param.pop()));
        } catch (LogoException ignored) {
        }
    }

    void setRoll(Stack<String> param) {
        try {
            primitive3D("3d.setroll");
            delay();
            app.getDrawPanel().setRoll(kernel.getCalculator().numberDouble(param.pop()));
        } catch (LogoException ignored) {
        }
    }

    void getPitch(Stack<String> param) {
        try {
            primitive3D("3d.pitch");
            setReturnValue(true);
            pushResult(Calculator.formatDouble(kernel.getActiveTurtle().pitch));
        } catch (LogoException ignored) {
        }
    }

    void getRoll(Stack<String> param) {
        try {
            primitive3D("3d.roll");
            setReturnValue(true);
            pushResult(Calculator.formatDouble(kernel.getActiveTurtle().roll));
        } catch (LogoException ignored) {
        }
    }

    void pitchDown(Stack<String> param) {
        delay();
        try {
            primitive3D("3d.downpitch");
            app.getDrawPanel().pitch(-kernel.getCalculator().numberDouble(param.pop()));
        } catch (LogoException ignored) {
        }
    }

    void rollLeft(Stack<String> param) {
        delay();
        try {
            primitive3D("3d.leftroll");
            app.getDrawPanel().roll(-kernel.getCalculator().numberDouble(param.pop()));
        } catch (LogoException ignored) {
        }
    }

    void pitchUp(Stack<String> param) {
        delay();
        try {
            primitive3D("3d.uppitch");
            app.getDrawPanel().pitch(kernel.getCalculator().numberDouble(param.pop()));
        } catch (LogoException ignored) {
        }
    }

    void rollRight(Stack<String> param) {
        delay();
        try {
            primitive3D("3d.rightroll");
            app.getDrawPanel().roll(kernel.getCalculator().numberDouble(param.pop()));
        } catch (LogoException ignored) {
        }
    }

    void enable3D(Stack<String> param) {
        app.getDrawPanel().perspective();
    }
}

/**
 * Title : XLogo Description : XLogo is an interpreter for the Logo programming
 * language
 *
 * @author Loïc Le Coq
 */
package xlogo.kernel;

import xlogo.Logo;
import xlogo.gui.Application;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
/**
 * This Thread is responsible for the turtle animation.
 * This animation has to be executed in a separated thread, else it will block
 * the event dispatcher thread
 */
public class Animation extends Thread {
    public static boolean executionLaunched = false;
    private boolean pause = false;
    private final ScrollListener scrollListener = new ScrollListener();
    private StringBuffer instruction;
    private Application app;

    public Animation() {
    }

    public Animation(Application app, StringBuffer instruction) {
        this.app = app;
        this.instruction = instruction;
    }

    public void run() {
        SwingUtilities.invokeLater(() -> {
            app.setCommandEnabled(false);// the command line is no longer active
        });
        executionLaunched = true;
        app.getDrawPanel().activateScrollListener(); // We activate the scroll Listener events on the drawing area
        app.scrollPane.getVerticalScrollBar().addMouseListener(scrollListener);
        app.scrollPane.getHorizontalScrollBar().addMouseListener(scrollListener);
        try {
            app.setKey(-1);
            app.error = false;
            LogoException.lance = false;
            app.getKernel().getInstructionBuffer().clear();
            Interpreter.reset();
            boolean b = true;
            while (b) {
                String st = app.getKernel().execute(instruction);
                if (!st.equals(""))
                    throw new LogoException(app, Logo
                            .getString("error.whattodo")
                            + " " + st + " ?");
                if (Interpreter.actionInstruction.length() == 0) b = false;
                else {
                    instruction = Interpreter.actionInstruction;
                    Interpreter.actionInstruction = new StringBuffer();
                }
            }
        } catch (LogoException e) {
        }
        app.setCommandEnabled(true);
        if (!app.viewer3DVisible()) app.focusCommandLine();
        executionLaunched = false;
        app.error = false;
        LogoException.lance = false;
        app.scrollPane.getVerticalScrollBar().removeMouseListener(scrollListener);
        app.scrollPane.getHorizontalScrollBar().removeMouseListener(scrollListener);
    }

    protected boolean isOnPause() {
        return pause;
    }

    public void setPause(boolean b) {
        pause = b;
    }

    class ScrollListener extends MouseAdapter {
        // If we move the Scrollbars while drawing,
        // this allows you to temporarily interrupt the execution
        public ScrollListener() {
        }

        public void mousePressed(MouseEvent e) {
            pause = true;
        }

        public void mouseReleased(MouseEvent e) {
            pause = false;
        }
    }
}
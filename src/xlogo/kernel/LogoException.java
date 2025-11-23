package xlogo.kernel;

import xlogo.Logo;
import xlogo.gui.Application;
import xlogo.utils.Utils;

import java.util.Stack;

/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo
 * programming language
 *
 * @author Loïc Le Coq
 */
public class LogoException extends Exception {
    private static final long serialVersionUID = 1L;
    public static boolean thrown;
    private Application app;

    public LogoException() {
    }

    public LogoException(Application app, String st) {
        this.app = app;
        while (!Interpreter.procedures.isEmpty() && Interpreter.procedures.peek().equals("("))
            Interpreter.procedures.pop();
        if (!app.error & !Interpreter.procedures.isEmpty()) {
            app.updateHistory("application.error", Logo.getString("exception.message.in") + " " + Interpreter.procedures.pop() + ", "
                    + Logo.getString("exception.message.lineNumber") + " " + getLineNumber() + ":\n");
        }
        if (!app.error) app.updateHistory("application.error", Utils.unescapeString(st) + "\n");

        app.focusCommandLine();
        app.error = true;
        app.getKernel().getInstructionBuffer().clear();
        Interpreter.operands = new Stack<String>();
        Interpreter.loops = new Stack<Loop>();
    }

    private int getLineNumber() {
        String string = Interpreter.lineNumber;
//	  System.out.println("bb"+string+"bb");
        if (string.equals("")) string = app.getKernel().getInstructionBuffer().toString();
//	  System.out.println("cc"+string+"cc");
        int id = string.indexOf("\\l");
        if (id != -1) {
            String lineNumber = "";
            int i = id + 2;
            char c = string.charAt(i);
            while (c != ' ') {
                lineNumber += c;
                i++;
                c = string.charAt(i);
            }
//		  System.out.println(lineNumber);
            return Integer.parseInt(lineNumber);
        }
        return 1;
    }

    class DisplayMessage implements Runnable {
        String msg;

        DisplayMessage(String msg) {
            this.msg = msg;
        }

        public void run() {
            app.updateHistory("application.error", msg);
        }
    }
}
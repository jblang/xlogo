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
    public static boolean lance;
    private Application cadre;

    public LogoException() {
    }

    public LogoException(Application cadre, String st) {
        this.cadre = cadre;
//    if (st.equals("siwhile")) st=Logo.messages.getString("tantque");
        while (!Interpreter.procedures.isEmpty() && Interpreter.procedures.peek().equals("("))
            Interpreter.procedures.pop();
        if (!cadre.error & !Interpreter.procedures.isEmpty()) {
            cadre.updateHistory("application.error", Logo.getString("exception.message.in") + " " + Interpreter.procedures.pop() + ", "
                    + Logo.getString("exception.message.lineNumber") + " " + getLineNumber() + ":\n");
        }
        if (!cadre.error) cadre.updateHistory("application.error", Utils.unescapeString(st) + "\n");

        cadre.focusCommandLine();
        cadre.error = true;
        cadre.getKernel().getInstructionBuffer().clear();
        Interpreter.operands = new Stack<String>();
        Interpreter.loops = new Stack<Loop>();
    }

    private int getLineNumber() {
        String string = Interpreter.lineNumber;
//	  System.out.println("bb"+string+"bb");
        if (string.equals("")) string = cadre.getKernel().getInstructionBuffer().toString();
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

    // Thread permettant l'animation dans le dispatch event queue
    class Affiche implements Runnable {
        String msg;

        Affiche(String msg) {
            this.msg = msg;
        }

        public void run() {
            cadre.updateHistory("application.error", msg);
        }
    }
}
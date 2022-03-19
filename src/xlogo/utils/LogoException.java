package xlogo.utils;

import xlogo.gui.GraphFrame;
import xlogo.Logo;
import xlogo.kernel.Interpreter;
import xlogo.kernel.LoopProperties;
import xlogo.kernel.Primitive;

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
    private GraphFrame graphFrame;

    public LogoException() {
    }

    public LogoException(GraphFrame graphFrame, String st) {
        this.graphFrame = graphFrame;
//    if (st.equals("siwhile")) st=Logo.messages.getString("tantque");
        while (!Interpreter.en_cours.isEmpty() && Interpreter.en_cours.peek().equals("(")) Interpreter.en_cours.pop();
        if (!graphFrame.error & !Interpreter.en_cours.isEmpty()) {
            graphFrame.editor.updateHistory("erreur", Logo.messages.getString("dans") + " " + Interpreter.en_cours.pop() + ", "
                    + Logo.messages.getString("line") + " " + getLineNumber() + ":\n");
        }
        if (!graphFrame.error) graphFrame.editor.updateHistory("erreur", Utils.unescapeString(st) + "\n");

        graphFrame.editor.focusCommandLine();
        graphFrame.error = true;
        Interpreter.calcul = new Stack<String>();
        Logo.kernel.getInstructionBuffer().clear();
        Primitive.stackLoop = new Stack<LoopProperties>();
    }

    private int getLineNumber() {
        String string = Interpreter.lineNumber;
//	  System.out.println("bb"+string+"bb");
        if (string.equals("")) string = Logo.kernel.getInstructionBuffer().toString();
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
            graphFrame.editor.updateHistory("erreur", msg);
        }
    }
}
/**
 * Title : XLogo Description : XLogo is an interpreter for the Logo programming
 * language
 *
 * @author Loïc Le Coq
 */
package xlogo.kernel;

import xlogo.gui.GraphFrame;
import xlogo.Logo;
import xlogo.utils.LogoException;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Stack;
//Ce thread gère l'animation de la tortue pendant l'exécution 

/**
 * This Thread is responsible of the turtle animation.
 * This animation has to be executed in a separated thread, else it will block
 * the event dispatcher thread 
 */
public class Animation extends Thread {
    public static boolean executionLaunched = false;
    private boolean pause = false;
    private GraphFrame graphFrame;
    private StringBuffer instruction;
    private final Souris souris = new Souris();
    private MemoryChecker cm = null;

    public Animation() {
    }

    public Animation(GraphFrame graphFrame, StringBuffer instruction) {
        this.graphFrame = graphFrame;
        this.instruction = instruction;
    }

    public void run() {
        //	currentThread().setPriority(Thread.MIN_PRIORITY);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                graphFrame.editor.setCommandEnabled(false);// la ligne de commandes
                // n'est plus active
            }
        });
        executionLaunched = true;
        graphFrame.getDrawPanel().active_souris(); // On active les événements souris sur
        // la zone de dessin
        graphFrame.scrollPane.getVerticalScrollBar().addMouseListener(souris);
        graphFrame.scrollPane.getHorizontalScrollBar().addMouseListener(souris);
        try {
            graphFrame.editor.setKey(-1);
            graphFrame.error = false;
            LogoException.lance = false;
            Interpreter.operande = Interpreter.operateur = Interpreter.drapeau_ouvrante = false;
            Logo.kernel.getInstructionBuffer().clear();
            Interpreter.calcul = new Stack<String>();
            Interpreter.nom = new Stack<String>();
            Interpreter.locale = new HashMap<String, String>();
            Interpreter.en_cours = new Stack<String>();
            cm = new MemoryChecker(graphFrame);
            cm.start();
            boolean b = true;
            while (b) {
                String st = Logo.kernel.execute(instruction);
                if (!st.equals(""))
                    throw new LogoException(graphFrame, Logo.messages
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
        graphFrame.editor.setCommandEnabled(true);
        if (!graphFrame.viewer3DVisible()) graphFrame.editor.focusCommandLine();
        executionLaunched = false;
        cm.setContinuer(false);
        graphFrame.error = false;
        LogoException.lance = false;
        graphFrame.scrollPane.getVerticalScrollBar().removeMouseListener(souris);
        graphFrame.scrollPane.getHorizontalScrollBar().removeMouseListener(souris);
    }

    protected boolean isOnPause() {
        return pause;
    }

    public void setPause(boolean b) {
        pause = b;
    }

    class Souris extends MouseAdapter { //Si on déplace les Scrollbar pendant
        // le dessin
        public Souris() {
        } // Ceci permet d'interrompre momentanément l'exécution

        public void mousePressed(MouseEvent e) {
            pause = true;
        }

        public void mouseReleased(MouseEvent e) {
            pause = false;
        }
    }
}
/**
 * Title : XLogo Description : XLogo is an interpreter for the Logo programming
 * language
 *
 * @author Loïc Le Coq
 */
package xlogo.kernel;

import xlogo.gui.Application;
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
    private Application cadre;
    private StringBuffer instruction;
    private final Souris souris = new Souris();
    private MemoryChecker cm = null;

    public Animation() {
    }

    public Animation(Application cadre, StringBuffer instruction) {
        this.cadre = cadre;
        this.instruction = instruction;
    }

    public void run() {
        //	currentThread().setPriority(Thread.MIN_PRIORITY);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                cadre.setCommandEnabled(false);// la ligne de commandes
                // n'est plus active
            }
        });
        executionLaunched = true;
        cadre.getDrawPanel().active_souris(); // On active les événements souris sur
        // la zone de dessin
        cadre.scrollPane.getVerticalScrollBar().addMouseListener(souris);
        cadre.scrollPane.getHorizontalScrollBar().addMouseListener(souris);
        try {
            cadre.setKey(-1);
            cadre.error = false;
            LogoException.lance = false;
            Interpreter.operande = Interpreter.operateur = Interpreter.drapeau_ouvrante = false;
            cadre.getKernel().getInstructionBuffer().clear();
            Interpreter.calcul = new Stack<String>();
            Interpreter.nom = new Stack<String>();
            Interpreter.locale = new HashMap<String, String>();
            Interpreter.en_cours = new Stack<String>();
            cm = new MemoryChecker(cadre);
            cm.start();
            boolean b = true;
            while (b) {
                String st = cadre.getKernel().execute(instruction);
                if (!st.equals(""))
                    throw new LogoException(cadre, Logo.messages
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
        cadre.setCommandEnabled(true);
        if (!cadre.viewer3DVisible()) cadre.focusCommandLine();
        executionLaunched = false;
        cm.setContinuer(false);
        cadre.error = false;
        LogoException.lance = false;
        cadre.scrollPane.getVerticalScrollBar().removeMouseListener(souris);
        cadre.scrollPane.getHorizontalScrollBar().removeMouseListener(souris);
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
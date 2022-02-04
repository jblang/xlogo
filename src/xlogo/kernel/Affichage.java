/**
 * Title : XLogo Description : XLogo is an interpreter for the Logo programming
 * language
 *
 * @author Loïc Le Coq
 */
package xlogo.kernel;

import xlogo.Application;
import xlogo.Logo;
import xlogo.MemoryChecker;
import xlogo.utils.myException;

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
public class Affichage extends Thread {
    public static boolean execution_lancee = false;
    private boolean pause = false;
    private Application cadre;
    private StringBuffer instruction;
    private final Souris souris = new Souris();
    private MemoryChecker cm = null;

    public Affichage() {
    }

    public Affichage(Application cadre, StringBuffer instruction) {
        this.cadre = cadre;
        this.instruction = instruction;
    }

    public void run() {
        //	currentThread().setPriority(Thread.MIN_PRIORITY);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                cadre.setCommandLine(false);// la ligne de commandes
                // n'est plus active
            }
        });
        execution_lancee = true;
        cadre.getArdoise().active_souris(); // On active les événements souris sur
        // la zone de dessin
        cadre.scrollArea.getVerticalScrollBar().addMouseListener(souris);
        cadre.scrollArea.getHorizontalScrollBar().addMouseListener(souris);
        try {
            cadre.setCar(-1);
            cadre.error = false;
            myException.lance = false;
            Interprete.operande = Interprete.operateur = Interprete.drapeau_ouvrante = false;
            cadre.getKernel().getInstructionBuffer().clear();
            Interprete.calcul = new Stack<String>();
            Interprete.nom = new Stack<String>();
            Interprete.locale = new HashMap<String, String>();
            Interprete.en_cours = new Stack<String>();
            cm = new MemoryChecker(cadre);
            cm.start();
            boolean b = true;
            while (b) {
                String st = cadre.getKernel().execute(instruction);
                if (!st.equals(""))
                    throw new myException(cadre, Logo.messages
                            .getString("error.whattodo")
                            + " " + st + " ?");
                if (Interprete.actionInstruction.length() == 0) b = false;
                else {
                    instruction = Interprete.actionInstruction;
                    Interprete.actionInstruction = new StringBuffer();
                }
            }
        } catch (myException e) {
        }
        cadre.setCommandLine(true);
        if (!cadre.viewer3DVisible()) cadre.focus_Commande();
        execution_lancee = false;
        cm.setContinuer(false);
        cadre.error = false;
        myException.lance = false;
        cadre.scrollArea.getVerticalScrollBar().removeMouseListener(souris);
        cadre.scrollArea.getHorizontalScrollBar().removeMouseListener(souris);
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
/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo
 * programming language
 *
 * @author Loïc Le Coq
 **/
package xlogo.kernel;

import xlogo.gui.Application;
import xlogo.Config;
import xlogo.Logo;
import xlogo.gui.MessageTextArea;

import javax.swing.*;


/**
 * This class is a thread that prevents from memory Overflow <br>
 * Those problems could happen when a program loops indefinitely<br>
 * Eg:<br> <br>
 * <tt>
 * To bad<br>
 * fd 1 rt 1 <br>
 * bad<br>
 * this lines will explode memory<br>
 * end <br>
 * </tt>
 * @author loic
 *
 */

public class MemoryChecker extends Thread {
    /**
     * The main frame
     */
    private final Application cadre;
    /**
     * This boolean indicates if the thread has to continue.<br>
     * If false, the thread will stop.
     */
    private boolean alive;

    /**
     * Constructs the Memory Checker for the main Frame
     * @param cadre the main Frame
     */
    public MemoryChecker(Application cadre) {
        this.cadre = cadre;
        alive = true;
    }

    /**
     * The Run Method for the Thread
     */
    public void run() {
        while (alive) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            long free = Runtime.getRuntime().freeMemory();
            long total = Runtime.getRuntime().totalMemory();
            long memoire_utilisee = (total - free) / 1024 / 1024;
//		System.out.println(memoire_utilisee);
            if (memoire_utilisee > 0.9 * Config.memoryLimit) {
                cadre.error = true;
                alive = false;
                String message = Logo.messages.getString("depassement_memoire");
                MessageTextArea jt = new MessageTextArea(message);
                JOptionPane.showMessageDialog(cadre, jt, Logo.messages.getString("erreur"), JOptionPane.ERROR_MESSAGE);

            }
        }
    }

    /**
     * Sets the boolean "continuer" for the thread.
     * @param b true or false
     */
    public void setContinuer(boolean b) {
        alive = b;
    }

}

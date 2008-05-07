/**
 * Title : XLogo Description : XLogo is an interpreter for the Logo programming
 * language
 * 
 * @author Loïc Le Coq
 */
package xlogo.kernel;

import java.util.Stack;
import java.util.HashMap;
import java.awt.event.*;
import javax.swing.SwingUtilities;
import xlogo.Application;
import xlogo.utils.myException;
import xlogo.MemoryChecker;
import xlogo.Logo;
//Ce thread gère l'animation de la tortue pendant l'exécution 


public class Affichage extends Thread {
	public static boolean execution_lancee = false;
	private boolean pause = false;
	private Application cadre;
	private StringBuffer instruction;
	private Souris souris = new Souris();
	private MemoryChecker cm = null;
	public Affichage() {
	}
	public Affichage(Application cadre, StringBuffer instruction) {
		this.cadre = cadre;
		this.instruction = instruction;
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
		cadre.jScrollPane1.getVerticalScrollBar().addMouseListener(souris);
		cadre.jScrollPane1.getHorizontalScrollBar().addMouseListener(souris);
		try {
			cadre.setCar(-1);
			cadre.error = false;
			myException.lance = false;
			Interprete.operande = Interprete.operateur = Interprete.drapeau_ouvrante = false;
			Interprete.instruction = new StringBuffer();
			Interprete.calcul = new Stack<String>();
			Interprete.nom = new Stack<String>();
			Interprete.locale = new HashMap<String,String>();
			Interprete.en_cours = new Stack<String>();
			cm = new MemoryChecker(cadre);
			cm.start();
			boolean b=true;
			while (b){
				String st = cadre.getKernel().execute(instruction);
				if (!st.equals(""))
					throw new myException(cadre, Logo.messages
							.getString("error.whattodo")
							+ " " + st + " ?");
				if (Interprete.actionInstruction.length()==0) b=false;
				else {
					instruction=Interprete.actionInstruction;
					Interprete.actionInstruction=new StringBuffer();
				}
			}
		} catch (myException e) {}
		cadre.setCommandLine(true);
		if (!cadre.viewer3DVisible()) cadre.focus_Commande();
		execution_lancee = false;
		cm.setContinuer(false);
		cadre.error = false;
		myException.lance = false;
		cadre.jScrollPane1.getVerticalScrollBar().removeMouseListener(souris);
		cadre.jScrollPane1.getHorizontalScrollBar().removeMouseListener(souris);
	}

	protected boolean isOnPause() {
		return pause;
	}

}
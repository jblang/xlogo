package xlogo.gui.preferences;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.ImageIcon;
import xlogo.utils.Utils;

import xlogo.Application;
import xlogo.Config;
/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo 
 * 						programming language
 * @author Loïc Le Coq
 */
public class Panel_Turtles extends JPanel {
	private static final long serialVersionUID = 1L;
	private ButtonGroup buttonGroup3 = new ButtonGroup(); //Pour les images de tortue
	private Icone[] icone = new Icone[7]; //POur les vignettes
	private GridBagLayout gridBagLayout2 = new GridBagLayout();
	Application cadre;
	protected Panel_Turtles(Application cadre){
		this.cadre=cadre;
		initGui();
	}
	private void initGui(){
		for (int i = 0; i < 7; i++) {
			icone[i] = new Icone(i);
			icone[i].setText("");
			buttonGroup3.add(icone[i]);
		}
		setBackground(Preference.violet);
		setLayout(gridBagLayout2);
		
		add(icone[5], new GridBagConstraints(2, 1, 1, 1, 1.0,
				1.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));
		add(icone[3], new GridBagConstraints(0, 1, 1, 1, 1.0,
				1.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));
		add(icone[4], new GridBagConstraints(1, 1, 1, 1, 1.0,
				1.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));
		add(icone[2], new GridBagConstraints(2, 0, 1, 1, 1.0,
				1.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));
		add(icone[1], new GridBagConstraints(1, 0, 1, 1, 1.0,
				1.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));
		add(icone[6], new GridBagConstraints(0, 2, 1, 1, 1.0,
				1.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));
		add(icone[0], new GridBagConstraints(0, 0, 1, 1, 1.0,
				1.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));
	}
	protected void update(){
		String chemin = "tortue";
		for (int i = 0; i < 7; i++) {
			if (icone[i].num() != -1) {
				chemin += i + ".png";
				Config.activeTurtle = i;
				break;
			}
		}
		if (!chemin.equals("tortue")) {
			cadre.getKernel().getActiveTurtle().setShape(Config.activeTurtle);
			cadre.getKernel().change_image_tortue(chemin);
		}
	}
	class Icone extends JToggleButton {
		private static final long serialVersionUID = 1L;
		private int numero;

		Icone(int numero) {
			this.numero = numero;
			ImageIcon ic = new ImageIcon(Utils.class.getResource("tortue"
					+ numero + ".png"));
			this.setIcon(ic);
			if (numero == Config.activeTurtle)
				setSelected(true);
		}

		int num() {
			if (isSelected())
				return numero;
			return -1;
		}
	}
}

package xlogo.gui.preferences;

import java.awt.Color;
import java.awt.event.ActionEvent;

class PanelColor extends AbstractPanelColor{

	private static final long serialVersionUID = 1L;
	PanelColor(Color c){
		super(c);
	}
	 public void actionPerformed(ActionEvent e){
	    	String cmd=e.getActionCommand();
	    	if (cmd.equals("bouton")){
	    		actionButton();
	    	}
	    }
}

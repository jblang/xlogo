


package xlogo.kernel.gui;

import javax.swing.JButton;

import xlogo.Config;
import xlogo.kernel.Interprete;
import java.awt.event.*;
import xlogo.Application;
import xlogo.utils.Utils;
public class GuiButton extends GuiComponent {
	private StringBuffer action;
	private Application app;
	
	public GuiButton(String id, String text,Application app){
		super.setId(id);
		guiObject=new JButton(text);
		this.app=app;
		java.awt.FontMetrics fm = app.getGraphics()
		.getFontMetrics(Config.police);
		originalWidth = fm.stringWidth(((JButton)(getGuiObject())).getText()) + 50;
		originalHeight=Config.police.getSize()+10;
		setSize(originalWidth,originalHeight);
	}

	public void actionPerformed(ActionEvent e){
		if (!app.commande_isEditable()){
			Interprete.actionInstruction.append(action);
		}
		else {
			app.affichage_Start(action);
		}
	}
	public boolean isButton(){
		return true;
	}
	public boolean isMenu(){
		return false;
	}
	public void setAction(StringBuffer action){
		this.action=action;
	}
	
}

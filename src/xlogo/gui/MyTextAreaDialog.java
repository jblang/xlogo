package xlogo.gui;

import java.awt.Color;

import javax.swing.JTextArea;

import xlogo.Config;
/**
 * This class creates the common yellow JTextArea for all dialog box. 
 * @author loic
 *
 */
public class MyTextAreaDialog extends JTextArea {

	private static final long serialVersionUID = 1L;
	public MyTextAreaDialog(String message){
		setText(message);
		setEditable(false);
		setBackground(new Color(255,255,177));
		setFont(Config.police);		
	}

}

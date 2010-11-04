package xlogo.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

import javax.swing.JTextArea;

import xlogo.Config;
import xlogo.StyledDocument.DocumentLogoHistorique;
import xlogo.gui.preferences.Panel_Font;
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

	public MyTextAreaDialog(String message,	DocumentLogoHistorique dsd){
		setFont(dsd.getFont());
		setText(message);
		setEditable(false);
		setBackground(new Color(255,255,177));
		setFont(Config.police);	
	}
}

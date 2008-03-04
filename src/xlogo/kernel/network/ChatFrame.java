package xlogo.kernel.network;

import java.awt.Color;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JTextField;
import java.awt.event.*;
import java.awt.Dimension;
import java.io.PrintWriter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.BorderLayout;
import xlogo.Config;
import xlogo.Application;
import xlogo.utils.Utils;
import xlogo.Logo;


public class ChatFrame extends JFrame implements ActionListener{
	private static final long serialVersionUID = 1L;
	private MutableAttributeSet local;
	private MutableAttributeSet distant;
	private JTextPane textPane;
	private DefaultStyledDocument dsd;
	private JScrollPane scroll;
	private JTextField textField;
	private Application app;
	private PrintWriter out;
	protected ChatFrame(PrintWriter out,Application app){
		this.app=app;
		this.out=out;
		initStyle();
		initGui();
		textField.addActionListener(this);
	}
	private void initStyle(){
		local = new SimpleAttributeSet();
		StyleConstants.setFontFamily(local,Config.police.getFamily());
		StyleConstants.setForeground(local, Color.black);
		StyleConstants.setFontSize(local,Config.police.getSize());
		
		distant = new SimpleAttributeSet();
		StyleConstants.setFontFamily(distant,Config.police.getFamily());
		StyleConstants.setForeground(distant, Color.RED);
		StyleConstants.setFontSize(distant,Config.police.getSize());	
	}
	private void initGui(){
		setTitle(Logo.messages.getString("chat"));
	    setIconImage(Toolkit.getDefaultToolkit().createImage(Utils.class.getResource("icone.png")));
		dsd=new DefaultStyledDocument();
		textPane=new JTextPane();
		textPane.setDocument(dsd);
		textPane.setEditable(false);
		textPane.setBackground(new Color(255,255,220));
		this.getContentPane().setLayout(new BorderLayout());
		textField=new JTextField();
		java.awt.FontMetrics fm = app.getGraphics().getFontMetrics(Config.police);
		int width=fm.stringWidth(Logo.messages.getString("stop_chat")) + 30;
		if (width<200) width=200;

		textPane.setPreferredSize(new Dimension(width,300));
		textField.setPreferredSize(new Dimension(width,Config.police.getSize()+10));
		scroll=new JScrollPane(textPane);
		getContentPane().add(scroll,BorderLayout.CENTER);
		getContentPane().add(textField,BorderLayout.SOUTH);
  		textPane.setBorder(BorderFactory.createLineBorder(Color.BLUE,3));
  		textField.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		pack();
		setVisible(true);
	}
	protected void append(String sty,String st){
		st+="\n";
			try{
				if (sty.equals("local")) 
					dsd.insertString(dsd.getLength(),st,local);
				if (sty.equals("distant")) 
					dsd.insertString(dsd.getLength(),">"+st,distant);
				textPane.setCaretPosition(dsd.getLength());
				
			}
			catch(BadLocationException e){}
	}
	
	protected void processWindowEvent(WindowEvent e){
		if (e.getID()==WindowEvent.WINDOW_CLOSING){
			out.println(NetworkServer.END_OF_FILE);
			dispose();
		}
	}
	public void actionPerformed(ActionEvent e){
		String txt=textField.getText();
		append("local",txt);
		textField.setText("");
		out.println(txt);
	}
}

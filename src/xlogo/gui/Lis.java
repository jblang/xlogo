package xlogo.gui;
/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo 
 * 						programming language
 * @author Loïc Le Coq
 */
// Frame for the primitive "read"


import javax.swing.JFrame;
import javax.swing.JTextField;
import java.awt.HeadlessException;
import javax.swing.JButton;
import java.awt.event.*;
import java.awt.*;
import xlogo.utils.Utils;
import xlogo.Logo;
public class Lis extends JFrame implements ActionListener{
	private static final long serialVersionUID = 1L;
  private JTextField texte=new JTextField();
  private JButton ok=new JButton(Logo.messages.getString("pref.ok"));
  public Lis() throws HeadlessException {
  }
  public Lis(String titre,int longueur){
    setIconImage(Toolkit.getDefaultToolkit().createImage(Utils.class.getResource("icone.png")));
    getContentPane().setLayout(new BorderLayout());
    this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    getContentPane().add(ok,BorderLayout.EAST);
    getContentPane().add(texte,BorderLayout.CENTER);
    texte.setPreferredSize(new Dimension(longueur,50));
    ok.setPreferredSize(new Dimension(75,50));
    texte.addActionListener(this);
    ok.addActionListener(this);
    pack();
    setTitle(titre);
	Dimension d=Toolkit.getDefaultToolkit().getScreenSize().getSize();
	int x=(int)(d.getWidth()/2-longueur/2);
	int y=(int)(d.getHeight()/2-25);
	setLocation(x,y);
    setVisible(true);
    texte.requestFocus();
  }
  public void actionPerformed(ActionEvent e){
    setVisible(false);
  }
  public String getText(){
  	return texte.getText();
  }
}
package xlogo.gui.preferences;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import xlogo.StyledDocument.DocumentLogo;
import xlogo.Config;
import xlogo.Application;
import xlogo.Logo;
/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo 
 * 						programming language
 * @author Loïc Le Coq
 */
public class Panel_Highlighter extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	private Application cadre;
	private Combo_Couleur p_comment;
	private Combo_Couleur p_parenthese;
	private Combo_Couleur p_primitive;
	private Combo_Couleur p_operande;
	private GridBagLayout gb = new GridBagLayout();
	private JTextPane jt=new JTextPane();
	private DocumentLogo dsd;
	private JCheckBox active_coloration = new JCheckBox();
	private JLabel l_active=new JLabel();
	private JButton bdefaut=new JButton();
	protected Panel_Highlighter(Application cadre){
		this.cadre=cadre;
		l_active.setFont(Config.police);
		l_active.setText(Logo.messages.getString("pref.highlight.enabled"));
		bdefaut.setFont(Config.police);
		bdefaut.setText(Logo.messages.getString("pref.highlight.init"));
		setLayout(gb);
        jt.setOpaque(true);
        jt.setBackground(Color.white);
        dsd=new DocumentLogo();
        jt.setDocument(dsd);
        dsd.setColore_Parenthese(true);
		p_comment=new Combo_Couleur(Config.coloration_commentaire,Config.style_commentaire,Logo.messages.getString("pref.highlight.comment"),this);;
		p_parenthese=new Combo_Couleur(Config.coloration_parenthese,Config.style_parenthese,Logo.messages.getString("pref.highlight.parenthesis"),this);;
		p_primitive=new Combo_Couleur(Config.coloration_primitive,Config.style_primitive,Logo.messages.getString("pref.highlight.primitive"),this);;
		p_operande=new Combo_Couleur(Config.coloration_operande,Config.style_operande,Logo.messages.getString("pref.highlight.operand"),this);;
		jt.setText(Logo.messages.getString("pref.highlight.example"));
		if (Config.COLOR_ENABLED) active_coloration.setSelected(true);
		else {
			active_coloration.setSelected(false);
			p_comment.setEnabled(false);
			p_primitive.setEnabled(false);
			p_parenthese.setEnabled(false);
			p_operande.setEnabled(false);
			bdefaut.setEnabled(false);	
		} 
        add(active_coloration, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(
						5, 10, 5, 10), 0, 0));
        add(l_active, new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(
						5, 10, 5, 10), 0, 0));
        add(p_comment, new GridBagConstraints(0, 1, 3, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						5, 10, 5, 10), 0, 0));
        add(p_primitive, new GridBagConstraints(0, 2, 3, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						5, 10, 5, 10), 0, 0));
        add(p_operande, new GridBagConstraints(0, 3, 3, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						5, 10, 5, 10), 0, 0));
        add(p_parenthese, new GridBagConstraints(0, 4, 3, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						5, 10, 5, 10), 0, 0));
        add(jt, new GridBagConstraints(0, 5, 3, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						10, 10, 10, 10), 0, 0));
        add(bdefaut, new GridBagConstraints(0, 6, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(
						10, 10, 10, 10), 0, 0));
        active_coloration.addActionListener(this);
        active_coloration.setActionCommand("checkbox");
        bdefaut.addActionListener(this);
        bdefaut.setActionCommand("bouton");
	}
	protected void update(){

		if (active_coloration.isSelected()) {
			if (!Config.COLOR_ENABLED){	
				Config.COLOR_ENABLED=true;
				cadre.editeur.toTextPane();
			}
			Config.coloration_commentaire=p_comment.color();
			Config.coloration_operande=p_operande.color();
			Config.coloration_primitive=p_primitive.color();
			Config.coloration_parenthese=p_parenthese.color();
			Config.style_commentaire=p_comment.style();
			Config.style_operande=p_operande.style();
			Config.style_primitive=p_primitive.style();
			Config.style_parenthese=p_parenthese.style();
			
			// On attribue les styles sélectionnés à l'éditeur 
		cadre.changeSyntaxHighlightingStyle(Config.coloration_commentaire,Config.style_commentaire,Config.coloration_primitive,Config.style_primitive,
				Config.coloration_parenthese,Config.style_parenthese,Config.coloration_operande,Config.style_operande);
		}
		else {
			if (Config.COLOR_ENABLED) {
				cadre.editeur.toTextArea();
				Config.COLOR_ENABLED=false;
			}
		}
		cadre.setColoration(Config.COLOR_ENABLED);
		cadre.resizeCommande();
		cadre.validate();
	}
		
	public void actionPerformed(ActionEvent e){
		String cmd=e.getActionCommand();
		if (cmd.equals("checkbox")){
			if (p_comment.isEnabled()){
				p_comment.setEnabled(false);
				p_primitive.setEnabled(false);
				p_parenthese.setEnabled(false);
				p_operande.setEnabled(false);
				bdefaut.setEnabled(false);
				dsd.setColoration(false);
				jt.setText(Logo.messages.getString("pref.highlight.example"));
			}
			else {
				p_comment.setEnabled(true);
				p_primitive.setEnabled(true);
				p_parenthese.setEnabled(true);
				p_operande.setEnabled(true);
				bdefaut.setEnabled(true);
				dsd.setColoration(true);
				rafraichis_texte();
			}
		}
		else if (cmd.equals("bouton")){
			p_comment.setColorAndStyle(Color.GRAY.getRGB(),Font.PLAIN);
			p_parenthese.setColorAndStyle(Color.RED.getRGB(),Font.BOLD);
			p_primitive.setColorAndStyle(new Color(0,128,0).getRGB(),Font.PLAIN);
			p_operande.setColorAndStyle(Color.BLUE.getRGB(),Font.PLAIN);
			rafraichis_texte();
		}
		
	}
	protected void rafraichis_texte(){
		dsd.initStyles(p_comment.color(),p_comment.style(),p_primitive.color(),p_primitive.style(),
				p_parenthese.color(),p_parenthese.style(),p_operande.color(),p_operande.style());
		jt.setText(Logo.messages.getString("pref.highlight.example"));
		dsd.initStyles(Config.coloration_commentaire,Config.style_commentaire,Config.coloration_primitive,Config.style_primitive,
				Config.coloration_parenthese,Config.style_parenthese,Config.coloration_operande,Config.style_operande);

	}
}

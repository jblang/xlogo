package xlogo.gui.preferences;

import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MediaTracker;
import java.awt.Toolkit;

import xlogo.Config;
import xlogo.utils.Utils;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.ButtonGroup;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import xlogo.Application;
import xlogo.Logo;
/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo 
 * 						programming language
 * @author Loïc Le Coq
 */
public class Panel_General extends JPanel {
	private static final long serialVersionUID = 1L;
	private Application cadre;

	private JList jl_langues = new JList(Logo.translationLanguage); //Pour les différentes langues
	private JScrollPane js_langues = new JScrollPane(jl_langues);
	private ButtonGroup buttonGroup2 = new ButtonGroup();
	private JCheckBox java = new JCheckBox();
	private JCheckBox windows = new JCheckBox();
	private JCheckBox motif = new JCheckBox();
	private JLabel Langue = new JLabel(Logo.messages.getString("pref.general.lang"));
	private JLabel Aspect = new JLabel(Logo.messages.getString("pref.general.aspect"));
	private GridBagLayout gridBagLayout3 = new GridBagLayout();
	private JLabel vitesse_defilement = new JLabel(Logo.messages.getString("pref.general.speed"));
	private JLabel lent = new JLabel(Logo.messages.getString("pref.general.slow"));
	private JLabel rapide = new JLabel(Logo.messages.getString("pref.general.fast"));
	private JSlider jSlider1 = new JSlider(0,100);
	protected Panel_General(Application cadre){
		this.cadre=cadre;
		initGui();
	}
	private void initGui(){
		Langue.setFont(Config.police);
		Aspect.setFont(Config.police);
		vitesse_defilement.setFont(Config.police);
		lent.setFont(Config.police);
		rapide.setFont(Config.police);
		java.setFont(Config.police);
		windows.setFont(Config.police);
		motif.setFont(Config.police);
		
		jl_langues.setCellRenderer(new Contenu());
		buttonGroup2.add(java);
		buttonGroup2.add(motif);
		buttonGroup2.add(windows);

		jl_langues.setSelectionBackground(Color.orange);
		jl_langues.setSelectedIndex(Config.langage);
		jSlider1.setValue(jSlider1.getMaximum()-Config.turtleSpeed);

		setLayout(gridBagLayout3);
		jl_langues.setBackground(Preference.violet);
		jl_langues.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		setBackground(Preference.violet);
		setFont(new java.awt.Font("DialogInput", 0, 12));

		java.setBackground(Preference.violet);
		java.setActionCommand(Logo.messages.getString("pref.general.metal"));
		java.setHorizontalAlignment(SwingConstants.LEFT);
		java.setHorizontalTextPosition(SwingConstants.LEFT);
		java.setText(Logo.messages.getString("pref.general.metal"));
		windows.setBackground(Preference.violet);
		windows.setActionCommand(Logo.messages.getString("pref.general.windows"));
		windows.setHorizontalAlignment(SwingConstants.LEFT);
		windows.setHorizontalTextPosition(SwingConstants.LEFT);
		windows.setText(Logo.messages.getString("pref.general.windows"));
		windows.setVerticalAlignment(SwingConstants.CENTER);
		motif.setBackground(Preference.violet);
		motif.setActionCommand(Logo.messages.getString("pref.general.motif"));
		motif.setHorizontalAlignment(SwingConstants.LEFT);
		motif.setHorizontalTextPosition(SwingConstants.LEFT);
		motif.setText(Logo.messages.getString("pref.general.motif"));
		switch(Config.looknfeel){
			case Config.LOOKNFEEL_JAVA:
				java.setSelected(true);
			break;
			case Config.LOOKNFEEL_MOTIF:
				motif.setSelected(true);
			break;
			case Config.LOOKNFEEL_NATIVE:
				windows.setSelected(true);
			break;
		}
		Langue.setForeground(Color.red);
		Langue.setOpaque(true);
		Langue.setHorizontalAlignment(SwingConstants.CENTER);
		Langue.setHorizontalTextPosition(SwingConstants.LEFT);
		Langue.setText(Logo.messages.getString("pref.general.lang"));
		Aspect.setForeground(Color.red);
		Aspect.setOpaque(true);
		Aspect.setHorizontalAlignment(SwingConstants.CENTER);
		Aspect.setHorizontalTextPosition(SwingConstants.LEFT);
		Aspect.setText(Logo.messages.getString("pref.general.aspect"));

		Color kaki=new Color(153,153,0);
		lent.setBackground(kaki);
		lent.setForeground(Color.red);
		lent.setOpaque(true);
		lent.setToolTipText("");
		lent.setHorizontalAlignment(SwingConstants.LEFT);
		lent.setHorizontalTextPosition(SwingConstants.LEFT);
		lent.setText(Logo.messages.getString("pref.general.slow"));
		lent.setVerticalAlignment(SwingConstants.BOTTOM);
		lent.setVerticalTextPosition(SwingConstants.BOTTOM);
		rapide.setBackground(kaki);
		rapide.setForeground(Color.red);
		rapide.setDebugGraphicsOptions(0);
		rapide.setOpaque(true);
		rapide.setHorizontalAlignment(SwingConstants.RIGHT);
		rapide.setHorizontalTextPosition(SwingConstants.RIGHT);
		rapide.setText(Logo.messages.getString("pref.general.fast"));
		rapide.setVerticalAlignment(SwingConstants.BOTTOM);
		rapide.setVerticalTextPosition(SwingConstants.BOTTOM);
		jSlider1.setBackground(kaki);
		jSlider1.setMajorTickSpacing(10);
		jSlider1.setMinorTickSpacing(5);
		jSlider1.setPaintTicks(true);
		jSlider1.setPaintLabels(true);
		jSlider1.setSnapToTicks(true);

		add(Langue, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						10, 10, 0, 10), 0, 0));
		add(js_langues, new GridBagConstraints(0, 1, 1, 3, 1.0,
				1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(10, 10, 10, 10), 0, 0));
		add(Aspect, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						10, 10, 0, 10), 0, 0));
		add(java, new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						0, 0, 0, 0), 0, 0));
		add(windows, new GridBagConstraints(1, 2, 1, 1, 1.0,
				1.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
				new Insets(0, 0, 0, 0), 0, 0));
		add(motif, new GridBagConstraints(1, 3, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						0, 0, 0, 0), 0, 0));

		add(vitesse_defilement, new GridBagConstraints(0, 4, 1,
				1, 1.0, 0.5, GridBagConstraints.EAST, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0));
		add(rapide, new GridBagConstraints(1, 5, 1, 1, 1.0, 0.5,
				GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(0,
						0, 0, 0), 0, 0));
		add(lent, new GridBagConstraints(0, 5, 1, 1, 1.0, 0.5,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,
						0, 0, 0), 0, 0));
		add(jSlider1, new GridBagConstraints(0, 6, 2, 1, 1.0,
				1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 10, 0), 0, 0));
	
	
	}
	/**
	 * Apply any modification, if some application parameter have been modified with this panel.
	 */
	protected void update(){
		// Language has changed?
		int indicateur = jl_langues.getSelectedIndex();
		
		if (indicateur != Config.langage && indicateur != -1) {
			cadre.changeLanguage(indicateur);
		}
		// Turtle Speed
		Config.turtleSpeed = jSlider1.getMaximum()-jSlider1.getValue();
		try { //Look and Feel has changed?
			if (windows.isSelected()) {
				Config.looknfeel=Config.LOOKNFEEL_NATIVE;
				UIManager.setLookAndFeel(UIManager
						.getSystemLookAndFeelClassName());
			} else if (java.isSelected()) {
				Config.looknfeel=Config.LOOKNFEEL_JAVA;
				UIManager
						.setLookAndFeel(new javax.swing.plaf.metal.MetalLookAndFeel());
			} else if (motif.isSelected()) {
				Config.looknfeel=Config.LOOKNFEEL_MOTIF;
				UIManager
						.setLookAndFeel(new com.sun.java.swing.plaf.motif.MotifLookAndFeel());
			}
			cadre.changeLookNFeel();
		} catch (Exception exc) {
			System.out.println(exc.toString());
		}
		
	
	}
	
	  class Contenu extends JLabel implements ListCellRenderer{
			private static final long serialVersionUID = 1L;
	  	private ImageIcon[] drapeau;
	  	Contenu(){
	  		setFont(Config.police);
	  		drapeau=new ImageIcon[Logo.translationLanguage.length];
	  		cree_icone();	
	  	}
	  	void cree_icone(){
	  		for (int i=0;i<Logo.translationLanguage.length;i++){
	  		Image image=null;
  			image= Toolkit.getDefaultToolkit().getImage(Utils.class.getResource("drapeau"+i+".png"));
  			MediaTracker tracker=new MediaTracker(this);
  			tracker.addImage(image,0);
  			try{tracker.waitForID(0);}
  			catch(InterruptedException e1){}
  			int largeur=image.getWidth(this);
  			int hauteur=image.getHeight(this);
  			double facteur = (double)Config.police.getSize()/(double)hauteur;
  			image=image.getScaledInstance((int)(facteur*largeur),(int)(facteur*hauteur),Image.SCALE_SMOOTH);
  			tracker=new MediaTracker(this);
			tracker.addImage(image,0);
			try{tracker.waitForID(0);}
			catch(InterruptedException e1){}
			drapeau[i]=new ImageIcon();
  			drapeau[i].setImage(image);
//			drapeau[i]=new ImageIcon(image);
	  		}
	  		
	  	}
	  	public Component getListCellRendererComponent(JList list, Object value,int 
	  index, boolean isSelected,boolean cellHasFocus){ 
	  		setOpaque(true);
	  		String s=value.toString(); 
	  		setText(s); 
	  		setIcon(drapeau[index]);
	  		if (isSelected) {
	  			setBackground(list.getSelectionBackground());
	  			setForeground(list.getSelectionForeground()); } 
	  		else{
	  			setBackground(list.getBackground());
	  			setForeground(list.getForeground()); 
	  		}
	  		setBorder(BorderFactory.createEmptyBorder(5,0,5,5));
	 		return(this); 
	 		} 
	  	}

}

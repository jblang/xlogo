package xlogo.gui.preferences;

import java.awt.Insets;
import java.awt.GridBagConstraints;
import javax.swing.JPanel;
import javax.swing.JCheckBox;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import xlogo.Config;
import xlogo.kernel.network.NetworkServer;
import xlogo.gui.MyTextAreaDialog;
import xlogo.Application;
import javax.swing.JOptionPane;
import xlogo.Logo;
/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo 
 * 						programming language
 * @author Loïc Le Coq
 */
public class Panel_Options extends JPanel {
	private static final long serialVersionUID = 1L;
	private Application cadre;
	private JCheckBox effacer_editeur = new JCheckBox(Logo.messages.getString("effacer_editeur"));
	private JCheckBox clearVariables = new JCheckBox(Logo.messages.getString("pref.options.eraseVariables"));
	private GridBagLayout gridBagLayout4 = new GridBagLayout();
	
	private JLabel label_screencolor=new JLabel(Logo.messages.getString("screencolor"));
	private PanelColor b_screencolor=new PanelColor(Config.screencolor); 
	private JLabel label_pencolor=new JLabel(Logo.messages.getString("pencolor"));
	private PanelColor b_pencolor=new PanelColor(Config.pencolor);
	private JLabel nb_tortues = new JLabel(Logo.messages.getString("nb_tortues"));
	private JLabel epaisseur_crayon = new JLabel(Logo.messages
			.getString("epaisseur_crayon"));
	private JLabel forme_crayon = new JLabel(Logo.messages.getString("forme_crayon"));
	private Object[] carre_rond = { Logo.messages.getString("carre"),
			Logo.messages.getString("rond") };
	private JComboBox jc = new JComboBox(carre_rond);
	private JTextField tortues = new JTextField();
	private JTextField epaisseur = new JTextField();
	private JLabel dimension_dessin = new JLabel(Logo.messages
			.getString("taille_dessin"));
	private JPanel taille_dessin = new JPanel();
	private JTextField largeur = new JTextField(String.valueOf(Config.imageWidth));
	private JLabel labelx = new JLabel("x");
	private JTextField hauteur = new JTextField(String.valueOf(Config.imageHeight));
	private JLabel label_memoire=new JLabel(Logo.messages.getString("memoire"));
	private JTextField memoire=new JTextField(String.valueOf(Config.tmp_memoire));
	private JLabel label_qualite=new JLabel(Logo.messages.getString("qualite_dessin"));
	private Object[] choix_qualite={Logo.messages.getString("normal"),Logo.messages.getString("haut"),Logo.messages.getString("bas")};
	private JComboBox jc_qualite=new JComboBox(choix_qualite);
	private PanelGrid panelGrid=new PanelGrid();
	private PanelAxis panelAxis=new PanelAxis();
	private BorderImagePanel borderPanel=new BorderImagePanel();
	private JLabel labelTcp = new JLabel(Logo.messages.getString("pref.options.tcp"));
	private JTextField textTcp = new JTextField(String.valueOf(Config.TCP_PORT));
	protected Panel_Options(Application cadre){
		this.cadre=cadre;
		epaisseur_crayon.setFont(Config.police);
		forme_crayon.setFont(Config.police);
		clearVariables.setFont(Config.police);
		effacer_editeur.setFont(Config.police);
		dimension_dessin.setFont(Config.police);
		label_memoire.setFont(Config.police);
		label_qualite.setFont(Config.police);
		jc.setFont(Config.police);
		tortues.setFont(Config.police);
		epaisseur.setFont(Config.police);
		largeur.setFont(Config.police);
		hauteur.setFont(Config.police);
		memoire.setFont(Config.police);
		jc_qualite.setFont(Config.police);
		label_pencolor.setFont(Config.police);
		label_screencolor.setFont(Config.police);
		nb_tortues.setFont(Config.police);
		labelTcp.setFont(Config.police);
		textTcp.setFont(Config.police);
		
		jc.setSelectedIndex(Config.penShape);
		if (Config.eraseImage)
			effacer_editeur.setSelected(true);
		if (Config.clearVariables)
			clearVariables.setSelected(true);
		epaisseur.setText(String.valueOf(Config.maxPenWidth));
		tortues.setText(String.valueOf(Config.maxTurtles));
		jc_qualite.setSelectedIndex(Config.quality);
		setBackground(Preference.violet);
		setLayout(gridBagLayout4);
		taille_dessin.add(largeur);
		taille_dessin.add(labelx);
		taille_dessin.add(hauteur);
		taille_dessin.setBackground(Preference.violet);
		effacer_editeur.setBackground(Preference.violet);
		clearVariables.setBackground(Preference.violet);
		
		add(panelGrid, new GridBagConstraints(0, 0, 2, 1,
				1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(10, 0, 0, 0), 0, 0));	
		add(panelAxis, new GridBagConstraints(0, 1, 2, 1,
				1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(10, 10, 0, 0), 0, 0));
		add(label_screencolor, new GridBagConstraints(0, 2, 1, 1, 1.0,
				1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(10, 10, 0, 10), 0, 0));
		add(b_screencolor, new GridBagConstraints(1, 2, 1, 1, 1.0,
				0.5, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(10, 10, 0, 10), 0, 0));
		add(label_pencolor, new GridBagConstraints(0, 3, 1, 1, 1.0,
				1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(10, 10, 0, 10), 0, 0));
		add(b_pencolor, new GridBagConstraints(1, 3, 1, 1, 1.0,
				1.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(10, 10, 0, 10), 0, 0));

		add(borderPanel, new GridBagConstraints(0, 4, 2, 1, 1.0,
				1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(10, 10, 0, 10), 0, 0));
		add(nb_tortues, new GridBagConstraints(0, 5, 1, 1, 1.0,
				1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(10, 10, 0, 10), 0, 0));
		add(tortues, new GridBagConstraints(1, 5, 1, 1, 1.0,
				1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(10, 10, 0, 10), 0, 0));
		add(epaisseur_crayon, new GridBagConstraints(0, 6, 1,
				1, 1.0, 1.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(10, 10, 0, 10), 0, 0));
		add(epaisseur, new GridBagConstraints(1, 6, 1, 1, 1.0,
				1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(10, 10, 0, 10), 0, 0));
		add(forme_crayon, new GridBagConstraints(0, 7, 1, 1,
				1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(10, 10, 0, 10), 0, 0));
		add(jc, new GridBagConstraints(1, 7, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(10, 10, 0, 10), 0, 0));
		add(label_qualite, new GridBagConstraints(0, 8, 1,
				1, 1.0, 1.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(10, 10, 0, 10), 0, 0));
		add(jc_qualite, new GridBagConstraints(1, 8, 1, 1,
				1.0, 1.0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		add(effacer_editeur, new GridBagConstraints(0, 9, 2, 1,
				1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(10, 0, 0, 0), 0, 0));
		add(clearVariables, new GridBagConstraints(0, 10, 2, 1,
				1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(10, 0, 0, 0), 0, 0));
		add(dimension_dessin, new GridBagConstraints(0, 11, 1,
				1, 1.0, 1.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(10, 10, 0, 10), 0, 0));
		add(taille_dessin, new GridBagConstraints(1, 11, 1, 1,
				1.0, 1.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
		add(label_memoire, new GridBagConstraints(0, 12, 1,
				1, 1.0, 1.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(10, 10, 0, 10), 0, 0));
		add(memoire, new GridBagConstraints(1, 12, 1, 1,
				1.0, 1.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
		add(labelTcp, new GridBagConstraints(0, 13, 1,
				1, 1.0, 1.0, GridBagConstraints.CENTER,
			GridBagConstraints.BOTH, new Insets(10, 10, 0, 10), 0, 0));
		add(textTcp, new GridBagConstraints(1, 13, 1, 1,
				1.0, 1.0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
	}
	protected void update(){
		// TCP Port
		try {
			int p=Integer.parseInt(textTcp.getText());
			if (p<=0) p=1948;
			Config.TCP_PORT=p;
		}
		catch(NumberFormatException e){
			Config.TCP_PORT=1948;
		}
		//pen color
		Config.pencolor=b_pencolor.getValue();
		// screencolor
		Config.screencolor=b_screencolor.getValue();
		// Clear screen when we leave editor?
		if (effacer_editeur.isSelected())
			Config.eraseImage = true;
		else Config.eraseImage=false;
		if (clearVariables.isSelected())
			Config.clearVariables = true;
		else Config.clearVariables=false;
		// Number of turtles
		try {
			int i = Integer.parseInt(tortues.getText());
			cadre.getKernel().setNumberOfTurtles(i);
		} 
		catch (NumberFormatException e2) {}
		
		// maximum pen width
		try {
			int i = Integer.parseInt(epaisseur.getText());
			Config.maxPenWidth = i;
			if (cadre.getKernel().getActiveTurtle().getPenWidth()*2>i){
				cadre.getKernel().getActiveTurtle().fixe_taille_crayon(i);
			}
		} catch (NumberFormatException e1) {
		}
		// pen shape
		Config.penShape = jc.getSelectedIndex();
		// Quality of drawing
		Config.quality=jc_qualite.getSelectedIndex();
		cadre.getKernel().setDrawingQuality(Config.quality);
		// Si on redimensionne la zone de dessin
		// Resize drawing zone
		try{
			boolean changement=false;
			int dim=Integer.parseInt(hauteur.getText());
			if (dim!=Config.imageHeight) changement=true;
			int tmp_hauteur=Config.imageHeight;
			Config.imageHeight=dim;
			dim=Integer.parseInt(largeur.getText());
			if (dim!=Config.imageWidth) changement=true;
			int tmp_largeur=Config.imageWidth;
			Config.imageWidth=dim;
			if (Config.imageWidth<100 || Config.imageHeight<100) {
				Config.imageWidth=1000;
				Config.imageHeight=1000;
			}
			if (changement) {
				int memoire_necessaire=Config.imageWidth*Config.imageHeight*4/1024/1024;
				int memoire_image=tmp_hauteur*tmp_largeur*4/1024/1024;
				long free=Runtime.getRuntime().freeMemory()/1024/1024;
				long total=Runtime.getRuntime().totalMemory()/1024/1024;
//				System.out.println("memoire envisagee "+(total-free+memoire_necessaire-memoire_image));
				if (total-free+memoire_necessaire-memoire_image<Config.memoire*0.8){
					cadre.resizeDrawingZone();
				}
				else{
					Config.imageWidth=tmp_largeur;
					Config.imageHeight=tmp_hauteur;
					largeur.setText(String.valueOf(Config.imageWidth));
					hauteur.setText(String.valueOf(Config.imageHeight));
					long conseil=64*((total-free+memoire_necessaire-memoire_image)/64)+64;
					if (total-free+memoire_necessaire-memoire_image>0.8*conseil) conseil+=64;
					if (conseil==Config.memoire) conseil+=64;
					String message=Logo.messages.getString("erreur_memoire")+" "+conseil+"\n"+Logo.messages.getString("relancer");
					MyTextAreaDialog jt=new MyTextAreaDialog(message); 
			      JOptionPane.showMessageDialog(this,jt,Logo.messages.getString("erreur"),JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		catch(NumberFormatException e1){
			Config.imageWidth=1000;
			Config.imageHeight=1000;
		}
		Config.tmp_memoire=Integer.parseInt(memoire.getText());
		if (Config.tmp_memoire<64) Config.tmp_memoire=64;
		// Draw the grid and axis
		boolean refresh=false;
		boolean b=panelGrid.gridVisible();
		if (b) {
			if (Config.drawGrid==false){
				refresh=true;
			}
			else { 
				if (Config.XGrid!=panelGrid.getXGrid()
						||Config.YGrid!=panelGrid.getYGrid()
						||Config.gridColor!=panelGrid.getGridColor())
					refresh=true;
			}
		}
		else if (Config.drawGrid==true){
			refresh=true;
		}
		b=panelAxis.xAxisVisible();
		if (b){
			if(Config.drawXAxis==false) refresh=true;
			else{
				if (Config.XAxis!=panelAxis.getXAxis()||
						Config.axisColor!=panelAxis.getAxisColor())
					refresh=true;
			}
		}
		else if (Config.drawXAxis==true) refresh=true;
		b=panelAxis.yAxisVisible();
		if (b){
			if(Config.drawYAxis==false) refresh=true;
			else{
				if (Config.YAxis!=panelAxis.getYAxis()||
						Config.axisColor!=panelAxis.getAxisColor())
					refresh=true;
			}
		}
		else if (Config.drawYAxis==true) refresh=true;
		if (refresh) refreshGridAxis();
		// Modify the image border
		Config.borderColor=borderPanel.getBorderColor();
		Config.borderImageSelected=borderPanel.getPath();
		Config.borderExternalImage=borderPanel.getExternalImages();
		cadre.calculateMargin();	
	}
	private void refreshGridAxis(){
		boolean b=panelGrid.gridVisible();
		Config.drawGrid=b;
		Config.XGrid=panelGrid.getXGrid();
		Config.YGrid=panelGrid.getYGrid();
		Config.gridColor=panelGrid.getGridColor();
		b=panelAxis.xAxisVisible();
		Config.drawXAxis=b;
		Config.XAxis=panelAxis.getXAxis();
		b=panelAxis.yAxisVisible();
		Config.drawYAxis=b;
		Config.YAxis=panelAxis.getYAxis();
		Config.axisColor=panelAxis.getAxisColor();
		cadre.getKernel().vide_ecran();
	}
}

/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo 
 * 						programming language
 * @author Loïc Le Coq
 */
package xlogo;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.help.CSH;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.swing.*;

import java.util.Stack;
import java.util.ArrayList;
import java.io.*;

import xlogo.kernel.DrawPanel;
import xlogo.utils.Utils;
import xlogo.gui.preferences.Panel_Font;
import xlogo.gui.preferences.Preference;
import xlogo.gui.*;
import xlogo.kernel.Affichage;
import xlogo.kernel.Kernel;
import xlogo.gui.translation.TranslateXLogo;
import xlogo.kernel.perspective.Viewer3D;
public class Application extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private static Stack<String> pile_historique = new Stack<String>();
	private int index_historique = 0;

	// Contient le chemin du fichiers actuellement
	// ouvert
	public static String path = null;
	public String tmp_path = null; // Lorsqu'on ouvre un fichier
	
	//	 numéro identifiant de la police de l'interface
	public static int police = Panel_Font.police_id(Config.police); 
  	
	private MenuListener menulistener;
	public Editor editeur;
	
	private JPanel contentPane;
	private BorderLayout borderLayout1 = new BorderLayout();
	private JPanel jPanel1 = new JPanel();
	private ZoneCommande commande=new ZoneCommande(this);
	private JLabel jLabel1 = new JLabel();
	private BorderLayout borderLayout2 = new BorderLayout();
	public JSplitPane jSplitPane1 = new JSplitPane();
	public JScrollPane scrollArea;
	private DrawPanel ardoise; 
	private HistoryPanel panneauHistorique1 = new HistoryPanel(this);

	public boolean error = false;
	boolean stop = false;

	public Affichage affichage = null;

	private Sound_Player son = new Sound_Player(this);
	private Touche touche = new Touche();
	
	
	private JMenuBar jMenuBar1 = new JMenuBar();
	private JMenu jMenuFile = new JMenu();
	private JMenuItem jMenuFileNouveau = new JMenuItem();
	private JMenuItem jMenuFileEnregistrer = new JMenuItem();
	private JMenuItem jMenuFileEnregistrer_sous = new JMenuItem();
	private JMenuItem jMenuFileOuvrir = new JMenuItem();
	private JMenuItem jMenuFileQuitter = new JMenuItem();
	private JMenu jMenuCapture = new JMenu();
	private JMenuItem jMenuImageCopy = new JMenuItem();
	private JMenuItem jMenuImageSave = new JMenuItem();
	private JMenuItem jMenuImagePrint = new JMenuItem();
	private JMenu jMenuZoneTexte=new JMenu();
	private JMenuItem jMenuZoneTexteItem = new JMenuItem();

	private JMenu jMenuEdition = new JMenu();
	private JMenuItem jMenuEditionCopier = new JMenuItem();
	private JMenuItem jMenuEditionColler = new JMenuItem();
	private JMenuItem jMenuEditionCouper = new JMenuItem();
	private JMenuItem jMenuEditionSelect = new JMenuItem();
	
	private JMenu jMenuOptions = new JMenu();
	private JMenuItem jMenuOptionsCouleurCrayon = new JMenuItem();
	private JMenuItem jMenuOptionsCouleurFond = new JMenuItem();
	private JMenuItem jMenuOptionsDemarrage = new JMenuItem();
	private JMenuItem jMenuOptionsPreference = new JMenuItem();
	private JMenuItem jMenuOptionsTraduction = new JMenuItem();
	private JMenuItem jMenuOptionsEraser = new JMenuItem();
	private Popup jpop;

	private JMenu jMenuHelp = new JMenu();
	private JMenuItem jMenuHelpOnLine;
	private JMenuItem jMenuHelpTranslateXLogo=new JMenuItem();
	private JMenuItem jMenuHelpAbout = new JMenuItem();
	private JMenuItem jMenuHelpLicence = new JMenuItem();
	private JMenuItem jMenuHelplicencefrancais = new JMenuItem();


	// Toolbar
	 private MyToolBar toolbar;
//private JSlider slider;
	// Dialog boxes available in menu
	// pref Box
	private Preference pf=null;
	// To define startup files
	private Demarrage dem=null;
	// To erase procedure
	private ProcedureEraser eraser=null;
	// To translate files
	private Traduc traduc=null;
	// To translate XLogo
	private TranslateXLogo tx=null;
	// To display 3D View
	private Viewer3D viewer3d=null;
	
	// Interpreter and drawer
	private Kernel kernel;
	
	
	/** Builds the main frame */
	public Application() {
		kernel=new Kernel(this);
		scrollArea= new JScrollPane();
		ardoise= new DrawPanel(this);
		kernel.initInterprete();
	
		menulistener= new MenuListener(this);
		jpop=new Popup(menulistener, commande);
		editeur=new Editor(this);		 
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		try {
			initGui();
		} catch (Exception e) {
			e.printStackTrace();
		}
	

	}
	/** Init the Gui Component */
	private void initGui() throws Exception {
	/*	java.util.Enumeration en=System.getProperties().keys();
		while(en.hasMoreElements()){
			System.out.println(en.nextElement());
		}
		ff(System.getProperty("java.io.tmpdir"));*/
		this.setText();
		
		setIconImage(Toolkit.getDefaultToolkit().createImage(
		Utils.class.getResource("icone.png")));
		contentPane = (JPanel) this.getContentPane();
		contentPane.setLayout(borderLayout1);
		Dimension d=Toolkit.getDefaultToolkit().getScreenSize();
		this.setSize(new Dimension(d.width,d.height*9/10));
		this.setTitle("XLogo");
		
		jMenuHelpLicence.addActionListener(menulistener);
		jMenuHelpLicence.setActionCommand(MenuListener.HELP_LICENCE);
		jMenuHelpTranslateXLogo.addActionListener(menulistener);
		jMenuHelpTranslateXLogo.setActionCommand(MenuListener.HELP_TRANSLATE_XLOGO);
		jMenuHelpAbout.addActionListener(menulistener);
		jMenuHelpAbout.setActionCommand(MenuListener.HELP_ABOUT);
		jMenuHelplicencefrancais.addActionListener(menulistener);
		jMenuHelplicencefrancais.setActionCommand(MenuListener.HELP_TRANSLATED_LICENCE);
		
		
		commande.setPreferredSize(new Dimension(300,18*Config.police.getSize()/10));
		jPanel1.setLayout(borderLayout2);
		jSplitPane1.setOrientation(JSplitPane.VERTICAL_SPLIT);
		panneauHistorique1.setMinimumSize(new Dimension(600, 40));

		jMenuEditionCopier.addActionListener(menulistener);
		jMenuEditionCopier.setActionCommand(MenuListener.EDIT_COPY);
		jMenuEditionColler.addActionListener(menulistener);
		jMenuEditionColler.setActionCommand(MenuListener.EDIT_PASTE);
		jMenuEditionCouper.addActionListener(menulistener);
		jMenuEditionCouper.setActionCommand(MenuListener.EDIT_CUT);
		jMenuEditionSelect.addActionListener(menulistener);
		jMenuEditionSelect.setActionCommand(MenuListener.EDIT_SELECT_ALL);
		
		
		jMenuFileOuvrir.addActionListener(menulistener);
		jMenuFileOuvrir.setActionCommand(MenuListener.FILE_OPEN);
		jMenuFileNouveau.addActionListener(menulistener);
		jMenuFileNouveau.setActionCommand(MenuListener.FILE_NEW);
		jMenuFileNouveau.setEnabled(false);
		jMenuFileEnregistrer_sous.addActionListener(menulistener);
		jMenuFileEnregistrer_sous.setActionCommand(MenuListener.FILE_SAVE_AS);
		jMenuFileEnregistrer.addActionListener(menulistener);
		jMenuFileEnregistrer.setActionCommand(MenuListener.FILE_SAVE);
		jMenuFileEnregistrer.setEnabled(false);
		jMenuFileQuitter.addActionListener(menulistener);
		jMenuFileQuitter.setActionCommand(MenuListener.FILE_QUIT);

		jMenuImageCopy.addActionListener(menulistener);
		jMenuImageCopy.setActionCommand(MenuListener.FILE_COPY_IMAGE);
		jMenuImageSave.addActionListener(menulistener);
		jMenuImageSave.setActionCommand(MenuListener.FILE_SAVE_IMAGE);
		jMenuImagePrint.addActionListener(menulistener);
		jMenuImagePrint.setActionCommand(MenuListener.FILE_PRINT_IMAGE);
		jMenuZoneTexteItem.addActionListener(menulistener);
		jMenuZoneTexteItem.setActionCommand(MenuListener.FILE_SAVE_TEXT);

		jMenuOptionsDemarrage.addActionListener(menulistener);
		jMenuOptionsDemarrage.setActionCommand(MenuListener.TOOLS_START_FILE);
		jMenuOptionsCouleurCrayon.addActionListener(menulistener);
		jMenuOptionsCouleurCrayon.setActionCommand(MenuListener.TOOLS_PEN_COLOR);
		jMenuOptionsCouleurFond.addActionListener(menulistener);
		jMenuOptionsCouleurFond.setActionCommand(MenuListener.TOOLS_SCREEN_COLOR);
		jMenuOptionsPreference.addActionListener(menulistener);
		jMenuOptionsPreference.setActionCommand(MenuListener.TOOLS_OPTIONS);
		jMenuOptionsTraduction.addActionListener(menulistener);
		jMenuOptionsTraduction.setActionCommand(MenuListener.TOOLS_TRANSLATOR);
		jMenuOptionsEraser.addActionListener(menulistener);
		jMenuOptionsEraser.setActionCommand(MenuListener.TOOLS_ERASER);
		jMenuFile.add(jMenuFileNouveau);
		jMenuFile.add(jMenuFileOuvrir);
		jMenuFile.add(jMenuFileEnregistrer_sous);
		jMenuFile.add(jMenuFileEnregistrer);
		jMenuFile.add(jMenuCapture);
		jMenuZoneTexte.add(jMenuZoneTexteItem);
		jMenuFile.add(jMenuZoneTexte);
		jMenuFile.add(jMenuFileQuitter);
		jMenuHelp.add(jMenuHelpLicence);
		jMenuHelp.add(jMenuHelplicencefrancais);
		jMenuHelp.add(jMenuHelpTranslateXLogo);
		jMenuHelp.add(jMenuHelpAbout);
		jMenuBar1.add(jMenuFile);
		jMenuBar1.add(jMenuEdition);
		jMenuBar1.add(jMenuOptions);
		jMenuBar1.add(jMenuHelp);
		contentPane.add(jPanel1, BorderLayout.NORTH);
		jPanel1.add(jLabel1, BorderLayout.WEST);
		jPanel1.add(commande, BorderLayout.CENTER);
		commande.setAlignmentY(JComponent.CENTER_ALIGNMENT);
/*		slider= new JSlider(JSlider.HORIZONTAL);
		slider.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e) {
			    JSlider source = (JSlider)e.getSource();
			    int value=source.getValue();
			    Config.turtleSpeed=source.getMaximum()-value;			
			}
		});

		slider.setValue(slider.getMaximum()-Config.turtleSpeed);
		//Create the label table
		Hashtable labelTable = new Hashtable();
		labelTable.put( new Integer( 0 ), new JLabel("Slow") );
		labelTable.put( new Integer( 100 ), new JLabel("Fast") );
		slider.setLabelTable( labelTable );
		slider.setPaintLabels(true);
		slider.setMajorTickSpacing(10);
//		slider.setMinorTickSpacing(10);
		slider.setPaintTicks(true);
		jPanel1.add(slider,BorderLayout.EAST);
		*/
		
		
		
		
		contentPane.add(jSplitPane1, BorderLayout.CENTER);
		jSplitPane1.add(scrollArea, JSplitPane.LEFT);
		ardoise.setSize(new java.awt.Dimension(
				(int)(Config.imageWidth*DrawPanel.zoom)
				,(int)(Config.imageHeight*DrawPanel.zoom)));
		scrollArea.getViewport().add(ardoise);
		scrollArea.getHorizontalScrollBar().setBlockIncrement(5);
		scrollArea.getVerticalScrollBar().setBlockIncrement(5);
		jSplitPane1.add(panneauHistorique1, JSplitPane.RIGHT);
		jMenuEdition.add(jMenuEditionCouper);
		jMenuEdition.add(jMenuEditionCopier);
		jMenuEdition.add(jMenuEditionColler);
		jMenuEdition.add(jMenuEditionSelect);
		jMenuCapture.add(jMenuImageCopy);
		jMenuCapture.add(jMenuImageSave);
		jMenuCapture.add(jMenuImagePrint);
		jMenuOptions.add(jMenuOptionsCouleurCrayon);
		jMenuOptions.add(jMenuOptionsCouleurFond);
		jMenuOptions.add(jMenuOptionsDemarrage);
		jMenuOptions.add(jMenuOptionsTraduction);
		jMenuOptions.add(jMenuOptionsEraser);
		jMenuOptions.add(jMenuOptionsPreference);

		// Toolbar
		toolbar=new MyToolBar(menulistener);
		getContentPane().add(toolbar,BorderLayout.EAST);
		
		
		
		MouseListener popupListener = new PopupListener();
		commande.addMouseListener(popupListener);

		this.setJMenuBar(jMenuBar1);
		jSplitPane1.setResizeWeight(0.8);
		commande.addKeyListener(touche);
		calculateMargin();
		jSplitPane1.getTopComponent().addComponentListener(new ComponentAdapter(){
			public void componentResized(ComponentEvent e){
				calculateMargin();
			}
		});
	}
	/**
	 * Called by the constructor or when language has been modified
	 */
	public void setText() {
		//		System.out.println(Config.police.getName());
		setFont(Config.police);
		// Texte interne à utiliser pour JFileChooser et JColorChooser
		UIManager.put("FileChooser.cancelButtonText", Logo.messages
				.getString("pref.cancel"));
		UIManager.put("FileChooser.cancelButtonToolTipText", Logo.messages
				.getString("pref.cancel"));
		UIManager.put("FileChooser.fileNameLabelText", Logo.messages
				.getString("nom_du_fichier"));
		UIManager.put("FileChooser.upFolderToolTipText", Logo.messages
				.getString("dossier_parent"));
		UIManager.put("FileChooser.lookInLabelText", Logo.messages
				.getString("rechercher_dans"));

		UIManager.put("FileChooser.newFolderToolTipText", Logo.messages
				.getString("nouveau_dossier"));
		UIManager.put("FileChooser.homeFolderToolTipText", Logo.messages
				.getString("repertoire_accueil"));
		UIManager.put("FileChooser.filesOfTypeLabelText", Logo.messages
				.getString("fichier_du_type"));
		UIManager.put("FileChooser.helpButtonText", Logo.messages
				.getString("menu.help"));

		UIManager.put("ColorChooser.rgbNameText", Logo.messages
				.getString("rgb"));
		UIManager.put("ColorChooser.rgbBlueText", Logo.messages
				.getString("bleu"));
		UIManager.put("ColorChooser.rgbGreenText", Logo.messages
				.getString("vert"));
		UIManager.put("ColorChooser.rgbRedText", Logo.messages
				.getString("rouge"));

		UIManager.put("ColorChooser.swatchesNameText", Logo.messages
				.getString("echantillon"));

		UIManager.put("ColorChooser.hsbNameText", Logo.messages
				.getString("hsb"));
		UIManager.put("ColorChooser.hsbBlueText", Logo.messages
				.getString("hsbbleu"));
		UIManager.put("ColorChooser.hsbGreenText", Logo.messages
				.getString("hsbvert"));
		UIManager.put("ColorChooser.hsbRedText", Logo.messages
				.getString("hsbrouge"));

		UIManager.put("ColorChooser.swatchesRecentText", Logo.messages
				.getString("dernier"));
		UIManager.put("ColorChooser.previewText", Logo.messages
				.getString("apercu"));
		UIManager.put("ColorChooser.sampleText", Logo.messages
				.getString("echantillon_texte"));
		UIManager.put("ColorChooser.okText", Logo.messages.getString("pref.ok"));
		UIManager.put("ColorChooser.resetText", Logo.messages
				.getString("restaurer"));
		UIManager.put("ColorChooser.cancelText", Logo.messages
				.getString("pref.cancel"));
		UIManager.put("ColorChooser.previewText", Logo.messages
				.getString("apercu"));

		/*
		 * UIManager.put("Menu.font", Config.police);
		 * UIManager.put("MenuItem.font", Config.police);
		 * 
		 * UIManager.put("Label.font",new
		 * javax.swing.plaf.FontUIResource(Config.police));
		 * UIManager.put("Button.font", new
		 * javax.swing.plaf.FontUIResource(Config.police));
		 * UIManager.put("OptionPane.font", new
		 * javax.swing.plaf.FontUIResource(Config.police));
		 * 
		 * UIManager.put("TextArea.font", Config.police);
		 * UIManager.put("ComboBox.font", Config.police);
		 * UIManager.put("List.font", Config.police);
		 * UIManager.put("CheckBox.font", Config.police);
		 * UIManager.put("TabbedPane.font", new
		 * javax.swing.plaf.FontUIResource(Config.police));
		 * UIManager.put("TabbedPane.Font", new
		 * javax.swing.plaf.FontUIResource(Config.police));
		 * UIManager.put("TabbedPane.tabFont", new
		 * javax.swing.plaf.FontUIResource(Config.police));
		 */
		UIManager.put("ColorChooser.font", new javax.swing.plaf.FontUIResource(
				Config.police));
		//		System.out.println(UIManager.get("ColorChooser.font"));
		// On change la police et le texte de tous les composants texte
		UIManager.put("OptionPane.buttonFont", Config.police);
		commande.setFont(Config.police);
		jMenuFile.setFont(Config.police);
		jMenuFileEnregistrer_sous.setFont(Config.police);
		jMenuFileNouveau.setFont(Config.police);
		jMenuHelp.setFont(Config.police);
		jMenuHelpLicence.setFont(Config.police);
		jMenuHelpAbout.setFont(Config.police);
		if (null==jMenuHelpOnLine) jMenuHelpOnLine=new JMenuItem();
		else {
			jMenuHelp.remove(jMenuHelpOnLine);
			jMenuHelpOnLine=new JMenuItem();
		}
		jMenuHelpOnLine.setFont(Config.police);
		jMenuHelpTranslateXLogo.setFont(Config.police);
		jMenuHelplicencefrancais.setFont(Config.police);
		jLabel1.setFont(Config.police);
		jMenuEdition.setFont(Config.police);
		jMenuEditionCopier.setFont(Config.police);
		jMenuEditionColler.setFont(Config.police);
		jMenuEditionCouper.setFont(Config.police);
		jMenuEditionSelect.setFont(Config.police);
		jMenuFileOuvrir.setFont(Config.police);
		jMenuFileEnregistrer.setFont(Config.police);
		jMenuFileQuitter.setFont(Config.police);
		jMenuCapture.setFont(Config.police);
		jMenuZoneTexte.setFont(Config.police);
		jMenuZoneTexteItem.setFont(Config.police);
		jMenuImageCopy.setFont(Config.police);
		jMenuImageSave.setFont(Config.police);
		jMenuImagePrint.setFont(Config.police);
		jMenuOptions.setFont(Config.police);
		jMenuOptionsDemarrage.setFont(Config.police);
		jMenuOptionsCouleurCrayon.setFont(Config.police);
		jMenuOptionsCouleurFond.setFont(Config.police);
		jMenuOptionsPreference.setFont(Config.police);
		jMenuOptionsTraduction.setFont(Config.police);
		jMenuOptionsEraser.setFont(Config.police);
		
		this.panneauHistorique1.changeFont(Config.police);
		editeur.setEditorFont(Config.police);

		jMenuFile.setText(Logo.messages.getString("menu.file"));
		jMenuFileEnregistrer_sous.setText(Logo.messages
				.getString("menu.file.saveas")+"...");
		jMenuFileNouveau.setText(Logo.messages.getString("nouveau"));
		jMenuHelp.setText(Logo.messages.getString("menu.help"));
		jMenuHelpLicence.setText(Logo.messages.getString("menu.help.licence"));
		jMenuHelpAbout.setText(Logo.messages.getString("menu.help.about"));
		jMenuHelpOnLine.setText(Logo.messages.getString("menu.help.online"));
		jMenuHelpTranslateXLogo.setText(Logo.messages.getString("menu.help.translatexlogo"));
		jMenuHelplicencefrancais.setText(Logo.messages
				.getString("menu.help.translatedlicence"));
		jLabel1.setText(Logo.messages.getString("commande") + "  ");
		jMenuEdition.setText(Logo.messages.getString("menu.edition"));
		jMenuEditionCopier.setText(Logo.messages.getString("menu.edition.copy"));
		jMenuEditionColler.setText(Logo.messages.getString("menu.edition.paste"));
		jMenuEditionCouper.setText(Logo.messages.getString("menu.edition.cut"));
		jMenuEditionSelect.setText(Logo.messages.getString("menu.edition.selectall"));
		jMenuFileOuvrir.setText(Logo.messages.getString("menu.file.open")+"...");
		jMenuFileEnregistrer.setText(Logo.messages.getString("menu.file.save"));
		jMenuFileQuitter.setText(Logo.messages.getString("menu.file.quit"));
		jMenuCapture.setText(Logo.messages.getString("menu.file.captureimage"));
		jMenuZoneTexte.setText(Logo.messages.getString("menu.file.textzone"));
		jMenuZoneTexteItem.setText(Logo.messages.getString("menu.file.textzone.rtf"));		
		jMenuImageCopy.setText(Logo.messages.getString("menu.file.captureimage.clipboard"));
		jMenuImageSave.setText(Logo.messages.getString("menu.file.saveas")+"...");
		jMenuImagePrint.setText(Logo.messages.getString("menu.file.captureimage.print"));
		jMenuOptions.setText(Logo.messages.getString("menu.tools"));
		jMenuOptionsDemarrage.setText(Logo.messages
				.getString("menu.tools.startup"));
		jMenuOptionsCouleurCrayon.setText(Logo.messages
				.getString("menu.tools.pencolor"));
		jMenuOptionsCouleurFond.setText(Logo.messages.getString("menu.tools.screencolor"));
		jMenuOptionsTraduction.setText(Logo.messages
				.getString("menu.tools.translate"));
		jMenuOptionsEraser.setText(Logo.messages
				.getString("menu.tools.eraser"));
		jMenuOptionsPreference.setText(Logo.messages.getString("menu.tools.preferences"));
		
		panneauHistorique1.updateText();
		
		jpop.setText();
		
		// Find the HelpSet file and create the HelpSet object:
		  HelpSet hs=null;
		   try {
			   String url="http://downloads.tuxfamily.org/xlogo/downloads-";
			   url+=Logo.getLocaleTwoLetters();
			   url+="/javahelp/manual-";
			   url+=Logo.getLocaleTwoLetters();
			   url+=".hs";
		      java.net.URL hsURL = new java.net.URL(url);
			   hs = new HelpSet(null, hsURL);
				// Create a HelpBroker object:
			   HelpBroker hb = hs.createHelpBroker();
			// Create a "Help" menu item to trigger the help viewer:
			   jMenuHelpOnLine.addActionListener(new CSH.DisplayHelpFromSource( hb ));
		   } catch (Exception ee) {
		      // Say what the exception really is
		      System.out.println( "HelpSet " + ee.getMessage());
		      if (null!=hs)  System.out.println("HelpSet "+ hs.getHelpSetURL()+" not found");
		   }
			jMenuHelp.insert(jMenuHelpOnLine,0);
		
		
		
		//		JColorChooser jc=new JColorChooser();
		//		affiche_nom(jc);
	}

	/*
	 * void affiche_nom(Component jc){ jc.setFont(Config.police);
	 * System.out.println(jc.getName()); if (jc instanceof Container){ int
	 * nb=((Container)jc).getComponentCount(); for (int i=0;i <nb;i++){
	 * Component jcbis=((Container)jc).getComponent(i);
	 * System.out.println(jcbis.getFont()); affiche_nom(jcbis); } } }
	 */
	/** Close the window */	
	protected void processWindowEvent(WindowEvent e) {
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			closeWindow();
		} else {
			super.processWindowEvent(e);
		}
	}
	/**
	 * When the frame is closed, show a Confirmation box
	 */
	public void closeWindow(){
    	setVisible(true);
    	String message=Logo.messages.getString("quitter?");
		MyTextAreaDialog jt=new MyTextAreaDialog(message);
		ImageIcon icone=new ImageIcon(Utils.class.getResource("icone.png"));
		String[] choix={Logo.messages.getString("pref.ok"),Logo.messages.getString("pref.cancel")};
		int retval=JOptionPane.showOptionDialog(this,
				jt,Logo.messages.getString("menu.file.quit"),JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE,icone,choix,choix[0]);	
      if (retval==JOptionPane.OK_OPTION){
      	// On écrit le fichier de configuration
        ecris_config();
        System.exit(0);
      }

		
	}

	/**
	 * Write the Configuration file when the user quits XLogo 
	 */ 
	//Ecrire le fichier de configuration lorsque l'on quitte XLOGO
	private void ecris_config() {
		try{
	        FileOutputStream f = new FileOutputStream(System.getProperty("user.home")+File.separator+".xlogo");
	        BufferedOutputStream b = new BufferedOutputStream(f);	
			OutputStreamWriter osw = new  OutputStreamWriter(b,  "UTF8");		
			StringBuffer sb =new StringBuffer(); 
			int eff = 0;
			if (Config.eraseImage)
				eff = 1;
			else
				eff = 0;
			sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
			sb.append("<xlogo>\n");
			sb.append("\t<lang value=\"");
			sb.append(Config.langage);
			sb.append("\"/>\n");
			sb.append("\t<speed value=\"");
			sb.append(Config.turtleSpeed);
			sb.append("\"/>\n");
			sb.append("\t<tcp_port value=\"");
			sb.append(Config.TCP_PORT);
			sb.append("\"/>\n");
			sb.append("\t<turtle_shape value=\"");
			sb.append(Config.activeTurtle);
			sb.append("\"/>\n");
			sb.append("\t<max_number_turtle value=\"");
			sb.append(Config.maxTurtles);
			sb.append("\"/>\n");
			sb.append("\t<pen_shape value=\"");
			sb.append(Config.penShape);
			sb.append("\"/>\n");
			sb.append("\t<cleanscreen_leaving_editor value=\"");
			sb.append(eff);
			sb.append("\"/>\n");
			sb.append("\t<pen_width_max value=\"");
			sb.append(Config.maxPenWidth);
			sb.append("\"/>\n");
			sb.append("\t<pen_color value=\"");
			sb.append(Config.pencolor.getRGB());
			sb.append("\"/>\n");
			sb.append("\t<screen_color value=\"");
			sb.append(Config.screencolor.getRGB());
			sb.append("\"/>\n");
			sb.append("\t<default_directory value=\"");
			sb.append(Config.defaultFolder);
			sb.append("\"/>\n");
			sb.append("\t<start_command value=\"");
			sb.append(Utils.specialCharacterXML(Config.a_executer));
			sb.append("\"/>\n");
			sb.append("\t<font name=\"");
			sb.append(Config.police.getName());
			sb.append("\" size=\"");
			sb.append(Config.police.getSize());
			sb.append("\"/>\n");
			sb.append("\t<width value=\"");
			sb.append(Config.imageWidth);
			sb.append("\"/>\n");
			sb.append("\t<height value=\"");
			sb.append(Config.imageHeight);
			sb.append("\"/>\n");
			sb.append("\t<memory value=\"");
			sb.append(Config.tmp_memoire);
			sb.append("\"/>\n");
			sb.append("\t<quality value=\"");
			sb.append(Config.quality);
			sb.append("\"/>\n");
			sb.append("\t<looknfeel value=\"");
			sb.append(Config.looknfeel);
			sb.append("\"/>\n");
			sb.append("\t<syntax_highlighting\n\t\tboolean=\"");
			sb.append(Config.COLOR_ENABLED);
			sb.append("\"\n\t\tcolor_commentaire=\"");
			sb.append(Config.coloration_commentaire);
			sb.append("\"\n\t\tcolor_operand=\"");
			sb.append(Config.coloration_operande);
			sb.append("\"\n\t\tcolor_parenthesis=\"");
			sb.append(Config.coloration_parenthese);
			sb.append("\"\n\t\tcolor_primitive=\"");
			sb.append(Config.coloration_primitive);
			sb.append("\"\n\t\tstyle_commentaire=\"");
			sb.append(Config.style_commentaire);
			sb.append("\"\n\t\tstyle_operand=\"");
			sb.append(Config.style_operande);
			sb.append("\"\n\t\tstyle_parenthesis=\"");
			sb.append(Config.style_parenthese);
			sb.append("\"\n\t\tstyle_primitive=\"");
			sb.append(Config.style_primitive);
			sb.append("\">\n\t</syntax_highlighting>\n\t<border_image\n");
			for(int i=0;i<Config.borderExternalImage.size();i++){
				sb.append("\t\timage"+i+"=\"");
				sb.append(Config.borderExternalImage.get(i));
				sb.append("\"\n");
			}
			if (null!=Config.borderColor){
				sb.append(">\n\t</border_image>\n\t<border_color\n\t\tvalue=\"");
				sb.append(Config.borderColor.getRGB());
				sb.append("\">\n\t</border_color>\n\t<border_image_selected\n\t\tvalue=\"");				
			}
			else sb.append(">\n\t</border_image>\n\t<border_image_selected\n\t\tvalue=\"");
			sb.append(Config.borderImageSelected);
			sb.append("\">\n\t</border_image_selected>\n\t<grid\n\t\tboolean=\"");
			sb.append(Config.drawGrid);
			sb.append("\"\n\t\txgrid=\"");
			sb.append(Config.XGrid);
			sb.append("\"\n\t\tygrid=\"");
			sb.append(Config.YGrid);
			sb.append("\"\n\t\tgridcolor=\"");
			sb.append(Config.gridColor);
			sb.append("\">\n\t</grid>\n\t<axis\n\t\tboolean_xaxis=\"");
			sb.append(Config.drawXAxis);
			sb.append("\"\n\t\tboolean_yaxis=\"");
			sb.append(Config.drawYAxis);
			sb.append("\"\n\t\txaxis=\"");
			sb.append(Config.XAxis);
			sb.append("\"\n\t\tyaxis=\"");
			sb.append(Config.YAxis);
			sb.append("\"\n\t\taxiscolor=\"");
			sb.append(Config.axisColor);
			sb.append("\">\n\t</axis>\n\t<startup_files\n");
			int i=0;
			while (!Config.path.isEmpty()) {
				String att="\t\tfile"+i+"=\"";
				sb.append(att);
				sb.append(Config.path.remove(Config.path.size()-1));
				sb.append("\"\n");
				i++;
			}
			sb.append(">\n\t</startup_files>\n");
			sb.append("</xlogo>");
			osw.write(new String(sb));
		//	System.out.println(sb);
			osw.close();
			b.close();
			f.close();
		}
		catch(IOException e){System.out.println("write error");}
/*		try {
			FileWriter file = new FileWriter(System.getProperty("user.home")
					+ File.separator + ".xlogo");
			BufferedWriter bfile = new BufferedWriter(file);
			int eff = 0;
			if (Logo.effacer_dessin)
				eff = 1;
			else
				eff = 0;
			String st = "# langue\n" + Config.langage + "\n# vitesse\n"
					+ Logo.vitesse + "\n# tortue choisie\n"
					+ Logo.tortue_choisie + "\n# nb max de tortues\n"
					+ Logo.tortues + "\n# forme crayon\n" + Logo.forme_crayon
					+ "\n# effacer dessin en quittant editeur\n" + eff
					+ "\n# epaisseur max crayon\n" + Logo.epaisseur_max
					+ "\n# repertoire par defaut\n" + Utils.SortieTexte(Logo.repertoire_defaut)
					+ "\n# a executer au demarrage\n" + Config.a_executer
					+ "\n# police\n" + Config.police.getName() + "\n"
					+ Config.police.getSize() + "\n# largeur\n"
					+ Logo.largeur_dessin + "\n# hauteur\n"
					+ Logo.hauteur_dessin + "\n# memoire\n"+Config.tmp_memoire
					+"\n# qualite\n"+Logo.quality+"\n# coloration\n"+Config.COLOR_ENABLED+" "+Config.coloration_commentaire+" "+
					Config.coloration_operande+" "+Config.coloration_parenthese+" "+Config.coloration_primitive+" "
					+Config.style_commentaire+" "+Config.style_operande+" "+Config.style_parenthese+" "+Config.style_primitive
					+"\n# fichiers de demarrage\n";
			while (!Config.path.isEmpty()) {
				st += Config.path.pop().toString() + "\n";
			}
			st += "# fin";

			bfile.write(st);
			bfile.close();
		} catch (Exception ex) {
		}*/

	}

	// Ce qu'il se passe en validant dans la zone de texte
	/**
	 * When the user types "Enter" in the Command Line
	 */
	public void commande_actionPerformed() {
	//	System.out.println("commandeTotal :"+Runtime.getRuntime().totalMemory()/1024/1024+" Free "+Runtime.getRuntime().freeMemory()/1024/1024);
		// Si une parenthese était sélectionnée, on désactive la décoloration
		commande.setActive(false);
	//	System.out.println(commande.getCaret().isVisible());
		if (stop)
			stop = false;
		String texte = commande.getText();
		if (!texte.equals("") && commande.isEditable()) {
			if (touche.tape) {
				touche.tape = false;
				pile_historique.pop();
			}
			if (pile_historique.size() > 49)
				pile_historique.remove(0);
			pile_historique.push(texte); // On rajoute ce texte à l'historique
			index_historique = pile_historique.size(); //on réajuste l'index de
			// l'historique

			panneauHistorique1.ecris("normal", texte + "\n");
			
			// On enlève les éventuels commentaires
			int a =texte.indexOf("#");
			while (a!=-1){
				if (a==0) {texte="";break;}
				else if (!texte.substring(a-1,a).equals("\\")){
					texte=texte.substring(0,a);
					break;
				} 
				a =texte.indexOf("#",a+1);
			}
	//	Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
			affichage_Start(Utils.decoupe(texte));

			
			
			// On efface la ligne de commande
			SwingUtilities.invokeLater(new Runnable(){
				public void run(){
					commande.setText("");					
				}
			});

		}
	}
	/**
	 * Launch the Thread Affichage with the instructions "st"
	 * @param st List of instructions
	 */
	public void affichage_Start(StringBuffer st){
		affichage = new Affichage(this,st );
		affichage.start();		
	}
	/**
	 * Get Method for Sound Player
	 * @return The Sound Player
	 */
	public Sound_Player getSon(){
		return son;
	}
	/**
	 * Close the preference dialog box
	 */
	public void close_Preference(){
		pf=null;
	}
	public void close_TranslateXLogo(){
		tx=null;	
	}
	// change language for the interface
	// change la langue de l'interface
/**
 * Modify the language for the interface
 * @param id The integer representing the choosen language
 */

	public void changeLanguage(int id){
		Config.langage = id;
		Logo.generateLanguage(id);
		setText();
		kernel.buildPrimitiveTreemap(id);
		editeur = new Editor(this);
		panneauHistorique1.changeLanguage();
		if (null!=viewer3d) viewer3d.setText();
	}
	// Change font for the interface
	// Change la police de l'interface
	/**
	 * Modify the font for the main Interface
	 * @param font The choosen Font
	 * @param size The font size
	 */
	public void changeFont(Font font,int size){
		Config.police = font;
		setText();
		panneauHistorique1.getDsd().change_police_interface(font, size);
	}
	/**
	 * Resize the dawing area
	 */
	public void resizeDrawingZone(){
		// resize the drawing image
		DrawPanel.dessin=new BufferedImage(Config.imageWidth,Config.imageHeight,BufferedImage.TYPE_INT_RGB);
		 // 	System.out.println("Total :"+Runtime.getRuntime().totalMemory()/1024+" max "+Runtime.getRuntime().maxMemory()/1024+" Free "+Runtime.getRuntime().freeMemory()/1024);
		MediaTracker tracker=new MediaTracker(ardoise);
		tracker.addImage(DrawPanel.dessin,0);
		try{tracker.waitForID(0);}
		catch(InterruptedException e){}
	//    ardoise1.getGraphics().drawImage(Ardoise.dessin,0,0,ardoise1);
	    ardoise.setPreferredSize(new Dimension(Config.imageWidth,Config.imageHeight));
	    ardoise.revalidate();
	    kernel.initGraphics();
	    //ardoise.repaint();
		calculateMargin();

		
	    Dimension d=scrollArea.getViewport().getViewRect().getSize();
	    Point p=new Point(Math.abs(Config.imageWidth/2-d.width/2),Math.abs(Config.imageHeight/2-d.height/2));
	    scrollArea.getViewport().setViewPosition(p);  	
	}
	/**
	 * Modify the Look&Feel for the Application
	 * @throws Exception
	 */
	public void changeLookNFeel() throws Exception {
		SwingUtilities.updateComponentTreeUI(this);
		SwingUtilities.updateComponentTreeUI(editeur);
	}
/**
 * 	Change Syntax Highlighting for the editor, 
 * 	 the command line and the History zone
 * @param c_comment Comment Color
 * @param sty_comment Comment style
 * @param c_primitive  Primitive Color
 * @param sty_primitive Primitive style
 * @param c_parenthese Parenthesis Color
 * @param sty_parenthese Parenthesis Style
 * @param c_operande Operand Color
 * @param sty_operande Operand style
 */
	public void changeSyntaxHighlightingStyle(int c_comment,int sty_comment,int c_primitive,int sty_primitive,
			int c_parenthese, int sty_parenthese, int c_operande, int sty_operande){
		editeur.initStyles(Config.coloration_commentaire,Config.style_commentaire,Config.coloration_primitive,Config.style_primitive,
				Config.coloration_parenthese,Config.style_parenthese,Config.coloration_operande,Config.style_operande);
		commande.initStyles(Config.coloration_commentaire,Config.style_commentaire,Config.coloration_primitive,Config.style_primitive,
				Config.coloration_parenthese,Config.style_parenthese,Config.coloration_operande,Config.style_operande);
		panneauHistorique1.getDsd().initStyles(Config.coloration_commentaire,Config.style_commentaire,Config.coloration_primitive,Config.style_primitive,
				Config.coloration_parenthese,Config.style_parenthese,Config.coloration_operande,Config.style_operande);
	}

	/**
	 * Enable or disable Syntax Highlighting
	 */
	public void setColoration(boolean b){
		panneauHistorique1.setColoration(b);
		commande.setColoration(b);
	}
	/**
	 * Resize the Command line (height)
	 */
	public void resizeCommande(){
		commande.setPreferredSize(new Dimension(300,Config.police.getSize()*18/10));
	}
	/**
	 * Return the drawing area
	 * @return The drawing area
	 */
	public DrawPanel getArdoise(){
		return ardoise;
	}  
	/**
	 * Write in the History Panel
	 * @param sty This String represents the style for writing.<br>
	 * The possibilities are "normal", "erreur", "commentaire" or "perso"<br>
	 * - normal: classic style<br>
	 * - erreur: Red, when an error occured<br>
	 * - commentaire: blue, when the user leaves the editor<br>
	 * - perso: To write with the primtive "write" or "print"
	 * @param txt The text to write
	 */
	public void ecris(String sty,String txt){
		panneauHistorique1.ecris(sty,txt);
	}
	/**
	 * Set Focus on the command line
	 */
	public void focus_Commande(){
		commande.requestFocus();
		commande.getCaret().setVisible(true);
	}
	/**
	 * Set the Menu "Save File" enable or disable
	 * @param b true or false 
	 */
	public void setEnabled_Record(boolean b){
		jMenuFileEnregistrer.setEnabled(true);
	}
	/**
	 * Set the Menu "File New" enable or disable
	 * @param b true or false 
	 */
	public void setEnabled_New(boolean b){
		jMenuFileNouveau.setEnabled(b);
	}
	/**
	 * Notice if the menu File-New is enabled.
	 * @return true if Menu File-New is enabled, false otherwise
	 */
	public boolean isEnabled_new(){
		return jMenuFileNouveau.isEnabled();
	}
	/**
	 * Notice if the command line is editable.
	 * @return true if Command Line is editable, false otherwise
	 */
	public boolean commande_isEditable(){
		return commande.isEditable();
	}
	/**
	 * Set the text in the command Line
	 * @param txt The text to write
	 */
	public void setCommandText(String txt){
		commande.setText(txt);
	}
	/**
	 * Get History panel
	 * @return The HistoryPanel
	 */
	public HistoryPanel getHistoryPanel(){
		return panneauHistorique1;
	}

	/**
	 *  Enable or disable the command line and the play button
	 * @param b The boolean true: enable command line, false: disable
	 */
	public void setCommandLine(boolean b){
		if (b) {
			if (SwingUtilities.isEventDispatchThread()){
				commande.setEditable(true);
				commande.setBackground(Color.WHITE);
				toolbar.enabledPlay(true);
			}
			else {
				SwingUtilities.invokeLater(new Runnable() {
					   public void run() {
						   toolbar.enabledPlay(true);
						   commande.setEditable(true);				
//						   commande.requestFocus();
							commande.setBackground(Color.WHITE);
					   }
					  });	
			}
		}
		else {
			toolbar.enabledPlay(false);
			commande.setEditable(false); 
			commande.setBackground(new Color(250, 232, 217));
		}
	}
	/**
	 * This methos copy the selected Text in the command line
	 */
	protected void copy(){
		commande.copy();
	}
	/**
	 * This methos cut the selected Text in the command line
	 */
	protected void cut(){
		commande.cut();
	}
	/**
	 * This methos paste the selected Text into the command line
	 */
	protected void paste(){
		commande.paste();
	}
	/**
	 * This methos  selects all the  Text in the command line
	 */
	protected void select_all(){
		commande.selectAll();
	}
	/**
	 * This method creates all primitives and loads the startup files
	 */
	 protected void genere_primitive(){
	 	kernel.initPrimitive();
	    // Générer les procédures du path
	    Stack<String> tampon=new Stack<String>();
		editeur.setAffichable(false);
	    int compteur=Config.path.size();

	    for (int i=0;i<compteur;i++){
	      String txt="";
			if (Config.path.get(i).equals("#####")) editeur.setAffichable(true); //on a terminé avec les fichiers de démarrage
	    else {
			try{
				txt=Utils.readLogoFile(Config.path.get(i));
			  	if (!editeur.getAffichable()) tampon.push(Config.path.get(i)); 
			  }
			  catch(IOException e2){System.out.println("Problème de lecture du fichier");}
			  catch(Exception e3){}
			editeur.setEditorStyledText(txt);
			try {
			  editeur.analyseprocedure();
			}
			catch(Exception e3){
				System.out.println(e3.toString());
			}
			editeur.clearText();
			editeur.setVisible(false);
		    }
	    }
	  
	  Config.path=new ArrayList<String>(tampon); //On ne garde dans le path que les fichiers de démarrage
	  editeur.setAffichable(true);
	  editeur.clearText();
	 }	
	 /**
	  * Set the last key pressed to the key corresponding to integer i
	  * @param i The key code
	  */
	 public void setCar(int i){
	 	touche.setCar(i);
	 } 
	 /**
	  * Returns an int that corresponds to the last key pressed.
	  * @return the int representing the last key pressed
	  */
	 public int getCar(){
	 	return touche.getCar();
	 }
	 private Application getApp(){
		 return(this);
	 }
	 /**
	  * Open the Preferences Dialog box
	  */
	 protected void prefOpen(){
	 	if (null==pf) {
			  SwingUtilities.invokeLater(new Runnable(){
				  public void run(){
						pf=new Preference(getApp());
						pf.setBounds(100,100,600,580);
						pf.setVisible(true);
				  } 
			  });
			}
		else pf.requestFocus();
	 } 
	 /**
	  * Open the TranslateXLogo Dialog box
	  */
	 protected void txOpen(){
		 	if (null==tx) {
				tx=new TranslateXLogo(this);
				tx.setBounds(100,100,600,300);
				tx.setVisible(true);
			}
			else tx.requestFocus();
	 }
	 /**
	  * Open the Startup Files Dialog box
	  */
	 protected void demOpen(){
	  	if (null==dem||!dem.isVisible()){
	        dem=new Demarrage(this);
	        dem.setBounds(100,100,400,250);
	        dem.setVisible(true);
	      }
	      else dem.requestFocus();
	 }	
	 
	/**
	 * This boolean indicates if the viewer3D is visible
	 * @return true or false
	 */
	public boolean viewer3DVisible(){
		if (null!=viewer3d)	return viewer3d.isVisible();
		return false;
	}
	 /**
	  * Initialize the 3D Viewer
	  */
	 public void initViewer3D(){
		if (null==viewer3d){
		    viewer3d=new Viewer3D(ardoise.getWorld3D(),ardoise.getBackgroundColor());
		  }
		 
	 }
	 public Viewer3D getViewer3D(){
		 return viewer3d;
	 }  
	 /**
	  * Open the Viewer3D Frame
	  */
	 public void viewerOpen(){
			if (null==viewer3d){
			    viewer3d=new Viewer3D(ardoise.getWorld3D(),ardoise.getBackgroundColor());
			   }
			   else {
				   viewer3d.setVisible(false);
			   }
			 viewer3d.insertBranch();
			viewer3d.setVisible(true);
			  viewer3d.requestFocus();
	 }
	 
	 /**
	  * Open the Tranlator tool Dialog box
	  */
	 protected void traducOpen(){
		if (null==traduc||!traduc.isVisible()){
		      traduc=new Traduc();
		      traduc.setVisible(true);
		    }
		    else {
		    	traduc.setVisible(false);
		    	traduc.setVisible(true);
	      	}
	 }
	 /**
	  * Open the ProcedureEraser Dialog box
	  */
	 protected void eraserOpen(){
	 	if (null==eraser||!eraser.isVisible()){
  			eraser=new ProcedureEraser(this);
  			eraser.setBounds(100,100,300,200);
  			eraser.setVisible(true);
  	}
  	else eraser.requestFocus();
	}
	 /**
	  * Update the ProcedureEraser Dialog box
	  */
	public void eraserUpdate(){
	 	if (null!=eraser&&eraser.isVisible()){
	 		eraser.dispose();
  			eraser=new ProcedureEraser(this);
  			eraser.setBounds(100,100,300,200);
  			eraser.setVisible(true);
	 	}
	}
	 /**
	  * Returns the current kernel
	  * @return The Kernel Object associated to main frame
	  */
	 public Kernel getKernel(){
	 	return kernel;
	 }
	 int horizontalMargin=15;
	 int verticalMargin=15;
	 /**
	  * This method  calculate the margin for the jScrollPane
	 	* and the border motif
	  */
	 public void calculateMargin(){
		 horizontalMargin=15;
		 verticalMargin=15;
		 Component top=jSplitPane1.getTopComponent();
		 int width=top.getWidth();
		 int height=top.getHeight();
		 int panelWidth=(int)(Config.imageWidth*DrawPanel.zoom);
		 int panelHeight=(int)(Config.imageHeight*DrawPanel.zoom);
	//	 System.out.println("imagePanel "+panelWidth+" "+panelHeight);
	//	 System.out.println("Swing component "+width+" "+height);
		 if (width>panelWidth+30){
					 horizontalMargin=(width-panelWidth)/2;
		 }
		 if (height>panelHeight+30){
				 verticalMargin=(height-panelHeight)/2;
		 }
		 if (null!=Config.borderColor){
			 SwingUtilities.invokeLater(new Runnable(){
				 public void run(){
					 scrollArea.setBorder(
							 BorderFactory.createMatteBorder(verticalMargin, horizontalMargin, 
									 verticalMargin, horizontalMargin, Config.borderColor));					 
				 }
			 });

		 }
		 else {
			 SwingUtilities.invokeLater(new Runnable(){
				 public void run(){
					 String name=Config.borderImageSelected;
					 ImageIcon icon=null;
					 if (Config.searchInternalImage(name)!=-1) icon=new ImageIcon(Utils.dimensionne_image(name,ardoise));
					 else if (Config.borderExternalImage.contains(name)) {
						 if (Utils.fileExists(name)) icon=new ImageIcon(name); 
						 else {
							 Config.borderImageSelected="background.png";
							 icon=new ImageIcon(Utils.dimensionne_image("background.png",ardoise));
						 }					 
				 }
				scrollArea.setBorder(
			 				 BorderFactory.createMatteBorder(verticalMargin,horizontalMargin,verticalMargin,horizontalMargin,icon));

				 }
			 
			 }
			 );
	 }
		 }
/**
 * 
 * @author loic
 * This class is the Controller for the Command Line<br>
 * It looks for key event, Upper and Lower Arrow for History<br>
 * And all other Characters 
 */
	 class Touche extends KeyAdapter {
		int car = -1;

		private boolean tape = false;
		public void setCar(int i){
			car=i;
		}
		public int getCar(){
		return car;	
		}
		public void keyPressed(KeyEvent e) {
			int ch = e.getKeyChar();
			int code =e.getKeyCode();
			if (commande.isEditable()) {
				if (code == KeyEvent.VK_UP) {
					if (index_historique > 0) {
						if (index_historique == pile_historique.size()) {
							tape = true;
							pile_historique.push(commande.getText());
						}
						index_historique--;
						commande.setText(pile_historique.get(index_historique));
					} else
						index_historique = 0;
				}
				else if (code == KeyEvent.VK_DOWN) {
					if (index_historique < pile_historique.size() - 1) {
						index_historique++;
						commande.setText(pile_historique.get(index_historique));
					} else
						index_historique = pile_historique.size() - 1;
				}
			} else {
				if (ch!=65535)	car = ch;
				else car=-code;
			}
		}
	}
	 /**
	  * Enables or disables the zoom buttons
	  * @param b The boolean  
	  */
	public void setZoomEnabled(boolean b){
		toolbar.setZoomEnabled(b);
	}
	 /**
	  * The Mouse popup Menu
	  * @author loic
	  *
	  */
	 
	class PopupListener extends MouseAdapter {

		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}

		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e);
		}

		private void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				jpop.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

}
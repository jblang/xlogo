/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo 
 * 						programming language
 * @author Loïc Le Coq
 */
package xlogo;
import java.util.StringTokenizer;
import java.io.File;

import javax.media.j3d.VirtualUniverse;
import javax.swing.UIManager;
import javax.swing.SwingUtilities;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.SAXException;

import java.awt.*;
import java.io.*;
import java.awt.image.BufferedImage;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Calendar;

import xlogo.utils.SimpleContentHandler;
import xlogo.gui.Selection_Langue;
import xlogo.utils.Utils;
import xlogo.kernel.DrawPanel;
import xlogo.kernel.Affichage;
/**
 * This class initializes the main frame, loads startup files and launches startup command
 * 
 * @author loic
 *
 */
public class Logo {
	  /**
	   * This ResourceBundle contains all messages for XLogo (menu, errors...)
	   */
	  public static ResourceBundle messages=null;
	  
	  
	public static String translationLanguage[]=new String[10];
  public static final String englishLanguage[] = { "French","English","Arabic","Spanish","Portuguese","Esperanto","German",
			"Galician","Asturian","Greek"};
  public static final String[] locales={"fr","en","ar","es","pt","eo","de","gl","as","el"};
	/**
	 * The main frame
	 */
  private Application frame=null;

  /**
   * On the first start, XLogo opens a dialog box where the user can select its language.
   */
  private Selection_Langue select=null;
  
//  private Language language;
  
  /**Builds Application with the valid Config*/
  public Logo() {
	  // Read the XML file .xlogo and extract default config
    readConfig();
    
    // Overwrite loaded config with command line arguments
    readCommandLineConfig();
    
    Config.defaultFolder=Utils.rajoute_backslash(Config.defaultFolder);
    if (null==messages)  generateLanguage(Config.langage); //Au cas où si le fichier de démarrage ne contient rien sur la langue
    // Initialize frame
    SwingUtilities.invokeLater(new Runnable(){
    	public void run(){
    		DrawPanel.dessin=new BufferedImage(Config.imageWidth,Config.imageHeight,BufferedImage.TYPE_INT_RGB);
    		frame = new Application();

    		frame.setVisible(true);
    	    //On vérifie que la taille mémoire est suffisante pour créer l'image de dessin
    	    // Checking that we have enough memory to create the image
    		int memoire_necessaire=Config.imageWidth*Config.imageHeight*4/1024/1024;
    		long free=Runtime.getRuntime().freeMemory()/1024/1024;
    		long total=Runtime.getRuntime().totalMemory()/1024/1024;
    		if (total-free+memoire_necessaire>Config.memoire*0.8){
    			Config.imageHeight=1000;
    			Config.imageWidth=1000;
    		}
    		// init frame
    	    init(frame);
    	    frame.setCommandLine(false);    
    	    //  On génère les primitives et les fichiers de démarrage
    	    // generate primitives and start up files
    	    frame.genere_primitive(); 
    	     
    	    // On Enregistre le temps auquel la session a commencé
    	    // hour when we launch XLogo
    	    Config.heure_demarrage=Calendar.getInstance().getTimeInMillis();
    	    
    	    
    	    // Command to execute on startup 
    	    
    	    // If this command is defined from the command line
    	    if (Config.autoLaunch){
    	  	  frame.affichage_Start(Utils.decoupe(Config.mainCommand));
    		  frame.getHistoryPanel().ecris("normal", Config.mainCommand+"\n");
    	    }
    	    // Else if this command is defined from the Start Up Dialog Box   	    
    	    else if (!Config.a_executer.equals("")) {     
    		      frame.affichage=new Affichage(frame,Utils.decoupe(Config.a_executer));
    			   frame.affichage.start(); 
    	    }
    	    else {
    	    	frame.setCommandLine(true);
    	    	frame.focus_Commande();
    	    }
    	   }
      });

  	}
  /**
   * Initializes the main Frame
   * @param frame The main Frame
   */
  private void init(Application frame){
    // on centre la tortue
  	// Centering turtle
	Dimension d=frame.scrollArea.getViewport().getViewRect().getSize();
	Point p=new Point(Math.abs(Config.imageWidth/2-d.width/2),Math.abs(Config.imageHeight/2-d.height/2));
	frame.scrollArea.getViewport().setViewPosition(p);  	

    // on affiche la tortue sur la zone de dessin
	// Displays turtle
	// 	System.out.println("Total :"+Runtime.getRuntime().totalMemory()/1024+" max "+Runtime.getRuntime().maxMemory()/1024+" Free "+Runtime.getRuntime().freeMemory()/1024);
	      MediaTracker tracker=new MediaTracker(frame);
	      tracker.addImage(DrawPanel.dessin,0);
	      try{tracker.waitForID(0);}
	      catch(InterruptedException e){}
      frame.getArdoise().getGraphics().drawImage(DrawPanel.dessin,0,0,frame);
      frame.scrollArea.validate();//getArdoise().revalidate();
      
      /////////////frame.getKernel().initDrawGraphics();
  }
  /**
   * Sets the selected language for all messages
   * @param id The integer that represents the language
   */
  public static void generateLanguage(int id){ // fixe la langue utilisée pour les messages
    Locale locale=null;
    locale=Logo.getLocale(id);
    messages=ResourceBundle.getBundle("langage",locale);
    translationLanguage[0]=Logo.messages.getString("pref.general.french");
  	translationLanguage[1]=Logo.messages.getString("pref.general.english");
    translationLanguage[2]=Logo.messages.getString("pref.general.arabic");
    translationLanguage[3]=Logo.messages.getString("pref.general.spanish");
    translationLanguage[4]=Logo.messages.getString("pref.general.portuguese");
  	translationLanguage[5]=Logo.messages.getString("pref.general.esperanto");
    translationLanguage[6]=Logo.messages.getString("pref.general.german");
    translationLanguage[7]=Logo.messages.getString("pref.general.galician");
    translationLanguage[8]=Logo.messages.getString("pref.general.asturian");
    translationLanguage[9]=Logo.messages.getString("pref.general.greek");
  }

  /**
   * This method initializes all XLogo's parameters from Command Line
   * java -jar xlogo.jar -a -lang fr file1.lgo ... 
   */
  private void readCommandLineConfig(){
	  int i=0;
	  while(i<Config.path.size()){
		  String element=Config.path.get(i);
		  // AutoLaunch main Command on startup
		  if (element.equals("-a")){
			  Config.autoLaunch=true;
			  Config.path.remove(i);
		  }
		  // Choosing language
		  else if (element.equals("-lang")){
			  Config.path.remove(i);
			  if (i<Config.path.size()) {
				  element=Config.path.get(i);
				  for (int j=0;j<Logo.locales.length;j++){
					  if (Logo.locales[j].equals(element)){
						  Config.langage=j;
						  Logo.generateLanguage(j);
						  break;
					  }
				  }
				  Config.path.remove(i);
			  }
		  	}
		  // Memory Heap Size
			else if (element.equals("-memory")){
				  Config.path.remove(i);
				  if (i<Config.path.size()) {
					  element=Config.path.get(i);
						try{
							int mem=Integer.parseInt(element);
							Config.memoire=mem;
							Config.tmp_memoire=mem;
							 Config.path.remove(i);	
							
						}
						catch(NumberFormatException e){}
				 }  
			 }
		  // TCP port
			else if (element.equals("-tcp_port")){
				  Config.path.remove(i);
				  if (i<Config.path.size()) {
					  element=Config.path.get(i);
						try{
							int port=Integer.parseInt(element);
							if (port <=0) port=1948;
							Config.TCP_PORT=port;
							 Config.path.remove(i);	
							
						}
						catch(NumberFormatException e){
							Config.TCP_PORT=1948;
						}
				 }  
			 }
		  
		  // Logo Files
		  else i++;
	  }
  }
  
  /**
   * This method initializes all parameters from the file .xlogo
   */
  private void readConfig(){
	Locale locale=null;
	try{
		// Try to read XML format (new config file)
	  FileInputStream fr = new FileInputStream(System.getProperty("user.home")+File.separator+".xlogo");
      BufferedInputStream bis = new BufferedInputStream(fr);	
      InputStreamReader isr = new  InputStreamReader(bis,  "UTF8");		
      try{
      	XMLReader saxReader = XMLReaderFactory.createXMLReader();
      	saxReader.setContentHandler(new SimpleContentHandler());
      	saxReader.parse(new InputSource(isr));
      }
      catch (SAXException e){
      	// Read the old config file format
  	  String s="";      	
	  FileReader ifr = new FileReader(System.getProperty("user.home")+File.separator+".xlogo");
	   while(ifr.ready()){
		char[] b = new char[64];
		int i=ifr.read(b);
		if (i==-1) break;
		s+=new String(b);
	  }
	  StringTokenizer st=new StringTokenizer(s,"\n");
	  
	  while(st.hasMoreTokens()){
		String element=st.nextToken();
		
		if (element.equals("# langue")){
			element=st.nextToken();
		  	int id=Integer.parseInt(element);
		  	Config.langage=id;
		  	generateLanguage(id);
		}
		else if (element.equals("# vitesse")){
		  element=st.nextToken();
		  Config.turtleSpeed=Integer.parseInt(element);
		}
		else if (element.equals("# tortue choisie")) {
		  element=st.nextToken();
		  Config.activeTurtle=Integer.parseInt(element);
		}
		else if (element.equals("# nb max de tortues")){
			element=st.nextToken();
			Config.maxTurtles=Integer.parseInt(element);
		}
		else if(element.equals("# forme crayon")){
			element=st.nextToken();
			Config.penShape=Integer.parseInt(element);
		}
		else if(element.equals("# effacer dessin en quittant editeur")){
			int id=Integer.parseInt(st.nextToken());
			if (id==0) Config.eraseImage=false;
			else Config.eraseImage=true;
		}
		else if(element.equals("# epaisseur max crayon")){
			element=st.nextToken();
			Config.maxPenWidth=Integer.parseInt(element);
		}
		else if(element.equals("# repertoire par defaut")) {
				Config.defaultFolder=st.nextToken();
                  File f=new File(Config.defaultFolder);
                  if (!f.isDirectory()) Config.defaultFolder=System.getProperty("user.home");
		}
		else if (element.equals("# a executer au demarrage")){
			element=st.nextToken();
			if (!element.equals("# aucun")) Config.a_executer=element;
		}
		else if (element.equals("# police")){
			element=st.nextToken();
			String nom=element;
			element=st.nextToken();
			Config.police=new Font(nom,Font.PLAIN,Integer.parseInt(element));
		}
		else if(element.equals("# hauteur")){
			Config.imageHeight=Integer.parseInt(st.nextToken());
		}
		else if(element.equals("# largeur")){
			Config.imageWidth=Integer.parseInt(st.nextToken());
		}
		else if (element.equals("# memoire")){
			element=st.nextToken();
			Config.memoire=Integer.parseInt(element);
			Config.tmp_memoire=Integer.parseInt(element);
		}
		else if (element.equals("# qualite")){
			element=st.nextToken();
			Config.quality=Integer.parseInt(element);
		}
		else if (element.equals("# coloration")){
			element=st.nextToken();
			StringTokenizer sti=new StringTokenizer(element);
			if (sti.countTokens()==9){
				Config.COLOR_ENABLED=(new Boolean(sti.nextToken())).booleanValue();
				Config.coloration_commentaire=Integer.parseInt(sti.nextToken());	
				Config.coloration_operande=Integer.parseInt(sti.nextToken());
				Config.coloration_parenthese=Integer.parseInt(sti.nextToken());
				Config.coloration_primitive=Integer.parseInt(sti.nextToken());
				Config.style_commentaire=Integer.parseInt(sti.nextToken());
				Config.style_operande=Integer.parseInt(sti.nextToken());
				Config.style_parenthese=Integer.parseInt(sti.nextToken());
				Config.style_primitive=Integer.parseInt(sti.nextToken());
			}
		}
		else if (element.equals("# fichiers de demarrage")) {
		  while (st.hasMoreTokens()){
			  element=st.nextToken();
//			  System.out.println(path+" "+element);
			if (!element.startsWith("#")) Config.path.add(element);
		  }
		}
	  }}
      
	}
	catch(Exception e){
	  System.out.println(e.toString());
	  //e.printStackTrace();
	  try{
		  SwingUtilities.invokeAndWait(new Runnable(){
			  public void run(){
				  select=new Selection_Langue();	
			  } 
		  });
	  }
	  catch(Exception e1){}
	  	while(!select.getSelection_faite()){
			try{
				Thread.sleep(50);
			}
			catch(InterruptedException e1){}
		}
		select.dispose();
		generateLanguage(Config.langage);
	}
	// Verify that all values are in valid range
	
	
	
  }

  
 
/**
 * The main methods
 * @param args The file *.lgo to load on startup
 */
  public static void main(String[] args)   {
	  try{
		  // Display the java3d version
		  java.util.Map<String,String> map=VirtualUniverse.getProperties();
		  System.out.println("Java3d :"+map.get("j3d.version"));
	  }
	  catch(Exception e){
		  System.out.println("Java3d problem");
		  e.printStackTrace();
	  }
	  
	  //new Test();
	  try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch(Exception e) {
      e.printStackTrace();
    }
/*    String tampon="";

    for(int i=0;i<args.length;i++){
    	if (i!=args.length-1) tampon+=args[i]+" ";
    	else tampon+=args[i];
    }
    StringTokenizer st=new StringTokenizer(tampon, "\u001B");
    while(st.hasMoreTokens()){
    	String element=st.nextToken();
    	Config.path.push(element);
    }
    */
    //Recuperer les fichiers de démarrage correspondant au double clic de souris
    // ou au lancement en ligne de commande
    
    for(int i=0;i<args.length;i++){
    		Config.path.add(args[i]);
    }
    
     
    Config.path.add(0,"#####");
    //try{;
    	new Logo();
/*    }
    catch(Exception e){

    	JFrame jf=new JFrame();
    	

    	String message=e.toString()+"\n"+e.getMessage();
    	
    	System.out.println(message);
		JTextArea jt=new JTextArea(message);
		jt.setPreferredSize(new Dimension(400,400));
		jt.setEditable(false);
		jt.setBackground(new Color(255,255,177));
		jt.setFont(Config.police);
		jf.getContentPane().add(jt);
		jf.pack();
    	jf.setVisible(true);
    }*/
    }
 /* public Language getLanguage(){
	  return language;
  }*/
  /**
	 * This method returns the Locale corresponding to the language "id"
	 * 
	 * @param id
	 *            The integer that represents the language
	 * @return The locale that corresponds to the desired language
	 */
public static Locale getLocale(int id){  // rend la locale
												// correspondant à l'entier
												// id
  Locale locale=null;
  switch(id){
    case Config.LANGUAGE_FRENCH: // french
      locale=new Locale("fr","FR");
      break;
    case Config.LANGUAGE_ENGLISH: // english
      locale=new Locale("en","US");
      break;
    case Config.LANGUAGE_ARABIC: // Arabic
      locale=new Locale("ar","MA");
      break;
    case Config.LANGUAGE_SPANISH: // spanish
    	locale=new Locale("es","ES");
    	break;
    case Config.LANGUAGE_PORTUGAL: // portuguese
      locale=new Locale("pt","BR");
      break;
     case Config.LANGUAGE_ESPERANTO: // esperanto
     	locale=new Locale("eo","EO");
     	break;
     case Config.LANGUAGE_GERMAN: // german
     locale=new Locale("de","DE");
     break;
     case Config.LANGUAGE_GALICIAN: // galician
      locale=new Locale("gl","ES");
     break;
     case Config.LANGUAGE_ASTURIAN: // Asturian
  	  locale=new Locale("as","ES");
  	break;
     case Config.LANGUAGE_GREEK: // Greek
  	   locale=new Locale("el","GR");
  	break;
  }
return locale;
}
public static String getLocaleTwoLetters(){
	  if (Config.langage>-1&& Config.langage<locales.length)
	  return locales[Config.langage];
	  else return "en";
}
  
}
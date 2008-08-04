/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo 
 * 						programming language
 * @author Loïc Le Coq
 */
package xlogo;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
/**
 * This class contains all parameters for XLogo
 * Those arguments are stored in the file .xlogo 
 * 
 * @author loic
 * 
 */
public class Config {
	/** Version number**/
	static final String version="0.9.94 beta, Augut, the 3nd";
	/** Drawing Quality **/
	public static int quality=0;
	/** Drawing Quality: normal **/
	public static final int QUALITY_NORMAL=0; // 0 normal
	/** Drawing Quality:high (vectorial) **/
	public static final int QUALITY_HIGH=1; // 1 vectoriel
	/** Drawing Quality: low **/
	public static final int QUALITY_LOW=2; // 2 basse 
	// Integer that represents the choosen language
	/**
	 * French language id
	 */
	public static final int LANGUAGE_FRENCH=0;
	/**
	 * English language id
	 */
	public static final int LANGUAGE_ENGLISH=1;
	/**
	 * Arabic language id
	 */
	public static final int LANGUAGE_ARABIC=2;
	/**
	 * Spanish language id
	 */	
	public static final int LANGUAGE_SPANISH=3;
	/**
	 * Portuguese language id
	 */	public static final int LANGUAGE_PORTUGAL=4;
	/**
	* Esperanto language id
	 */	public static final int LANGUAGE_ESPERANTO=5;
	/**
	 * German language id
	 */	public static final int LANGUAGE_GERMAN=6;
	/**
	 * Galician language id
	*/
	 public static final int LANGUAGE_GALICIAN=7;
	/**
	 * Asturian language id
	*/
	 public static final int LANGUAGE_ASTURIAN=8;
	/**
	 * Greek language id
	*/
	 public static final int LANGUAGE_GREEK=9;
	 /**
	  * This integer represents the selected language
	  */
	 public static int langage=0;  
	
	/** This integer represents the selected looknfeel for the appplication**/
	 
	public static int looknfeel=1;
	/**Native looknfeel**/
	public static final int LOOKNFEEL_NATIVE=0;
	/**Java looknfeel**/
	public static final int LOOKNFEEL_JAVA=1;
	/**Motif looknfeel**/
	public static final int LOOKNFEEL_MOTIF=2;
	
	/**
	 * This integer represents the drawing area width
	 */
	public static int imageWidth=1000;
	/**
	 * This integer represents the drawing area height
	 */
	public static int imageHeight=1000;
	/**
	 * This integer represents the memoy allocated to the Java Virtual Machine.<br>
	 *  This integer has to be increased for example when the main Image in the drawing Area is very big.
	 */
	public static int memoire=64; //Mémoire max réelle attribuée à la JVM
								  // Real Max Value for memory allocated to the JVM
	/**
	 * Value for the memory in Prefs tab. Will be written when application will be closed
	 */
	public static int tmp_memoire=64; // Correspond à la valeur dans l'onglet préférences. Sera écrit en fin d'application.
									// Value for the memory in Prefs tab. Will be written when application will be closed
	/**
	 * Integer that represents the active turtle's shape
	 */
	public static int activeTurtle=0; // Numéro de la tortue qui dessine
										// id for the active turtle
	/**
	 * Maximum allowed pen size 
	 */
	public static int maxPenWidth=-1;  //epaisseur maximum autorisée pour le crayon
										// Maximum pen width
	/**
	 * This boolean indicates if the drawing area has to be cleaned when the editor is left.
	 */
	public static boolean eraseImage = false; //effacer le dessin en quittant l'éditeur
												// erase the image when we leave the editor?
	/**
	 * Max value for the turtles number 
	 */
	public static int maxTurtles  = 16; // Nombre maximum de tortues
									// Max value for the number of turtles
	/**
	 * Default screen color: This color is used when the primitive "clearscreen" is used.
	 */
	public static Color screencolor=Color.WHITE;
	/**
	 * Default pen color: This color is used when the primitive "clearscreen" is used.
	 */
	public static Color pencolor=Color.BLACK;
	/**
	 * Selected pen shape: Square
	 */
	public static final int PEN_SHAPE_SQUARE=0;
	/**
	 * Selected pen shape: Oval
	 */
	public static final int PEN_SHAPE_OVAL=1;
	/**
	 * This integer represents the pen shape
	 */
	public static int penShape = 0 ; // 0 pour carré 1 pour rond
	/**
	 * This integer represents the turtle's speed for drawing <br>
	 * Slow: 100
	 * Fast: 0
	 */
	public static int turtleSpeed=0;		// Représente la vitesse de la tortue 0 rapide 100 lent
									// 0--> speed 100 --> slow 
	/**
	 * This String contains the command to execute on Startup. <br>
	 * Configured in the dialog box "startup files"
	 */	
  public static String a_executer="";
  /**
   * The default folder for the user when the application starts.<br>
   * This folder corresponds to the last opened or saved file in format lgo
   */
  public static String defaultFolder=System.getProperty("user.home");
  /**
   * This Stack contains all startup files
   */
  public static ArrayList<String> path=new ArrayList<String>(); // Fichiers de démarrage

/**
 *  syntax Highlighting: Color for primitives
 */
  public static int coloration_primitive=new Color(0,128,0).getRGB();
  /**
   *  syntax Highlighting: Style for primitives
   */
  public static int style_primitive=Font.PLAIN;
  /**
   *  syntax Highlighting: Color for operands: numbers....
   */
  public static int coloration_operande=Color.BLUE.getRGB();
  /**
   *  syntax Highlighting: Style for operands
   */
  public static int style_operande=Font.PLAIN;
  /**
   *  syntax Highlighting: Color for comments 
   */
  public static int coloration_commentaire=Color.GRAY.getRGB();
  /**
   *  syntax Highlighting: Style for comments
   */
  public static int style_commentaire=Font.PLAIN;
  /**
   *  syntax Highlighting: Color for parenthesis
   */
  public static int coloration_parenthese=Color.RED.getRGB();
  /**
   *  syntax Highlighting: Style for parenthesis
   */
  public static int style_parenthese=Font.BOLD;
  /**
   *  boolean that indicates if syntax Highlighting is enabled
   */
  public static boolean COLOR_ENABLED=true;
  /**
   * This boolean indicates if the grid is enabled
   */
  public static boolean drawGrid=false;
  /**
   * This integer represents the X distance for the grid
   */
  public static int XGrid=20;
  /**
   * This integer represents the Y distance for the grid
   */
  public static int YGrid=20;
  /**
   * This integer represents the grid Color
   */
  public static int gridColor=Color.DARK_GRAY.getRGB();
  /**
   * This boolean indicates if the X axis is enabled
   */
  public static boolean drawXAxis=false;
  /**
   * This boolean indicates if the Y axis is enabled
   */
  public static boolean drawYAxis=false;
  /**
   * This integer represents the axis Color
   */ public static int axisColor=new Color(255,0,102).getRGB();
   /**
    * This integer represents the X distance between two divisions on the X Axis
    */
  public static int XAxis=30;
  /**
   * This integer represents the X distance between two divisions on the Y Axis
   */
  public static int YAxis=30;
  
  /** This long represents the hour of XLogo starting**/
  public static long heure_demarrage; //heure a laquelle on a lance xlogo
  /** This font is the default font for all menus ... in XLogo Applciation**/
  public static Font police=new Font("dialog",Font.PLAIN,12); // Police de l'interface et de l'éditeur
 /**Color for the border around drawing area**/
  public static Color borderColor=null;
  /** The Image is for the border around drawing area **/
  public static String borderImageSelected="background.png";
 
  /** This Vector contains all images added by the user for image Border**/
  public static ArrayList<String> borderExternalImage=new ArrayList<String>();
  /** The default image defined by default that are included in XLogo**/
  public static String[] borderInternalImage={"background.png"};
  /** This String represents the main command accessible with the button play in the toolbar**/
  public static String mainCommand="";
  /**
   * This boolean indicates if Xlogo must launch the main Command on XLogo startup
   * It overrides the String a_executer
   */
  public static boolean autoLaunch=false;
  /**
   * Default Constructor
   */
  Config(){

	}
  public static int searchInternalImage(String st){
	  for (int i=0;i<borderInternalImage.length;i++){
		  if (st.equals(borderInternalImage[i])) return i;
	  }
	  return -1;
  }
}

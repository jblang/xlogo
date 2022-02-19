/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo
 * programming language
 *
 * @author Loïc Le Coq
 */
package xlogo;

import java.awt.*;
import java.util.ArrayList;

/**
 * This class contains all parameters for XLogo
 * Those arguments are stored in the file .xlogo 
 *
 * @author loic
 *
 */
public class Config {
    public static final String VERSION = "1.0.0 beta 2";
    public static final String WEB_SITE = "github.com/jblang/xlogo";
    public static final boolean DEBUG = false;

    /**
     * The quality of drawing
     */
    public static final int DRAW_QUALITY_NORMAL = 0;
    public static final int DRAW_QUALITY_HIGH = 1;
    public static final int DRAW_QUALITY_LOW = 2;
    public static int drawQuality = 0;

    /**
     * The selected language
     */
    public static final int LANGUAGE_FRENCH = 0;
    public static final int LANGUAGE_ENGLISH = 1;
    public static final int LANGUAGE_ARABIC = 2;
    public static final int LANGUAGE_SPANISH = 3;
    public static final int LANGUAGE_PORTUGUESE = 4;
    public static final int LANGUAGE_ESPERANTO = 5;
    public static final int LANGUAGE_GERMAN = 6;
    public static final int LANGUAGE_GALICIAN = 7;
    public static final int LANGUAGE_ASTURIAN = 8;
    public static final int LANGUAGE_GREEK = 9;
    public static final int LANGUAGE_ITALIAN = 10;
    public static final int LANGUAGE_CATALAN = 11;
    public static final int LANGUAGE_HUNGARIAN = 12;
    public static int language = 0;

    /**
     * The selected look and feel for the app
     */
    public static final int LAF_NATIVE = 0;
    public static final int LAF_LIGHT = 1;
    public static final int LAF_DARK = 2;
    public static int lookAndFeel = 1;

    /**
     * The drawing area width
     */
    public static int imageWidth = 1000;

    /**
     * The drawing area height
     */
    public static int imageHeight = 1000;

    /**
     * The memory currently allocated to the Java Virtual Machine.
     * This integer has to be increased for example when the main Image in the drawing area is very big.
     */
    public static int memoryLimit = 64;

    /**
     * Value for the memory in Prefs tab. Will be written when application will be closed.
     */
    public static int newMemoryLimit = 64;

    /**
     * The active turtle's shape
     */
    public static int activeTurtle = 0; // drawing turtle number

    /**
     * Maximum allowed pen size
     */
    public static int maxPenWidth = -1;  //maximum thickness allowed for the pencil

    /**
     * Indicates whether the drawing area has to be cleaned when the editor is left.
     */
    public static boolean eraseImage = false; //delete the drawing when exiting the editor

    /**
     * Indicates whether variables are deleted when closing the editor.
     */
    public static boolean clearVariables = false;

    /**
     * Max value for the turtles number
     */
    public static int maxTurtles = 16; // Maximum number of turtles

    /**
     * Default screen color: This color is used when the primitive "clearscreen" is used.
     */
    public static Color screenColor = Color.WHITE;

    /**
     * Default pen color: This color is used when the primitive "clearscreen" is used.
     */
    public static Color penColor = Color.BLACK;

    /**
     * The pen shape
     */
    public static final int PEN_SHAPE_SQUARE = 0;
    public static final int PEN_SHAPE_OVAL = 1;
    public static int penShape = 0;

    /**
     * The turtle's speed for drawing <br>
     * Slow: 100
     * Fast: 0
     */
    public static int turtleSpeed = 0;

    /**
     * The command to execute on Startup. <br>
     * Configured in the dialog box "startup files"
     */
    public static String startupCommand = "";

    /**
     * The default folder for the user when the application starts.<br>
     * This folder corresponds to the last opened or saved file in format lgo
     */
    public static String defaultFolder = System.getProperty("user.home");

    /**
     * This Stack contains all startup files
     */
    public static ArrayList<String> startupFiles = new ArrayList<>();

    /**
     * Syntax Highlighting: Color for primitives
     */
    public static int syntaxPrimitiveColor = new Color(0, 128, 0).getRGB();

    /**
     * Syntax Highlighting: Style for primitives
     */
    public static int syntaxPrimitiveStyle = Font.PLAIN;

    /**
     * Syntax Highlighting: Color for operands: numbers....
     */
    public static int syntaxOperandColor = Color.BLUE.getRGB();

    /**
     * Syntax Highlighting: Style for operands
     */
    public static int syntaxOperandStyle = Font.PLAIN;

    /**
     * Syntax Highlighting: Color for comments
     */
    public static int syntaxCommentColor = Color.GRAY.getRGB();

    /**
     * Syntax Highlighting: Style for comments
     */
    public static int syntaxCommentStyle = Font.PLAIN;

    /**
     * Syntax Highlighting: Color for brackets
     */
    public static int syntaxBracketColor = Color.RED.getRGB();

    /**
     * Syntax Highlighting: Style for brackets
     */
    public static int syntaxBracketStyle = Font.BOLD;

    /**
     *  Indicates whether syntax Highlighting is enabled
     */
    public static boolean syntaxHighlightingEnabled = true;

    /**
     * Indicates whether the grid is enabled
     */
    public static boolean gridEnabled = false;

    /**
     * The X distance for the grid
     */
    public static int xGridSpacing = 20;

    /**
     * The Y distance for the grid
     */
    public static int yGridSpacing = 20;

    /**
     * The grid Color
     */
    public static int gridColor = Color.DARK_GRAY.getRGB();

    /**
     * Indicates whether the X axis is enabled
     */
    public static boolean xAxisEnabled = false;

    /**
     * Indicates whether the Y axis is enabled
     */
    public static boolean yAxisEnabled = false;

    /**
     * The axis Color
     */
    public static int axisColor = new Color(255, 0, 102).getRGB();

    /**
     * The X distance between two divisions on the X Axis
     */
    public static int xAxisSpacing = 30;

    /**
     * The X distance between two divisions on the Y Axis
     */
    public static int yAxisSpacing = 30;

    /**
     * This long represents the hour of XLogo starting
     */
    public static long startupHour;

    /**
     * The default font for drawing labels
     */
    public static Font font = new Font("dialog", Font.PLAIN, 12);

    /**
     * Color for the border around drawing area
     */
    public static Color borderColor = null;

    /**
     * The image for the border around drawing area
     */
    public static String borderImageSelected = "background.png";

    /**
     * Contains all images added by the user for image border
     */
    public static ArrayList<String> userBorderImages = new ArrayList<>();

    /**
     * The default image defined by default that are included in XLogo
     */
    public static String[] includedBorderImages = {"background.png"};

    /**
     * The main command accessible with the button play in the toolbar
     */
    public static String mainCommand = "";

    /**
     * Indicates whether Xlogo must launch the main Command on XLogo startup
     */
    public static boolean autoLaunch = false;

    /**
     * TCP Port for robotics and network flows
     */
    public static int tcpPort = 1948;

    // Disable instantiation
    private Config() {}

    public static int searchInternalImage(String st) {
        for (int i = 0; i < includedBorderImages.length; i++) {
            if (st.equals(includedBorderImages[i])) return i;
        }
        return -1;
    }

}

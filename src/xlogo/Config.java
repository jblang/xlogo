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

    public static final int DRAW_QUALITY_NORMAL = 0;
    public static final int DRAW_QUALITY_HIGH = 1;
    public static final int DRAW_QUALITY_LOW = 2;
    private int drawQuality = 0;

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
    private int language = LANGUAGE_ENGLISH;

    public static final int LAF_NATIVE = 0;
    public static final int LAF_LIGHT = 1;
    public static final int LAF_DARK = 2;
    private int lookAndFeel = LAF_DARK;

    private int imageWidth = 1000;
    private int imageHeight = 1000;
    private int memoryLimit = 256;
    private int activeTurtle = 0;
    private int maxPenWidth = -1;
    private boolean eraseImage = false;
    private boolean clearVariables = false;
    private int maxTurtles = 16;
    private Color screenColor = Color.WHITE;
    private Color penColor = Color.BLACK;

    public static final int PEN_SHAPE_SQUARE = 0;
    public static final int PEN_SHAPE_OVAL = 1;
    private int penShape = 0;

    private int turtleSpeed = 0;
    private String startupCommand = "";
    private String defaultFolder = System.getProperty("user.home");
    private ArrayList<String> startupFiles = new ArrayList<>();
    private int syntaxPrimitiveColor = new Color(0, 128, 0).getRGB();
    private int syntaxPrimitiveStyle = Font.PLAIN;
    private int syntaxOperandColor = Color.BLUE.getRGB();
    private int syntaxOperandStyle = Font.PLAIN;
    private int syntaxCommentColor = Color.GRAY.getRGB();
    private int syntaxCommentStyle = Font.PLAIN;
    private int syntaxBracketColor = Color.RED.getRGB();
    private int syntaxBracketStyle = Font.BOLD;
    private boolean syntaxHighlightingEnabled = true;
    private boolean gridEnabled = false;
    private int xGridSpacing = 20;
    private int yGridSpacing = 20;
    private int gridColor = Color.DARK_GRAY.getRGB();
    private boolean xAxisEnabled = false;
    private boolean yAxisEnabled = false;
    private int axisColor = new Color(255, 0, 102).getRGB();
    private int xAxisSpacing = 30;
    private int yAxisSpacing = 30;
    private Font font = new Font("dialog", Font.PLAIN, 12);
    private int tcpPort = 1948;

    /**
     * The quality of drawing
     */
    public int getDrawQuality() {
        return drawQuality;
    }

    public void setDrawQuality(int drawQuality) {
        this.drawQuality = drawQuality;
    }

    /**
     * The selected language
     */
    public int getLanguage() {
        return language;
    }

    public void setLanguage(int language) {
        this.language = language;
    }

    /**
     * The selected look and feel for the app
     */
    public int getLookAndFeel() {
        return lookAndFeel;
    }

    public void setLookAndFeel(int lookAndFeel) {
        this.lookAndFeel = lookAndFeel;
    }

    /**
     * The drawing area width
     */
    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    /**
     * The drawing area height
     */
    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    /**
     * The memory currently allocated to the Java Virtual Machine.
     * This integer has to be increased for example when the main Image in the drawing area is very big.
     */
    public int getMemoryLimit() {
        return memoryLimit;
    }

    public void setMemoryLimit(int memoryLimit) {
        this.memoryLimit = memoryLimit;
    }

    /**
     * The active turtle's shape
     */
    public int getActiveTurtle() {
        return activeTurtle;
    }

    public void setActiveTurtle(int activeTurtle) {
        this.activeTurtle = activeTurtle;
    }

    /**
     * Maximum allowed pen size
     */
    public int getMaxPenWidth() {
        return maxPenWidth;
    }

    public void setMaxPenWidth(int maxPenWidth) {
        this.maxPenWidth = maxPenWidth;
    }

    /**
     * Indicates whether the drawing area has to be cleaned when the editor is left.
     */
    public boolean isEraseImage() {
        return eraseImage;
    }

    public void setEraseImage(boolean eraseImage) {
        this.eraseImage = eraseImage;
    }

    /**
     * Indicates whether variables are deleted when closing the editor.
     */
    public boolean isClearVariables() {
        return clearVariables;
    }

    public void setClearVariables(boolean clearVariables) {
        this.clearVariables = clearVariables;
    }

    /**
     * Max value for the turtles number
     */
    public int getMaxTurtles() {
        return maxTurtles;
    }

    public void setMaxTurtles(int maxTurtles) {
        this.maxTurtles = maxTurtles;
    }

    /**
     * Default screen color: This color is used when the primitive "clearscreen" is used.
     */
    public Color getScreenColor() {
        return screenColor;
    }

    public void setScreenColor(Color screenColor) {
        this.screenColor = screenColor;
    }

    /**
     * Default pen color: This color is used when the primitive "clearscreen" is used.
     */
    public Color getPenColor() {
        return penColor;
    }

    public void setPenColor(Color penColor) {
        this.penColor = penColor;
    }

    /**
     * The pen shape
     */
    public int getPenShape() {
        return penShape;
    }

    public void setPenShape(int penShape) {
        this.penShape = penShape;
    }

    /**
     * The turtle's speed for drawing <br>
     * Slow: 100
     * Fast: 0
     */
    public int getTurtleSpeed() {
        return turtleSpeed;
    }

    public void setTurtleSpeed(int turtleSpeed) {
        this.turtleSpeed = turtleSpeed;
    }

    /**
     * The command to execute on Startup. <br>
     * Configured in the dialog box "startup files"
     */
    public String getStartupCommand() {
        return startupCommand;
    }

    public void setStartupCommand(String startupCommand) {
        this.startupCommand = startupCommand;
    }

    /**
     * The default folder for the user when the application starts.<br>
     * This folder corresponds to the last opened or saved file in format lgo
     */
    public String getDefaultFolder() {
        return defaultFolder;
    }

    public void setDefaultFolder(String defaultFolder) {
        this.defaultFolder = defaultFolder;
    }

    /**
     * This Stack contains all startup files
     */
    public ArrayList<String> getStartupFiles() {
        return startupFiles;
    }

    public void setStartupFiles(ArrayList<String> startupFiles) {
        this.startupFiles = startupFiles;
    }

    /**
     * Syntax Highlighting: Color for primitives
     */
    public int getSyntaxPrimitiveColor() {
        return syntaxPrimitiveColor;
    }

    public void setSyntaxPrimitiveColor(int syntaxPrimitiveColor) {
        this.syntaxPrimitiveColor = syntaxPrimitiveColor;
    }

    /**
     * Syntax Highlighting: Style for primitives
     */
    public int getSyntaxPrimitiveStyle() {
        return syntaxPrimitiveStyle;
    }

    public void setSyntaxPrimitiveStyle(int syntaxPrimitiveStyle) {
        this.syntaxPrimitiveStyle = syntaxPrimitiveStyle;
    }

    /**
     * Syntax Highlighting: Color for operands: numbers....
     */
    public int getSyntaxOperandColor() {
        return syntaxOperandColor;
    }

    public void setSyntaxOperandColor(int syntaxOperandColor) {
        this.syntaxOperandColor = syntaxOperandColor;
    }

    /**
     * Syntax Highlighting: Style for operands
     */
    public int getSyntaxOperandStyle() {
        return syntaxOperandStyle;
    }

    public void setSyntaxOperandStyle(int syntaxOperandStyle) {
        this.syntaxOperandStyle = syntaxOperandStyle;
    }

    /**
     * Syntax Highlighting: Color for comments
     */
    public int getSyntaxCommentColor() {
        return syntaxCommentColor;
    }

    public void setSyntaxCommentColor(int syntaxCommentColor) {
        this.syntaxCommentColor = syntaxCommentColor;
    }

    /**
     * Syntax Highlighting: Style for comments
     */
    public int getSyntaxCommentStyle() {
        return syntaxCommentStyle;
    }

    public void setSyntaxCommentStyle(int syntaxCommentStyle) {
        this.syntaxCommentStyle = syntaxCommentStyle;
    }

    /**
     * Syntax Highlighting: Color for brackets
     */
    public int getSyntaxBracketColor() {
        return syntaxBracketColor;
    }

    public void setSyntaxBracketColor(int syntaxBracketColor) {
        this.syntaxBracketColor = syntaxBracketColor;
    }

    /**
     * Syntax Highlighting: Style for brackets
     */
    public int getSyntaxBracketStyle() {
        return syntaxBracketStyle;
    }

    public void setSyntaxBracketStyle(int syntaxBracketStyle) {
        this.syntaxBracketStyle = syntaxBracketStyle;
    }

    /**
     *  Indicates whether syntax Highlighting is enabled
     */
    public boolean isSyntaxHighlightingEnabled() {
        return syntaxHighlightingEnabled;
    }

    public void setSyntaxHighlightingEnabled(boolean syntaxHighlightingEnabled) {
        this.syntaxHighlightingEnabled = syntaxHighlightingEnabled;
    }

    /**
     * Indicates whether the grid is enabled
     */
    public boolean isGridEnabled() {
        return gridEnabled;
    }

    public void setGridEnabled(boolean gridEnabled) {
        this.gridEnabled = gridEnabled;
    }

    /**
     * The X distance for the grid
     */
    public int getXGridSpacing() {
        return xGridSpacing;
    }

    public void setXGridSpacing(int xGridSpacing) {
        this.xGridSpacing = xGridSpacing;
    }

    /**
     * The Y distance for the grid
     */
    public int getYGridSpacing() {
        return yGridSpacing;
    }

    public void setYGridSpacing(int yGridSpacing) {
        this.yGridSpacing = yGridSpacing;
    }

    /**
     * The grid Color
     */
    public int getGridColor() {
        return gridColor;
    }

    public void setGridColor(int gridColor) {
        this.gridColor = gridColor;
    }

    /**
     * Indicates whether the X axis is enabled
     */
    public boolean isXAxisEnabled() {
        return xAxisEnabled;
    }

    public void setXAxisEnabled(boolean xAxisEnabled) {
        this.xAxisEnabled = xAxisEnabled;
    }

    /**
     * Indicates whether the Y axis is enabled
     */
    public boolean isYAxisEnabled() {
        return yAxisEnabled;
    }

    public void setYAxisEnabled(boolean yAxisEnabled) {
        this.yAxisEnabled = yAxisEnabled;
    }

    /**
     * The axis Color
     */
    public int getAxisColor() {
        return axisColor;
    }

    public void setAxisColor(int axisColor) {
        this.axisColor = axisColor;
    }

    /**
     * The X distance between two divisions on the X Axis
     */
    public int getXAxisSpacing() {
        return xAxisSpacing;
    }

    public void setXAxisSpacing(int xAxisSpacing) {
        this.xAxisSpacing = xAxisSpacing;
    }

    /**
     * The X distance between two divisions on the Y Axis
     */
    public int getYAxisSpacing() {
        return yAxisSpacing;
    }

    public void setYAxisSpacing(int yAxisSpacing) {
        this.yAxisSpacing = yAxisSpacing;
    }

    /**
     * The default font for drawing labels
     */
    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    /**
     * TCP Port for robotics and network flows
     */
    public int getTcpPort() {
        return tcpPort;
    }

    public void setTcpPort(int tcpPort) {
        this.tcpPort = tcpPort;
    }
}

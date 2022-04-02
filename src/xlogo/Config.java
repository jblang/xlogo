/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo
 * programming language
 *
 * @author Loïc Le Coq
 */
package xlogo;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;

import java.awt.*;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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

    private Language language = Language.ENGLISH;

    public static final int LAF_NATIVE = 0;
    public static final int LAF_LIGHT = 1;
    public static final int LAF_DARK = 2;
    private int lookAndFeel = LAF_DARK;

    private int imageWidth = 400;
    private int imageHeight = 400;
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

    // Editor settings
    private boolean syntaxHighlightingEnabled = true;
    private Theme editorTheme = new Theme(new RSyntaxTextArea());
    private int tabSize = RSyntaxTextArea.getDefaultTabSize();
    private boolean tabsEmulated = true;
    private boolean paintTabLines = false;
    private boolean marginLineEnabled = false;
    private int marginLinePosition = RSyntaxTextArea.getDefaultMarginLinePosition();
    private boolean whitespaceVisible = false;
    private boolean eolMarkersVisible = false;
    private boolean highlightCurrentLine = true;
    private boolean bracketMatchingEnabled = true;
    private boolean hyperlinksEnabled = true;
    private boolean antiAliasingEnabled = true;
    private boolean fractionalFontMetricsEnabled = false;

    static Config read() throws IOException {
        var configPath = Paths.get(System.getProperty("user.home"), ".xlogo");
        if (Files.isDirectory(configPath))
            configPath = configPath.resolve("config.xml");
        var fis = new FileInputStream(configPath.toFile());
        XMLDecoder dec = new XMLDecoder(fis);
        var config = (Config) dec.readObject();
        dec.close();
        fis.close();
        return config;
    }

    /**
     * Write the Configuration file when the user quits XLogo
     */
    public void write() throws IOException {
        var configPath = Paths.get(System.getProperty("user.home"), ".xlogo");
        if (Files.isRegularFile(configPath))
            Files.delete(configPath);
        if (!Files.exists(configPath))
            Files.createDirectory(configPath);
        var configFile = configPath.resolve("config.xml").toFile();
        var fos = new FileOutputStream(configFile);
        var enc = new XMLEncoder(fos);
        enc.writeObject(this);
        enc.close();
        fos.close();
        var xmxFile = configPath.resolve("xmx.txt");
        Files.writeString(xmxFile, String.valueOf(getMemoryLimit()));
    }

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
    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
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
    public boolean getEraseImage() {
        return eraseImage;
    }

    public void setEraseImage(boolean eraseImage) {
        this.eraseImage = eraseImage;
    }

    /**
     * Indicates whether variables are deleted when closing the editor.
     */
    public boolean getClearVariables() {
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
     * Defines the syntax highlighting for the editor
     */
    public Theme getEditorTheme() {
        return editorTheme;
    }

    public void setEditorTheme(Theme theme) {
        this.editorTheme = theme;
    }

    public int getTabSize() {
        return tabSize;
    }

    public void setTabSize(int tabSize) {
        this.tabSize = tabSize;
    }

    public boolean isTabsEmulated() {
        return tabsEmulated;
    }

    public void setTabsEmulated(boolean tabsEmulated) {
        this.tabsEmulated = tabsEmulated;
    }

    public boolean isPaintTabLines() {
        return paintTabLines;
    }

    public void setPaintTabLines(boolean paintTabLines) {
        this.paintTabLines = paintTabLines;
    }

    public boolean isMarginLineEnabled() {
        return marginLineEnabled;
    }

    public void setMarginLineEnabled(boolean marginLineEnabled) {
        this.marginLineEnabled = marginLineEnabled;
    }

    public int getMarginLinePosition() {
        return marginLinePosition;
    }

    public void setMarginLinePosition(int marginLinePosition) {
        this.marginLinePosition = marginLinePosition;
    }

    public boolean isWhitespaceVisible() {
        return whitespaceVisible;
    }

    public void setWhitespaceVisible(boolean whitespaceVisible) {
        this.whitespaceVisible = whitespaceVisible;
    }

    public boolean isEolMarkersVisible() {
        return eolMarkersVisible;
    }

    public void setEolMarkersVisible(boolean eolMarkersVisible) {
        this.eolMarkersVisible = eolMarkersVisible;
    }

    public boolean isSyntaxHighlightingEnabled() {
        return syntaxHighlightingEnabled;
    }

    public void setSyntaxHighlightingEnabled(boolean syntaxHighlightingEnabled) {
        this.syntaxHighlightingEnabled = syntaxHighlightingEnabled;
    }

    public boolean isHighlightCurrentLine() {
        return highlightCurrentLine;
    }

    public void setHighlightCurrentLine(boolean highlightCurrentLine) {
        this.highlightCurrentLine = highlightCurrentLine;
    }

    public boolean isBracketMatchingEnabled() {
        return bracketMatchingEnabled;
    }

    public void setBracketMatchingEnabled(boolean bracketMatchingEnabled) {
        this.bracketMatchingEnabled = bracketMatchingEnabled;
    }

    public boolean isHyperlinksEnabled() {
        return hyperlinksEnabled;
    }

    public void setHyperlinksEnabled(boolean hyperlinksEnabled) {
        this.hyperlinksEnabled = hyperlinksEnabled;
    }

    public void loadEditorTheme(String name) {
        try {
            editorTheme = Theme.load(getClass().getResourceAsStream(name));
        } catch (IOException ignored) {
        }
    }

    public void loadLightEditorTheme() {
        loadEditorTheme("/org/fife/ui/rsyntaxtextarea/themes/default.xml");
    }

    public void loadDarkEditorTheme() {
        loadEditorTheme("/org/fife/ui/rsyntaxtextarea/themes/monokai.xml");
    }

    public void configureEditor(RSyntaxTextArea editor) {
        editorTheme.apply(editor);
        editor.setTabSize(tabSize);
        editor.setTabsEmulated(tabsEmulated);
        if (syntaxHighlightingEnabled)
            editor.setSyntaxEditingStyle("text/logo");
        else
            editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
        editor.setPaintTabLines(paintTabLines);
        editor.setMarginLineEnabled(marginLineEnabled);
        editor.setMarginLinePosition(marginLinePosition);
        editor.setWhitespaceVisible(whitespaceVisible);
        editor.setEOLMarkersVisible(eolMarkersVisible);
        editor.setHighlightCurrentLine(highlightCurrentLine);
        editor.setBracketMatchingEnabled(bracketMatchingEnabled);
        editor.setHyperlinksEnabled(hyperlinksEnabled);
        editor.setAntiAliasingEnabled(antiAliasingEnabled);
        editor.setFractionalFontMetricsEnabled(fractionalFontMetricsEnabled);

        // Hard-coded values
        editor.setAnimateBracketMatching(false);
        editor.setCloseCurlyBraces(false);
        editor.setClearWhitespaceLinesEnabled(false);
        editor.setPaintMatchedBracketPair(true);
        editor.setWrapStyleWord(false);
    }

    public void saveEditorConfiguration(RSyntaxTextArea editor) {
        syntaxHighlightingEnabled = editor.getSyntaxEditingStyle().equals("text/logo");
        editorTheme = new Theme(editor);
        tabSize = editor.getTabSize();
        tabsEmulated = editor.getTabsEmulated();
        paintTabLines = editor.getPaintTabLines();
        marginLineEnabled = editor.isMarginLineEnabled();
        marginLinePosition = editor.getMarginLinePosition();
        whitespaceVisible = editor.isWhitespaceVisible();
        eolMarkersVisible = editor.getEOLMarkersVisible();
        highlightCurrentLine = editor.getHighlightCurrentLine();
        bracketMatchingEnabled = editor.isBracketMatchingEnabled();
        hyperlinksEnabled = editor.getHyperlinksEnabled();
        antiAliasingEnabled = editor.getAntiAliasingEnabled();
        fractionalFontMetricsEnabled = editor.getFractionalFontMetricsEnabled();
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

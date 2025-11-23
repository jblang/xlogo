/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo
 * programming language
 *
 * @author Loïc Le Coq
 */
package xlogo;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;
import xlogo.gui.Application;
import xlogo.gui.LanguageListRenderer;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * This class initializes the main frame, loads startup files and launches startup command
 *
 * @author loic
 */
public class Logo {
    public static final String VERSION = "1.0.0 beta 5";
    public static final String WEB_SITE = "github.com/jblang/xlogo";
    public static Config config = new Config();
    static long startupHour = Calendar.getInstance().getTimeInMillis();
    static String mainCommand = "";
    static boolean autoLaunch = false;

    // Configure macOS-specific settings before any Swing components are created
    static {
        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            // Use macOS screen menu bar instead of in-window menu
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            // Set application name shown in menu bar
            System.setProperty("apple.awt.application.name", "XLogo");
            // Follow macOS system appearance (dark/light mode)
            System.setProperty("apple.awt.application.appearance", "system");
        }
    }

    /**
     * The main methods
     *
     * @param args The file *.lgo to load on startup
     */
    public static void main(String[] args) {
        // Set macOS dock icon
        setDockIcon();

        // Check if we need to re-exec with proper JVM arguments
        if (!"true".equals(System.getProperty("xlogo.launched"))) {
            if (relaunchWithJvmArgs(args)) {
                return; // Successfully relaunched, exit this instance
            }
            // If relaunch failed, continue with current JVM
        }

        config.getStartupFiles().addAll(Arrays.asList(args));
        config.getStartupFiles().add(0, "#####");

        // Register syntax highlighter
        AbstractTokenMakerFactory atmf = (AbstractTokenMakerFactory) TokenMakerFactory.getDefaultInstance();
        atmf.putMapping("text/logo", "xlogo.kernel.LogoTokenMaker");

        try {
            config = Config.read();
            launchApp();
        } catch (Exception e) {
            // Default to system theme
            applySystemTheme();
            // Prompt for language
            SwingUtilities.invokeLater(Logo::askLanguage);
        }
    }

    /**
     * Relaunches XLogo with proper JVM arguments for memory and JOGL compatibility.
     *
     * @param args The original command line arguments
     * @return true if relaunch was successful, false if we should continue in current JVM
     */
    private static boolean relaunchWithJvmArgs(String[] args) {
        try {
            int memory = Math.max(getMemoryFromArgs(args), getMemoryFromFile());

            Path jvm = Paths.get(System.getProperty("java.home"), "bin", "java");
            String jarPath = Logo.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();

            // Only relaunch if running from a JAR file
            if (!jarPath.endsWith(".jar")) {
                return false;
            }

            java.util.List<String> command = new ArrayList<>();
            command.add(jvm.toString());
            command.add("-Xmx" + memory + "m");
            // Mark as launched to prevent infinite relaunch loop
            command.add("-Dxlogo.launched=true");
            // macOS-specific settings for native menu bar and window theming
            if (System.getProperty("os.name").toLowerCase().contains("mac")) {
                command.add("-Dapple.laf.useScreenMenuBar=true");
                command.add("-Dapple.awt.application.name=XLogo");
                command.add("-Dapple.awt.application.appearance=system");
            }
            // JOGL workarounds for Java 9+ (https://jogamp.org/bugzilla/show_bug.cgi?id=1317)
            command.add("--add-exports=java.base/java.lang=ALL-UNNAMED");
            command.add("--add-exports=java.desktop/sun.awt=ALL-UNNAMED");
            command.add("--add-exports=java.desktop/sun.java2d=ALL-UNNAMED");
            command.add("-jar");
            command.add(jarPath);
            command.addAll(Arrays.asList(args));

            System.out.println("Relaunching XLogo with: " + String.join(" ", command));

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.inheritIO();
            pb.start();

            // Exit immediately - the child process will continue running
            System.exit(0);
            return true;
        } catch (Exception e) {
            System.err.println("Failed to relaunch XLogo: " + e.getMessage());
            return false;
        }
    }

    /**
     * Reads the memory limit from the -memory command line argument.
     *
     * @param args Command line arguments
     * @return Memory in MB, or 0 if not specified
     */
    private static int getMemoryFromArgs(String[] args) {
        for (int i = 0; i < args.length - 1; i++) {
            if ("-memory".equals(args[i])) {
                try {
                    return Integer.parseInt(args[i + 1]);
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return 0;
    }

    /**
     * Reads the memory limit from ~/.xlogo/xmx.txt.
     *
     * @return Memory in MB, or default of 256 if file doesn't exist or is invalid
     */
    private static int getMemoryFromFile() {
        int defaultMemory = 256;
        Path xmxFile = Paths.get(System.getProperty("user.home"), ".xlogo", "xmx.txt");
        try {
            return Integer.parseInt(Files.readString(xmxFile).trim());
        } catch (Exception e) {
            return defaultMemory;
        }
    }

    public static void launchApp() {
        readCommandLineConfig();
        SwingUtilities.invokeLater(Application::new);
    }

    /**
     * This method initializes all XLogo's parameters from Command Line
     * java -jar xlogo.jar -a -lang fr file1.lgo ...
     */
    private static void readCommandLineConfig() {
        int i = 0;
        while (i < config.getStartupFiles().size()) {
            String element = config.getStartupFiles().get(i);
            // AutoLaunch main Command on startup
            // Choosing language
            // Memory Heap Size
            // TCP port
            // Logo Files
            switch (element) {
                case "-a":
                    autoLaunch = true;
                    config.getStartupFiles().remove(i);
                    break;
                case "-lang":
                    config.getStartupFiles().remove(i);
                    if (i < config.getStartupFiles().size()) {
                        element = config.getStartupFiles().get(i);
                        for (var lang : Language.values()) {
                            if (lang.code.equals(element)) {
                                config.setLanguage(lang);
                                break;
                            }
                        }
                        config.getStartupFiles().remove(i);
                    }
                    break;
                case "-memory":
                    config.getStartupFiles().remove(i);
                    if (i < config.getStartupFiles().size()) {
                        element = config.getStartupFiles().get(i);
                        try {
                            int mem = Integer.parseInt(element);
                            config.setMemoryLimit(mem);
                            config.getStartupFiles().remove(i);

                        } catch (NumberFormatException ignored) {
                        }
                    }
                    break;
                case "-tcp_port":
                    config.getStartupFiles().remove(i);
                    if (i < config.getStartupFiles().size()) {
                        element = config.getStartupFiles().get(i);
                        try {
                            int port = Integer.parseInt(element);
                            if (port <= 0) port = 1948;
                            config.setTcpPort(port);
                            config.getStartupFiles().remove(i);

                        } catch (NumberFormatException e) {
                            config.setTcpPort(1948);
                        }
                    }
                    break;
                default:
                    i++;
                    break;
            }
        }
    }

    public static long getStartupHour() {
        return startupHour;
    }

    public static String getMainCommand() {
        return mainCommand;
    }

    public static void setMainCommand(String mainCommand) {
        Logo.mainCommand = mainCommand;
    }

    public static boolean isAutoLaunchEnabled() {
        return autoLaunch;
    }

    public static ImageIcon getIcon(String name) {
        var res = Logo.class.getResource("resources/icons/" + name + ".svg");
        if (res == null)
            return null;
        else
            return new FlatSVGIcon(res);
    }

    public static ImageIcon getAppIcon() {
        return new ImageIcon(Objects.requireNonNull(Logo.class.getResource("resources/appicon.png")));
    }

    /**
     * Sets the taskbar/dock icon using the Taskbar API (Java 9+).
     * Uses platform-appropriate icon (rounded for macOS, square for others).
     * Silently fails on platforms that don't support it.
     */
    private static void setDockIcon() {
        try {
            if (Taskbar.isTaskbarSupported()) {
                Taskbar taskbar = Taskbar.getTaskbar();
                if (taskbar.isSupported(Taskbar.Feature.ICON_IMAGE)) {
                    boolean isMac = System.getProperty("os.name").toLowerCase().contains("mac");
                    Image icon = isMac ? getMacOSAppIcon().getImage() : getAppIcon().getImage();
                    taskbar.setIconImage(icon);
                }
            }
        } catch (Exception ignored) {
            // Taskbar not supported on this platform
        }
    }

    public static ImageIcon getMacOSAppIcon() {
        return new ImageIcon(Objects.requireNonNull(Logo.class.getResource("resources/appicon-macos.png")));
    }

    public static FlatSVGIcon getTurtle(int i) {
        var res = Logo.class.getResource("resources/turtles/turtle" + i + ".svg");
        if (res == null)
            return null;
        else
            return new FlatSVGIcon(res);
    }

    public static ResourceBundle getLanguageBundle(Locale locale) {
        return ResourceBundle.getBundle("xlogo.resources.language", locale);
    }

    public static ResourceBundle getPrimitiveBundle(Locale locale) {
        return ResourceBundle.getBundle("xlogo.resources.primitives", locale);
    }

    static void askLanguage() {
        var f = new JFrame();
        f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        f.setIconImage(getAppIcon().getImage());
        f.setLayout(new BorderLayout());
        f.setTitle("XLogo");
        var ok = new JButton("OK");
        f.getContentPane().add(ok, BorderLayout.PAGE_END);

        var l = new JList<>(Language.values());
        l.setCellRenderer(new LanguageListRenderer(true));
        l.setSelectedIndex(config.getLanguage().ordinal());
        l.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        l.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        l.setVisibleRowCount(4);
        f.getContentPane().add(new JScrollPane(l), BorderLayout.CENTER);
        ok.addActionListener(e -> {
            config.setLanguage(Language.values()[l.getSelectedIndex()]);
            f.dispose();
            launchApp();
        });
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    public static String getLanguageCode() {
        return config.getLanguage().code;
    }

    public static String getString(String string) {
        return config.getLanguage().messages.getString(string);
    }

    public static String[] getTranslatedLanguages() {
        var langs = Language.values();
        var names = new String[langs.length];
        int i = 0;
        for (var lang : langs)
            names[i++] = getString(lang.key);
        return names;
    }

    /**
     * Apply theme based on system dark mode setting
     */
    private static void applySystemTheme() {
        try {
            boolean isMac = System.getProperty("os.name").toLowerCase().contains("mac");
            boolean useDark = isSystemDarkMode();
            if (useDark) {
                UIManager.setLookAndFeel(isMac ? new FlatMacDarkLaf() : new FlatDarkLaf());
                config.loadDarkEditorTheme();
            } else {
                UIManager.setLookAndFeel(isMac ? new FlatMacLightLaf() : new FlatLightLaf());
                config.loadLightEditorTheme();
            }
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    /**
     * Detect if the system is in dark mode
     */
    public static boolean isSystemDarkMode() {
        // macOS: check defaults read -g AppleInterfaceStyle
        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            try {
                Process process = Runtime.getRuntime().exec(
                    new String[]{"defaults", "read", "-g", "AppleInterfaceStyle"});
                process.waitFor();
                java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(process.getInputStream()));
                String line = reader.readLine();
                return line != null && line.toLowerCase().contains("dark");
            } catch (Exception e) {
                return false;
            }
        }
        // Windows: check registry for AppsUseLightTheme
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            try {
                Process process = Runtime.getRuntime().exec(
                    new String[]{"reg", "query",
                        "HKCU\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize",
                        "/v", "AppsUseLightTheme"});
                process.waitFor();
                java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("AppsUseLightTheme")) {
                        return line.contains("0x0");
                    }
                }
            } catch (Exception e) {
                return false;
            }
        }
        // Linux/other: check GTK theme name for "dark"
        String gtkTheme = System.getenv("GTK_THEME");
        if (gtkTheme != null && gtkTheme.toLowerCase().contains("dark")) {
            return true;
        }
        // Default to light mode
        return false;
    }
}
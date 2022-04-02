/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo
 * programming language
 *
 * @author Loïc Le Coq
 */
package xlogo;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;
import xlogo.gui.Application;
import xlogo.gui.LanguageListRenderer;

import javax.swing.*;
import java.awt.*;
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

    /**
     * The main methods
     *
     * @param args The file *.lgo to load on startup
     */
    public static void main(String[] args) {
        config.getStartupFiles().addAll(Arrays.asList(args));
        config.getStartupFiles().add(0, "#####");

        // Register syntax highlighter
        AbstractTokenMakerFactory atmf = (AbstractTokenMakerFactory) TokenMakerFactory.getDefaultInstance();
        atmf.putMapping("text/logo", "xlogo.kernel.LogoTokenMaker");

        try {
            config = Config.read();
            launchApp();
        } catch (Exception e) {
            // Default to dark look and feel
            try {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } catch (UnsupportedLookAndFeelException ex) {
                ex.printStackTrace();
            }
            config.loadDarkEditorTheme();
            // Prompt for language
            SwingUtilities.invokeLater(Logo::askLanguage);
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
}
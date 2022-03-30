/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo
 * programming language
 *
 * @author Loïc Le Coq
 */
package xlogo;

import com.formdev.flatlaf.FlatDarkLaf;
import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;
import xlogo.gui.Application;
import xlogo.gui.LanguageListRenderer;
import xlogo.resources.ResourceLoader;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * This class initializes the main frame, loads startup files and launches startup command
 *
 * @author loic
 */
public class Logo {
    public static final String VERSION = "1.0.0 beta 5";
    public static final String WEB_SITE = "github.com/jblang/xlogo";
    public static final String[] nativeLanguages = {"Francais", "English", "عربية", "Español", "Português", "Esperanto", "Deutsch", "Galego", "Asturianu", "Ελληνικά", "Italiano", "Català", "Magyar"};
    public static final String[] locales = {"fr", "en", "ar", "es", "pt", "eo", "de", "gl", "as", "el", "it", "ca", "hu"};
    public static ResourceBundle messages;
    public static String[] translatedLanguages;
    public static Config config = new Config();
    static long startupHour = Calendar.getInstance().getTimeInMillis();
    static String mainCommand = "";
    static boolean autoLaunch = false;

    /**
     * Sets the selected language for all messages
     *
     * @param id The integer that represents the language
     */
    public static void generateLanguage(int id) { // fixe la langue utilisée pour les messages
        Locale locale = Logo.getLocale(id);
        messages = ResourceLoader.getLanguageBundle(locale);
        translatedLanguages = new String[]{
                Logo.messages.getString("pref.general.french"),
                Logo.messages.getString("pref.general.english"),
                Logo.messages.getString("pref.general.arabic"),
                Logo.messages.getString("pref.general.spanish"),
                Logo.messages.getString("pref.general.portuguese"),
                Logo.messages.getString("pref.general.esperanto"),
                Logo.messages.getString("pref.general.german"),
                Logo.messages.getString("pref.general.galician"),
                Logo.messages.getString("pref.general.asturian"),
                Logo.messages.getString("pref.general.greek"),
                Logo.messages.getString("pref.general.italian"),
                Logo.messages.getString("pref.general.catalan"),
                Logo.messages.getString("pref.general.hungarian")
        };
    }

    /**
     * The main methods
     *
     * @param args The file *.lgo to load on startup
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        config.getStartupFiles().addAll(Arrays.asList(args));
        config.getStartupFiles().add(0, "#####");

        // Register syntax highlighter
        AbstractTokenMakerFactory atmf = (AbstractTokenMakerFactory) TokenMakerFactory.getDefaultInstance();
        atmf.putMapping("text/logo", "xlogo.kernel.LogoTokenMaker");

        tryConfig();
    }

    private static void launchApp() {
        readCommandLineConfig();
        generateLanguage(config.getLanguage());
        SwingUtilities.invokeLater(Application::new);
    }

    /**
     * This method returns the Locale corresponding to the language "id"
     *
     * @param id The integer that represents the language
     * @return The locale that corresponds to the desired language
     */
    public static Locale getLocale(int id) {
        switch (id) {
            case Config.LANGUAGE_FRENCH:
                return new Locale("fr", "FR");
            case Config.LANGUAGE_ENGLISH:
                return new Locale("en", "US");
            case Config.LANGUAGE_ARABIC:
                return new Locale("ar", "MA");
            case Config.LANGUAGE_SPANISH:
                return new Locale("es", "ES");
            case Config.LANGUAGE_PORTUGUESE:
                return new Locale("pt", "BR");
            case Config.LANGUAGE_ESPERANTO:
                return new Locale("eo", "EO");
            case Config.LANGUAGE_GERMAN:
                return new Locale("de", "DE");
            case Config.LANGUAGE_GALICIAN:
                return new Locale("gl", "ES");
            case Config.LANGUAGE_ASTURIAN:
                return new Locale("as", "ES");
            case Config.LANGUAGE_GREEK:
                return new Locale("el", "GR");
            case Config.LANGUAGE_ITALIAN:
                return new Locale("it", "IT");
            case Config.LANGUAGE_CATALAN:
                return new Locale("ca", "ES");
            case Config.LANGUAGE_HUNGARIAN:
                return new Locale("hu", "HU");
            default:
                return null;
        }
    }

    public static String getLocaleTwoLetters() {
        if (config.getLanguage() > -1 && config.getLanguage() < locales.length)
            return locales[config.getLanguage()];
        else
            return "en";
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
                        for (int j = 0; j < Logo.locales.length; j++) {
                            if (Logo.locales[j].equals(element)) {
                                config.setLanguage(j);
                                Logo.generateLanguage(j);
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

    /**
     * This method initializes all parameters from the file .xlogo
     */
    private static void tryConfig() {
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
            generateLanguage(Config.LANGUAGE_ENGLISH);
            SwingUtilities.invokeLater(LanguageSelection::new);
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

    static class LanguageSelection extends JFrame {
        private final JList<String> languageList = new JList<>(Logo.nativeLanguages);

        public LanguageSelection() {
            setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            setIconImage(ResourceLoader.getAppIcon().getImage());
            setLayout(new BorderLayout());
            setTitle("XLogo");
            JButton okButton = new JButton("OK");
            getContentPane().add(okButton, BorderLayout.PAGE_END);

            languageList.setCellRenderer(new LanguageListRenderer(true));
            languageList.setSelectedIndex(Logo.config.getLanguage());
            languageList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            languageList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
            languageList.setVisibleRowCount(4);
            JScrollPane scroll = new JScrollPane(languageList);
            getContentPane().add(scroll, BorderLayout.CENTER);
            okButton.addActionListener(e -> {
                config.setLanguage(languageList.getSelectedIndex());
                dispose();
                launchApp();
            });
            pack();
            setLocationRelativeTo(null);
            setVisible(true);
        }
    }

}
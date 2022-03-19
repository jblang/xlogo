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
import xlogo.gui.*;
import xlogo.kernel.Animation;
import xlogo.kernel.Kernel;
import xlogo.utils.Utils;

import javax.media.j3d.VirtualUniverse;
import javax.swing.*;
import java.awt.*;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * This class initializes the main frame, loads startup files and launches startup command
 *
 * @author loic
 */
public class Logo {
    public static final String VERSION = "1.0.0 beta 3";
    public static final String WEB_SITE = "github.com/jblang/xlogo";
    public static final String[] englishLanguage = {"French", "English", "Arabic", "Spanish", "Portuguese", "Esperanto", "German", "Galician", "Asturian", "Greek", "Italian", "Catalan", "Hungarian"};
    public static final String[] locales = {"fr", "en", "ar", "es", "pt", "eo", "de", "gl", "as", "el", "it", "ca", "hu"};
    public static ResourceBundle messages;
    public static String[] translationLanguage = new String[13];
    public static Config config = new Config();
    public static Kernel kernel;
    static int memoryLimit = config.getMemoryLimit();
    static long startupHour;
    static String mainCommand = "";
    static boolean autoLaunch = false;
    private EditorFrame editor;
    private LanguageSelection select;

    /**
     * Builds GraphFrame with the valid Config
     */
    public Logo() {
        // Read the XML file .xlogo and extract default config
        readConfig(this);

        // Overwrite loaded config with command line arguments
        readCommandLineConfig();

        if (null == messages)
            generateLanguage(config.getLanguage()); //Au cas où si le fichier de démarrage ne contient rien sur la langue
        // Initialize frame
        SwingUtilities.invokeLater(() -> {
            editor = new EditorFrame();
            editor.changeLookAndFeel();
            editor.setVisible(true);
            //On vérifie que la taille mémoire est suffisante pour créer l'image de dessin
            // Checking that we have enough memory to create the image
            int memoryRequired = config.getImageWidth() * config.getImageHeight() * 4 / 1024 / 1024;
            long free = Runtime.getRuntime().freeMemory() / 1024 / 1024;
            long total = Runtime.getRuntime().totalMemory() / 1024 / 1024;
            if (total - free + memoryRequired > getMemoryLimit() * 0.8) {
                config.setImageHeight(1000);
                config.setImageWidth(1000);
            }
            // init frame
            init(editor);
            editor.setCommandEnabled(false);
            //  On génère les primitives et les fichiers de démarrage
            // generate primitives and start up files
            editor.generatePrimitives();

            // On Enregistre le temps auquel la session a commencé
            // hour when we launch XLogo
            startupHour = Calendar.getInstance().getTimeInMillis();

            // Command to execute on startup

            // If this command is defined from the command line
            if (autoLaunch) {
                editor.startAnimation(Utils.formatCode(getMainCommand()));
                editor.getHistoryPanel().setText("normal", getMainCommand() + "\n");
            }
            // Else if this command is defined from the Start Up Dialog Box
            else if (!config.getStartupCommand().equals("")) {
                editor.graphFrame.animation = new Animation(editor.graphFrame, Utils.formatCode(config.getStartupCommand()));
                editor.graphFrame.animation.start();
            } else {
                editor.setCommandEnabled(true);
                editor.focusCommandLine();
            }
        });

    }

    public static ImageIcon getIcon(String name) {
        var res = Logo.class.getResource("resources/icons/" + name + ".svg");
        if (res == null)
            return null;
        else
            return new FlatSVGIcon(res);
    }

    public static FlatSVGIcon getFlag(String name) {
        var res = Logo.class.getResource("resources/flags/" + name + ".svg");
        if (res == null)
            return null;
        else
            return new FlatSVGIcon(res);
    }

    public static FlatSVGIcon getFlag(int i) {
        return getFlag(locales[i]);
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

    /**
     * Sets the selected language for all messages
     *
     * @param id The integer that represents the language
     */
    public static void generateLanguage(int id) { // fixe la langue utilisée pour les messages
        Locale locale = Logo.getLocale(id);
        messages = ResourceBundle.getBundle("langage", locale);
        translationLanguage[0] = Logo.messages.getString("pref.general.french");
        translationLanguage[1] = Logo.messages.getString("pref.general.english");
        translationLanguage[2] = Logo.messages.getString("pref.general.arabic");
        translationLanguage[3] = Logo.messages.getString("pref.general.spanish");
        translationLanguage[4] = Logo.messages.getString("pref.general.portuguese");
        translationLanguage[5] = Logo.messages.getString("pref.general.esperanto");
        translationLanguage[6] = Logo.messages.getString("pref.general.german");
        translationLanguage[7] = Logo.messages.getString("pref.general.galician");
        translationLanguage[8] = Logo.messages.getString("pref.general.asturian");
        translationLanguage[9] = Logo.messages.getString("pref.general.greek");
        translationLanguage[10] = Logo.messages.getString("pref.general.italian");
        translationLanguage[11] = Logo.messages.getString("pref.general.catalan");
        translationLanguage[12] = Logo.messages.getString("pref.general.hungarian");
    }

    /**
     * The main methods
     *
     * @param args The file *.lgo to load on startup
     */
    public static void main(String[] args) {

        try {
            // Display the java3d version
            var map = VirtualUniverse.getProperties();
            System.out.println("Java3d :" + map.get("j3d.version"));
        } catch (Exception e) {
            System.out.println("Java3d problem");
            e.printStackTrace();
        }

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Recuperer les fichiers de démarrage correspondant au double clic de souris
        // ou au lancement en ligne de commande

        config.getStartupFiles().addAll(Arrays.asList(args));
        config.getStartupFiles().add(0, "#####");

        //try{;
        new Logo();
    }

    /**
     * This method returns the Locale corresponding to the language "id"
     *
     * @param id The integer that represents the language
     * @return The locale that corresponds to the desired language
     */
    public static Locale getLocale(int id) {  // rend la locale
        // correspondant à l'entier id
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
        if (config.getLanguage() > -1 && config.getLanguage() < locales.length) return locales[config.getLanguage()];
        else return "en";
    }

    /**
     * This method initializes all parameters from the file .xlogo
     * @param logo
     */
    private static void readConfig(Logo logo) {
        try {
            FileInputStream fis = new FileInputStream(System.getProperty("user.home") + File.separator + ".xlogo");
            XMLDecoder dec = new XMLDecoder(fis);
            config = (Config) dec.readObject();
            memoryLimit = config.getMemoryLimit();
            dec.close();
            fis.close();
       } catch (Exception e) {
            e.printStackTrace();
            try {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } catch (UnsupportedLookAndFeelException ex) {
                ex.printStackTrace();
            }
            try {
                SwingUtilities.invokeAndWait(() -> logo.select = new LanguageSelection());
            } catch (Exception ignored) {
            }
            while (!logo.select.getSelection_faite()) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ignored) {
                }
            }
            logo.select.dispose();
            generateLanguage(config.getLanguage());
        }
        // Verify that all values are in valid range
    }

    /**
     * Write the Configuration file when the user quits XLogo
     */
    public static void writeConfig() {
        try {
            FileOutputStream fos = new FileOutputStream(System.getProperty("user.home") + File.separator + ".xlogo");
            XMLEncoder enc = new XMLEncoder(fos);
            enc.writeObject(config);
            enc.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static long getStartupHour() {
        return startupHour;
    }

    public static int getMemoryLimit() {
        return memoryLimit;
    }

    public static String getMainCommand() {
        return mainCommand;
    }

    public static void setMainCommand(String mainCommand) {
        Logo.mainCommand = mainCommand;
    }

    /**
     * Initializes the main Frame
     *
     * @param editor The main Frame
     */
    private void init(EditorFrame editor) {
        // on centre la tortue
        // Centering turtle
        Dimension d = editor.graphFrame.scrollPane.getViewport().getViewRect().getSize();
        Point p = new Point(Math.abs(config.getImageWidth() / 2 - d.width / 2), Math.abs(config.getImageHeight() / 2 - d.height / 2));
        editor.graphFrame.scrollPane.getViewport().setViewPosition(p);

        // on affiche la tortue sur la zone de dessin
        // Displays turtle
        MediaTracker tracker = new MediaTracker(editor);
        try {
            tracker.waitForID(0);
        } catch (InterruptedException ignored) {
        }
        editor.graphFrame.scrollPane.validate();//getArdoise().revalidate();
   }

    /**
     * This method initializes all XLogo's parameters from Command Line
     * java -jar xlogo.jar -a -lang fr file1.lgo ...
     */
    private void readCommandLineConfig() {
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
                            memoryLimit = mem;
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

}
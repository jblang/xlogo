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
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import xlogo.gui.Application;
import xlogo.gui.LanguageSelection;
import xlogo.kernel.Animation;
import xlogo.utils.SimpleContentHandler;
import xlogo.utils.Utils;

import javax.media.j3d.VirtualUniverse;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * This class initializes the main frame, loads startup files and launches startup command
 *
 * @author loic
 */
public class Logo {
    public static final String[] englishLanguage = {"French", "English", "Arabic", "Spanish", "Portuguese", "Esperanto", "German", "Galician", "Asturian", "Greek", "Italian", "Catalan", "Hungarian"};
    public static final String[] locales = {"fr", "en", "ar", "es", "pt", "eo", "de", "gl", "as", "el", "it", "ca", "hu"};
    /**
     * This ResourceBundle contains all messages for XLogo (menu, errors...)
     */
    public static ResourceBundle messages = null;
    public static String[] translationLanguage = new String[13];
    /**
     * The main frame
     */
    private Application frame = null;

    /**
     * On the first start, XLogo opens a dialog box where the user can select its language.
     */
    private LanguageSelection select = null;

//  private Language language;

    /**
     * Builds Application with the valid Config
     */
    public Logo() {
        // Read the XML file .xlogo and extract default config
        readConfig();

        // Overwrite loaded config with command line arguments
        readCommandLineConfig();

        Config.defaultFolder = Utils.rajoute_backslash(Config.defaultFolder);
        if (null == messages)
            generateLanguage(Config.language); //Au cas où si le fichier de démarrage ne contient rien sur la langue
        // Initialize frame
        SwingUtilities.invokeLater(() -> {
            frame = new Application();

            frame.setVisible(true);
            //On vérifie que la taille mémoire est suffisante pour créer l'image de dessin
            // Checking that we have enough memory to create the image
            int memoire_necessaire = Config.imageWidth * Config.imageHeight * 4 / 1024 / 1024;
            long free = Runtime.getRuntime().freeMemory() / 1024 / 1024;
            long total = Runtime.getRuntime().totalMemory() / 1024 / 1024;
            if (total - free + memoire_necessaire > Config.memoryLimit * 0.8) {
                Config.imageHeight = 1000;
                Config.imageWidth = 1000;
            }
            // init frame
            init(frame);
            frame.setCommandEnabled(false);
            //  On génère les primitives et les fichiers de démarrage
            // generate primitives and start up files
            frame.generatePrimitives();

            // On Enregistre le temps auquel la session a commencé
            // hour when we launch XLogo
            Config.startupHour = Calendar.getInstance().getTimeInMillis();


            // Command to execute on startup

            // If this command is defined from the command line
            if (Config.autoLaunch) {
                frame.startAnimation(Utils.decoupe(Config.mainCommand));
                frame.getHistoryPanel().ecris("normal", Config.mainCommand + "\n");
            }
            // Else if this command is defined from the Start Up Dialog Box
            else if (!Config.startupCommand.equals("")) {
                frame.animation = new Animation(frame, Utils.decoupe(Config.startupCommand));
                frame.animation.start();
            } else {
                frame.setCommandEnabled(true);
                frame.focusCommandLine();
            }
        });

    }

    public static ImageIcon getIcon(String name) {
        var path = "resources/icons/" + name + ".svg";
        return new FlatSVGIcon(Objects.requireNonNull(Logo.class.getResource(path)));
    }

    public static FlatSVGIcon getFlag(String name) {
        var path = "resources/flags/" + name + ".svg";
        return new FlatSVGIcon(Objects.requireNonNull(Logo.class.getResource(path)));
    }

    public static FlatSVGIcon getFlag(int i) {
        return getFlag(locales[i]);
    }

    public static ImageIcon getAppIcon() {
        var path = "resources/appicon.png";
        return new ImageIcon(Objects.requireNonNull(Logo.class.getResource(path)));
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

        Config.startupFiles.addAll(Arrays.asList(args));
        Config.startupFiles.add(0, "#####");
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
        return switch (id) {
            case Config.LANGUAGE_FRENCH -> new Locale("fr", "FR");
            case Config.LANGUAGE_ENGLISH -> new Locale("en", "US");
            case Config.LANGUAGE_ARABIC -> new Locale("ar", "MA");
            case Config.LANGUAGE_SPANISH -> new Locale("es", "ES");
            case Config.LANGUAGE_PORTUGUESE -> new Locale("pt", "BR");
            case Config.LANGUAGE_ESPERANTO -> new Locale("eo", "EO");
            case Config.LANGUAGE_GERMAN -> new Locale("de", "DE");
            case Config.LANGUAGE_GALICIAN -> new Locale("gl", "ES");
            case Config.LANGUAGE_ASTURIAN -> new Locale("as", "ES");
            case Config.LANGUAGE_GREEK -> new Locale("el", "GR");
            case Config.LANGUAGE_ITALIAN -> new Locale("it", "IT");
            case Config.LANGUAGE_CATALAN -> new Locale("ca", "ES");
            case Config.LANGUAGE_HUNGARIAN -> new Locale("hu", "HU");
            default -> null;
        };
    }

    public static String getLocaleTwoLetters() {
        if (Config.language > -1 && Config.language < locales.length) return locales[Config.language];
        else return "en";
    }

    /**
     * Initializes the main Frame
     *
     * @param frame The main Frame
     */
    private void init(Application frame) {
        // on centre la tortue
        // Centering turtle
        Dimension d = frame.scrollPane.getViewport().getViewRect().getSize();
        Point p = new Point(Math.abs(Config.imageWidth / 2 - d.width / 2), Math.abs(Config.imageHeight / 2 - d.height / 2));
        frame.scrollPane.getViewport().setViewPosition(p);

        // on affiche la tortue sur la zone de dessin
        // Displays turtle
        MediaTracker tracker = new MediaTracker(frame);
        try {
            tracker.waitForID(0);
        } catch (InterruptedException ignored) {
        }
        frame.scrollPane.validate();//getArdoise().revalidate();
   }

    /**
     * This method initializes all XLogo's parameters from Command Line
     * java -jar xlogo.jar -a -lang fr file1.lgo ...
     */
    private void readCommandLineConfig() {
        int i = 0;
        while (i < Config.startupFiles.size()) {
            String element = Config.startupFiles.get(i);
            // AutoLaunch main Command on startup
            switch (element) {
                case "-a" -> {
                    Config.autoLaunch = true;
                    Config.startupFiles.remove(i);
                }
                // Choosing language
                case "-lang" -> {
                    Config.startupFiles.remove(i);
                    if (i < Config.startupFiles.size()) {
                        element = Config.startupFiles.get(i);
                        for (int j = 0; j < Logo.locales.length; j++) {
                            if (Logo.locales[j].equals(element)) {
                                Config.language = j;
                                Logo.generateLanguage(j);
                                break;
                            }
                        }
                        Config.startupFiles.remove(i);
                    }
                }
                // Memory Heap Size
                case "-memory" -> {
                    Config.startupFiles.remove(i);
                    if (i < Config.startupFiles.size()) {
                        element = Config.startupFiles.get(i);
                        try {
                            int mem = Integer.parseInt(element);
                            Config.memoryLimit = mem;
                            Config.newMemoryLimit = mem;
                            Config.startupFiles.remove(i);

                        } catch (NumberFormatException ignored) {
                        }
                    }
                }
                // TCP port
                case "-tcp_port" -> {
                    Config.startupFiles.remove(i);
                    if (i < Config.startupFiles.size()) {
                        element = Config.startupFiles.get(i);
                        try {
                            int port = Integer.parseInt(element);
                            if (port <= 0) port = 1948;
                            Config.tcpPort = port;
                            Config.startupFiles.remove(i);

                        } catch (NumberFormatException e) {
                            Config.tcpPort = 1948;
                        }
                    }
                }

                // Logo Files
                default -> i++;
            }
        }
    }

    /**
     * This method initializes all parameters from the file .xlogo
     */
    private void readConfig() {
        try {
            // Try to read XML format (new config file)
            FileInputStream fr = new FileInputStream(System.getProperty("user.home") + File.separator + ".xlogo");
            BufferedInputStream bis = new BufferedInputStream(fr);
            InputStreamReader isr = new InputStreamReader(bis, StandardCharsets.UTF_8);
            try {
                XMLReader saxReader = XMLReaderFactory.createXMLReader();
                saxReader.setContentHandler(new SimpleContentHandler());
                saxReader.parse(new InputSource(isr));
            } catch (SAXException e) {
                // Read the old config file format
                String s = "";
                FileReader ifr = new FileReader(System.getProperty("user.home") + File.separator + ".xlogo");
                while (ifr.ready()) {
                    char[] b = new char[64];
                    int i = ifr.read(b);
                    if (i == -1) break;
                    s += new String(b);
                }
                StringTokenizer st = new StringTokenizer(s, "\n");

                while (st.hasMoreTokens()) {
                    String element = st.nextToken();

                    switch (element) {
                        case "# langue": {
                            element = st.nextToken();
                            int id = Integer.parseInt(element);
                            Config.language = id;
                            generateLanguage(id);
                            break;
                        }
                        case "# vitesse":
                            element = st.nextToken();
                            Config.turtleSpeed = Integer.parseInt(element);
                            break;
                        case "# tortue choisie":
                            element = st.nextToken();
                            Config.activeTurtle = Integer.parseInt(element);
                            break;
                        case "# nb max de tortues":
                            element = st.nextToken();
                            Config.maxTurtles = Integer.parseInt(element);
                            break;
                        case "# forme crayon":
                            element = st.nextToken();
                            Config.penShape = Integer.parseInt(element);
                            break;
                        case "# effacer dessin en quittant editeur": {
                            int id = Integer.parseInt(st.nextToken());
                            Config.eraseImage = id != 0;
                            break;
                        }
                        case "# epaisseur max crayon":
                            element = st.nextToken();
                            Config.maxPenWidth = Integer.parseInt(element);
                            break;
                        case "# repertoire par defaut":
                            Config.defaultFolder = st.nextToken();
                            File f = new File(Config.defaultFolder);
                            if (!f.isDirectory()) Config.defaultFolder = System.getProperty("user.home");
                            break;
                        case "# a executer au demarrage":
                            element = st.nextToken();
                            if (!element.equals("# aucun")) Config.startupCommand = element;
                            break;
                        case "# police":
                            element = st.nextToken();
                            String nom = element;
                            element = st.nextToken();
                            Config.font = new Font(nom, Font.PLAIN, Integer.parseInt(element));
                            break;
                        case "# hauteur":
                            Config.imageHeight = Integer.parseInt(st.nextToken());
                            break;
                        case "# largeur":
                            Config.imageWidth = Integer.parseInt(st.nextToken());
                            break;
                        case "# memoire":
                            element = st.nextToken();
                            Config.memoryLimit = Integer.parseInt(element);
                            Config.newMemoryLimit = Integer.parseInt(element);
                            break;
                        case "# qualite":
                            element = st.nextToken();
                            Config.drawQuality = Integer.parseInt(element);
                            break;
                        case "# coloration":
                            element = st.nextToken();
                            StringTokenizer sti = new StringTokenizer(element);
                            if (sti.countTokens() == 9) {
                                Config.syntaxHighlightingEnabled = Boolean.parseBoolean(sti.nextToken());
                                Config.syntaxCommentColor = Integer.parseInt(sti.nextToken());
                                Config.syntaxOperandColor = Integer.parseInt(sti.nextToken());
                                Config.syntaxBracketColor = Integer.parseInt(sti.nextToken());
                                Config.syntaxPrimitiveColor = Integer.parseInt(sti.nextToken());
                                Config.syntaxCommentStyle = Integer.parseInt(sti.nextToken());
                                Config.syntaxOperandStyle = Integer.parseInt(sti.nextToken());
                                Config.syntaxBracketStyle = Integer.parseInt(sti.nextToken());
                                Config.syntaxPrimitiveStyle = Integer.parseInt(sti.nextToken());
                            }
                            break;
                        case "# fichiers de demarrage":
                            while (st.hasMoreTokens()) {
                                element = st.nextToken();
                                if (!element.startsWith("#")) Config.startupFiles.add(element);
                            }
                            break;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    public void run() {
                        select = new LanguageSelection();
                    }
                });
                try {
                    UIManager.setLookAndFeel(new FlatDarkLaf());
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
            } catch (Exception ignored) {
            }
            while (!select.getSelection_faite()) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ignored) {
                }
            }
            select.dispose();
            generateLanguage(Config.language);
        }
        // Verify that all values are in valid range
    }
}
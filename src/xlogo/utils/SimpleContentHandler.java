package xlogo.utils;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import xlogo.Config;
import xlogo.Logo;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo
 * programming language
 *
 * @author Loïc Le Coq
 */

public class SimpleContentHandler implements ContentHandler {

    public SimpleContentHandler() {
        super();
        // We define default Locator
//                   locator = new LocatorImpl();
    }

    public void setDocumentLocator(Locator value) {
        //                 locator =  value;
    }

    public void startDocument() throws SAXException {
    }

    public void endDocument() throws SAXException {
    }

    public void startPrefixMapping(String prefix, String URI) throws SAXException {
    }

    public void endPrefixMapping(String prefix) throws SAXException {
    }

    // Open new Tag
    public void startElement(String nameSpaceURI, String localName, String rawName, Attributes attributs) throws SAXException {
        analyseBalise(localName, attributs);
/*                   System.out.println("Ouverture de la balise: "+localName);
                   for (int index = 0; index < attributs.getLength(); index++) { // on parcourt la liste des attributs
                           System.out.println("     - " +  attributs.getLocalName(index) + " = " + attributs.getValue(index));
                   }*/
    }

    // Close tag
    public void endElement(String nameSpaceURI, String localName, String rawName) throws SAXException {
        /*           System.out.print("Fermeture de la balise : " + localName);

                   if ( ! "".equals(nameSpaceURI)) { // name space non null
                           System.out.print("appartenant a l'espace de nommage : " + localName);
                   }

                   System.out.println();*/
    }

    public void characters(char[] ch, int start, int end) throws SAXException {
    }

    public void ignorableWhitespace(char[] ch, int start, int end) throws SAXException {
    }

    public void processingInstruction(String target, String data) throws SAXException {
    }

    public void skippedEntity(String arg0) throws SAXException {
    }

    private void analyseBalise(String tag, Attributes attributs) {
        if (tag.equals("lang")) {
            int id;
            try {
                id = Integer.parseInt(attributs.getValue(0));
            } catch (NumberFormatException e) {
                id = Config.LANGUAGE_ENGLISH;
            }
            Config.langage = id;
            Logo.generateLanguage(id);
        } else if (tag.equals("speed")) {
            int id;
            try {
                id = Integer.parseInt(attributs.getValue(0));
                // Check if the turtle speed is valid
                if (id < 0 || id > 100) id = 0;
            } catch (NumberFormatException e) {
                id = 0;
            }
            Config.turtleSpeed = id;
        } else if (tag.equals("tcp_port")) {
            int id;
            try {
                id = Integer.parseInt(attributs.getValue(0));
                // Check if the turtle speed is valid
                if (id <= 0) id = 1948;
            } catch (NumberFormatException e) {
                id = 1948;
            }
            Config.TCP_PORT = id;
        } else if (tag.equals("turtle_shape")) {
            int id;
            try {
                id = Integer.parseInt(attributs.getValue(0));
                String chemin = "tortue" + id + ".png";
                // Check if the turtle image exists
                java.net.URL url = Utils.class.getResource(chemin);
                if (null == url) id = 0;
            } catch (NumberFormatException e) {
                id = 0;
            }

            Config.activeTurtle = id;
        } else if (tag.equals("max_number_turtle")) {
            int id;
            try {
                id = Integer.parseInt(attributs.getValue(0));
            } catch (NumberFormatException e) {
                id = 16;
            }
            Config.maxTurtles = id;
        } else if (tag.equals("pen_shape")) {
            int id;
            try {
                id = Integer.parseInt(attributs.getValue(0));
                // Check if this integer is valid
                if (id != Config.PEN_SHAPE_OVAL && id != Config.PEN_SHAPE_SQUARE) id = 0;
            } catch (NumberFormatException e) {
                id = Config.PEN_SHAPE_SQUARE;
            }

            Config.penShape = id;
        } else if (tag.equals("cleanscreen_leaving_editor")) {
            int id;
            try {
                id = Integer.parseInt(attributs.getValue(0));
            } catch (NumberFormatException e) {
                id = 0;
            }

			Config.eraseImage = id != 0;
        } else if (tag.equals("clear_variables_closing_editor")) {
            int id;
            try {
                id = Integer.parseInt(attributs.getValue(0));
            } catch (NumberFormatException e) {
                id = 0;
            }

			Config.clearVariables = id != 0;
        } else if (tag.equals("pen_width_max")) {
            int id;
            try {
                id = Integer.parseInt(attributs.getValue(0));
            } catch (NumberFormatException e) {
                id = -1;
            }
            Config.maxPenWidth = id;
        } else if (tag.equals("default_directory")) {
            Config.defaultFolder = attributs.getValue(0);
            File f = new File(Config.defaultFolder);
            if (null == f || !f.isDirectory()) Config.defaultFolder = System.getProperty("user.home");
        } else if (tag.equals("start_command")) {
            if (attributs.getLength() != 0)
                Config.a_executer = attributs.getValue(0);
        } else if (tag.equals("font")) {
            String name = "";
            int size = 0;
            for (int index = 0; index < attributs.getLength(); index++) { // on parcourt la liste des attributs
                if (attributs.getLocalName(index).equals("name"))
                    name = attributs.getValue(index);
                else if (attributs.getLocalName(index).equals("size"))
                    try {
                        size = Integer.parseInt(attributs.getValue(index));
                    } catch (NumberFormatException e) {
                        size = 14;
                    }
            }

            Config.police = new Font(name, Font.PLAIN, size);
        } else if (tag.equals("width")) {
            int id;
            try {
                id = Integer.parseInt(attributs.getValue(0));
                // check if valid
                if (id <= 0) id = 1000;
            } catch (NumberFormatException e) {
                id = 1000;
            }

            Config.imageWidth = id;
        } else if (tag.equals("pen_color")) {
            int id;
            try {
                id = Integer.parseInt(attributs.getValue(0));
            } catch (NumberFormatException e) {
                id = Color.BLACK.getRGB();
            }

            Config.pencolor = new Color(id);
        } else if (tag.equals("screen_color")) {
            int id;
            try {
                id = Integer.parseInt(attributs.getValue(0));
            } catch (NumberFormatException e) {
                id = Color.WHITE.getRGB();
            }
            Config.screencolor = new Color(id);
        } else if (tag.equals("height")) {
            int id;
            try {
                id = Integer.parseInt(attributs.getValue(0));
                // check valid
                if (id <= 0) id = 1000;
            } catch (NumberFormatException e) {
                id = 1000;
            }

            Config.imageHeight = id;
        } else if (tag.equals("memory")) {
            int id;
            try {
                id = Integer.parseInt(attributs.getValue(0));
                // check valid
                if (id <= 0) id = 64;
            } catch (NumberFormatException e) {
                id = 64;
            }
            Config.memoire = id;
            Config.tmp_memoire = id;
        } else if (tag.equals("quality")) {
            int id;
            try {
                id = Integer.parseInt(attributs.getValue(0));
                // check valid
                if (id != Config.QUALITY_HIGH && id != Config.QUALITY_LOW && id != Config.QUALITY_NORMAL)
                    id = Config.QUALITY_NORMAL;
            } catch (NumberFormatException e) {
                id = Config.QUALITY_NORMAL;
            }
            Config.quality = id;
        } else if (tag.equals("grid")) {

            for (int index = 0; index < attributs.getLength(); index++) { // on parcourt la liste des attributs
                String att = attributs.getLocalName(index);
                if (att.equals("boolean")) {
                    Config.drawGrid = Boolean.parseBoolean(attributs.getValue(index));
                } else if (att.equals("xgrid")) {
                    try {
                        Config.XGrid = Integer.parseInt(attributs.getValue(index));
                    } catch (NumberFormatException e) {
                        Config.XGrid = 30;
                    }
                } else if (att.equals("ygrid")) {
                    try {
                        Config.YGrid = Integer.parseInt(attributs.getValue(index));
                    } catch (NumberFormatException e) {
                        Config.YGrid = 30;
                    }

                } else if (att.equals("gridcolor")) {
                    try {
                        Config.gridColor = Integer.parseInt(attributs.getValue(index));
                    } catch (NumberFormatException e) {
                        Config.gridColor = Color.LIGHT_GRAY.getRGB();
                    }
                }
            }
        } else if (tag.equals("axis")) {
            for (int index = 0; index < attributs.getLength(); index++) { // on parcourt la liste des attributs
                String att = attributs.getLocalName(index);
                if (att.equals("boolean_xaxis")) {
                    Config.drawXAxis = Boolean.parseBoolean(attributs.getValue(index));
                } else if (att.equals("boolean_yaxis")) {
                    Config.drawYAxis = Boolean.parseBoolean(attributs.getValue(index));
                } else if (att.equals("xaxis")) {
                    try {
                        Config.XAxis = Integer.parseInt(attributs.getValue(index));
                    } catch (NumberFormatException e) {
                        Config.XAxis = 50;
                    }
                } else if (att.equals("yaxis")) {
                    try {
                        Config.YAxis = Integer.parseInt(attributs.getValue(index));
                    } catch (NumberFormatException e) {
                        Config.YAxis = 50;
                    }
                } else if (att.equals("axiscolor")) {
                    try {
                        Config.axisColor = Integer.parseInt(attributs.getValue(index));
                    } catch (NumberFormatException e) {
                        Config.axisColor = Color.BLACK.getRGB();
                    }
                }
            }
        } else if (tag.equals("border_image")) {
            for (int index = 0; index < attributs.getLength(); index++) { // on parcourt la liste des attributs
                Config.borderExternalImage.add(attributs.getValue(index));
            }
        } else if (tag.equals("border_color")) {
            try {
                Config.borderColor = new Color(Integer.parseInt(attributs.getValue(0)));
            } catch (NumberFormatException e) {
                Config.borderColor = new java.awt.Color(218, 176, 130);
            }

        } else if (tag.equals("border_image_selected")) {
            Config.borderImageSelected = attributs.getValue(0);
        } else if (tag.equals("looknfeel")) {
            try {
                Config.looknfeel = Integer.parseInt(attributs.getValue(0));
            } catch (NumberFormatException e) {
                Config.looknfeel = Config.LOOKNFEEL_JAVA;
            }
            try {
                switch (Config.looknfeel) {
                    case Config.LOOKNFEEL_NATIVE:
                        UIManager.setLookAndFeel(UIManager
                                .getSystemLookAndFeelClassName());
                        break;
                    case Config.LOOKNFEEL_JAVA:
                        UIManager.setLookAndFeel(new javax.swing.plaf.metal.MetalLookAndFeel());
                        break;
                    case Config.LOOKNFEEL_MOTIF:
                        //UIManager.setLookAndFeel(new com.sun.java.swing.plaf.motif.MotifLookAndFeel());
                        break;
                    default:
                        UIManager.setLookAndFeel(new javax.swing.plaf.metal.MetalLookAndFeel());
                        break;
                }

            } catch (Exception exc) {
                System.out.println(exc);
            }
        } else if (tag.equals("syntax_highlighting")) {
            for (int index = 0; index < attributs.getLength(); index++) { // on parcourt la liste des attributs
                String att = attributs.getLocalName(index);
                if (att.equals("boolean")) {
                    Config.COLOR_ENABLED = Boolean.parseBoolean(attributs.getValue(index));
//           				Config.COLOR_ENABLED=Boolean.parseBoolean(attributs.getValue(index));
                } else if (att.equals("color_commentaire")) {
                    try {
                        Config.coloration_commentaire = Integer.parseInt(attributs.getValue(index));
                    } catch (NumberFormatException e) {
                    }
                } else if (att.equals("color_operand")) {
                    try {
                        Config.coloration_operande = Integer.parseInt(attributs.getValue(index));
                    } catch (NumberFormatException e) {
                    }
                } else if (att.equals("color_parenthesis")) {
                    try {
                        Config.coloration_parenthese = Integer.parseInt(attributs.getValue(index));
                    } catch (NumberFormatException e) {
                    }
                } else if (att.equals("color_primitive")) {
                    try {
                        Config.coloration_primitive = Integer.parseInt(attributs.getValue(index));
                    } catch (NumberFormatException e) {
                    }
                } else if (att.equals("style_commentaire")) {
                    try {
                        Config.style_commentaire = Integer.parseInt(attributs.getValue(index));
                    } catch (NumberFormatException e) {
                    }
                } else if (att.equals("style_operand")) {
                    try {
                        Config.style_operande = Integer.parseInt(attributs.getValue(index));
                    } catch (NumberFormatException e) {
                    }
                } else if (att.equals("style_parenthesis")) {
                    try {
                        Config.style_parenthese = Integer.parseInt(attributs.getValue(index));
                    } catch (NumberFormatException e) {
                    }
                } else if (att.equals("style_primitive")) {
                    try {
                        Config.style_primitive = Integer.parseInt(attributs.getValue(index));
                    } catch (NumberFormatException e) {
                    }
                }
            }
        } else if (tag.equals("startup_files")) {
            for (int index = 0; index < attributs.getLength(); index++) { // on parcourt la liste des attributs
                Config.path.add(0, attributs.getValue(index));
            }
        }
    }
}
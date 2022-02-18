package xlogo.utils;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
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
            Config.language = id;
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
            Config.tcpPort = id;
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
                Config.startupCommand = attributs.getValue(0);
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

            Config.font = new Font(name, Font.PLAIN, size);
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

            Config.penColor = new Color(id);
        } else if (tag.equals("screen_color")) {
            int id;
            try {
                id = Integer.parseInt(attributs.getValue(0));
            } catch (NumberFormatException e) {
                id = Color.WHITE.getRGB();
            }
            Config.screenColor = new Color(id);
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
            Config.memoryLimit = id;
            Config.newMemoryLimit = id;
        } else if (tag.equals("quality")) {
            int id;
            try {
                id = Integer.parseInt(attributs.getValue(0));
                // check valid
                if (id != Config.DRAW_QUALITY_HIGH && id != Config.DRAW_QUALITY_LOW && id != Config.DRAW_QUALITY_NORMAL)
                    id = Config.DRAW_QUALITY_NORMAL;
            } catch (NumberFormatException e) {
                id = Config.DRAW_QUALITY_NORMAL;
            }
            Config.drawQuality = id;
        } else if (tag.equals("grid")) {

            for (int index = 0; index < attributs.getLength(); index++) { // on parcourt la liste des attributs
                String att = attributs.getLocalName(index);
                if (att.equals("boolean")) {
                    Config.gridEnabled = Boolean.parseBoolean(attributs.getValue(index));
                } else if (att.equals("xgrid")) {
                    try {
                        Config.xGridSpacing = Integer.parseInt(attributs.getValue(index));
                    } catch (NumberFormatException e) {
                        Config.xGridSpacing = 30;
                    }
                } else if (att.equals("ygrid")) {
                    try {
                        Config.yGridSpacing = Integer.parseInt(attributs.getValue(index));
                    } catch (NumberFormatException e) {
                        Config.yGridSpacing = 30;
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
                    Config.xAxisEnabled = Boolean.parseBoolean(attributs.getValue(index));
                } else if (att.equals("boolean_yaxis")) {
                    Config.yAxisEnabled = Boolean.parseBoolean(attributs.getValue(index));
                } else if (att.equals("xaxis")) {
                    try {
                        Config.xAxisSpacing = Integer.parseInt(attributs.getValue(index));
                    } catch (NumberFormatException e) {
                        Config.xAxisSpacing = 50;
                    }
                } else if (att.equals("yaxis")) {
                    try {
                        Config.yAxisSpacing = Integer.parseInt(attributs.getValue(index));
                    } catch (NumberFormatException e) {
                        Config.yAxisSpacing = 50;
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
                Config.userBorderImages.add(attributs.getValue(index));
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
                Config.lookAndFeel = Integer.parseInt(attributs.getValue(0));
            } catch (NumberFormatException e) {
                Config.lookAndFeel = Config.LAF_DARK;
            }
            try {
                switch (Config.lookAndFeel) {
                    case Config.LAF_NATIVE:
                        UIManager.setLookAndFeel(UIManager
                                .getSystemLookAndFeelClassName());
                        break;
                    case Config.LAF_LIGHT:
                        UIManager.setLookAndFeel(new FlatLightLaf());
                        break;
                    default:
                        UIManager.setLookAndFeel(new FlatDarkLaf());
                        break;
                }

            } catch (Exception exc) {
                System.out.println(exc);
            }
        } else if (tag.equals("syntax_highlighting")) {
            for (int index = 0; index < attributs.getLength(); index++) { // on parcourt la liste des attributs
                String att = attributs.getLocalName(index);
                if (att.equals("boolean")) {
                    Config.syntaxHighlightingEnabled = Boolean.parseBoolean(attributs.getValue(index));
//           				Config.COLOR_ENABLED=Boolean.parseBoolean(attributs.getValue(index));
                } else if (att.equals("color_commentaire")) {
                    try {
                        Config.syntaxCommentColor = Integer.parseInt(attributs.getValue(index));
                    } catch (NumberFormatException e) {
                    }
                } else if (att.equals("color_operand")) {
                    try {
                        Config.syntaxOperandColor = Integer.parseInt(attributs.getValue(index));
                    } catch (NumberFormatException e) {
                    }
                } else if (att.equals("color_parenthesis")) {
                    try {
                        Config.syntaxBracketColor = Integer.parseInt(attributs.getValue(index));
                    } catch (NumberFormatException e) {
                    }
                } else if (att.equals("color_primitive")) {
                    try {
                        Config.syntaxPrimitiveColor = Integer.parseInt(attributs.getValue(index));
                    } catch (NumberFormatException e) {
                    }
                } else if (att.equals("style_commentaire")) {
                    try {
                        Config.syntaxCommentStyle = Integer.parseInt(attributs.getValue(index));
                    } catch (NumberFormatException e) {
                    }
                } else if (att.equals("style_operand")) {
                    try {
                        Config.syntaxOperandStyle = Integer.parseInt(attributs.getValue(index));
                    } catch (NumberFormatException e) {
                    }
                } else if (att.equals("style_parenthesis")) {
                    try {
                        Config.syntaxBracketStyle = Integer.parseInt(attributs.getValue(index));
                    } catch (NumberFormatException e) {
                    }
                } else if (att.equals("style_primitive")) {
                    try {
                        Config.syntaxPrimitiveStyle = Integer.parseInt(attributs.getValue(index));
                    } catch (NumberFormatException e) {
                    }
                }
            }
        } else if (tag.equals("startup_files")) {
            for (int index = 0; index < attributs.getLength(); index++) { // on parcourt la liste des attributs
                Config.startupFiles.add(0, attributs.getValue(index));
            }
        }
    }
}
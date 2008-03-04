
package xlogo.utils;

import java.awt.Font;
import java.io.File;
import javax.swing.UIManager;
import java.awt.Color;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import xlogo.Config;
import xlogo.Logo;
/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo 
 * 						programming language
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
                   analyseBalise(localName,attributs);
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
           private void analyseBalise(String tag,Attributes attributs){
           	if (tag.equals("lang")){
                int id=Integer.parseInt(attributs.getValue(0));
           		Config.langage=id;
           		Logo.generateLanguage(id);
           	}
           	else if (tag.equals("speed")){
           		Config.turtleSpeed=Integer.parseInt(attributs.getValue(0));
           	}
           	else if (tag.equals("turtle_shape")){
           		Config.activeTurtle=Integer.parseInt(attributs.getValue(0));
           	}
           	else if (tag.equals("max_number_turtle")){
           		Config.maxTurtles=Integer.parseInt(attributs.getValue(0));
           	}
           	else if (tag.equals("pen_shape")){
           		Config.penShape=Integer.parseInt(attributs.getValue(0));
           	}
           	else if (tag.equals("cleanscreen_leaving_editor")){
    			int id=Integer.parseInt(attributs.getValue(0));
    			if (id==0) Config.eraseImage=false;
    			else Config.eraseImage=true;
           	}
           	else if (tag.equals("pen_width_max")){
           		Config.maxPenWidth=Integer.parseInt(attributs.getValue(0));
           	}
           	else if (tag.equals("default_directory")){
                Config.defaultFolder=attributs.getValue(0);
                File f=new File(Config.defaultFolder);
                if (!f.isDirectory()) Config.defaultFolder=System.getProperty("user.home");
           	}
           	else if (tag.equals("start_command")){
           		if (attributs.getLength()!=0)
           			Config.a_executer=attributs.getValue(0);
           	}
           	else if (tag.equals("font")){
    			String name="";
    			int size=0;
           		for (int index = 0; index < attributs.getLength(); index++) { // on parcourt la liste des attributs
           			if (attributs.getLocalName(index).equals("name"))  
           				name=attributs.getValue(index);
           			else if (attributs.getLocalName(index).equals("size"))
           				size=Integer.parseInt(attributs.getValue(index));
           		}
           		Config.police=new Font(name,Font.PLAIN,size);
           	}
        	else if (tag.equals("width")){
           		Config.imageWidth=Integer.parseInt(attributs.getValue(0));
           	}
        	else if (tag.equals("pen_color")){
        		Config.pencolor=new Color(Integer.parseInt(attributs.getValue(0)));
        	}
        	else if (tag.equals("screen_color")){
        		Config.screencolor=new Color(Integer.parseInt(attributs.getValue(0)));
        	}
        	else if (tag.equals("height")){
           		Config.imageHeight=Integer.parseInt(attributs.getValue(0));
           	}
        	else if (tag.equals("memory")){
           		Config.memoire=Integer.parseInt(attributs.getValue(0));
           		Config.tmp_memoire=Integer.parseInt(attributs.getValue(0));
           	}
        	else if (tag.equals("quality")){
           		Config.quality=Integer.parseInt(attributs.getValue(0));
           	}
        	else if (tag.equals("grid")){
          		for (int index = 0; index < attributs.getLength(); index++) { // on parcourt la liste des attributs
          			String att=attributs.getLocalName(index);
           			if (att.equals("boolean"))  {
           				Boolean b=new Boolean(attributs.getValue(index));
           				Config.drawGrid=b.booleanValue();
           			}
           			else if (att.equals("xgrid")){
           				Config.XGrid=Integer.parseInt(attributs.getValue(index));
           			}
           			else if (att.equals("ygrid")){
           				Config.YGrid=Integer.parseInt(attributs.getValue(index));
           			}
           			else if (att.equals("gridcolor")){
           				Config.gridColor=Integer.parseInt(attributs.getValue(index));
           			}
           		}
        	}
        	else if (tag.equals("axis")){
          		for (int index = 0; index < attributs.getLength(); index++) { // on parcourt la liste des attributs
          			String att=attributs.getLocalName(index);
           			if (att.equals("boolean_xaxis"))  {
           				Boolean b=new Boolean(attributs.getValue(index));
           				Config.drawXAxis=b.booleanValue();
           			}
           			else if (att.equals("boolean_yaxis"))  {
           				Boolean b=new Boolean(attributs.getValue(index));
           				Config.drawYAxis=b.booleanValue();
           			}
           			else if (att.equals("xaxis")){
           				Config.XAxis=Integer.parseInt(attributs.getValue(index));
           			}
           			else if (att.equals("yaxis")){
           				Config.YAxis=Integer.parseInt(attributs.getValue(index));
           			}
           			else if (att.equals("axiscolor")){
           				Config.axisColor=Integer.parseInt(attributs.getValue(index));
           			}
           		}
        	}
           	
        	else if (tag.equals("border_image")){
        		for (int index = 0; index < attributs.getLength(); index++) { // on parcourt la liste des attributs
      				Config.borderExternalImage.add(attributs.getValue(index));
      			}
        	}
        	else if (tag.equals("border_color")){
        		Config.borderColor=new Color(Integer.parseInt(attributs.getValue(0)));
        	}
        	else if (tag.equals("border_image_selected")){
        		Config.borderImageSelected=attributs.getValue(0);	
        	}
        	else if (tag.equals("looknfeel")){
        		Config.looknfeel=Integer.parseInt(attributs.getValue(0));
        		try{
        		switch(Config.looknfeel){
        			case Config.LOOKNFEEL_NATIVE:
					UIManager.setLookAndFeel(UIManager
							.getSystemLookAndFeelClassName());
        			break;
        			case Config.LOOKNFEEL_JAVA:
        				UIManager.setLookAndFeel(new javax.swing.plaf.metal.MetalLookAndFeel());
        			break;
        			case Config.LOOKNFEEL_MOTIF:
					UIManager
							.setLookAndFeel(new com.sun.java.swing.plaf.motif.MotifLookAndFeel());
        			break;
        		} 
        		
        		}catch (Exception exc) {
        			System.out.println(exc.toString());
        		}
        	}
        	else if (tag.equals("syntax_highlighting")){
          		for (int index = 0; index < attributs.getLength(); index++) { // on parcourt la liste des attributs
          			String att=attributs.getLocalName(index);
           			if (att.equals("boolean"))  {
           				Boolean b=new Boolean(attributs.getValue(index));
           				Config.COLOR_ENABLED=b.booleanValue();
//           				Config.COLOR_ENABLED=Boolean.parseBoolean(attributs.getValue(index));
           				}
           			else if (att.equals("color_commentaire"))
           				Config.coloration_commentaire=Integer.parseInt(attributs.getValue(index));
           			else if (att.equals("color_operand"))
           				Config.coloration_operande=Integer.parseInt(attributs.getValue(index));
           			else if (att.equals("color_parenthesis"))
       					Config.coloration_parenthese=Integer.parseInt(attributs.getValue(index));
           			else if (att.equals("color_primitive"))
           				Config.coloration_primitive=Integer.parseInt(attributs.getValue(index));
           			else if (att.equals("style_commentaire"))
           				Config.style_commentaire=Integer.parseInt(attributs.getValue(index));
       				else if (att.equals("style_operand"))
           				Config.style_operande=Integer.parseInt(attributs.getValue(index));
       				else if (att.equals("style_parenthesis"))
           				Config.style_parenthese=Integer.parseInt(attributs.getValue(index));
       				else if (att.equals("style_primitive"))
           				Config.style_primitive=Integer.parseInt(attributs.getValue(index));
          		}
        	}
      		else if (tag.equals("startup_files")){
      			for (int index = 0; index < attributs.getLength(); index++) { // on parcourt la liste des attributs
      				Config.path.add(0,attributs.getValue(index));
      			}
     		}
        }
	}
/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo 
 * 						programming language
 * @author Loïc Le Coq
 */

package xlogo;
import java.awt.*;
import java.io.*;

import javax.swing.JColorChooser;
import javax.swing.JFileChooser;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import javax.swing.text.rtf.RTFEditorKit;

import xlogo.utils.WebPage;
import java.util.Locale;
import xlogo.utils.Utils;
import xlogo.utils.ExtensionFichier;
import xlogo.utils.WriteImage;
import xlogo.gui.AImprimer;
import xlogo.gui.MyTextAreaDialog;
import xlogo.kernel.Workspace;
import xlogo.kernel.Procedure;
import xlogo.kernel.Kernel;
import xlogo.kernel.DrawPanel;
/**
 * This class is the Controller for the main frame.<br>
 * All events are interpreted by this class
 * @author loic
 *
 */
public class MenuListener extends JDialog implements ActionListener{
	private static final long serialVersionUID = 1L;
	private double zoomfactor=1.25;
	protected static final String FILE_NEW="new";
	protected static final String FILE_OPEN="open";
	protected static final String FILE_SAVE_AS="save_as";
	protected static final String FILE_SAVE="save";
	protected static final String FILE_SAVE_IMAGE="record_image";
	protected static final String FILE_COPY_IMAGE="copy_image";
	protected static final String FILE_PRINT_IMAGE="print_image";
	protected static final String FILE_SAVE_TEXT="save_text";
	protected static final String FILE_QUIT="quit";
	protected static final String EDIT_SELECT_ALL="select_all";
	public static final String EDIT_COPY="copy";
	public static final String EDIT_CUT="cut";
	public static final String EDIT_PASTE="paste";
	protected static final String TOOLS_PEN_COLOR="pen_color";
	protected static final String TOOLS_SCREEN_COLOR="screen_color";
	protected static final String TOOLS_TRANSLATOR="translator";
	protected static final String TOOLS_OPTIONS="preferences";
	protected static final String TOOLS_START_FILE="start_file";
	protected static final String TOOLS_ERASER="eraser";
	protected static final String HELP_LICENCE="licence";
	protected static final String HELP_TRANSLATED_LICENCE="translated_licence";
	protected static final String HELP_TRANSLATE_XLOGO="translate_xlogo";
	protected static final String HELP_ABOUT="about";
	public static final String PLAY="play";
	public static final String ZOOMIN="zoomin";
	public static final String ZOOMOUT="zoomout";
	
	
	private static final String WEB_SITE="http://xlogo.tuxfamily.org";
	private static final String MAIL="loic@xlogo.tuxfamily.org";
	private Application cadre;
	private Workspace wp;
	private Kernel kernel;
  
	/**
	 * Attached the controller MenuListener to the main Frame
	 * @param cadre main Frame
	 */
  public MenuListener(Application cadre){
    this.cadre=cadre;
    this.kernel=cadre.getKernel();
    this.wp=kernel.getWorkspace();
    
  }
  /**
   * This method dispatches all events from the main Frame and executes the task corresponding to the incoming event.
   */
  public void actionPerformed(ActionEvent e){
    String cmd=e.getActionCommand();
    if (MenuListener.FILE_QUIT.equals(cmd)){     //Quitter
    	cadre.closeWindow();
    }
    else if (MenuListener.EDIT_COPY.equals(cmd)){   //Copier
      cadre.copy();
    }
    else if (MenuListener.EDIT_CUT.equals(cmd)){   //Couper
      cadre.cut();
    }
    else if (MenuListener.EDIT_PASTE.equals(cmd)){   //Coller
      cadre.paste();
    }
    else if (MenuListener.EDIT_SELECT_ALL.equals(cmd)){ // select all
    	cadre.select_all();
    }
    else if(MenuListener.FILE_NEW.equals(cmd)){ //nouveau
		String[] choix={Logo.messages.getString("pref.ok"),Logo.messages.getString("pref.cancel")};
		ImageIcon icone=new ImageIcon(Utils.class.getResource("icone.png"));
		MyTextAreaDialog jt=new MyTextAreaDialog(Logo.messages.getString("enregistrer_espace"));
		int retval=JOptionPane.showOptionDialog(cadre,jt,Logo.messages.getString("enregistrer_espace"),JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE,icone,choix,choix[0]);	
		
		if (retval==JOptionPane.OK_OPTION){
			wp.deleteAllProcedures();
			wp.deleteAllVariables();
			if (null!=Application.path) {
				Application.path=null;
				cadre.setTitle("XLOGO");
			}
			cadre.setEnabled_New(false);	
		  }
		  else cadre.setEnabled_New(true);
    }
    else if(MenuListener.FILE_SAVE.equals(cmd)|MenuListener.FILE_SAVE_AS.equals(cmd)){   //Enregistrer
      String path=Application.path;
      if (MenuListener.FILE_SAVE_AS.equals(cmd)||null==path) {
        JFileChooser jf=new JFileChooser(Utils.SortieTexte(Config.defaultFolder));
        String[] ext={".lgo"};
        jf.addChoosableFileFilter(new ExtensionFichier(Logo.messages.getString("fichiers_logo"),ext));
				Utils.recursivelySetFonts(jf,Config.police);
		
        int retval=jf.showDialog(cadre,Logo.messages
				.getString("menu.file.save"));
        if (retval==JFileChooser.APPROVE_OPTION){
          path=jf.getSelectedFile().getPath();
          String path2=path.toLowerCase();  // on garde la casse du path pour les systemes d'exploitation faisant la diff�rence
          if (!path2.endsWith(".lgo")) path+=".lgo";
          Application.path=path;
					cadre.setEnabled_Record(true);
					cadre.setTitle("XLOGO"+"          "+path);
					try {
						File f=new File(path);
						Config.defaultFolder=Utils.rajoute_backslash(f.getParent());
					}
					catch(NullPointerException e2){}
        }
      }
      try{
        String aecrire="";
        try {
          for (int i = 0; i < wp.getNumberOfProcedure(); i++) {
            Procedure procedure = wp.getProcedure(i);
            aecrire+=procedure.toString();
          }
        }catch(NullPointerException ex){ex.printStackTrace();} //Si aucune procedure n'a ete definie.
        Utils.writeLogoFile(path,aecrire);
      }
      catch(NullPointerException e3){System.out.println("annulation");} //Si l'utilisateur annule
      catch(IOException e2){cadre.ecris("erreur",Logo.messages.getString("error.ioecriture"));}
		}
    else if(MenuListener.FILE_OPEN.equals(cmd)){              //Ouvrir
      JFileChooser jf=new JFileChooser(Utils.SortieTexte(Config.defaultFolder));
      String[] ext={".lgo"};
      jf.addChoosableFileFilter(new ExtensionFichier(Logo.messages.getString("fichiers_logo"),ext));
			Utils.recursivelySetFonts(jf,Config.police);
      int retval=jf.showDialog(cadre,Logo.messages.getString("menu.file.open"));
      if (retval==JFileChooser.APPROVE_OPTION){
        String txt="";
        String path=jf.getSelectedFile().getPath();
        try{
        	txt=Utils.readLogoFile(path);
        }
        catch(IOException e1){cadre.ecris("erreur",Logo.messages.getString("error.iolecture"));}
        if (!txt.equals("")){
          if (!cadre.editeur.isVisible()) cadre.editeur.setVisible(true);
         	 cadre.editeur.setEditorStyledText(txt);
         	 cadre.editeur.initMainCommand();
         	cadre.editeur.discardAllEdits();
        }
       cadre.tmp_path=path;
			}
    }
    else if(MenuListener.TOOLS_SCREEN_COLOR.equals(cmd)){      //couleur du fond
    Color color=JColorChooser.showDialog(cadre,Logo.messages.getString("couleur_du_fond"),cadre.getArdoise().getBackgroundColor());
    if (null!=color){
      Locale locale=Logo.generateLocale(Config.langage);
      java.util.ResourceBundle rs=java.util.ResourceBundle.getBundle("primitives",locale);
      String fcfg=rs.getString("fcfg");
      fcfg=fcfg.substring(0,fcfg.indexOf(" "));
      cadre.ecris("commentaire",fcfg+" ["+color.getRed()+" "+color.getGreen()+" "+color.getBlue()+"]\n");
      kernel.fcfg(color);

    }
   }
   else if(MenuListener.TOOLS_PEN_COLOR.equals(cmd)){      //Couleur du crayon
    Color color=JColorChooser.showDialog(cadre,Logo.messages.getString("couleur_du_crayon"),kernel.getActiveTurtle().couleurcrayon);
    if (null!=color){
      Locale locale=Logo.generateLocale(Config.langage);
      java.util.ResourceBundle rs=java.util.ResourceBundle.getBundle("primitives",locale);
      String fcc=rs.getString("fcc");
      fcc=fcc.substring(0,fcc.indexOf(" "));
      cadre.ecris("commentaire",fcc+" ["+color.getRed()+" "+color.getGreen()+" "+color.getBlue()+"]\n");
      kernel.fcc(color);
    }
  }
  else if(MenuListener.TOOLS_OPTIONS.equals(cmd)){  //preferences
			if (cadre.editeur.isVisible()) JOptionPane.showMessageDialog(cadre,Logo.messages.getString("ferme_editeur"),Logo.messages.getString("erreur"),JOptionPane.ERROR_MESSAGE);
			else cadre.prefOpen();
  }
	else if(MenuListener.TOOLS_TRANSLATOR.equals(cmd)){
		cadre.traducOpen();
	}
	else if (MenuListener.FILE_COPY_IMAGE.equals(cmd)){  // Copier l'image au presse-papier
		Thread copie=new CopyImage();
		copie.start();
  }
  else if (MenuListener.FILE_SAVE_IMAGE.equals(cmd)){  //Enregistrer l'image au format png ou jpg
	  WriteImage writeImage=new WriteImage(cadre,cadre.getArdoise().getSelectionImage());
	  int value=writeImage.chooseFile();
  		if (value==JFileChooser.APPROVE_OPTION){
  	        writeImage.start();
  		} 
  }
  else if (MenuListener.FILE_PRINT_IMAGE.equals(cmd)){    //imprimer l'image
	    AImprimer can=new AImprimer(cadre.getArdoise().getSelectionImage());
	    Thread imprime=new Thread(can);
	    imprime.start();
  }
  else if (MenuListener.FILE_SAVE_TEXT.equals(cmd)){
  	RTFEditorKit myRTFEditorKit = new RTFEditorKit();
   	StyledDocument myStyledDocument =cadre.getHistoryPanel().sd_Historique();
   	try{
   		JFileChooser jf=new JFileChooser(Utils.SortieTexte(Config.defaultFolder));
   		String[] ext={".rtf"};
  	    jf.addChoosableFileFilter(new ExtensionFichier(Logo.messages.getString("fichiers_rtf"),ext));
  	    Utils.recursivelySetFonts(jf,Config.police);
  	    int retval=jf.showDialog(cadre,Logo.messages.getString("menu.file.save"));
  	    if (retval==JFileChooser.APPROVE_OPTION){
  	    	String path=jf.getSelectedFile().getPath();
  	        String path2=path.toLowerCase();  // on garde la casse du path pour les syst�mes d'exploitation faisant la diff�rence
  	        if (!path2.endsWith(".rtf")) path+=".rtf";
  	        	FileOutputStream myFileOutputStream = new FileOutputStream(path);
	  	    	myRTFEditorKit.write(myFileOutputStream, myStyledDocument, 0,myStyledDocument.getLength()-1);
	  	    	myFileOutputStream.close();
  	         }
  	    }
  	    catch(FileNotFoundException e1){}
  	    catch(IOException e2){}
  	    catch(BadLocationException e3){}
  	    catch(NullPointerException e4){}
  }
  else if (MenuListener.HELP_ABOUT.equals(cmd)){   //Boite de dialogue A propos
  	String message=Logo.messages.getString("message_a_propos1")+Config.version+"\n\n"
		+Logo.messages.getString("message_a_propos2")+" "+MenuListener.WEB_SITE+"\n\n"+Logo.messages.getString("message_a_propos3")+"\n     "+MenuListener.MAIL;
	MyTextAreaDialog jt=new MyTextAreaDialog(message);
		ImageIcon icone=new ImageIcon(Utils.class.getResource("icone.png"));
		JOptionPane.showMessageDialog(null,jt,Logo.messages.getString("menu.help.about"),JOptionPane.INFORMATION_MESSAGE,(Icon)icone);
		
  }
  else if (MenuListener.ZOOMIN.equals(cmd)){
	  cadre.getArdoise().zoom(zoomfactor*DrawPanel.zoom);
  }
  else if (MenuListener.ZOOMOUT.equals(cmd)){
	  cadre.getArdoise().zoom(1/zoomfactor*DrawPanel.zoom);
  } 
  else if (MenuListener.HELP_LICENCE.equals(cmd)|MenuListener.HELP_TRANSLATED_LICENCE.equals(cmd)){     // Affichage de la licence
   JFrame frame=new JFrame(Logo.messages.getString("menu.help.licence"));
	frame.setIconImage(Toolkit.getDefaultToolkit().createImage(
			WebPage.class.getResource("icone.png")));
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    frame.setSize(500,500);
    WebPage editorPane = new WebPage();
    editorPane.setEditable(false);
    String path="gpl/gpl-";
    if (MenuListener.HELP_LICENCE.equals(cmd))  path+="en";
    else {
    	switch(Config.langage){
    		case Config.LANGUAGE_FRENCH: //french
    			path+="fr";
    		break;
    		case Config.LANGUAGE_ENGLISH: // english
    			path+="en";
    		break;
    		case Config.LANGUAGE_ARABIC: // Arabic
    			path+="ar";
    		break;
    		case Config.LANGUAGE_SPANISH: // spanish
    			path+="es";
    		break;
    		case Config.LANGUAGE_PORTUGAL: // portuguese
    			path+="pt";	
    		break;
    		case Config.LANGUAGE_ESPERANTO: //esperanto
    			path+="eo";
    		break;
    		case Config.LANGUAGE_GERMAN: // german
    			path+="de";
    		break;
    		case Config.LANGUAGE_GALICIAN: // galician
    			path+="gl";
    		break;
    		case Config.LANGUAGE_ASTURIAN: //asturian
    			path+="es";
    		break;
    		case Config.LANGUAGE_GREEK: // greek
    			path+="el";
    		break;
    	}
    }
     path+=".html";    
    java.net.URL helpURL = MenuListener.class.getResource(path);
    if (helpURL != null) {
        try {
            editorPane.setPage(helpURL);
        } catch (IOException e1) {
            System.err.println("Attempted to read a bad URL: " + helpURL);
        }
    } else {
        System.err.println("Couldn't find file: "+path);
    }

//    Put the editor pane in a scroll pane.
    JScrollPane editorScrollPane = new JScrollPane(editorPane);
    editorScrollPane.setVerticalScrollBarPolicy(
                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    editorScrollPane.setPreferredSize(new Dimension(250, 145));
    editorScrollPane.setMinimumSize(new Dimension(10, 10));
    frame.getContentPane().add(editorScrollPane);
    frame.setVisible(true);
  }  
  else if (MenuListener.TOOLS_START_FILE.equals(cmd)){   // definir les fichiers de demarrage
  	cadre.demOpen();
  }
    // Tool to translate XLogo
  else if (MenuListener.HELP_TRANSLATE_XLOGO.equals(cmd)){
	  cadre.txOpen();
	  
  }
  else if (MenuListener.TOOLS_ERASER.equals(cmd)){	// procedure eraser
  	cadre.eraserOpen();
  }
  else if (MenuListener.PLAY.equals(cmd)){
	  cadre.affichage_Start(Utils.decoupe(Config.mainCommand));
	  cadre.getHistoryPanel().ecris("normal", Config.mainCommand+"\n");
  }
  }
  
  /**
   * This class is a thread that copy the Image selection into the clipboard
   */
  class CopyImage extends Thread implements Transferable{
		private BufferedImage image=null;
		Clipboard clip=Toolkit.getDefaultToolkit().getSystemClipboard();
		CopyImage(){
			image=cadre.getArdoise().getSelectionImage();
		}
		public void run(){
	        	clip.setContents(this,null);
		}
		public DataFlavor[] getTransferDataFlavors() {
			return new DataFlavor[] {DataFlavor.imageFlavor };
		}

		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return DataFlavor.imageFlavor.equals(flavor);
		}
		public Object getTransferData(DataFlavor flavor)throws UnsupportedFlavorException {
			if (!isDataFlavorSupported(flavor)) {
				throw new UnsupportedFlavorException(flavor);
			}
			return image;
		}
	 }
  
  
}

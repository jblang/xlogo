/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo 
 * 						programming language
 * @author Loïc Le Coq
 */
package xlogo.utils;
import java.awt.Container;
import java.awt.Font;
import java.awt.Image;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.MediaTracker;
import java.io.BufferedReader;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import xlogo.kernel.MyCalculator;
import xlogo.kernel.Affichage;
import xlogo.Config;
import xlogo.Logo;

public class Utils {
	public static Image dimensionne_image(String nom,Component jf){
		Image image=null;
		image= Toolkit.getDefaultToolkit().getImage(Utils.class.getResource(nom));
		MediaTracker tracker=new MediaTracker(jf);
		tracker.addImage(image,0);
		try{tracker.waitForID(0);}
		catch(InterruptedException e1){}
		double largeur_ecran=Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		int largeur=image.getWidth(jf); 
		int hauteur=image.getHeight(jf);
		// On fait attention à la résolution de l'utilisateur
		double facteur = largeur_ecran/1024.0; //les images sont prévues pour 1024x768
		if ((int)(facteur+0.001)!=1){
			image=image.getScaledInstance((int)(facteur*largeur),(int)(facteur*hauteur),Image.SCALE_SMOOTH);
			tracker=new MediaTracker(jf);
			tracker.addImage(image,0);
			try{tracker.waitForID(0);}
			catch(InterruptedException e1){}
		}
		return image;
	  }
	public static void recursivelySetFonts(Component comp, Font font) {
	    comp.setFont(font);
	    if (comp instanceof Container) {
	      Container cont = (Container) comp;
	      for(int j=0, ub=cont.getComponentCount(); j<ub; ++j)
			recursivelySetFonts(cont.getComponent(j), font);
	    }
	}
	public static String rajoute_backslash(String st){
		StringBuffer tampon=new StringBuffer();
		for(int j=0;j<st.length();j++){
			char c=st.charAt(j);
			if (c=='\\') tampon.append("\\\\");
			else if (c==' ') tampon.append("\\e");
			else if ("()[]#".indexOf(c)!=-1) tampon.append("\\"+c);
			else tampon.append(c);
		}
		return(new String(tampon));
	  }
	public static String SortieTexte(String chaine){ // Enlève les backslash
		StringBuffer buffer=new StringBuffer();
		boolean backslash=false;
		boolean ignore=false;
		for (int j=0;j<chaine.length();j++){
			char c=chaine.charAt(j);
			if (backslash) {
				if (c=='e') buffer.append(' ');
//				else if (c=='\\') buffer.append('\\');
				else if (c=='n') buffer.append("\n");
				else if(c=='v') buffer.append("");
				else if(c=='l') {
					ignore=true;
				}
				else if("[]()#\\".indexOf(c)>-1) buffer.append(c);
				backslash=false;
			}
			else {
				if (c=='\\') backslash=true;
				else if (!ignore) buffer.append(c);
				else if (c==' ') ignore=false;
			}
		}
		return MyCalculator.getOutputNumber(new String(buffer));
	}
	/**
	 * This method is formatting the String st.<br>
	 * - Unused white spaces are deleted.<br>
	 * - The character \ is modified to \\ <br>
	 * - The sequence "\ " is modified to "\e"<br>
	 * - The sequence "\ " is modified to "\e"<br>
 	 * - The sequence "\ " is modified to "\e"<br>
 	 * - The sequence "\ " is modified to "\e"<br>
	 * @param st The String instruction to format
	 * @return The formatted instructions
	 */
	public static StringBuffer decoupe(String st) {  
		StringBuffer buffer = new StringBuffer();
		// If last character is a white space
		boolean espace=false;
		// If last character is a backslash
		boolean backslash=false;
		// If last character is a word
		boolean mot=false;
		
		int crochet_liste=0;
//		boolean variable=false;
		// If XLogo is running a program
		boolean execution_lancee=Affichage.execution_lancee;
		for(int i=0;i<st.length();i++){
			char c=st.charAt(i);
			if (c==' ') {
				if (!espace&&buffer.length()!=0) {
					if (backslash) buffer.append("\\e");
					else {
						buffer.append(c);
						espace=true;
						mot=false;
	//					variable=false;
					}
					backslash=false;
				}
			}
			else if(c=='\\'&&!backslash) {
				espace=false;
				backslash=true;
			}
			else if(c=='\"'){
				if (espace&&crochet_liste<=0){
					mot=true;
				}
				buffer.append(c);
				espace=false;
				backslash=false;
			}
			else if (c==':'){
		/*		if (espace&&crochet_liste<=0){
					variable=true;
				}*/
				buffer.append(c);
				espace=false;
				backslash=false;
			}
			else if (c=='['||c==']'||c=='('||c==')'){
				//Modifications apportées
				if (backslash) {
					buffer.append("\\"+c);
					backslash=false;
				}
				else {
					if (c=='[') crochet_liste++;
					else if (c==']') crochet_liste--;
					if (espace||buffer.length()==0) {buffer.append(c+" ");espace=true;}
					else {
						buffer.append(" "+c+" ");
						espace=true;
					}
				}
			}
			else if (c=='+'||c=='-'||c=='*'||c=='/'||c=='='||c=='<'||c=='>'||c=='&'||c=='|'){
				// à modifier (test + fin)
				if (mot||crochet_liste>0) {
					buffer.append(c);
					if (espace) espace=false;
				}
				else { 
					String op=String.valueOf(c);
					// Looking for operator <= or >=
					if (c=='<'||c=='>'){
						if (i+1<st.length()){
							if (st.charAt(i+1)=='='){
								op+="=";
								i++;
							}
						}
					}
					if (espace) buffer.append(op+" ");
					else {
						espace=true;
						if (buffer.length()!=0) buffer.append(" "+op+" ");
						// If buffer is empty no white space before
						else buffer.append(op+" ");
					}
				}
			}
			else{
				if (backslash){
					if (c=='n')	buffer.append("\\n");
					else if (c=='\\') buffer.append("\\\\"); 
					else if (c=='v'&& execution_lancee) buffer.append("\"");
					else if(c=='e'&& execution_lancee) buffer.append("\\e");
					else if (c=='#') buffer.append("\\#");
					else if (c=='l'&&execution_lancee) buffer.append("\\l");
					else { 
						buffer.append(c);
					}
				}
				else {
					buffer.append(c);	
				}
				backslash=false;
				espace=false;
			}
		}
		//System.out.println(buffer);
		// Remove the space when the user write only "*" or "+" in the command line
		//if (buffer.length()>0&&buffer.charAt(0)==' ') buffer.deleteCharAt(0);
		return (buffer);
	}
	
	
	
	public static String specialCharacterXML(String st){
		st=st.replaceAll("&","&amp;");
		st=st.replaceAll("<","&lt;");
		st=st.replaceAll("\"","&quot;");
		st=st.replaceAll(">","&gt;");
		st=st.replaceAll("'","&apos;");
		
		return st;
	}
	public static String readLogoFile(String path) throws IOException{
		String txt="";
		// The old format before XLogo 0.9.23 is no longer supported from version 0.9.30
		try{
          	// New format for XLogo >=0.923
          	// Encoded with UTF-8
    		StringBuffer sb=new StringBuffer();
          	 FileInputStream fr = new FileInputStream(path);
              InputStreamReader isr = new  InputStreamReader(fr,  "UTF8");
              BufferedReader brd=new BufferedReader(isr);
              while (brd.ready()){
              	sb.append(brd.readLine());
              	sb.append("\n");
              }
              txt=new String(sb);
           }
           catch(FileNotFoundException e1){
             // tentative fichier réseau 
           	  try{
           		  URL url =new java.net.URL(path);
           		  StringBuffer sb=new StringBuffer();
           		  java.io.InputStream fr = url.openStream();
           		  InputStreamReader isr = new  InputStreamReader(fr,  "UTF8");
           		  BufferedReader brd=new BufferedReader(isr);
           		  while (brd.ready()){
           			  String st=brd.readLine();
           			  sb.append(st);
           			  sb.append("\n");
                     }
           		  txt=new String(sb);
           	  }
           	  catch( java.net.MalformedURLException e){
           		  System.out.println("File not found: "+path.toString());	
           	  }
          }
          catch(Exception e){e.printStackTrace();}
          if (txt.startsWith("# "+Logo.messages.getString("mainCommand"))){
        	  int id=txt.indexOf("\n");
        	  if (id!=-1){
        		  Config.mainCommand=txt.substring(("# "+Logo.messages.getString("mainCommand")).length(),id).trim();
        		  txt=txt.substring(id+1);
        	  }
          };
          return txt;
	}
	public static void writeLogoFile(String path,String txt) throws IOException{
		try{
			if (!Config.mainCommand.trim().equals("")) {
				String heading="# "+Logo.messages.getString("mainCommand")+" "+Config.mainCommand+"\n";
				txt=heading+txt;
			}
			FileOutputStream f = new FileOutputStream(path);
			BufferedOutputStream b = new BufferedOutputStream(f);	
			OutputStreamWriter osw = new  OutputStreamWriter(b,  "UTF8");
	        osw.write(txt);
	        osw.close();
	        b.close();
	        f.close();

		}
		catch(FileNotFoundException e1){e1.printStackTrace();}
	}
	public static boolean fileExists(String name){
		File f=new File(name);
		return f.exists();
	}
  public static String primitiveName(String generic){
			Locale locale = Logo.getLocale(Config.langage);
			ResourceBundle prim = ResourceBundle.getBundle(
					"primitives", locale);
			String st = prim.getString(generic);
			StringTokenizer str = new StringTokenizer(st);
			while (str.hasMoreTokens()) {
				st = str.nextToken();
			}
			return st;
	  }
}

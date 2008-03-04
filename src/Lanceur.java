/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo 
 * 						programming language
 * @author Loïc Le Coq
 */

import java.io.*;
import java.util.StringTokenizer;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import java.util.Calendar;

/**
 * @author loic
 * 
 * This class extracts the file tmp_xlogo.jar from the main archive
 * in the temporary file's directory
 * and then launch the command:
 * <br>
 * java -D-jar -Xmx64m -Djava.library.path=path_to_tmp tmp_xlogo.jar<br>
 * <br>
 * XLogo executes with a predefined heap size for the Virtual Machine (in the example 64M)
 */
public class Lanceur {
	/**
	 * The process which contains the XLogo application
	 */
	private Process p;
	/**
	 * The temporary folder which contains all files to start XLogo
	 */
	private File folder=null;
	private File[] files=new File[5];
	private int memoire=64;
	/**
	 * Constructs Lanceur with arguments args<br>
	 * @param args The arguments for the class Lanceur (path to file in lgo format)
	 */
	Lanceur(String[] args){
		// Look for old files from XLogo crash
		cleanTmp();
		lis_config();
		// extract application in the java.io.tmp directory
		extrait_application();
		try{
			// Add the tmp to the path
			String newPath=folder.getAbsolutePath();
			
			String javaLibraryPath = System.getProperty("java.library.path")+File.pathSeparatorChar + newPath;
			// Bug when launching under Windows with java webstart
			javaLibraryPath=javaLibraryPath.replaceAll("\"", "");
			System.out.println("Path: "+javaLibraryPath+"\n");
			
			String[] commande=new String[5+args.length];
			commande[0]=System.getProperty("java.home")+File.separator+"bin"+File.separator+"java";
			commande[1]="-jar";
			commande[2]="-Xmx"+memoire+"m";
			commande[3]="-Djava.library.path="+javaLibraryPath;
			commande[4]=files[0].getAbsolutePath();
			for (int i=0;i<args.length;i++){
				commande[i+5]=args[i];
			}
			System.out.println("<----- Starting XLogo ---->");
			String cmd="";
			for(int i=0;i<commande.length;i++){
				cmd+=commande[i]+" ";
			}
			System.out.println(cmd+"\n\n");
			p=Runtime.getRuntime().exec(commande);
			// Recept Message from the Process
			new Thread() {
				public void run() {
					try {
						BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
						String line = "";
						try {
							while((line = reader.readLine()) != null) {
								// Traitement du flux de sortie de l'application si besoin est
								System.out.println(line);
							}
						} finally {
							reader.close();
						}
					} catch(IOException ioe) {
						ioe.printStackTrace();
					}
				}
			}.start();
			// Recept Error Message from the Process
			new Thread() {
				public void run() {
					try {
						BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
						String line = "";
						try {
							while((line = reader.readLine()) != null) {
								// Traitement du flux de sortie de l'application si besoin est
								System.out.println(line);
							}
						} finally {
							reader.close();
						}
					} catch(IOException ioe) {
						ioe.printStackTrace();
					}
				}
			}.start();
			p.waitFor();
		}
		catch(InterruptedException e3){System.out.println(e3);}
		catch(IOException e1){System.out.println(e1);}
		catch(SecurityException e2){System.out.println(e2);}
		// Restore path
		String pathToFolder=folder.getAbsolutePath();
		String path=System.getProperty("java.library.path");
		StringTokenizer st=new StringTokenizer(path,File.pathSeparator);
		String newPath="";
		while (st.hasMoreTokens()){
			if (!newPath.equals("")) newPath+=File.pathSeparator;
			String element=st.nextToken();
			if (!element.equals(pathToFolder)) newPath+=element;
		}
		System.setProperty("java.library.path", newPath);
		// Delete tmp files
		for (int i=0;i<files.length;i++){
			if (null!=files[i]) files[i].delete();
		}
		folder.delete();
		System.out.println("Closing XLogo. Cleaning tmp file");
	}
	/**
	 * This method checks for unused old XLogo files in temporary directory<br>
	 * If it found files older than 24 hours with the prefix tmp_xlogo, these files are deleted.
	 */
	private void cleanTmp(){
		String path=System.getProperty("java.io.tmpdir");
		File f=new File(path);
		File[] files=f.listFiles();
		if (null!=files){
			for (int i=0;i<files.length;i++){
				try{
					if (files[i].getName().startsWith("tmp_xlogo")) {
						long fileTime=files[i].lastModified();
						long time=Calendar.getInstance().getTimeInMillis();
						// Delete file if it's more than 24 hours old 
						if (time-fileTime>24*3600*1000)	{
							if (files[i].isDirectory()) deleteDirectory(files[i]);
							files[i].delete();
						
						}
					}
				}
				catch(Exception e){e.printStackTrace();}
			}
		}
	}
	/**
	 * This method extracts the file tmp_xlogo.jar from the archive and copy it into the temporary directory.
	 */
	private void extrait_application(){
			// Create in the "java.io.tmpdir" a directory called tmp_xlogo 
			int i=0;
			String tmpPath=System.getProperty("java.io.tmpdir")+File.separator+"tmp_xlogo";
			while (true){
				folder=new File(tmpPath+i);
				if (!folder.exists()) break;
				else i++;
			}
			boolean b=folder.mkdir();
			System.out.println("Creating tmp_xlogo directory - success: "+b);
			// extract the file tmp_xlogo.jar in this folder
            InputStream src= Lanceur.class.getResourceAsStream("tmp_xlogo.jar");
            files[0]=new File(folder.getAbsolutePath()+File.separator+"tmp_xlogo.jar");
			b=copier(src,files[0]);
			System.out.println("Copying tmp_xlogo.jar - success: "+b);
			// extract the native driver for java 3d in this folder
			String os=System.getProperty("os.name").toLowerCase();
			String arch=System.getProperty("os.arch");
			System .out.println("Operating system: "+os);
			System .out.println("Architecture: "+arch);
			// Linux 
			if (os.indexOf("linux")!=-1){
				if (arch.indexOf("86")!=-1){
		            InputStream lib= Lanceur.class.getResourceAsStream("linux/x86/libj3dcore-ogl.so");
		            files[1]=new File(folder.getAbsolutePath()+File.separator+"libj3dcore-ogl.so");
					copier(lib,files[1]);
		            lib= Lanceur.class.getResourceAsStream("linux/x86/libj3dcore-ogl-cg.so");
		            files[2]=new File(folder.getAbsolutePath()+File.separator+"libj3dcore-ogl-cg.so");
					copier(lib,files[2]);
				}
				else{
				      InputStream lib= Lanceur.class.getResourceAsStream("linux/amd64/libj3dcore-ogl.so");
			            files[1]=new File(folder.getAbsolutePath()+File.separator+"libj3dcore-ogl.so");
						copier(lib,files[1]);
				}
			}
			// windows
			else if (os.indexOf("windows")!=-1){
				if (arch.indexOf("86")!=-1){
					InputStream lib= Lanceur.class.getResourceAsStream("windows/x86/j3dcore-d3d.dll");
					files[1]=new File(folder.getAbsolutePath()+File.separator+"j3dcore-d3d.dll");
					b=copier(lib,files[1]);
					System.out.println("Copying library 1 - success: "+b);
					lib= Lanceur.class.getResourceAsStream("windows/x86/j3dcore-ogl.dll");
					files[2]=new File(folder.getAbsolutePath()+File.separator+"j3dcore-ogl.dll");
					b=copier(lib,files[2]);
					System.out.println("Copying library 2 - success: "+b);
					lib= Lanceur.class.getResourceAsStream("windows/x86/j3dcore-ogl-cg.dll");
					files[3]=new File(folder.getAbsolutePath()+File.separator+"j3dcore-ogl-cg.dll");
					b=copier(lib,files[3]);
					System.out.println("Copying library 3 - success: "+b);
					lib= Lanceur.class.getResourceAsStream("windows/x86/j3dcore-ogl-chk.dll");
					files[4]=new File(folder.getAbsolutePath()+File.separator+"j3dcore-ogl-chk.dll");
					b=copier(lib,files[4]);
					System.out.println("Copying library 4 - success: "+b);
				}	
				else{
					InputStream lib= Lanceur.class.getResourceAsStream("windows/amd64/j3dcore-ogl.dll");
					files[1]=new File(folder.getAbsolutePath()+File.separator+"j3dcore-ogl.dll");
					copier(lib,files[1]);
				}
			}
			// Mac os
			else if (os.indexOf("mac")!=-1){
				
			}
			// solaris
			else if (os.indexOf("solaris")!=-1){
				
			}
	}
	/**
	 * This method reads the Memory needed by XLogo in the file .xlogo
	 */
	 private void lis_config(){
	 	try{
		 // Try to read XML format (new config file)
			FileInputStream fr = new FileInputStream(System.getProperty("user.home")+File.separator+".xlogo");
			BufferedInputStream bis = new BufferedInputStream(fr);	
		    InputStreamReader isr = new  InputStreamReader(bis,  "UTF8");		
		    try{
		     	XMLReader saxReader = XMLReaderFactory.createXMLReader();
		      	saxReader.setContentHandler(new MemoryContentHandler());
		      	saxReader.parse(new InputSource(isr));
		    }
		     catch (SAXException e){
		 			// Read the old config file format
		    	 	String s="";
		    	 	FileReader ifr = new FileReader(System.getProperty("user.home")+File.separator+".xlogo");
		    	 	while(ifr.ready()){
		    	 		char[] b = new char[64];
		    	 		int i=ifr.read(b);
		    	 		if (i==-1) break;
		    	 		s+=new String(b);
		    	 	}
		    	 	StringTokenizer st=new StringTokenizer(s,"\n");
		    	 	while(st.hasMoreTokens()){
		    	 		String element=st.nextToken();
		    	 		if (element.equals("# memoire")){
		    	 			element=st.nextToken();
		    	 			memoire=Integer.parseInt(element);
		    	 		}
		    	 	}
		     	}
	 		}
	 		catch(FileNotFoundException e1){System.out.println("No File \".xlogo\". Will create one...");}
	 		catch(IOException e2){}
	 }
	 		
	 /**
	  * Main method
	  * @param args The path toward "lgo" files
	  */
	public static void main(String[] args) {
	new Lanceur(args);
	}
	/**
	 * This method copy the file tmp_xlogo.jar from the archive to the file Destination.
	 * @param destination The output file
	 * @return true if success, false otherwise
	 */
	private boolean copier(InputStream src, File destination )
	{
	        boolean resultat = false;
	        // Declaration des flux
	        java.io.FileOutputStream destinationFile=null;
	        try {
	                // Création du fichier :
	                destination.createNewFile();
	                // Ouverture des flux

	                destinationFile = new java.io.FileOutputStream(destination);
	                // Lecture par segment de 0.5Mo 
	                byte buffer[]=new byte[512*1024];
	                int nbLecture;
	                while( (nbLecture = src.read(buffer)) != -1 ) {
	                        destinationFile.write(buffer, 0, nbLecture);
	                } 
	                // Copie réussie
	                resultat = true;
	        } catch( java.io.FileNotFoundException f ) {
	        } catch( java.io.IOException e ) {
	        } finally {
	                // Quoi qu'il arrive, on ferme les flux
	                try {
	                        src.close();
	                } catch(Exception e) { }
	                try {
	                        destinationFile.close();
	                } catch(Exception e) { }
	        } 
	        return( resultat );
	}
	/** this class is a very basic XML parser
	**/
	class MemoryContentHandler implements ContentHandler{
		//   private Locator locator;
		    
	  		public MemoryContentHandler() {
	  			    super();
	                  // We define default Locator
	        //          locator = new LocatorImpl();
	          }
	          public void setDocumentLocator(Locator value) {
	          //        locator =  value;
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
	          	if (tag.equals("memory")){
	          		memoire=Integer.parseInt(attributs.getValue(0));
	          	}
	       }

	}
	
	/**
	 * Delete a the directory created by Logo in /tmp
	 * @param path The Directory path
	 * @return true if success
	 */
	private boolean deleteDirectory(File path) { 
        boolean resultat = true; 
        if( path.exists() ) { 
                File[] files = path.listFiles(); 
                if (null!=files){
                	for(int i=0; i<files.length; i++) { 
                        if(files[i].isDirectory()) { 
                                resultat &= deleteDirectory(files[i]); 
                        } 
                        else { 
                        resultat &= files[i].delete(); 
                        } 
                	}  
                }
        } 
        resultat &= path.delete(); 
        return( resultat ); 
} 
	
}
package xlogo.utils;
import javax.swing.filechooser.*;
import java.io.File;
//Permet de filtrer les fichiers dans un FileChooser
// You can add a filter in a FileChooser

/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo 
 * 						programming language
 * @author Loïc Le Coq
 */
public class ExtensionFichier extends FileFilter {
private String description;   //Description du type de fichiers (Ex: "Fichiers JPEG")
private String[] extension;     //Extension (incluant le '.') Ex: .jpg .java
  public ExtensionFichier() {
  }
  public ExtensionFichier(String description,String[] extension){
  this.description=description;
  this.extension=extension;
  }
  public boolean accept(File f) {
    if (f.isDirectory()) return true;
    String nomFichier = f.getPath().toLowerCase();
    for (int i=0;i<extension.length;i++){
    	if (nomFichier.endsWith(extension[i])) return true;
    	
    }
    return false;
  }
  public String getDescription() {
	  StringBuffer sb=new StringBuffer();
	  sb.append(description);
	  sb.append(" (");
	  for (int i=0;i<extension.length;i++){
		  sb.append("*");
		  sb.append(extension[i]);
		  if (i!=extension.length-1) sb.append(", ");
	  }
	  sb.append(")");
	  return new String(sb);
  }
}
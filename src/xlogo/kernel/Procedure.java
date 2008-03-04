/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo 
 * 						programming language
 * @author Loïc Le Coq
 */
package xlogo.kernel;
import java.util.Stack;
import java.io.*;
import xlogo.Logo;
import xlogo.utils.Utils;
public class Procedure {

  public boolean affichable; // false lorsque c'est une procédure d'un fichier de démarrage
  public String comment; // Line of comment introducing the procedure
  public int nbparametre;
  public String name;
  public Stack<String> variable=new Stack<String>();
  public Stack<String> optVariables=new Stack<String>();
  public Stack<StringBuffer> optVariablesExp=new Stack<StringBuffer>();
  public String instruction="";
  public String instr=null;
  public String instruction_sauve="";  // En cas de mauvaise écriture dans l'éditeur
public 	String instr_sauve=null;		  // Permet de revenir aux valeurs antérieures d'une 
public 	Stack<String> variable_sauve=new Stack<String>(); // procédure avant modification
public Procedure() {
  }
  public Procedure(String name,int nbparametre,Stack<String> variable,Stack<String> optVariables,Stack<StringBuffer> optVariablesExp,boolean affichable){
    this.name=name;
    this.nbparametre=nbparametre;
    this.variable=variable;
    this.optVariables=optVariables;
    this.optVariablesExp=optVariablesExp;
    this.affichable=affichable;
  }
  public void decoupe(){
  	if (null==instr){
  		StringBuffer sb=new StringBuffer();
        try{
        	String line="";
        	StringReader sr=new StringReader(instruction);
        	BufferedReader bfr=new BufferedReader(sr);
        	int lineNumber=0;
			// Append the number of the line
			sb.append("\\l");
			sb.append(lineNumber);
			sb.append(" ");
        	while (bfr.ready()){
        		lineNumber++;
        		// read the line
        		try{
        			line=bfr.readLine().trim();
        		}
        		catch(NullPointerException e1){
        			break;
        		}
        		// delete comments
        		line=deleteComments(line);
        		line=Utils.decoupe(line).toString().trim();
        		sb.append(line);
        		if (!line.equals("")) {
        			sb.append(" ");
        			// Append the number of the line
        			sb.append("\\l");
        			sb.append(lineNumber);
        			sb.append(" ");
        		}
        	}  			
        }
        catch(IOException e){}
        instr=sb.toString();
  //  System.out.println("****************************"+name+"\n"+instr+"\n\n");
    }

  }
  private String deleteComments(String line){
	  int index=line.indexOf("#");
	  while(index!=-1){
		  if (index==0) {return "";}
		  else if (line.charAt(index-1)!='\\'){
			  return line.substring(0, index);
		  }
		  else index=line.indexOf("#",index+1);
	  }
	  return line;
  }
  public String toString(){
//    return("nom "+name+" nombre paramètres "+nbparametre+" identifiant "+id+"\n"+variable.toString()+"\n"+instr+"\ninstrction_sauve"+instruction_sauve+"\ninstr_sauve\n"+instr_sauve);
	  StringBuffer sb=new StringBuffer();
      if (affichable) {
      	sb.append(comment);
        sb.append(Logo.messages.getString("pour") + " " + name);
        for (int j = 0; j < nbparametre; j++) {
          sb.append(" :");
          sb.append(variable.get(j).toString());
        }
        for (int j=0;j<optVariables.size();j++){
        	sb.append(" [ :");
        	sb.append(optVariables.get(j));
        	sb.append(" ");
        	sb.append(optVariablesExp.get(j).toString());
        	sb.append(" ]");    	  
	     }
        sb.append("\n");
        sb.append(instruction);
        sb.append(Logo.messages.getString("fin"));
        sb.append("\n");
      }
//      System.out.println("a"+sb+"a");
      return new String(sb);
    }
  public String getName(){
  	return (name);
  }
}
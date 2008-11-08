package xlogo.utils;
import java.util.Stack;

import xlogo.kernel.Interprete;
import xlogo.Logo;
import xlogo.kernel.LoopProperties;
import xlogo.Application;
import xlogo.utils.Utils;
import xlogo.kernel.Primitive;
/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo 
 * 						programming language
 * @author Loïc Le Coq
 */
public class myException extends Exception {
	private static final long serialVersionUID = 1L;
	private Application cadre;
	public static boolean lance;
	
  public myException() {
  }
  // Thread permettant l'affichage dans le dispatch event queue
  class Affiche implements Runnable{
  	String msg;
  	Affiche(String msg){
  		this.msg=msg;
  	}
  public void run(){
  	cadre.ecris("erreur",msg);
  }	
  }
  public myException(Application cadre,String st){
  	this.cadre=cadre;
//    if (st.equals("siwhile")) st=Logo.messages.getString("tantque");
	while(!Interprete.en_cours.isEmpty()&&Interprete.en_cours.peek().equals("(")) Interprete.en_cours.pop();
    if (!cadre.error&!Interprete.en_cours.isEmpty()) {
    	cadre.ecris("erreur",Logo.messages.getString("dans")+" "+Interprete.en_cours.pop()+", "
    			+Logo.messages.getString("line")+" "+getLineNumber()+":\n");
    }
    if (!cadre.error) cadre.ecris("erreur",Utils.SortieTexte(st) + "\n");

    cadre.focus_Commande();
    cadre.error=true;
    Interprete.calcul=new Stack<String>();
    cadre.getKernel().getInstructionBuffer().clear();
    Primitive.stackLoop=new Stack<LoopProperties>();
  }
  private int getLineNumber(){
	  String string=Interprete.lineNumber;
//	  System.out.println("bb"+string+"bb");
	  if (string.equals("")) string=cadre.getKernel().getInstructionBuffer().toString();
//	  System.out.println("cc"+string+"cc");
	  int id=string.indexOf("\\l");
	  if (id!=-1) {
		  String lineNumber="";
		  int i=id+2;
		  char c=string.charAt(i);
		  while(c!=' ') {
			  lineNumber+=c;
			  i++;
			  c=string.charAt(i);
		  }
//		  System.out.println(lineNumber);
		  return Integer.parseInt(lineNumber);
	  } 
	  return 1;
  }
}
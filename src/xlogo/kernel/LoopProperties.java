/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo 
 * 						programming language
 * @author Loïc Le Coq
 */
package xlogo.kernel;
import java.math.BigDecimal;

public class LoopProperties {
	private boolean conserver=false;
	private String var="";
	private BigDecimal compteur,fin,increment;
	private String instr,id;
	//repete ou tantque
	LoopProperties(BigDecimal compteur,BigDecimal fin,BigDecimal increment,String instr, String id){
		this.compteur=compteur;
		this.fin=fin;
		this.increment=increment;
		this.instr=instr;
		this.id=id;
	}
	//repetepour
	LoopProperties(BigDecimal compteur,BigDecimal fin,BigDecimal increment,String instr, String id,String var){
		this.compteur=compteur;
		this.fin=fin;
		this.increment=increment;
		this.instr=instr;
		this.id=id;
		this.var=var;
	}
	String gerVar(){
		return var;
	}
	void incremente(){
		compteur=compteur.add(increment);
		compteur=new BigDecimal(LaunchPrimitive.eraseZero(compteur));
	//	System.out.println(compteur);
	}
	String getId(){
		return id;
	}
	BigDecimal getCompteur(){
		return compteur;
	}
	BigDecimal getFin(){
		return fin;
	}
	BigDecimal getIncrement(){
		return increment;
	}
	String getInstr(){
		return instr;	
	}
	public String toString(){
		return(compteur+" "+fin+" "+increment+"\n"+instr+"\n"+id);
	}
	void AffecteVar(boolean first){
		String element=String.valueOf(compteur);
		if (element.endsWith(".0")) element=element.substring(0,element.length()-2) ;
        if (element.startsWith(".")||element.equals("")) element="0"+element;

		if (Interprete.locale.containsKey(var)){
			if (first) conserver=true;
			Interprete.locale.put(var, element);
		} 
		else {
			Interprete.locale.put(var,element);
		}
	}
	void DeleteVar(){
		if (!conserver){
			if (Interprete.locale.containsKey(var)){
				Interprete.locale.remove(var);
			}
/*			else {
				id = Application.globale.search(var);
				Application.valeur.remove(Application.valeur.size() - id);
				Application.globale.remove(Application.globale.size() - id);
			}*/
		}
	}

}

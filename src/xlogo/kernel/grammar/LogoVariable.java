package xlogo.kernel.grammar;
/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo 
 * 						programming language
 * Licence : GPL
 * @author Loïc Le Coq
 */
public class LogoVariable extends LogoType{
	String name;
	LogoVariable(String name){
		this.name=name;
	}
	public boolean isVariable(){
		return true;
	}
	public String getName(){
		return name;
	}
	public String toString(){
		return ":"+name;
	}
	@Override
	public String toDebug() {
		return "(VARIABLE) :"+name;
	}
}

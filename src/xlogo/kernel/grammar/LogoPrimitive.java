/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo 
 * 						programming language
 * Licence : GPL
 * @author Loïc Le Coq
 */
package xlogo.kernel.grammar;
public class LogoPrimitive extends LogoType {
	private int id;
	private String name;
	LogoPrimitive(int id,String name){
		this.id=id;
		this.name=name;
	}
	public boolean isPrimitive() {
		return true;
	}
	public int getId(){
		return id;
	}
	public String toString(){
		return name;
	}
	@Override
	public String toDebug() {
		return "(PRIMITIVE id="+id+") "+name;
	}
}

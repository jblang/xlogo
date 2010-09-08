/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo 
 * 						programming language
 * Licence : GPL
 * @author Loïc Le Coq
 */
package xlogo.kernel.grammar;

/**
 * @author loic
 *
 */
public class LogoNumber extends LogoType {
	private double value;
	LogoNumber(double value){
		this.value=value;
	}
	public double getValue(){
		return value;
	}
	public boolean isWord(){
		return true;
	}
	public boolean isNumber(){
		return true;
	}
	public String toString(){
		return String.valueOf(value);
	}
	@Override
	public String toDebug() {
		return "(NUMBER) "+String.valueOf(value);
	}
}

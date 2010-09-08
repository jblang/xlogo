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
public class LogoException extends LogoType{
	private String message;
	public LogoException(String message){
		this.message=message;
	}
	public boolean isException(){
		return true;
	}
	public String toString(){
		return message;
	}

	@Override
	public String toDebug() {
		return "(EXCEPTION) "+message;
	}

}

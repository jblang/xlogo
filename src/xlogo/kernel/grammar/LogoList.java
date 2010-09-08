/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo 
 * 						programming language
 * Licence : GPL
 * @author Loïc Le Coq
 */
package xlogo.kernel.grammar;

import java.util.Vector;


public class LogoList extends LogoType{
	private Vector<LogoType> vector;
	LogoList(Vector<LogoType> vector){
		this.vector=vector;
	}
	LogoList(){
		vector=new Vector<LogoType>();
	}
	public boolean isList(){
		return true;
	}
	public void add(LogoType type){
		vector.add(type);
	}
	public Vector<LogoType> getVector(){
		return vector;
	}
	public String toString(){
		StringBuffer sb=new StringBuffer();
		sb.append("[ ");
		for(int i=0;i<vector.size();i++){
			sb.append(vector.get(i).toString());
			sb.append(" ");
		}
		sb.append("]");
		return sb.toString();
	}

	@Override
	public String toDebug() {
		StringBuffer sb=new StringBuffer();
		sb.append("(LIST) ");
		sb.append(toString());
		return sb.toString();

	}
	
}
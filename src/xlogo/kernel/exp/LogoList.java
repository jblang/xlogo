package xlogo.kernel.exp;

import java.util.Vector;
public class LogoList extends LogoArgument {
	Vector<LogoArgument> list;
	protected boolean isList(){
		return true;
	}

}

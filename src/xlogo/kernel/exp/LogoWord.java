package xlogo.kernel.exp;

public class LogoWord extends LogoArgument {
	String value;
	LogoWord(String value){
		this.value=value;
	}
	protected boolean isWord(){
		return true;
	}

}

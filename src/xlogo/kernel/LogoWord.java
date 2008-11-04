package xlogo.kernel;

public class LogoWord extends LogoArgument {
	private String value;
	private boolean isNumber=false;
	LogoWord(String value,boolean isNumber){
		this.value=value;
		this.isNumber=isNumber;
	}
	LogoWord(String value){
		this.value=value;
	}
	protected boolean isWord(){
		return true;
	}
	protected boolean isNumber(){
		return isNumber;
	}
	protected boolean isLineNumber(){
		if (value.matches("\\\\l([0-9])+ ")) return true;
		return false;
	}
	protected String getValue(){
		return value;
	}
	public String toString(){
		if (isNumber) return value;
		return "\""+value;
	}
	protected int getLength(){
		return value.length();
	}
	protected LogoWord itemWord(int id){
		String c=String.valueOf(value.charAt(id-1));
		try{
			Double.parseDouble(c);
			return new LogoWord(c,true);
		}
		catch(NumberFormatException e){
			return new LogoWord(c);			
		}
	}
	protected LogoArgument clone(){
		return new LogoWord(value,isNumber);
	}
}

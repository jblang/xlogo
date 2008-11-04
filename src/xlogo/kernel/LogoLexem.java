package xlogo.kernel;

public class LogoLexem {
	protected boolean isList(){
		return false;
	}
	protected boolean isWord(){
		return false;
	}
	protected boolean isNumber(){
		return false;
	}
	protected boolean isLineNumber(){
		return false;
	}
	protected boolean isStartBracket(){
		return false;
	}
	protected boolean isEndBracket(){
		return false;
	}
	protected boolean isStartParenthesis(){
		return false;
	}
	protected boolean isCommand(){
		return false;
	}
	protected boolean isVariable(){
		return false;
	}
	protected boolean endParenthesis(){
		return false;
	}
	protected boolean isEndProcedure(){
		return false;
	}
	protected boolean isEndLoop(){
		return false;
	}
}

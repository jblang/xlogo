package xlogo.kernel;

public abstract class LogoArgument extends LogoLexem{
	abstract protected String getValue();
	abstract protected LogoArgument clone();
}

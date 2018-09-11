
public class Token {
	final public String symbol;
	final public String lexema;
	final public int line;
	
	public Token(String symbol, String lexema, int line)
	{
		this.symbol = symbol;
		this.lexema = lexema;
		this.line = line;
	}
	
	public String getSymbol() {
		return symbol;
	}
	
	public String getLexema() {
		return lexema;
	}
	
	public int getLine() {
		return line;
	}
}

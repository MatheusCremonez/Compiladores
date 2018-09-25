
public class Token {
	final private String symbol;
	final private String lexema;
	final private int line;
	
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

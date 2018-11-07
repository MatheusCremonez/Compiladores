package Symbols;

public class Symbol {
	
	private String lexema;
	private boolean closed;
	private String type;
	
	public Symbol(String lexema) {
		this.setLexema(lexema);
		this.setClosed(false);
		this.setType(null);
	}
	
	public boolean isClosed() {
		return closed;
	}
	
	public boolean isNotClosed() {
		return !closed;
	}

	public void setClosed(boolean closed) {
		this.closed = closed;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLexema() {
		return lexema;
	}

	public void setLexema(String lexema) {
		this.lexema = lexema;
	}
}

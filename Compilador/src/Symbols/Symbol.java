package Symbols;

public class Symbol {
	
	private String lexema;
	private boolean closed;
	private String type;
	private int label;
	
	public Symbol(String lexema, int label) {
		this.setLexema(lexema);
		this.setClosed(false);
		this.setType(null);
		this.setLabel(label);
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

	public int getLabel() {
		return label;
	}

	public void setLabel(int label) {
		this.label = label;
	}
}

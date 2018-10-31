package Symbols;

public class Procedure extends Symbol {
	
	private boolean closed;

	public Procedure(String lexema) {
		super(lexema);
		this.setClosed(false);
	}

	public boolean isClosed() {
		return closed;
	}

	public void setClosed(boolean closed) {
		this.closed = closed;
	}	
		
	
}

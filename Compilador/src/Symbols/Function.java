package Symbols;

public class Function extends Symbol {
	private boolean closed;
	private String type;
	
	public Function(String lexema) {
		super(lexema);
		this.setClosed(false);
		this.setType(null);
	}

	public boolean isClosed() {
		return closed;
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
	
	
}

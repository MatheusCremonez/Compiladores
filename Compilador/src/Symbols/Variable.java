package Symbols;

public class Variable extends Symbol {

	private String type;
	
	public Variable(String lexema) {
		super(lexema);
		this.setType(null);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}	
	
}

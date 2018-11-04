package Symbols;

import java.util.ArrayList;
import java.util.List;

public class TableOfSymbols {

	private List<Symbol> stackOfSymbols;
	
	public TableOfSymbols() {
		stackOfSymbols = new ArrayList<Symbol>();
	}
	
	public void insert(Symbol symbol) {
		stackOfSymbols.add(symbol);		
	}
	
	public boolean lookProgramName(String lexema) {
		if (lexema.equals(stackOfSymbols.get(0).lexema)) {
			return true;
		}
		return false;
	}
	
	public boolean search(String lexema) {
		int i;
		
		for (i = (stackOfSymbols.size() - 1); i >= 0; i--) {
			if (stackOfSymbols.get(i) instanceof Variable) {
				if(lexema.equals(stackOfSymbols.get(i).lexema)) {
					return true;
				}
			} else {
				break;
			}
			
		}
		
		for (int j = i; j >= 0; j--) {
			if ((stackOfSymbols.get(i) instanceof Procedure) || (stackOfSymbols.get(i) instanceof Function)) {
				if(lexema.equals(stackOfSymbols.get(i).lexema)) {
					return true;
				}
			}
		}
		
		return lookProgramName(lexema);
	}
	
	public boolean searchVariable (String lexema) {
		for (int i = (stackOfSymbols.size() - 1); i >= 0; i--) {
			if (stackOfSymbols.get(i) instanceof Variable) {
				if(lexema.equals(stackOfSymbols.get(i).lexema)) {
					return true;
				}
			}
		}
		
		return lookProgramName(lexema);
	}
	
	public boolean searchProcedure (String lexema) {
		for (int i = (stackOfSymbols.size() - 1); i >= 0; i--) {
			if (stackOfSymbols.get(i) instanceof Procedure) {
				if(lexema.equals(stackOfSymbols.get(i).lexema)) {
					return true;
				}
			}
		}
		
		return lookProgramName(lexema);
	}
	
	public boolean searchFunction (String lexema) {
		for (int i = (stackOfSymbols.size() - 1); i >= 0; i--) {
			if (stackOfSymbols.get(i) instanceof Function) {
				if(lexema.equals(stackOfSymbols.get(i).lexema)) {
					return true;
				}
			}
		}
		
		return lookProgramName(lexema);
	}
	
	public void insertTypeOnFunction(String type) {
		Symbol symbol = stackOfSymbols.get(stackOfSymbols.size() - 1);
		
		if (symbol instanceof Function && symbol.getType() == null) {
			stackOfSymbols.get(stackOfSymbols.size() - 1).setType(type);
		}
			
	}
	
	
	public void debugTable() {
		for(int i = 0; i < stackOfSymbols.size(); i++) {
			System.out.println(stackOfSymbols.get(i).lexema + " " + stackOfSymbols.get(i).getType());
		}
			
	}
}

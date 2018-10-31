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
	
	public void debugTable() {
		for(int i = 0; i < stackOfSymbols.size(); i++) {
			System.out.println(stackOfSymbols.get(i).lexema);
		}
			
	}
}


/*
 * Implementar aqui a tabela de s�mbolos e todos os seus m�todos como:
 * Inserir um s�mbolo na tabela
 * Pesquisar dupiclidade na tabela
 * Entre outros...
 */
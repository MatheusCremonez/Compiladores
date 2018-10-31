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
	
	public boolean search(String lexema) {
		int i;
		
		if (lexema.equals(stackOfSymbols.get(0).lexema)) {
			return true;
		}
		
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
		
		return false;
	}
	
	public void debugTable() {
		for(int i = 0; i < stackOfSymbols.size(); i++) {
			System.out.println(stackOfSymbols.get(i).lexema);
		}
			
	}
}


/*
 * Implementar aqui a tabela de símbolos e todos os seus métodos como:
 * Inserir um símbolo na tabela
 * Pesquisar dupiclidade na tabela
 * Entre outros...
 */
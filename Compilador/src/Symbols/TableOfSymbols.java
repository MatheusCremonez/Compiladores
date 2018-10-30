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
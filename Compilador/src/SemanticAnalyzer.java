import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import Constants.*;
import Exceptions.SemanticException;
import Symbols.TableOfSymbols;

public class SemanticAnalyzer {
	TableOfSymbols tableOfSymbols = new TableOfSymbols();
	
	public void setTableOfSymbols(TableOfSymbols table) {
		tableOfSymbols = table;
	}
	
	public String expressionToPostfix(List<Token> expression) {
		List<String> stack = new ArrayList<String>();
		String output = "";
		
		for (int a = 0; a < expression.size(); a++) {
			String parcel = expression.get(a).getLexema();
			
			if (Constants.NUMERO_SIMBOLO.equals(expression.get(a).getSymbol()) ||
				Constants.IDENTIFICADOR_SIMBOLO.equals(expression.get(a).getSymbol())) {
				output = output.concat(parcel + " ");
			} else if (Constants.ABRE_PARENTESES_SIMBOLO.equals(expression.get(a).getSymbol())) { 
				stack.add(parcel);
			} else if (Constants.FECHA_PARENTESES_SIMBOLO.equals(expression.get(a).getSymbol())) {
				int stackTop = stack.size() - 1;
				while (!(Constants.ABRE_PARENTESES_LEXEMA.equals(stack.get(stackTop)))) {
					output = output.concat(stack.get(stackTop) + " ");
					stack.remove(stackTop);
					stackTop--;
				}
				stack.remove(stackTop); // remove o abre parenteses sem inclui-lo na saida

			} else {
				if (stack.isEmpty()) {
					stack.add(parcel);
				} else {
					int newOperatorPriority;
					int stackTopOperatorPriority;
					int stackTop = stack.size() - 1;
					do {

						newOperatorPriority = defineOperatorsPriority(parcel);
						stackTopOperatorPriority = defineOperatorsPriority(stack.get(stackTop));
						
						if (stackTopOperatorPriority >= newOperatorPriority) {
							output = output.concat(stack.get(stackTop) + " ");
							stack.remove(stackTop);
							stackTop--;
						}
						
					} while (stackTopOperatorPriority >= newOperatorPriority && !(stack.isEmpty()));
					
					if (stackTopOperatorPriority < newOperatorPriority || stack.isEmpty()) {
						stack.add(parcel);
					}
				}
			}
			
		}
		
		int stackTop = stack.size() - 1;
		if (!stack.isEmpty()) {
			for(int i = stackTop; i >= 0; i--) {
				output = output.concat(stack.get(i) + " ");
				stack.remove(i);
			}
		}
		System.out.println("Saída: " + output);
		return output;
	}
	
	public String returnTypeOfExpression(String expression) throws SemanticException {
		String type = separatePostFixExpression(expression);
		
		if (type == "0") {
			return Constants.INTEIRO_LEXEMA;
		} else {
			return Constants.BOOLEANO_LEXEMA;
		}
	}
	
	private String separatePostFixExpression(String expression) throws SemanticException{
		String[] aux = expression.split(" ");
		List<String> expressionList = new ArrayList<String>(Arrays.asList(aux));
		
		
		for(int j = 0; j < expressionList.size() ; j++) {
			String parcel = expressionList.get(j);
			if (!(isOperator(parcel)) && !(isUnaryOperator(parcel))) {
				if (Constants.INTEIRO_LEXEMA.equals(tableOfSymbols.searchTypeOfVariableOrFunction(parcel))) {
					expressionList.set(j, "0");
				} else if (Constants.BOOLEANO_LEXEMA.equals(tableOfSymbols.searchTypeOfVariableOrFunction(parcel))){
					expressionList.set(j, "1");
				} else if (Constants.VERDADEIRO_LEXEMA.equals(parcel) || Constants.FALSO_LEXEMA.equals(parcel)) {
					expressionList.set(j, "1");
				} else {
					expressionList.set(j, "0");
				}
			}
			
		}
		
		for(int i = 0; i < expressionList.size() ; i++) {
			if (isOperator(expressionList.get(i))) {
				
				String operation = returnOperationType(expressionList.get(i - 2), expressionList.get(i - 1), expressionList.get(i));
				
				expressionList.remove(i);
				expressionList.remove(i-1);
				expressionList.remove(i-2);
				expressionList.add(i-2, operation);
				
				i = 0;
			} else if (isUnaryOperator(expressionList.get(i))){
				String operation = returnOperationType(expressionList.get(i - 1), null, expressionList.get(i));
				
				expressionList.remove(i);
				expressionList.remove(i-1);
				expressionList.add(i-1, operation);
				
				i = 0;
			}
		}
		return expressionList.get(0);
	}
	
	private boolean isOperator(String parcel) {
		
		if (Constants.MULTIPLICACAO.equals(parcel) ||
			Constants.DIVISAO.equals(parcel) ||
			Constants.MAIS.equals(parcel) ||
			Constants.MENOS.equals(parcel) || 
			Constants.MAIOR.equals(parcel) ||
		    Constants.MENOR.equals(parcel) ||
		    Constants.MAIOR_IGUAL.equals(parcel) ||
		    Constants.MENOR_IGUAL.equals(parcel) ||
		    Constants.IGUAL.equals(parcel) ||
		    Constants.DIFERENTE.equals(parcel) ||
		    Constants.E.equals(parcel) ||
		    Constants.OU.equals(parcel)) {
			
			return true;
		}
		
		return false;
	}	
	
	private String returnOperationType(String firstType, String secondType, String operator) throws SemanticException {
		// 0 representa um tipo inteiro
		// 1 representa um tipo booleano
		
		if (isOperator(operator)) {
			if (isMathOperator(operator)) {
				if (firstType == "0" && secondType == "0") {
					return "0";
				}
				
				throw new SemanticException("Operações aritméticas(+ | - | * | div) devem envolver duas variáveis inteiras");
			} else if (isRelationalOperator(operator)) {
				if (firstType == "0" && secondType == "0") {
					return "1";
				}
				
				throw new SemanticException("Operações relacionais(!= | = | < | <= | > | >=) devem envolver duas variáveis inteiras");
			} else {
				if (firstType == "1" && secondType == "1") {
					return "1";
				}
				
				throw new SemanticException("Operações lógicas(e | ou) devem envolver duas variáveis booleanas");
			}
		} else {
			if (isUnaryMathOperator(operator)) {
				if (firstType == "0") {
					return "0";
				}
				
				throw new SemanticException("Operações envolvendo operadores unários(+ | -) devem ser com variáveis inteiras.\n");
			} else {
				if (firstType == "1") {
					return "1";
				}
				
				throw new SemanticException("Operações envolvendo operador unário(NÃO) devem ser com variáveis booleanas.\n");
			}
		}
	}
	
	private boolean isUnaryOperator(String parcel) {
		
		if (Constants.MAIS_UNARIO.equals(parcel) ||
			Constants.MENOS_UNARIO.equals(parcel) ||
			Constants.NAO.equals(parcel) ) {
			return true;
		}
		
		return false;
	}
	
	private boolean isUnaryMathOperator(String parcel) {
		
		if (Constants.MAIS_UNARIO.equals(parcel) ||
			Constants.MENOS_UNARIO.equals(parcel)) {
			return true;
		}
		
		return false;
	}
	
	private boolean isMathOperator(String parcel) {
		
		if (Constants.MULTIPLICACAO.equals(parcel) ||
			Constants.DIVISAO.equals(parcel) ||
			Constants.MAIS.equals(parcel) ||
			Constants.MENOS.equals(parcel)) {
			
			return true;
		}
		
		return false;
	}
	
	private boolean isRelationalOperator(String parcel) {
		
		if (Constants.MAIOR.equals(parcel) ||
		    Constants.MENOR.equals(parcel) ||
		    Constants.MAIOR_IGUAL.equals(parcel) ||
		    Constants.MENOR_IGUAL.equals(parcel) ||
		    Constants.IGUAL.equals(parcel) ||
		    Constants.DIFERENTE.equals(parcel)) {
			
			return true;
		}
		
		return false;
	}
	
	private int defineOperatorsPriority(String operator) {
		if (Constants.MAIS_UNARIO.equals(operator) ||
			Constants.MENOS_UNARIO.equals(operator) ||
			Constants.NAO.equals(operator)) {
			return 5;
		} else if (Constants.MULTIPLICACAO.equals(operator) ||
				   Constants.DIVISAO.equals(operator)) {
			return 4;
		} else if (Constants.MAIS.equals(operator) ||
				   Constants.MENOS.equals(operator)) {
			return 3;
		} else if (Constants.MAIOR.equals(operator) ||
				   Constants.MENOR.equals(operator) ||
				   Constants.MAIOR_IGUAL.equals(operator) ||
				   Constants.MENOR_IGUAL.equals(operator) ||
				   Constants.IGUAL.equals(operator) ||
				   Constants.DIFERENTE.equals(operator)) {
			return 2;
		} else if (Constants.E.equals(operator)) {
			return 1;
		} else if (Constants.OU.equals(operator)) {
			return 0;
		}
		
		return -1;
	}
	
	public void whoCallsMe(String type, String caller) throws SemanticException {
		if (Constants.SE_LEXEMA.equals(caller) || Constants.ENQUANTO_LEXEMA.equals(caller)) {
			if (!(Constants.BOOLEANO_LEXEMA.equals(type))) {
				throw new SemanticException("A condição presente no '" + caller.toUpperCase() + "' deveria resultar num tipo booleano");
			}
		} else {
			String callerType = tableOfSymbols.searchTypeOfVariableOrFunction(caller);
			
			if(!(type.equals(callerType))) {
				throw new SemanticException("Não é possível realizar a atribuição de uma expressão do tipo " 
											+ type + " em uma variável/função do tipo " + callerType);
			}
		}
	}

	
}


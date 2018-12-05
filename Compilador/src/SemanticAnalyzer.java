import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import Constants.*;
import Exceptions.SemanticException;
import Symbols.Function;
import Symbols.Procedure;
import Symbols.Symbol;
import Symbols.TableOfSymbols;
import Symbols.Variable;



public class SemanticAnalyzer {
	private TableOfSymbols tableOfSymbols;

	private ArrayList<Token> functionTokenList = new ArrayList<Token>();
	private int lineWithoutReturn;
	private int line;
	private boolean error;
	
	public SemanticAnalyzer() {
		tableOfSymbols = new TableOfSymbols();
	}

	/* Métodos envolvidos com a Tabela de Símbolos */

	/* Métodos de inserção */

	// Programa
	public void insertProgram(Token token) {
		tableOfSymbols.insert(new Symbol(token.getLexema(), -1, -1));
	}

	// Procedimento ou Função
	public void insertProcOrFunc(Token token, String type, int label) {
		if (Constants.PROCEDIMENTO.equals(type)) {
			tableOfSymbols.insert(new Procedure(token.getLexema(), label));
		} else if (Constants.FUNCAO.equals(type)) {
			tableOfSymbols.insert(new Function(token.getLexema(), label));
		}
	}

	// Variável
	public void insertVariable(Token token, int position) {
		tableOfSymbols.insert(new Variable(token.getLexema(), position));
	}

	// Tipo Função
	public void insertTypeOnFunction(String type) {
		tableOfSymbols.insertTypeOnFunction(type);
	}

	// Tipo Variável
	public void insertTypeOnVariable(Token token) {
		tableOfSymbols.insertTypeOnVariable(token.getLexema());
	}

	/* Métodos de busca */

	public void searchFunction(Token token) throws SemanticException {
		if (!(tableOfSymbols.searchFunction(token.getLexema()))) {
			throw new SemanticException(
					"Função '" + token.getLexema() + "' não está declarada.\nLinha: " + token.getLine());
		}
	}

	public int searchFunctionLabel(Token token) throws SemanticException {
		int labelResult = tableOfSymbols.searchFunctionLabel(token.getLexema());

		if (labelResult == -1) {
			throw new SemanticException(
					"Função '" + token.getLexema() + "' não está declarada.\nLinha: " + token.getLine());
		} else {
			return labelResult;
		}
	}

	public void searchFunctionWithTheSameName(Token token) throws SemanticException {
		if (tableOfSymbols.searchFunction(token.getLexema())) {
			throw new SemanticException("Já existe uma função com o mesmo nome da função da linha: " + token.getLine());
		}
	}

	public void searchInTableOfSymbols(Token token) throws SemanticException {
		if (tableOfSymbols.search(token.getLexema())) {
			throw new SemanticException(
					"Já existe uma variável com o mesmo nome da variável da linha: " + token.getLine());
		}
	}

	public void searchProcedure(Token token) throws SemanticException {
		if (!(tableOfSymbols.searchProcedure(token.getLexema()))) {
			throw new SemanticException(
					"Procedimento '" + token.getLexema() + "' não está declarado.\nLinha: " + token.getLine());
		}
	}

	public int searchProcedureLabel(Token token) throws SemanticException {
		int labelResult = tableOfSymbols.searchProcedureLabel(token.getLexema());

		if (labelResult == -1) {
			throw new SemanticException(
					"Procedimento '" + token.getLexema() + "' não está declarado.\nLinha: " + token.getLine());
		} else {
			return labelResult;
		}
	}

	public void searchProcedureWithTheSameName(Token token) throws SemanticException {
		if (tableOfSymbols.searchProcedure(token.getLexema())) {
			throw new SemanticException(
					"Já existe um procedimento com o mesmo nome do procedimento da linha: " + token.getLine());
		}
	}

	public int searchSymbol(Token token) throws SemanticException {
		int index = tableOfSymbols.searchSymbol(token.getLexema());

		if (index >= 0) {
			return index;
		}

		throw new SemanticException("Variável ou Função '" + token.getLexema()
				+ "' não está definida no escopo atual. \n Linha: " + token.getLine());
	}

	public void searchVariable(Token token) throws SemanticException {
		if (!(tableOfSymbols.searchVariable(token.getLexema()))) {
			throw new SemanticException(
					"A variável " + token.getLexema() + " não está definida.\nLinha: " + token.getLine());
		}
	}

	public boolean searchVariableOrFunction(Token token) throws SemanticException {
		if (!(tableOfSymbols.searchVariable(token.getLexema()) || tableOfSymbols.searchFunction(token.getLexema()))) {
			throw new SemanticException(
					"A variável ou função " + token.getLexema() + " não está definida.\nLinha: " + token.getLine());
		} else {
			// Variável
			if (tableOfSymbols.searchVariable(token.getLexema())) {
				return false;
			}
			// Função
			return true;
		}
	}

	/* Métodos de recuperação */
	public String getLexemaOfSymbol(int index) {
		return tableOfSymbols.getSymbol(index).getLexema();
	}

	/* Outros métodos sobre a tabela de simbolos */
	public boolean isValidFunction(int index) {
		if (tableOfSymbols.getSymbol(index) instanceof Function
				&& (tableOfSymbols.getSymbol(index).getType() == Constants.INTEIRO_LEXEMA
						|| tableOfSymbols.getSymbol(index).getType() == Constants.BOOLEANO_LEXEMA)) {
			return true;
		}

		return false;
	}

	public void cleanTableLevel() {
		tableOfSymbols.cleanLevel();
	}

	/* Métodos Semânticos */

	public String expressionToPostfix(List<Token> expression) {
		List<String> stack = new ArrayList<String>();
		String output = "";

		for (int a = 0; a < expression.size(); a++) {
			String parcel = expression.get(a).getLexema();
			
			if (Constants.NUMERO_SIMBOLO.equals(expression.get(a).getSymbol()) ||
				Constants.IDENTIFICADOR_SIMBOLO.equals(expression.get(a).getSymbol()) || 
				Constants.VERDADEIRO_SIMBOLO.equals(expression.get(a).getSymbol()) ||
				Constants.FALSO_SIMBOLO.equals(expression.get(a).getSymbol())) {

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
			for (int i = stackTop; i >= 0; i--) {
				output = output.concat(stack.get(i) + " ");
				stack.remove(i);
			}
		}
		// System.out.println("Saída: " + output);
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

	private String separatePostFixExpression(String expression) throws SemanticException {
		String[] aux = expression.split(" ");
		List<String> expressionList = new ArrayList<String>(Arrays.asList(aux));

		for (int j = 0; j < expressionList.size(); j++) {
			String parcel = expressionList.get(j);
			if (!(isOperator(parcel)) && !(isUnaryOperator(parcel))) {
				if (Constants.INTEIRO_LEXEMA.equals(tableOfSymbols.searchTypeOfVariableOrFunction(parcel))) {
					expressionList.set(j, "0");
				} else if (Constants.BOOLEANO_LEXEMA.equals(tableOfSymbols.searchTypeOfVariableOrFunction(parcel))) {
					expressionList.set(j, "1");
				} else if (Constants.VERDADEIRO_LEXEMA.equals(parcel) || Constants.FALSO_LEXEMA.equals(parcel)) {
					expressionList.set(j, "1");
				} else {
					expressionList.set(j, "0");
				}
			}

		}

		for (int i = 0; i < expressionList.size(); i++) {
			if (isOperator(expressionList.get(i))) {

				String operation = returnOperationType(expressionList.get(i - 2), expressionList.get(i - 1),
						expressionList.get(i));

				expressionList.remove(i);
				expressionList.remove(i - 1);
				expressionList.remove(i - 2);
				expressionList.add(i - 2, operation);

				i = 0;
			} else if (isUnaryOperator(expressionList.get(i))) {
				String operation = returnOperationType(expressionList.get(i - 1), null, expressionList.get(i));

				expressionList.remove(i);
				expressionList.remove(i - 1);
				expressionList.add(i - 1, operation);

				i = 0;
			}
		}
		return expressionList.get(0);
	}

	private boolean isOperator(String parcel) {

		if (Constants.MULTIPLICACAO.equals(parcel) || Constants.DIVISAO.equals(parcel) || Constants.MAIS.equals(parcel)
				|| Constants.MENOS.equals(parcel) || Constants.MAIOR.equals(parcel) || Constants.MENOR.equals(parcel)
				|| Constants.MAIOR_IGUAL.equals(parcel) || Constants.MENOR_IGUAL.equals(parcel)
				|| Constants.IGUAL.equals(parcel) || Constants.DIFERENTE.equals(parcel) || Constants.E.equals(parcel)
				|| Constants.OU.equals(parcel)) {

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

				throw new SemanticException(
						"Operações aritméticas(+ | - | * | div) devem envolver duas variáveis inteiras");
			} else if (isRelationalOperator(operator)) {
				if (firstType == "0" && secondType == "0") {
					return "1";
				}

				throw new SemanticException(
						"Operações relacionais(!= | = | < | <= | > | >=) devem envolver duas variáveis inteiras");
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

				throw new SemanticException(
						"Operações envolvendo operadores unários(+ | -) devem ser com variáveis inteiras.\n");
			} else {
				if (firstType == "1") {
					return "1";
				}

				throw new SemanticException(
						"Operações envolvendo operador unário(NÃO) devem ser com variáveis booleanas.\n");
			}
		}
	}

	private boolean isUnaryOperator(String parcel) {

		if (Constants.MAIS_UNARIO.equals(parcel) || Constants.MENOS_UNARIO.equals(parcel)
				|| Constants.NAO.equals(parcel)) {
			return true;
		}

		return false;
	}

	private boolean isUnaryMathOperator(String parcel) {

		if (Constants.MAIS_UNARIO.equals(parcel) || Constants.MENOS_UNARIO.equals(parcel)) {
			return true;
		}

		return false;
	}

	private boolean isMathOperator(String parcel) {

		if (Constants.MULTIPLICACAO.equals(parcel) || Constants.DIVISAO.equals(parcel) || Constants.MAIS.equals(parcel)
				|| Constants.MENOS.equals(parcel)) {

			return true;
		}

		return false;
	}

	private boolean isRelationalOperator(String parcel) {

		if (Constants.MAIOR.equals(parcel) || Constants.MENOR.equals(parcel) || Constants.MAIOR_IGUAL.equals(parcel)
				|| Constants.MENOR_IGUAL.equals(parcel) || Constants.IGUAL.equals(parcel)
				|| Constants.DIFERENTE.equals(parcel)) {

			return true;
		}

		return false;
	}

	private int defineOperatorsPriority(String operator) {
		if (Constants.MAIS_UNARIO.equals(operator) || Constants.MENOS_UNARIO.equals(operator)
				|| Constants.NAO.equals(operator)) {
			return 5;
		} else if (Constants.MULTIPLICACAO.equals(operator) || Constants.DIVISAO.equals(operator)) {
			return 4;
		} else if (Constants.MAIS.equals(operator) || Constants.MENOS.equals(operator)) {
			return 3;
		} else if (Constants.MAIOR.equals(operator) || Constants.MENOR.equals(operator)
				|| Constants.MAIOR_IGUAL.equals(operator) || Constants.MENOR_IGUAL.equals(operator)
				|| Constants.IGUAL.equals(operator) || Constants.DIFERENTE.equals(operator)) {
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
				throw new SemanticException(
						"A condição presente no '" + caller.toUpperCase() + "' deveria resultar num tipo booleano");
			}
		} else {
			String callerType = tableOfSymbols.searchTypeOfVariableOrFunction(caller);

			if (!(type.equals(callerType))) {
				throw new SemanticException("Não é possível realizar a atribuição de uma expressão do tipo " + type
						+ " em uma variável/função do tipo " + callerType);
			}
		}
	}

	public String positionOfVariable(String variable) {
		int position = tableOfSymbols.searchPositionOfVariable(variable);

		return Integer.toString(position);
	}

	// Formata a expressão para usar na geração de código
	public String formatExpression(String expression) {
		String[] aux = expression.split(" ");
		String newExpression = "";
		int auxPosition;

		for (int i = 0; i < aux.length; i++) {
			if(!tableOfSymbols.searchFunction(aux[i])) {
				auxPosition = tableOfSymbols.searchPositionOfVariable(aux[i]);

				if (auxPosition != -1) {
					newExpression = newExpression.concat("p" + auxPosition + " ");
				} else {
					newExpression = newExpression.concat(aux[i] + " ");
				}
			} else {
				int labelResult = tableOfSymbols.searchFunctionLabel(aux[i]);
				newExpression = newExpression.concat("funcao" + labelResult + " ");
			}
			
		}

		return newExpression;
	}

	
	/* Métodos envolvendo o retorno de função */
	
	public void insertTokenOnFunctionList(Token token) {
		functionTokenList.add(token);
	}
	
	public void verifyFunctionList(String label) {
		Token auxToken = null;
		
		boolean conditionalThenReturn = false;
		boolean conditionalElseReturn = false;
		int thenPosition = -1;
		int elsePosition = thenPosition;
		
		for(int i = 0; i < functionTokenList.size(); i++) {
			if(Constants.SE_SIMBOLO.equals(functionTokenList.get(i).getSymbol())
			   && functionTokenList.get(i).getLexema().contains(label)) {
				functionTokenList.remove(i);
				i--;
			} else if (Constants.ENTAO_SIMBOLO.equals(functionTokenList.get(i).getSymbol()) 
					&& functionTokenList.get(i).getLexema().contains(label)) {
				if(functionTokenList.size() > (i + 1)) {
					if (Constants.IDENTIFICADOR_SIMBOLO.equals(functionTokenList.get(i + 1).getSymbol())) {
						conditionalThenReturn = true;
						auxToken = functionTokenList.get(i + 1);
					}	
				} else {
					lineWithoutReturn = functionTokenList.get(i).getLine();
				}
				thenPosition = i;
			}  else if (Constants.SENAO_SIMBOLO.equals(functionTokenList.get(i).getSymbol()) 
					&& functionTokenList.get(i).getLexema().contains(label)) {
				if(functionTokenList.size() > (i + 1)) {
					if (Constants.IDENTIFICADOR_SIMBOLO.equals(functionTokenList.get(i + 1).getSymbol())) {
						conditionalElseReturn = true;
						elsePosition = i + 1;
						auxToken = functionTokenList.get(i + 1);
					} 	
				} else {
					lineWithoutReturn = functionTokenList.get(i).getLine();
					elsePosition = i;
				}
				
			}
		}
		
		if(elsePosition == (-1)) elsePosition = functionTokenList.size() - 1;
		
		removeIf(elsePosition, thenPosition, (conditionalThenReturn && conditionalElseReturn), auxToken);
	}
	
	public boolean thisFunctionHasReturn(String nameOfFunction) throws SemanticException{
		int aux = 0;
		
		for(int i = 0 ; i < functionTokenList.size(); i++ ) {
			if (nameOfFunction.equals(functionTokenList.get(i).getLexema())) {
				aux++;
				if (aux == functionTokenList.size()) {
					return true;
				}
			}	
		}
		
		

		error = true;
		if (lineWithoutReturn != 0)	line = lineWithoutReturn;
		
		throw new SemanticException("Nem todos os caminhos possíveis da função possuem retorno."
				+ "\nLinha: " + line);
	}
	
	private void removeIf(int start, int end, boolean functionReturn, Token tokenFunction) {
		for(int i = start; i >= end; i--) {
			functionTokenList.remove(i);
		}
		
		if(functionReturn && tokenFunction != null) {
			functionTokenList.add(tokenFunction);
		}
	}
	
	public void clearFunctionList() {
		functionTokenList.clear();
	}

	
	public void debugTableFunction() {
		for(int i = 0; i < functionTokenList.size(); i++) {
			System.out.println(functionTokenList.get(i).getLexema());
		}
	}
	//

	public void setLine(int line) {
		this.line = line;
	}
	public int getLine() {
		return line;
	}

	public boolean hasError() {
		return error;
	}
	
	//Debug Tabela de Simbolos

	public void debugTable() {
		tableOfSymbols.debugTable();
	}

}

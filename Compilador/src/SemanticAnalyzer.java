import java.util.ArrayList;
import java.util.List;
import Constants.*;

public class SemanticAnalyzer {
	
	public void expressionToPostfix(List<Token> expression) {
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

	
}


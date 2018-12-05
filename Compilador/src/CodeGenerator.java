import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Constants.Constants;

public class CodeGenerator {

	private String code = "";
	private int variableInMemory = 0;
	private List<Integer> variableAlloc = new ArrayList<Integer>();

	public void createCode(String value1, String value2, String value3) {

		code = code.concat(value1 + " ").concat(value2 + " ").concat(value3 + "\r\n");

	}

	public void createCode(String expressionPosFix) {
		String[] aux = expressionPosFix.split(" ");

		for (int a = 0; a < aux.length; a++) {
			if (aux[a].contains("p")) {

				String[] value = aux[a].split("p");
				code = code.concat(Constants.LDV + " ").concat(value[1]).concat("\r\n");

			} else if (aux[a].contains("funcao")) {

				String[] value = aux[a].split("funcao");
				code = code.concat(Constants.CALL + " ").concat(Constants.LABEL + value[1]).concat("\r\n");
				
			} else if (aux[a].equals(Constants.MAIS)) {
				code = code.concat(Constants.ADD).concat("\r\n");
			} else if (aux[a].equals(Constants.MENOS)) {
				code = code.concat(Constants.SUB).concat("\r\n");
			} else if (aux[a].equals(Constants.MULTIPLICACAO)) {
				code = code.concat(Constants.MULT).concat("\r\n");
			} else if (aux[a].equals(Constants.DIVISAO)) {
				code = code.concat(Constants.DIVI).concat("\r\n");
			} else if (aux[a].equals(Constants.E)) {
				code = code.concat(Constants.AND).concat("\r\n");
			} else if (aux[a].equals(Constants.OU)) {
				code = code.concat(Constants.OR).concat("\r\n");
			} else if (aux[a].equals(Constants.MENOR)) {
				code = code.concat(Constants.CME).concat("\r\n");
			} else if (aux[a].equals(Constants.MAIOR)) {
				code = code.concat(Constants.CMA).concat("\r\n");
			} else if (aux[a].equals(Constants.IGUAL)) {
				code = code.concat(Constants.CEQ).concat("\r\n");
			} else if (aux[a].equals(Constants.DIFERENTE)) {
				code = code.concat(Constants.CDIF).concat("\r\n");
			} else if (aux[a].equals(Constants.MENOR_IGUAL)) {
				code = code.concat(Constants.CMEQ).concat("\r\n");
			} else if (aux[a].equals(Constants.MAIOR_IGUAL)) {
				code = code.concat(Constants.CMAQ).concat("\r\n");
			} else if (aux[a].equals(Constants.MENOS_UNARIO)) {
				code = code.concat(Constants.INV).concat("\r\n");
			} else if (aux[a].equals(Constants.MAIS_UNARIO)) {
				// do nothing
			} else if (aux[a].equals(Constants.NAO)) {
				code = code.concat(Constants.NEG).concat("\r\n");
			} else {
				if(aux[a].equals(Constants.VERDADEIRO_LEXEMA)) {
					code = code.concat(Constants.LDC).concat(" 1").concat("\r\n");
				} else if(aux[a].equals(Constants.FALSO_LEXEMA)) {
					code = code.concat(Constants.LDC).concat(" 0").concat("\r\n");
				} else if(aux[a].equals(Constants.EMPTY)){
					// do nothing
				} else {
					code = code.concat(Constants.LDC + " ").concat(aux[a]).concat("\r\n");
				}
			}
		}
	}

	public void createCode(String command, int countVariable) {
		if (Constants.ALLOC.equals(command)) {
			code = code.concat(command + " ").concat(variableInMemory + " ").concat(countVariable + "\r\n");
			variableInMemory = variableInMemory + countVariable;
			variableAlloc.add(countVariable);
		}
		else {		
			
			if (countVariable == 0) {
				code = code.concat(command + "\r\n");
			} else {
				int position = variableAlloc.size() - 1;
				int countVariableToDalloc = variableAlloc.get(position);
				
				variableInMemory = variableInMemory - countVariableToDalloc;
				code = code.concat(command + " ").concat(variableInMemory + " ").concat(countVariableToDalloc + "\r\n");
				variableAlloc.remove(position);	
			}		
				
			
		}
	}
	
	public void createFile() {
		try {
			File directory = new File("..\\Testes\\CodigoFinalVM", "code.txt");
			directory.createNewFile();

			FileWriter file = new FileWriter(directory);
			file.write(code);
			file.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void debugCode() {
		System.out.println(this.code);
	}

}
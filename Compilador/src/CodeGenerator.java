import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
				code = code.concat("LDV" + " ").concat(value[1]).concat("\r\n");

			} else if (aux[a].equals("+")) {
				code = code.concat("ADD").concat("\r\n");
			} else if (aux[a].equals("-")) {
				code = code.concat("SUB").concat("\r\n");
			} else if (aux[a].equals("*")) {
				code = code.concat("MULT").concat("\r\n");
			} else if (aux[a].equals("div")) {
				code = code.concat("DIVI").concat("\r\n");
			} else if (aux[a].equals("e")) {
				code = code.concat("AND").concat("\r\n");
			} else if (aux[a].equals("ou")) {
				code = code.concat("OR").concat("\r\n");
			} else if (aux[a].equals("<")) {
				code = code.concat("CME").concat("\r\n");
			} else if (aux[a].equals(">")) {
				code = code.concat("CMA").concat("\r\n");
			} else if (aux[a].equals("=")) {
				code = code.concat("CEQ").concat("\r\n");
			} else if (aux[a].equals("!=")) {
				code = code.concat("CDIF").concat("\r\n");
			} else if (aux[a].equals("<=")) {
				code = code.concat("CMEQ").concat("\r\n");
			} else if (aux[a].equals(">=")) {
				code = code.concat("CMAQ").concat("\r\n");
			} else if (aux[a].equals("-u")) {
				code = code.concat("INV").concat("\r\n");
			} else if (aux[a].equals("+u")) {
				// do nothing
			} else if (aux[a].equals("nao")) {
				code = code.concat("NEG").concat("\r\n");
			} else {
				if(aux[a].equals("verdadeiro")) {
					code = code.concat("LDC").concat(" 1").concat("\r\n");
				} else if(aux[a].equals("falso")) {
					code = code.concat("LDC").concat(" 0").concat("\r\n");
				} else {
					code = code.concat("LDC" + " ").concat(aux[a]).concat("\r\n");	
				}
			}
		}
	}

	public void createCode(String command, int countVariable) {
		if (command == "ALLOC") {
			code = code.concat("ALLOC" + " ").concat(variableInMemory + " ").concat(countVariable + "\r\n");
			variableInMemory = variableInMemory + countVariable;
			variableAlloc.add(countVariable);
		}
		else {
			int position = variableAlloc.size() - 1;
			int countVariableToDalloc = variableAlloc.get(position);
			
			code = code.concat("DALLOC" + " ").concat(variableInMemory + " ").concat(countVariableToDalloc + "\r\n");
			variableInMemory = variableInMemory - countVariableToDalloc;
			variableAlloc.remove(position);
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
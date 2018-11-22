import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CodeGenerator {

	private String code = "";

	public void createCode(String value1, String value2, String value3) {

		code = code.concat(value1 + " ").concat(value2 + " ").concat(value3 + "\r\n");

	}

	public void createCode(String expressionPosFix) {
		String[] aux = expressionPosFix.split(" ");

		for (int a = 0; a < aux.length; a++) {
			if (aux[a].contains("p")) {

				String[] value = aux[a].split("p");
				code = code.concat("LDV" + " ").concat(value[1] + " ").concat("" + "\r\n");

			} else if (aux[a].equals("+")) {
				code = code.concat("ADD" + " ").concat("" + " ").concat("" + "\r\n");
			} else if (aux[a].equals("-")) {
				code = code.concat("SUB" + " ").concat("" + " ").concat("" + "\r\n");
			} else if (aux[a].equals("*")) {
				code = code.concat("MULT" + " ").concat("" + " ").concat("" + "\r\n");
			} else if (aux[a].equals("div")) {
				code = code.concat("DIVI" + " ").concat("" + " ").concat("" + "\r\n");
			} else if (aux[a].equals("e")) {
				code = code.concat("AND" + " ").concat("" + " ").concat("" + "\r\n");
			} else if (aux[a].equals("ou")) {
				code = code.concat("OR" + " ").concat("" + " ").concat("" + "\r\n");
			} else if (aux[a].equals("<")) {
				code = code.concat("CME" + " ").concat("" + " ").concat("" + "\r\n");
			} else if (aux[a].equals(">")) {
				code = code.concat("CMA" + " ").concat("" + " ").concat("" + "\r\n");
			} else if (aux[a].equals("=")) {
				code = code.concat("CEQ" + " ").concat("" + " ").concat("" + "\r\n");
			} else if (aux[a].equals("!=")) {
				code = code.concat("CDIF" + " ").concat("" + " ").concat("" + "\r\n");
			} else if (aux[a].equals("<=")) {
				code = code.concat("CMEQ" + " ").concat("" + " ").concat("" + "\r\n");
			} else if (aux[a].equals(">=")) {
				code = code.concat("CMAQ" + " ").concat("" + " ").concat("" + "\r\n");
			} else if (aux[a].equals("-u")) {
				code = code.concat("INV" + " ").concat("" + " ").concat("" + "\r\n");
			} else if (aux[a].equals("+u")) {
				//do nothing
			} else if (aux[a].equals("nao")) {
				code = code.concat("NEG" + " ").concat("" + " ").concat("" + "\r\n");
			} else {
				code = code.concat("LDC" + " ").concat(aux[a] + " ").concat("" + "\r\n");
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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import Constants.Constants;

public class CodeGenerator {

	private String code = "";

	public void createCode(String value1, String value2, String value3) {

		code = code.concat(value1 + " ").concat(value2 + " ").concat(value3 + "\r\n");
		
	}
	
	public void createCode(String expressionPosFix) {
		String[] aux = expressionPosFix.split(" ");
		
		//Em Desenvolvimento
		/* for (int a = 0; a < aux.length ; a++) {
			if (aux[a].equals("")) { //ignora
				code = code.concat("LDV" + " ").concat(value2 + " ").concat("" + "\r\n");
			} else if(aux[a].equals("")){
				code = code.concat("LDC" + " ").concat(value2 + " ").concat("" + "\r\n");
			} else if(aux[a].equals("+")){
				code = code.concat("ADD" + " ").concat("" + " ").concat("" + "\r\n");
			} else if(aux[a].equals("-")){
				code = code.concat("SUB" + " ").concat("" + " ").concat("" + "\r\n");
			} else if(aux[a].equals("*")){
				code = code.concat("MULT" + " ").concat("" + " ").concat("" + "\r\n");
			} else if(aux[a].equals("div")){
				code = code.concat("DIVI" + " ").concat("" + " ").concat("" + "\r\n");
			} else if(aux[a].equals("and")){
				code = code.concat("AND" + " ").concat("" + " ").concat("" + "\r\n");
			} else if(aux[a].equals("or")){
				code = code.concat("OR" + " ").concat("" + " ").concat("" + "\r\n");
			} else if(aux[a].equals("")){
				code = code.concat("" + " ").concat("" + " ").concat("" + "\r\n");
			} else if(aux[a].equals("")){
				code = code.concat("" + " ").concat("" + " ").concat("" + "\r\n");
			} else if(aux[a].equals("")){
				code = code.concat("" + " ").concat("" + " ").concat("" + "\r\n");
			} else if(aux[a].equals("")){
				code = code.concat("" + " ").concat("" + " ").concat("" + "\r\n");
			} else if(aux[a].equals("")){
				code = code.concat("" + " ").concat("" + " ").concat("" + "\r\n");
			} else if(aux[a].equals("")){
				code = code.concat("" + " ").concat("" + " ").concat("" + "\r\n");
			} else if(aux[a].equals("")){
				code = code.concat("" + " ").concat("" + " ").concat("" + "\r\n");
			} 
		} */
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
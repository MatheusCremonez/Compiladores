import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CodeGenerator {

	private String code = "";

	public void createCode(String value1, String value2, String value3) {

		code = code.concat(value1 + " ").concat(value2 + " ").concat(value3 + "\r\n");
		
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
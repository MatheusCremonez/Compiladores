import java.util.ArrayList;

public class LexicalAnalyzer {

	public LexicalAnalyzer(String file) {
		// System.out.println(file);
		analise(file);
	}

	public void analise(String file) {
		int index = 0;
		char token;
		ArrayList listaToken = new ArrayList();

		char caracter = file.charAt(index);

		while (index < file.length()) {
			while ((((caracter == '{') || (caracter == ' ')) && (index < file.length()))) {
				if (caracter == '{') {
					while (caracter != '}') {
						index++;
						if (index < file.length()) {
							caracter = file.charAt(index);
						}
					}
					index++;
					caracter = file.charAt(index);
				}
				while (caracter == ' ') {
					index++;
					if (index < file.length()) {
						caracter = file.charAt(index);
					}
				}
			}
			if ((index < file.length())) {
				if(caracter == '\n') {
					index++;
				}
				else {
					token = pegaToken(caracter);
					listaToken.add(token);
					index++;
				}
				
				if (index < file.length()) {
					caracter = file.charAt(index);
				}
			}

		}

		// Debug
		for (index = 0; index < listaToken.size(); index++) {
			System.out.println(listaToken.get(index));
		}
	}

	public char pegaToken(char caracter) {
		return caracter;
	}
}

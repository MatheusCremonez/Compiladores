import java.util.ArrayList;
import java.util.List;

public class LexicalAnalyzer {

	private int index = 0;
	private int line = 1;
	private final String fontFile;
	
	public LexicalAnalyzer(String file) {
		// System.out.println(file);
		fontFile = file;
		analise(fontFile);
	}

	public void analise(String file) {
		
		
		List<Token> listaToken = new ArrayList();

		char caracter = leCaracter();

		while (index < file.length()) {
			while ((((caracter == '{') || (caracter == ' ')) && (index < file.length()))) {
				if (caracter == '{') {
					while (caracter != '}') {
						index++;
						if (index < file.length()) {
							caracter = leCaracter();
						}
					}
					index++;
					caracter = leCaracter();
				}
				while (caracter == ' ') {
					index++;
					if (index < file.length()) {
						caracter = leCaracter();
					}
				}
			}
			if ((index < file.length())) {
				if(caracter == '\n') {
					index++;
					line++;
				}
				else {
					Token token = pegaToken(caracter);
					listaToken.add(token);
					index++;
				}
				
				if (index < file.length()) {
					caracter = file.charAt(index);
				}
			}

		}

		// Debug
		for (int i = 0; i < listaToken.size(); i++) {
			System.out.print(listaToken.get(i).symbol + " ");
			System.out.print(listaToken.get(i).lexema + " ");
			System.out.print(listaToken.get(i).line);
			System.out.println();
		}
	}
	
	public final char leCaracter() {
		return fontFile.charAt(index);
	}

	public final Token pegaToken(char caracter) {
		Token token = new Token("scaracter",Character.toString(caracter), line);
		return token;
	}
}

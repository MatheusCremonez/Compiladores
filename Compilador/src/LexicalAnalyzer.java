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

		List<Token> listaToken = new ArrayList<Token>();

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
				if (caracter == '\n') {
					index++;
					line++;
				} else {
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

	public Token pegaToken(char caracter) {
		// Token token = new Token("scaracter", Character.toString(caracter), line);
		Token token = trataAtribuicao(caracter);
		return token;
	}

	public final Token trataOperadorAritmetico(char caracter) {
		if (caracter == '+') {
			Token token = new Token("Smais", Character.toString(caracter), line);

			index++;
			if (index < fontFile.length()) {
				caracter = leCaracter();
			}

			return token;

		} else if (caracter == '-') {
			Token token = new Token("Smenos", Character.toString(caracter), line);

			index++;
			if (index < fontFile.length()) {
				caracter = leCaracter();
			}

			return token;

		} else { // Operador *
			Token token = new Token("Smult", Character.toString(caracter), line);

			index++;
			if (index < fontFile.length()) {
				caracter = leCaracter();
			}

			return token;
		}
	}

	public Token trataAtribuicao(char caracter) {

		// já chega sabendo que o primeiro caracter é um :

		if ((index + 1) < fontFile.length()) {
			char caracter2 = fontFile.charAt(index + 1);
			if (caracter2 == '=') {
				Token token = new Token("satribuição", Character.toString(caracter), line);
				index = index + 2;
				if (index < fontFile.length()) {
					caracter = leCaracter();
				}

				return token;
			} else {
				Token token = new Token("Sdoispontos", Character.toString(caracter), line);
				index++;
				if (index < fontFile.length()) {
					caracter = leCaracter();
				}

				return token;
			}
		} else {
			Token token = new Token("Sdoispontos", Character.toString(caracter), line);
			index++;
			if (index < fontFile.length()) {
				caracter = leCaracter();
			}

			return token;
		}
	}
}

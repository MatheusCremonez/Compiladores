import Exceptions.LexicalException;
import Constants.*;

public class LexicalAnalyzer {

	public boolean error = false; // pode ser private (alterar depois)
	private String message;
	private int index = 0;
	private int line = 1;
	private final String fontFile;

	public LexicalAnalyzer(String file) {
		fontFile = file;
	}

	public Token lexical() {
		char caracter;
		Token token;

		if (index < fontFile.length()) {
			caracter = leCaracter();

			try {
				caracter = verificaCaracteresIgnorados(caracter);
			} catch (LexicalException e) {
				setMessage(e.getMessage());
			}

			if (index < fontFile.length()) {
				try {
					token = pegaToken(caracter);
					index++;
					return token;
				} catch (LexicalException e) {
					setMessage(e.getMessage());
				}
			}
		}
		
		if(index >= fontFile.length()) {
			return new Token(Constants.FIM_ARQUIVO, Constants.FIM_ARQUIVO, line);
		}
		return new Token(Constants.ERRO , Constants.FIM_ARQUIVO, line);
	}

	private char verificaCaracteresIgnorados(char caracter) throws LexicalException {
		while (caracter == '{' || caracter == ' ' || caracter == '	' || caracter == '\n') {
			if (caracter == '{') {
				while (caracter != '}') {
					index++;
					if (index >= fontFile.length()) {
						error = true;
						throw new LexicalException("Há algum caracter inválido na linha " + line + ".");
					}
					if (caracter == '\n') {
						line++;
					}
					caracter = leCaracter();
				}
				if (caracter == '}') {
					index++;
					if (index >= fontFile.length()) {
						return caracter;
					}
					caracter = leCaracter();
				}
			}
			if (caracter == ' ') {
				index++;
				if (index >= fontFile.length()) {
					return caracter;
				}
				caracter = leCaracter();
			}

			if (caracter == '	') {
				index++;
				if (index >= fontFile.length()) {
					return caracter;
				}
				caracter = leCaracter();
			}

			if (caracter == '\n') {
				index++;
				line++;
				if (index >= fontFile.length()) {
					return caracter;
				}
				caracter = leCaracter();
			}
		}

		return caracter;
	}	

	private final Token pegaToken(char caracter) throws LexicalException {

		if (Character.isDigit(caracter)) {
			return trataDigito(caracter);
		} else if (Character.isLetter(caracter)) {
			return trataIdentificadorPalavraReservada(caracter);
		} else if (caracter == ':') {
			return trataAtribuicao(caracter);
		} else if (caracter == '+' || caracter == '-' || caracter == '*') {
			return trataOperadorAritmetico(caracter);
		} else if (caracter == '<' || caracter == '>' || caracter == '=' || caracter == '!') {
			return trataOperadorRelacional(caracter);
		} else if (caracter == ';' || caracter == ',' || caracter == '(' || caracter == ')' || caracter == '.') {
			return trataPontuacao(caracter);
		} else {
			error = true;
			index = fontFile.length();
			throw new LexicalException("Há algum caracter inválido na linha " + line + ".");
		}
	}
	
	private final char leCaracter() {
		return fontFile.charAt(index);
	}

	private final Token trataDigito(char caracter) {
		String num;
		char newCaracter;

		num = Character.toString(caracter);

		if (index < fontFile.length()) {
			newCaracter = leCaracter();
			while (Character.isDigit(newCaracter) && (index + 1) < fontFile.length()
					&& !(isInvalidCharacter(newCaracter))) {
				index++;
				newCaracter = leCaracter();
				if (Character.isDigit(newCaracter)) {
					num = num + Character.toString(newCaracter);
				}
			}
			if (!(Character.isDigit(newCaracter))) {
				index--;
			}

		}

		return new Token(Constants.NUMERO_SIMBOLO, num, line);

	}

	private final Token trataIdentificadorPalavraReservada(char caracter) {
		String id;
		char newCaracter;

		id = Character.toString(caracter);

		if (index < fontFile.length()) {
			newCaracter = leCaracter();
			if (Character.isLetter(newCaracter)) {
				while ((Character.isLetter(newCaracter) || Character.isDigit(newCaracter) || newCaracter == '_')
						&& (index + 1) < fontFile.length() && !(isInvalidCharacter(newCaracter))) {
					index++;
					newCaracter = leCaracter();
					if ((Character.isLetter(newCaracter) || Character.isDigit(newCaracter) || newCaracter == '_')) {
						id = id + Character.toString(newCaracter);
					}
				}
			}

			if (!(Character.isLetter(newCaracter) || Character.isDigit(newCaracter) || newCaracter == '_')) {
				index--;
			}

		}

		return switchIdentifier(id);

	}

	private final Token trataOperadorAritmetico(char caracter) {
		if (caracter == '+') {
			return new Token(Constants.MAIS_SIMBOLO, Character.toString(caracter), line);
		} else if (caracter == '-') {
			return new Token(Constants.MENOS_SIMBOLO, Character.toString(caracter), line);
		} else {
			return new Token(Constants.MULT_SIMBOLO, Character.toString(caracter), line);
		}
	}

	private final Token trataOperadorRelacional(char caracter) throws LexicalException {

		String op = Character.toString(caracter);
		char newCaracter;

		if (caracter == '<') {

			index++;
			if (index < fontFile.length()) {
				newCaracter = leCaracter();
				if (newCaracter == '=') {
					op = op + newCaracter;
					return new Token(Constants.MENOR_IGUAL_SIMBOLO, op, line);
				} else {
					index--;
				}
			}

			return new Token(Constants.MENOR_SIMBOLO, op, line);
		} else if (caracter == '>') {

			index++;
			if (index < fontFile.length()) {
				newCaracter = leCaracter();
				if (newCaracter == '=') {
					op = op + newCaracter;
					return new Token(Constants.MAIOR_IGUAL_SIMBOLO, op, line);
				} else {
					index--;
				}
			}
			return new Token(Constants.MAIOR_SIMBOLO, op, line);
		} else if (caracter == '=') {
			return new Token(Constants.IGUAL_SIMBOLO, op, line);
		} else if (caracter == '!') {
			index++;
			if (index < fontFile.length()) {
				newCaracter = leCaracter();
				if (newCaracter == '=') {
					op = op + newCaracter;
					return new Token(Constants.DIFERENTE_SIMBOLO, op, line);
				}
			}
			error = true;
			index = fontFile.length();
			throw new LexicalException("Há algum caracter inválido na linha " + line + ".");
		}
		error = true;
		index = fontFile.length();
		throw new LexicalException("Há algum caracter inválido na linha " + line + ".");
	}

	private final Token trataAtribuicao(char caracter) {

		String palavra = Character.toString(caracter);

		if ((index + 1) < fontFile.length()) {
			index = index + 1;
			char caracter2 = leCaracter();

			if (caracter2 == '=') {
				return new Token(Constants.ATRIBUICAO_SIMBOLO, palavra + Character.toString(caracter2), line);
			} else {
				index--;
				return new Token(Constants.DOIS_PONTOS_SIMBOLO, palavra, line);
			}
		} else {
			return new Token(Constants.DOIS_PONTOS_SIMBOLO, palavra, line);
		}
	}

	private final Token trataPontuacao(char caracter) {
		if (caracter == ';') {
			return new Token(Constants.PONTO_VIRGULA_SIMBOLO, Character.toString(caracter), line);
		} else if (caracter == ',') {
			return new Token(Constants.VIRGULA_SIMBOLO, Character.toString(caracter), line);
		} else if (caracter == '(') {
			return new Token(Constants.ABRE_PARENTESES_SIMBOLO, Character.toString(caracter), line);
		} else if (caracter == ')') {
			return new Token(Constants.FECHA_PARENTESES_SIMBOLO, Character.toString(caracter), line);
		} else { // considera ser o ponto (.)
			return new Token("sponto", Character.toString(caracter), line);
		}
	}

	private final Token switchIdentifier(String id) {
		switch (id) {
		case "programa":
			return new Token(Constants.PROGRAMA_SIMBOLO, id, line);
		case "se":
			return new Token(Constants.SE_SIMBOLO, id, line);
		case "entao":
			return new Token(Constants.ENTAO_SIMBOLO, id, line);
		case "senao":
			return new Token(Constants.SENAO_SIMBOLO, id, line);
		case "enquanto":
			return new Token(Constants.ENQUANTO_SIMBOLO, id, line);
		case "faca":
			return new Token(Constants.FACA_SIMBOLO, id, line);
		case "inicio":
			return new Token(Constants.INICIO_SIMBOLO, id, line);
		case "fim":
			return new Token(Constants.FIM_SIMBOLO, id, line);
		case "escreva":
			return new Token(Constants.ESCREVA_SIMBOLO, id, line);
		case "leia":
			return new Token(Constants.LEIA_SIMBOLO, id, line);
		case "var":
			return new Token(Constants.VAR_SIMBOLO, id, line);
		case "inteiro":
			return new Token(Constants.INTEIRO_SIMBOLO, id, line);
		case "booleano":
			return new Token(Constants.BOOLEANO_SIMBOLO, id, line);
		case "verdadeiro":
			return new Token(Constants.VERDADEIRO_SIMBOLO, id, line);
		case "falso":
			return new Token(Constants.FALSO_SIMBOLO, id, line);
		case "procedimento":
			return new Token(Constants.PROCEDIMENTO_SIMBOLO, id, line);
		case "funcao":
			return new Token(Constants.FUNCAO_SIMBOLO, id, line);
		case "div":
			return new Token(Constants.DIV_SIMBOLO, id, line);
		case "e":
			return new Token(Constants.E_SIMBOLO, id, line);
		case "ou":
			return new Token(Constants.OU_SIMBOLO, id, line);
		case "nao":
			return new Token(Constants.NAO_SIMBOLO, id, line);
		default:
			return new Token(Constants.IDENTIFICADOR_SIMBOLO, id, line);
		}
	}

	private final boolean isInvalidCharacter(char character) {
		return (character == '\n' || Character.isWhitespace(character) || Character.isSpaceChar(character));
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public final String getMessage() {
		return this.message;
	}
	
	public final int getLine() {
		return this.line;
	}
	
}

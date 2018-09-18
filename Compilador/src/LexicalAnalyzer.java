import java.util.ArrayList;
import java.util.List;

public class LexicalAnalyzer {

	public String message;
	public boolean error = false;
	private int index = 0;
	private int line = 1;
	private final String fontFile;

	public LexicalAnalyzer(String file) {
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
					caracter = leCaracter();
				}
			}

		}

		if(!error) {
			for (int i = 0; i < listaToken.size(); i++) {
				String newMessage = listaToken.get(i).symbol + " " + listaToken.get(i).lexema + " " + listaToken.get(i).line;
				if(i == 0) setMessage(newMessage); 
				else setMessage(getMessage() + '\n' + newMessage); 
			}
		}
	}

	public final char leCaracter() {
		return fontFile.charAt(index);
	}
		
	public final Token pegaToken(char caracter) {
		
		if(Character.isDigit(caracter)) {
			return trataDigito(caracter);
		}
		else if(Character.isLetter(caracter)) {
			return trataIdentificadorPalavraReservada(caracter);
		}
		else if(caracter == ':') {
			return trataAtribuicao(caracter);
		}
		else if(caracter == '+' || caracter == '-' || caracter == '*') {
			return trataOperadorAritmetico(caracter);
		}
		else if(caracter == '<' || caracter == '>' || caracter == '=' || caracter == '!') {
			return trataOperadorRelacional(caracter);
		}
		else if(caracter == ';' || caracter == ',' || caracter == '(' || caracter == ')' || caracter == '.') {
			return trataPontuacao(caracter);
		}
		else {
			setMessage("Há algum caracter inválido na linha "+ line + ".");
			error = true;
			index = fontFile.length();
			return new Token("erro", Character.toString(caracter), line); 
		}
	}

	public final Token trataDigito(char caracter) {
		String num;
		char newCaracter;
		
		num = Character.toString(caracter);
		
		if (index < fontFile.length()) {
			newCaracter = leCaracter();
			while (Character.isDigit(newCaracter) && (index+1) < fontFile.length() && !(isInvalidCharacter(newCaracter))) {
				index++;
				newCaracter = leCaracter();
				if(Character.isDigit(newCaracter)) {
					num = num + Character.toString(newCaracter);
				}
			}
			if(!(Character.isDigit(newCaracter))) {
				index--;
			}
			
		}
		
		return new Token("snúmero", num, line);

	}

	public final Token trataIdentificadorPalavraReservada(char caracter) {
		String id;
		char newCaracter;
		
		id = Character.toString(caracter);
		
		if (index < fontFile.length()) {
			newCaracter = leCaracter();
			while ((Character.isLetter(newCaracter) || newCaracter == '_') && (index+1) < fontFile.length() && !(isInvalidCharacter(newCaracter))) {
				index++;
				newCaracter = leCaracter();
				if((Character.isLetter(newCaracter) || newCaracter == '_')) {
					id = id + Character.toString(newCaracter);
				}
			}
			if(!(Character.isLetter(newCaracter) || newCaracter == '_')) {
				index--;
			}
			
		}
		
		return switchIdentifier(id);
		
	}
	
	public final Token trataOperadorAritmetico(char caracter) {		
		if (caracter == '+') {
			return new Token("Smais", Character.toString(caracter), line);
		} else if (caracter == '-') {
			return new Token("Smenos", Character.toString(caracter), line);
		} else { 
			return new Token("Smult", Character.toString(caracter), line);
		}
	}

	public final Token trataOperadorRelacional(char caracter) {
		
		String op = Character.toString(caracter);
		char newCaracter;
		
		if(caracter == '<') {
			
			index++;
			if (index < fontFile.length()) {
				newCaracter = leCaracter();
				if(isInvalidCharacter(newCaracter)) {
					index--;
				}
				if(newCaracter == '=') {
					op = op + newCaracter;
					return new Token("smenorig", op, line);
				}
			}
			
			return new Token("smenor", op, line);
		}
		else if(caracter == '>') {
			
			index++;
			if (index < fontFile.length()) {
				newCaracter = leCaracter();
				if(isInvalidCharacter(newCaracter)) {
					index--;
				}
				if(newCaracter == '=') {
					op = op + newCaracter;
					return new Token("smaiorig", op, line);
				}
			}
			return new Token("smaior", op, line);
		}
		else if(caracter == '=') {
			return new Token("sigual", op, line);
		}
		else if(caracter == '!') {
			index++;
			if (index < fontFile.length()) {
				newCaracter = leCaracter();
				if(newCaracter == '=') {
					op = op + newCaracter;
					return new Token("sdif", op, line);
				}
			}
			setMessage("Há algum caracter inválido na linha "+ line + ".");
			error = true;
			index = fontFile.length();
			return new Token("erro", op, line);
		}
		setMessage("Há algum caracter inválido na linha "+ line + ".");
		error = true;
		index = fontFile.length();
		return new Token("erro", op, line);
	}
	
	public final Token trataAtribuicao(char caracter) {

		String palavra = Character.toString(caracter);

		if ((index + 1) < fontFile.length()) {
			index = index + 1;
			char caracter2 = leCaracter();

			if (caracter2 == '=') {
				return new Token("satribuição", palavra + Character.toString(caracter2), line);
			} else {
				index--;
				return new Token("Sdoispontos", palavra, line);
			}
		} else {
			return new Token("Sdoispontos", palavra, line);
		}
		
	}
	
	public final Token trataPontuacao(char caracter) {
		if (caracter == ';') {
			return new Token("sponto_vírgula", Character.toString(caracter), line);
		} else if (caracter == ',') {
			return new Token("svírgula", Character.toString(caracter), line);
		} else if (caracter == '(') {
			return new Token("sabre_parênteses", Character.toString(caracter), line);
		} else if (caracter == ')') {
			return new Token("sfecha_parênteses", Character.toString(caracter), line);
		} else { // considera ser o ponto (.)
			return new Token("sponto", Character.toString(caracter), line);
		}
	}
	
	public final Token switchIdentifier(String id)
	{
		switch(id) {
		case "programa":
			return new Token("s" + id, id, line);
		case "se":
			return new Token("s" + id, id, line);
		case "entao":
			return new Token("s" + id, id, line);
		case "senao":
			return new Token("s" + id, id, line);
		case "enquanto":
			return new Token("s" + id, id, line);
		case "faca":
			return new Token("s" + id, id, line);
		case "início":
			return new Token("s" + id, id, line);
		case "fim":
			return new Token("s" + id, id, line);
		case "escreva":
			return new Token("s" + id, id, line);
		case "leia":
			return new Token("s" + id, id, line);
		case "var":
			return new Token("s" + id, id, line);
		case "inteiro":
			return new Token("s" + id, id, line);
		case "booleano":
			return new Token("s" + id, id, line);
		case "verdadeiro":
			return new Token("s" + id, id, line);
		case "falso":
			return new Token("s" + id, id, line);
		case "procedimento":
			return new Token("s" + id, id, line);
		case "funcao":
			return new Token("s" + id, id, line);
		case "div":
			return new Token("s" + id, id, line);
		case "e":
			return new Token("s" + id, id, line);
		case "ou":
			return new Token("s" + id, id, line);
		case "nao":
			return new Token("s" + id, id, line);
		default:
			return new Token("sidentificador", id, line);
		}
	}
	
	public final boolean isInvalidCharacter(char character) {
		return (character == '\n' || Character.isWhitespace(character) || Character.isSpaceChar(character));
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public final String getMessage() {
		return this.message;
	}
}

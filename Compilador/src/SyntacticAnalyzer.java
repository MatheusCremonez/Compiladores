import Exceptions.MissingTokenException;
import Exceptions.SyntacticException;

import Constants.Constants;

public class SyntacticAnalyzer {

	private String message;
	private LexicalAnalyzer la;
	private Token token;

	public SyntacticAnalyzer(String file) {
		la = new LexicalAnalyzer(file);
		syntactic();
	}

	public void syntactic() {

		try {
			analisadorSintatico();
		} catch (SyntacticException e) {
			setMessage(e.getMessage());
		} catch (MissingTokenException e) {
			setMessage(e.getMessage());
		} catch (NullPointerException e) {
			setMessage("Sintaticamente, o código está incorreto."); //ANALISAR ERROS COMO ESSE 
			//(talvez criar um método que analisa qual foi o último token lido antes da exceção)
		}
		if (la.error) {
			setMessage(la.getMessage());
		}
	}
	
	private void analisadorSintatico() throws SyntacticException, MissingTokenException {
		token = la.lexical();
		if (token.getSymbol().equals(Constants.PROGRAMA_SIMBOLO)) {
			token = la.lexical();
			if (token.getSymbol().equals(Constants.IDENTIFICADOR_SIMBOLO)) {
				token = la.lexical();
				if (token.getSymbol().equals(Constants.PONTO_VIRGULA_SIMBOLO)) {
					//analisa_bloco()
					token = la.lexical(); //ALTERNATIVO
					if (token.getSymbol().equals(Constants.PONTO_SIMBOLO)) {
						token = la.lexical();
						if(token == null) { // token = null simboliza o fim do arquivo
							setMessage("Compilação sintática realizada com sucesso.");
						} else {
							throw new SyntacticException("Trecho de código inesperado na linha: " + token.getLine());
						}
					} else {
						throw new MissingTokenException(Constants.PONTO_LEXEMA, Constants.PONTO_SIMBOLO, 
								token.getLexema(), token.getSymbol(), token.getLine());
					}
				} else {
					throw new MissingTokenException(Constants.PONTO_VIRGULA_LEXEMA, Constants.PONTO_VIRGULA_SIMBOLO, 
							token.getLexema(), token.getSymbol(), token.getLine());
				}
			} else {
				throw new MissingTokenException(Constants.IDENTIFICADOR_LEXEMA, Constants.IDENTIFICADOR_SIMBOLO, 
						token.getLexema(), token.getSymbol(), token.getLine());
			}
		} else {
			throw new MissingTokenException(Constants.PROGRAMA_LEXEMA, Constants.PROGRAMA_SIMBOLO, 
					token.getLexema(), token.getSymbol(), token.getLine());
		}
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public final String getMessage() {
		return this.message;
	}
}

import java.util.ArrayList;
import java.util.List;

import Exceptions.LexicalException;
import Exceptions.MissingTokenException;
import Exceptions.SyntacticException;

import Constants.Constants;

public class SyntacticAnalyzer {

	private String message;
	private LexicalAnalyzer la;
	private Token token;
	private Constants constant;

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
		if (token.getSymbol().equals(constant.PROGRAMA_SIMBOLO)) {
			token = la.lexical();
			if (token.getSymbol().equals(constant.IDENTIFICADOR_SIMBOLO)) {
				token = la.lexical();
				if (token.getSymbol().equals(constant.PONTO_VIRGULA_SIMBOLO)) {
					//analisa_bloco()
					token = la.lexical(); //ALTERNATIVO
					if (token.getSymbol().equals(constant.PONTO_SIMBOLO)) {
						token = la.lexical();
						if(token == null) { // token = null simboliza o fim do arquivo
							setMessage("Compilação sintática realizada com sucesso.");
						} else {
							throw new SyntacticException("Trecho de código inesperado na linha: " + token.getLine());
						}
					} else {
						throw new MissingTokenException(constant.PONTO_LEXEMA, constant.PONTO_SIMBOLO, 
								token.getLexema(), token.getSymbol(), token.getLine());
					}
				} else {
					throw new MissingTokenException(constant.PONTO_VIRGULA_LEXEMA, constant.PONTO_VIRGULA_SIMBOLO, 
							token.getLexema(), token.getSymbol(), token.getLine());
				}
			} else {
				throw new MissingTokenException(constant.IDENTIFICADOR_LEXEMA, constant.IDENTIFICADOR_SIMBOLO, 
						token.getLexema(), token.getSymbol(), token.getLine());
			}
		} else {
			throw new MissingTokenException(constant.PROGRAMA_LEXEMA, constant.PROGRAMA_SIMBOLO, 
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

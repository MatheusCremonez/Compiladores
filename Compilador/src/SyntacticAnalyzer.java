import Constants.*;
import Exceptions.SyntacticException;

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
		} catch (NullPointerException e) {
			setMessage("Sintaticamente, o código está incorreto."); // ANALISAR ERROS COMO ESSE
			// (talvez criar um método que analisa qual foi o último token lido antes da
			// exceção)
		}
		if (la.error) {
			setMessage(la.getMessage());
		}
	}

	private void analisadorSintatico() throws SyntacticException {
		token = la.lexical();
		if (token.getSymbol().equals(Constants.PROGRAMA_SIMBOLO)) {
			token = la.lexical();
			if (token.getSymbol().equals(Constants.IDENTIFICADOR_SIMBOLO)) {
				token = la.lexical();
				if (token.getSymbol().equals(Constants.PONTO_VIRGULA_SIMBOLO)) {
					analisa_bloco();
					if (token.getSymbol().equals(Constants.PONTO_SIMBOLO)) {
						token = la.lexical();
						if (token == null) { // token = null simboliza o fim do arquivo
							setMessage("Compilação sintática realizada com sucesso.");
						} else {
							throw new SyntacticException("Trecho de código inesperado na linha: " + token.getLine());
						}
					} else {
						throw new SyntacticException(Constants.PONTO_LEXEMA, Constants.PONTO_SIMBOLO, token.getLexema(),
								token.getSymbol(), token.getLine());
					}
				} else {
					throw new SyntacticException(Constants.PONTO_VIRGULA_LEXEMA, Constants.PONTO_VIRGULA_SIMBOLO,
							token.getLexema(), token.getSymbol(), token.getLine());
				}
			} else {
				throw new SyntacticException(Constants.IDENTIFICADOR_LEXEMA, Constants.IDENTIFICADOR_SIMBOLO,
						token.getLexema(), token.getSymbol(), token.getLine());
			}
		} else {
			throw new SyntacticException(Constants.PROGRAMA_LEXEMA, Constants.PROGRAMA_SIMBOLO, token.getLexema(),
					token.getSymbol(), token.getLine());
		}
	}

	public void analisa_bloco() throws SyntacticException {
		token = la.lexical();

		analisa_et_variaveis();
		analisa_subrotinas();
		analisa_comandos();

	}

	public void analisa_et_variaveis() throws SyntacticException {

		if (token.getSymbol().equals(Constants.VAR_SIMBOLO)) {
			token = la.lexical();
			if (token.getSymbol().equals(Constants.IDENTIFICADOR_SIMBOLO)) {
				while (token.getSymbol().equals(Constants.IDENTIFICADOR_SIMBOLO)) {
					analisa_variaveis();
					if (token.getSymbol().equals(Constants.PONTO_VIRGULA_SIMBOLO)) {
						token = la.lexical();
					} else {
						throw new SyntacticException(Constants.PONTO_VIRGULA_LEXEMA, Constants.PONTO_VIRGULA_SIMBOLO,
								token.getLexema(), token.getSymbol(), token.getLine());
					}
				}
			} else {
				throw new SyntacticException(Constants.IDENTIFICADOR_LEXEMA, Constants.IDENTIFICADOR_SIMBOLO,
						token.getLexema(), token.getSymbol(), token.getLine());
			}
		}

	}

	public void analisa_variaveis() throws SyntacticException {

		do {
			if (token.getSymbol().equals(Constants.IDENTIFICADOR_SIMBOLO)) {
				token = la.lexical();
				if (token.getSymbol().equals(Constants.VIRGULA_SIMBOLO)
						|| token.getSymbol().equals(Constants.DOIS_PONTOS_SIMBOLO)) {
					if (token.getSymbol().equals(Constants.VIRGULA_SIMBOLO)) {
						token = la.lexical();
						if (token.getSymbol().equals(Constants.DOIS_PONTOS_SIMBOLO)) {
							throw new SyntacticException(Constants.IDENTIFICADOR_LEXEMA,
									Constants.IDENTIFICADOR_SIMBOLO, token.getLexema(), token.getSymbol(),
									token.getLine());
						}
					}
				} else {
					throw new SyntacticException(Constants.PONTO_VIRGULA_LEXEMA, Constants.PONTO_VIRGULA_SIMBOLO,
							Constants.DOIS_PONTOS_LEXEMA, Constants.DOIS_PONTOS_SIMBOLO, token.getLexema(),
							token.getSymbol(), token.getLine());
				}
			} else {
				throw new SyntacticException(Constants.IDENTIFICADOR_LEXEMA, Constants.IDENTIFICADOR_SIMBOLO,
						token.getLexema(), token.getSymbol(), token.getLine());
			}

		} while (!(token.getSymbol().equals(Constants.DOIS_PONTOS_SIMBOLO)));
		token = la.lexical();

		analisa_tipo();
	}

	public void analisa_tipo() throws SyntacticException {
		if (!(token.getSymbol().equals(Constants.INTEIRO_SIMBOLO))
				&& !(token.getSymbol().equals(Constants.BOOLEANO_SIMBOLO))) {
			throw new SyntacticException(Constants.INTEIRO_LEXEMA, Constants.INTEIRO_SIMBOLO, Constants.BOOLEANO_LEXEMA,
					Constants.BOOLEANO_SIMBOLO, token.getLexema(), token.getSymbol(), token.getLine());
		}
		token = la.lexical();
	}

	public void analisa_subrotinas() {

	}

	public void analisa_comandos() {

	}

	public void setMessage(String message) {
		this.message = message;
	}

	public final String getMessage() {
		return this.message;
	}
}

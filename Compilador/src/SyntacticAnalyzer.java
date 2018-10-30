import Constants.*;
import Exceptions.SyntacticException;
import Symbols.Symbol;
import Symbols.TableOfSymbols;
import Symbols.Variable;

public class SyntacticAnalyzer {

	private String message;
	private int errorLine;
	private LexicalAnalyzer la;
	private SemanticAnalyzer semantic;
	private TableOfSymbols table;
	private Token token;

	public SyntacticAnalyzer(String file) {
		la = new LexicalAnalyzer(file);
		semantic = new SemanticAnalyzer();
		table = new TableOfSymbols();
		syntactic();
	}

	public void syntactic() {
		try {
			analisadorSintatico();
			table.debugTable();
		} catch (SyntacticException e) {
			errorLine = token.getLine();
			setMessage(e.getMessage());
		}
		if (la.error) {
			setMessage(la.getMessage());
			errorLine = la.getLine();
		}
	}

	private void analisadorSintatico() throws SyntacticException {
		token = la.lexical();
		if (token.getSymbol().equals(Constants.PROGRAMA_SIMBOLO)) {
			token = la.lexical();
			if (token.getSymbol().equals(Constants.IDENTIFICADOR_SIMBOLO)) {
				table.insert(new Symbol(token.getLexema()));
				token = la.lexical();
				if (token.getSymbol().equals(Constants.PONTO_VIRGULA_SIMBOLO)) {
					analisaBloco();
					if (token.getSymbol().equals(Constants.PONTO_SIMBOLO)) {
						token = la.lexical();
						if (token.getSymbol().equals(Constants.FIM_ARQUIVO)) { 
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

	public void analisaBloco() throws SyntacticException {
		token = la.lexical();

		analisaEtVariaveis();
		analisaSubrotinas();
		analisaComandos();
	}

	public void analisaEtVariaveis() throws SyntacticException {
		if (token.getSymbol().equals(Constants.VAR_SIMBOLO)) {
			token = la.lexical();
			if (token.getSymbol().equals(Constants.IDENTIFICADOR_SIMBOLO)) {
				while (token.getSymbol().equals(Constants.IDENTIFICADOR_SIMBOLO)) {
					analisaVariaveis();
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

	public void analisaVariaveis() throws SyntacticException {
		do {
			if (token.getSymbol().equals(Constants.IDENTIFICADOR_SIMBOLO)) {
				table.insert(new Variable(token.getLexema()));
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
		analisaTipo();
	}

	public void analisaTipo() throws SyntacticException {
		if (!(token.getSymbol().equals(Constants.INTEIRO_SIMBOLO))
				&& !(token.getSymbol().equals(Constants.BOOLEANO_SIMBOLO))) {
			throw new SyntacticException(Constants.INTEIRO_LEXEMA, Constants.INTEIRO_SIMBOLO, Constants.BOOLEANO_LEXEMA,
					Constants.BOOLEANO_SIMBOLO, token.getLexema(), token.getSymbol(), token.getLine());
		}
		token = la.lexical();
	}

	public void analisaSubrotinas() throws SyntacticException {
		if ((token.getSymbol().equals(Constants.PROCEDIMENTO_SIMBOLO))
				|| (token.getSymbol().equals(Constants.FUNCAO_SIMBOLO))) {
			// terá questões semanticas aqui no futuro
		}

		while ((token.getSymbol().equals(Constants.PROCEDIMENTO_SIMBOLO))
				|| (token.getSymbol().equals(Constants.FUNCAO_SIMBOLO))) {

			if (token.getSymbol().equals(Constants.PROCEDIMENTO_SIMBOLO)) {
				analisaDeclaracaoProcedimento();
			} else {
				analisaDeclaracaoFuncao();
			}

			if (token.getSymbol().equals(Constants.PONTO_VIRGULA_SIMBOLO)) {
				token = la.lexical();
			} else {
				throw new SyntacticException(Constants.PONTO_VIRGULA_LEXEMA, Constants.PONTO_VIRGULA_SIMBOLO,
						token.getLexema(), token.getSymbol(), token.getLine());
			}
		}
	}

	public void analisaComandos() throws SyntacticException {
		if (token.getSymbol().equals(Constants.INICIO_SIMBOLO)) {
			token = la.lexical();
			analisaComandoSimples();

			while (!(token.getSymbol().equals(Constants.FIM_SIMBOLO))) {
				if (token.getSymbol().equals(Constants.PONTO_VIRGULA_SIMBOLO)) {

					token = la.lexical();
					if (!(token.getSymbol().equals(Constants.FIM_SIMBOLO))) {
						analisaComandoSimples();
					}
				} else {
					throw new SyntacticException(Constants.PONTO_VIRGULA_LEXEMA, Constants.PONTO_VIRGULA_SIMBOLO,
							token.getLexema(), token.getSymbol(), token.getLine());
				}
			}
			token = la.lexical();
		} else {
			throw new SyntacticException(Constants.INICIO_LEXEMA, Constants.INICIO_SIMBOLO, token.getLexema(),
					token.getSymbol(), token.getLine());
		}
	}

	public void analisaComandoSimples() throws SyntacticException {
		if (token.getSymbol().equals(Constants.IDENTIFICADOR_SIMBOLO)) {
			analisaAtribChprocedimento();
		} else if (token.getSymbol().equals(Constants.SE_SIMBOLO)) {
			analisaSe();
		} else if (token.getSymbol().equals(Constants.ENQUANTO_SIMBOLO)) {
			analisaEnquanto();
		} else if (token.getSymbol().equals(Constants.LEIA_SIMBOLO)) {
			analisaLeia();
		} else if (token.getSymbol().equals(Constants.ESCREVA_SIMBOLO)) {
			analisaEscreva();
		} else {
			analisaComandos();
		}
				
	}

	public void analisaAtribChprocedimento() throws SyntacticException {
		token = la.lexical();
		if (token.getSymbol().equals(Constants.ATRIBUICAO_SIMBOLO)) {
			analisaAtribuição();
		} else {
			chamadaProcedimento();
		}
	}

	public void analisaAtribuição() throws SyntacticException {
		token = la.lexical();
		analisaExpressao();
	}

	public void chamadaProcedimento() {
		// terá questões semanticas aqui no futuro
	}

	public void chamadaFuncao() {
		// terá questões semanticas aqui no futuro
	}

	public void analisaLeia() throws SyntacticException {
		token = la.lexical();
		if (token.getSymbol().equals(Constants.ABRE_PARENTESES_SIMBOLO)) {
			token = la.lexical();
			if (token.getSymbol().equals(Constants.IDENTIFICADOR_SIMBOLO)) {
				token = la.lexical();
				if (token.getSymbol().equals(Constants.FECHA_PARENTESES_SIMBOLO)) {
					token = la.lexical();
				} else {
					throw new SyntacticException(Constants.FECHA_PARENTESES_LEXEMA, Constants.FECHA_PARENTESES_SIMBOLO,
							token.getLexema(), token.getSymbol(), token.getLine());
				}
			} else {
				throw new SyntacticException(Constants.IDENTIFICADOR_LEXEMA, Constants.IDENTIFICADOR_SIMBOLO,
						token.getLexema(), token.getSymbol(), token.getLine());
			}
		} else {
			throw new SyntacticException(Constants.ABRE_PARENTESES_LEXEMA, Constants.ABRE_PARENTESES_SIMBOLO,
					token.getLexema(), token.getSymbol(), token.getLine());
		}
	}

	public void analisaEscreva() throws SyntacticException {
		token = la.lexical();
		if (token.getSymbol().equals(Constants.ABRE_PARENTESES_SIMBOLO)) {
			token = la.lexical();
			if (token.getSymbol().equals(Constants.IDENTIFICADOR_SIMBOLO)) {
				token = la.lexical();
				if (token.getSymbol().equals(Constants.FECHA_PARENTESES_SIMBOLO)) {
					token = la.lexical();
				} else {
					throw new SyntacticException(Constants.FECHA_PARENTESES_LEXEMA, Constants.FECHA_PARENTESES_SIMBOLO,
							token.getLexema(), token.getSymbol(), token.getLine());
				}
			} else {
				throw new SyntacticException(Constants.IDENTIFICADOR_LEXEMA, Constants.IDENTIFICADOR_SIMBOLO,
						token.getLexema(), token.getSymbol(), token.getLine());
			}
		} else {
			throw new SyntacticException(Constants.ABRE_PARENTESES_LEXEMA, Constants.ABRE_PARENTESES_SIMBOLO,
					token.getLexema(), token.getSymbol(), token.getLine());
		}
	}

	public void analisaEnquanto() throws SyntacticException {
		token = la.lexical();
		analisaExpressao();
		if (token.getSymbol().equals(Constants.FACA_SIMBOLO)) {
			token = la.lexical();
			analisaComandoSimples();
		} else {
			throw new SyntacticException(Constants.FACA_LEXEMA, Constants.FACA_SIMBOLO, token.getLexema(),
					token.getSymbol(), token.getLine());
		}
	}

	public void analisaSe() throws SyntacticException {
		token = la.lexical();
		analisaExpressao();
		if (token.getSymbol().equals(Constants.ENTAO_SIMBOLO)) {
			token = la.lexical();
			analisaComandoSimples();
			if (token.getSymbol().equals(Constants.SENAO_SIMBOLO)) {
				token = la.lexical();
				analisaComandoSimples();
			}
			
		} else {
			throw new SyntacticException(Constants.ENTAO_LEXEMA, Constants.ENTAO_SIMBOLO, token.getLexema(),
					token.getSymbol(), token.getLine());
		}
	}
	
	public void analisaDeclaracaoProcedimento() throws SyntacticException {
		token = la.lexical();
		if (token.getSymbol().equals(Constants.IDENTIFICADOR_SIMBOLO)) {
			token = la.lexical();
			if (token.getSymbol().equals(Constants.PONTO_VIRGULA_SIMBOLO)) {
				analisaBloco();
			}
			else {
				throw new SyntacticException(Constants.PONTO_VIRGULA_LEXEMA, Constants.PONTO_VIRGULA_SIMBOLO, token.getLexema(),
						token.getSymbol(), token.getLine());
			}
		}
		else {
			throw new SyntacticException(Constants.IDENTIFICADOR_LEXEMA, Constants.IDENTIFICADOR_SIMBOLO, token.getLexema(),
					token.getSymbol(), token.getLine());
		}
	}
	
	public void analisaDeclaracaoFuncao() throws SyntacticException {
		token = la.lexical();
		if (token.getSymbol().equals(Constants.IDENTIFICADOR_SIMBOLO)) {
			token = la.lexical();
			if (token.getSymbol().equals(Constants.DOIS_PONTOS_SIMBOLO)) {
				token = la.lexical();
				if(token.getSymbol().equals(Constants.INTEIRO_SIMBOLO) || token.getSymbol().equals(Constants.BOOLEANO_SIMBOLO)) {
					token = la.lexical();
					if (token.getSymbol().equals(Constants.PONTO_VIRGULA_SIMBOLO)) {
						analisaBloco();
					}
				}
				else {
					throw new SyntacticException(Constants.INTEIRO_LEXEMA, Constants.INTEIRO_SIMBOLO,
							Constants.BOOLEANO_LEXEMA, Constants.BOOLEANO_SIMBOLO, token.getLexema(), token.getSymbol(),
							token.getLine());
				}
			}
			else {
				throw new SyntacticException(Constants.DOIS_PONTOS_LEXEMA, Constants.DOIS_PONTOS_SIMBOLO, token.getLexema(),
						token.getSymbol(), token.getLine());
			}
			
		}
		else {
			throw new SyntacticException(Constants.IDENTIFICADOR_LEXEMA, Constants.IDENTIFICADOR_SIMBOLO, token.getLexema(),
					token.getSymbol(), token.getLine());
		}
	}

	public void analisaExpressao() throws SyntacticException {
		analisaExpressaoSimples();
		if (token.getSymbol().equals(Constants.MAIOR_SIMBOLO) || token.getSymbol().equals(Constants.MAIOR_IGUAL_SIMBOLO)
				|| token.getSymbol().equals(Constants.IGUAL_SIMBOLO)
				|| token.getSymbol().equals(Constants.MENOR_SIMBOLO)
				|| token.getSymbol().equals(Constants.MENOR_IGUAL_SIMBOLO)
				|| token.getSymbol().equals(Constants.DIFERENTE_SIMBOLO)) {
			token = la.lexical();
			analisaExpressaoSimples();
		}
	}

	public void analisaExpressaoSimples() throws SyntacticException {
		if (token.getSymbol().equals(Constants.MAIS_SIMBOLO) || token.getSymbol().equals(Constants.MENOS_SIMBOLO)) {
			token = la.lexical();
		}
		analisaTermo();
		while (token.getSymbol().equals(Constants.MAIS_SIMBOLO) || token.getSymbol().equals(Constants.MENOS_SIMBOLO)
				|| token.getSymbol().equals(Constants.OU_SIMBOLO)) {
			token = la.lexical();
			analisaTermo();
		}

	}

	public void analisaTermo() throws SyntacticException {
		analisaFator();
		while (token.getSymbol().equals(Constants.MULT_SIMBOLO) || token.getSymbol().equals(Constants.DIV_SIMBOLO)
				|| token.getSymbol().equals(Constants.E_SIMBOLO)) {
			token = la.lexical();
			analisaFator();
		}
	}

	public void analisaFator() throws SyntacticException {
		if (token.getSymbol().equals(Constants.IDENTIFICADOR_SIMBOLO)) {
			// futuro semantico
			token = la.lexical(); //provisorio
		} else if (token.getSymbol().equals(Constants.NUMERO_SIMBOLO)) {
			token = la.lexical();
		} else if (token.getSymbol().equals(Constants.NAO_SIMBOLO)) {
			token = la.lexical();
			analisaFator();
		} else if (token.getSymbol().equals(Constants.ABRE_PARENTESES_SIMBOLO)) {
			token = la.lexical();
			analisaExpressao();
			if (token.getSymbol().equals(Constants.FECHA_PARENTESES_SIMBOLO)) {
				token = la.lexical();
			} else {
				throw new SyntacticException(Constants.FECHA_PARENTESES_LEXEMA, Constants.FECHA_PARENTESES_SIMBOLO,
						token.getLexema(), token.getSymbol(), token.getLine());
			}
		} else if (token.getSymbol().equals(Constants.VERDADEIRO_SIMBOLO)
				|| token.getSymbol().equals(Constants.FALSO_SIMBOLO)) {
			token = la.lexical();
		} else {
			throw new SyntacticException("Expressão incompleta na linha: " + token.getLine());
		}
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public final String getMessage() {
		return this.message;
	}
	
	public final int getErrorLine() {
		return this.errorLine;
	}
}

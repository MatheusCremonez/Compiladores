import java.util.ArrayList;
import java.util.List;

import Constants.*;
import Exceptions.SemanticException;
import Exceptions.SyntacticException;
import Symbols.Function;
import Symbols.Procedure;
import Symbols.Symbol;
import Symbols.TableOfSymbols;
import Symbols.Variable;

public class SyntacticAnalyzer {

	private String message;
	private int errorLine;
	private LexicalAnalyzer la;
	private SemanticAnalyzer semantic;
	//private CodeGenerator generator;
	private TableOfSymbols table;
	private Token token;
	private List<Token> expression = new ArrayList<Token>();

	public SyntacticAnalyzer(String file) {
		la = new LexicalAnalyzer(file);
		semantic = new SemanticAnalyzer();
		//generator = new CodeGenerator();
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
		} catch (SemanticException e) {
			errorLine = token.getLine();
			setMessage(e.getMessage());
		}
		
		if (la.error) {
			setMessage(la.getMessage());
			errorLine = la.getLine();
		}
	}

	private void analisadorSintatico() throws SyntacticException, SemanticException {
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

	private void analisaBloco() throws SyntacticException, SemanticException {
		token = la.lexical();

		analisaEtVariaveis();
		analisaSubrotinas();
		analisaComandos();
	}

	private void analisaEtVariaveis() throws SyntacticException, SemanticException {
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

	private void analisaVariaveis() throws SyntacticException, SemanticException {
		do {
			if (token.getSymbol().equals(Constants.IDENTIFICADOR_SIMBOLO)) {
				if ( ! table.search(token.getLexema())) {
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
					throw new SemanticException("Já existe uma variável com o mesmo nome da variável da linha: " + token.getLine());
				}
				
			} else {
				throw new SyntacticException(Constants.IDENTIFICADOR_LEXEMA, Constants.IDENTIFICADOR_SIMBOLO,
						token.getLexema(), token.getSymbol(), token.getLine());
			}

		} while (!(token.getSymbol().equals(Constants.DOIS_PONTOS_SIMBOLO)));

		token = la.lexical();
		analisaTipo();
	}

	private void analisaTipo() throws SyntacticException {
		if (!(token.getSymbol().equals(Constants.INTEIRO_SIMBOLO))
				&& !(token.getSymbol().equals(Constants.BOOLEANO_SIMBOLO))) {
			throw new SyntacticException(Constants.INTEIRO_LEXEMA, Constants.INTEIRO_SIMBOLO, Constants.BOOLEANO_LEXEMA,
					Constants.BOOLEANO_SIMBOLO, token.getLexema(), token.getSymbol(), token.getLine());
		}
		else {
			table.insertTypeOnVariable(token.getLexema());
		}
		token = la.lexical();
	}

	private void analisaSubrotinas() throws SyntacticException, SemanticException {
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

	private void analisaComandos() throws SyntacticException, SemanticException {
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

	private void analisaComandoSimples() throws SyntacticException, SemanticException {
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

	private void analisaAtribChprocedimento() throws SyntacticException, SemanticException {
		Token aux = token;
		token = la.lexical();
		
		if (token.getSymbol().equals(Constants.ATRIBUICAO_SIMBOLO)) {
			analisaAtribuicao(aux);
		} else {
			chamadaProcedimento(aux);
		}
	}

	private void analisaAtribuicao(Token attributionToken) throws SyntacticException, SemanticException {
		token = la.lexical();
		analisaExpressao();
		
		semantic.setTableOfSymbols(table);
		String aux = semantic.expressionToPostfix(expression);
		String type = semantic.returnTypeOfExpression(aux);
		semantic.whoCallsMe(type, attributionToken.getLexema());
		System.out.println("Tipo da Expressão:" + type);
		expression.clear();
	}

	private void chamadaProcedimento(Token auxToken) throws SemanticException {
		if (table.searchProcedure(auxToken.getLexema())) {
			// OK é um procedimento
		} else {
			throw new SemanticException("Procedimento '" + auxToken.getLexema() + "' não está declarado.\nLinha: " + auxToken.getLine());
		}
	}

	private void chamadaFuncao(int index) throws SemanticException{
		String symbolLexema = table.getSymbol(index).getLexema();
		if (table.searchFunction(symbolLexema)) {
			// OK é uma função
			token = la.lexical();
		} else {
			throw new SemanticException("Função '" + symbolLexema + "' não está declarada.\nLinha: " + token.getLine());
		}
	}

	private void analisaLeia() throws SyntacticException, SemanticException {
		token = la.lexical();
		if (token.getSymbol().equals(Constants.ABRE_PARENTESES_SIMBOLO)) {
			token = la.lexical();
			if (token.getSymbol().equals(Constants.IDENTIFICADOR_SIMBOLO)) {
				if (table.searchVariable(token.getLexema())) {
					token = la.lexical();
					if (token.getSymbol().equals(Constants.FECHA_PARENTESES_SIMBOLO)) {
						token = la.lexical();
					} else {
						throw new SyntacticException(Constants.FECHA_PARENTESES_LEXEMA, Constants.FECHA_PARENTESES_SIMBOLO,
								token.getLexema(), token.getSymbol(), token.getLine());
					}
				} else {
					throw new SemanticException("A variável atribuída ao método 'leia' não está definida.\nLinha: " + token.getLine());
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

	private void analisaEscreva() throws SyntacticException, SemanticException {
		token = la.lexical();
		if (token.getSymbol().equals(Constants.ABRE_PARENTESES_SIMBOLO)) {
			token = la.lexical();
			if (token.getSymbol().equals(Constants.IDENTIFICADOR_SIMBOLO)) {
				if (table.searchVariable(token.getLexema()) || table.searchFunction(token.getLexema())) {
					token = la.lexical();
					if (token.getSymbol().equals(Constants.FECHA_PARENTESES_SIMBOLO)) {
						token = la.lexical();
					} else {
						throw new SyntacticException(Constants.FECHA_PARENTESES_LEXEMA, Constants.FECHA_PARENTESES_SIMBOLO,
								token.getLexema(), token.getSymbol(), token.getLine());
					}
				} else {
					throw new SemanticException("A variável ou função atribuída ao método 'escreva' não está definida.\nLinha: " + token.getLine());
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

	private void analisaEnquanto() throws SyntacticException, SemanticException {
		token = la.lexical();
		analisaExpressao();
		
		semantic.setTableOfSymbols(table);
		String aux = semantic.expressionToPostfix(expression);
		String type = semantic.returnTypeOfExpression(aux);
		semantic.whoCallsMe(type, Constants.ENQUANTO_LEXEMA);
		System.out.println("(ENQUANTO)Tipo da Expressão:" + type);
		expression.clear();
		
		if (token.getSymbol().equals(Constants.FACA_SIMBOLO)) {
			token = la.lexical();
			analisaComandoSimples();
		} else {
			throw new SyntacticException(Constants.FACA_LEXEMA, Constants.FACA_SIMBOLO, token.getLexema(),
					token.getSymbol(), token.getLine());
		}
	}

	private void analisaSe() throws SyntacticException, SemanticException {
		token = la.lexical();
		analisaExpressao();
		
		semantic.setTableOfSymbols(table);
		String aux = semantic.expressionToPostfix(expression);
		String type = semantic.returnTypeOfExpression(aux);
		semantic.whoCallsMe(type, Constants.SE_LEXEMA);
		System.out.println("(SE)Tipo da Expressão:" + type);
		expression.clear();
		
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
	
	private void analisaDeclaracaoProcedimento() throws SyntacticException, SemanticException {
		token = la.lexical();
		if (token.getSymbol().equals(Constants.IDENTIFICADOR_SIMBOLO)) {
			if (! table.searchProcedure(token.getLexema())) {
				table.insert(new Procedure(token.getLexema()));
				token = la.lexical();
				if (token.getSymbol().equals(Constants.PONTO_VIRGULA_SIMBOLO)) {
					analisaBloco();
				} else {
					throw new SyntacticException(Constants.PONTO_VIRGULA_LEXEMA, Constants.PONTO_VIRGULA_SIMBOLO, token.getLexema(),
							token.getSymbol(), token.getLine());
				}
			} else {
				throw new SemanticException("Já existe um procedimento com o mesmo nome do procedimento da linha: " + token.getLine());
			}
		}
		else {
			throw new SyntacticException(Constants.IDENTIFICADOR_LEXEMA, Constants.IDENTIFICADOR_SIMBOLO, token.getLexema(),
					token.getSymbol(), token.getLine());
		}
		table.cleanLevel();
	}
	
	private void analisaDeclaracaoFuncao() throws SyntacticException, SemanticException {
		token = la.lexical();
		if (token.getSymbol().equals(Constants.IDENTIFICADOR_SIMBOLO)) {
			if(! table.searchFunction(token.getLexema())) {
				table.insert(new Function(token.getLexema()));
				token = la.lexical();
				
				if (token.getSymbol().equals(Constants.DOIS_PONTOS_SIMBOLO)) {
					token = la.lexical();
		
					if(token.getSymbol().equals(Constants.INTEIRO_SIMBOLO) || token.getSymbol().equals(Constants.BOOLEANO_SIMBOLO)) {
						if (token.getSymbol().equals(Constants.INTEIRO_SIMBOLO)) {
							table.insertTypeOnFunction(Constants.INTEIRO_LEXEMA);
						} else {
							table.insertTypeOnFunction(Constants.BOOLEANO_LEXEMA);
						}
						token = la.lexical();
						
						if (token.getSymbol().equals(Constants.PONTO_VIRGULA_SIMBOLO)) {
							analisaBloco();
						}
					} else {
						throw new SyntacticException(Constants.INTEIRO_LEXEMA, Constants.INTEIRO_SIMBOLO,
								Constants.BOOLEANO_LEXEMA, Constants.BOOLEANO_SIMBOLO, token.getLexema(), token.getSymbol(),
								token.getLine());
					}
				} else {
					throw new SyntacticException(Constants.DOIS_PONTOS_LEXEMA, Constants.DOIS_PONTOS_SIMBOLO, token.getLexema(),
							token.getSymbol(), token.getLine());
				}
			} else {
				throw new SemanticException("Já existe uma função com o mesmo nome da função da linha: " + token.getLine());
			}
			
		} else {
			throw new SyntacticException(Constants.IDENTIFICADOR_LEXEMA, Constants.IDENTIFICADOR_SIMBOLO, token.getLexema(),
					token.getSymbol(), token.getLine());
		}
		table.cleanLevel();
	}

	private void analisaExpressao() throws SyntacticException, SemanticException {
		analisaExpressaoSimples();
		if (token.getSymbol().equals(Constants.MAIOR_SIMBOLO) || token.getSymbol().equals(Constants.MAIOR_IGUAL_SIMBOLO)
				|| token.getSymbol().equals(Constants.IGUAL_SIMBOLO)
				|| token.getSymbol().equals(Constants.MENOR_SIMBOLO)
				|| token.getSymbol().equals(Constants.MENOR_IGUAL_SIMBOLO)
				|| token.getSymbol().equals(Constants.DIFERENTE_SIMBOLO)) {
			expression.add(token);
			token = la.lexical();
			analisaExpressaoSimples();
		}
	}

	private void analisaExpressaoSimples() throws SyntacticException, SemanticException {
		if (token.getSymbol().equals(Constants.MAIS_SIMBOLO) || token.getSymbol().equals(Constants.MENOS_SIMBOLO)) {
			Token aux = new Token(token.getSymbol(), token.getLexema() + "u", token.getLine()); //passando o operador unário para o semantico
			expression.add(aux);
			token = la.lexical();
		}
		analisaTermo();
		while (token.getSymbol().equals(Constants.MAIS_SIMBOLO) || token.getSymbol().equals(Constants.MENOS_SIMBOLO)
				|| token.getSymbol().equals(Constants.OU_SIMBOLO)) {
			expression.add(token);
			token = la.lexical();
			analisaTermo();
		}
	}

	private void analisaTermo() throws SyntacticException, SemanticException {
		analisaFator();
		while (token.getSymbol().equals(Constants.MULT_SIMBOLO) || token.getSymbol().equals(Constants.DIV_SIMBOLO)
				|| token.getSymbol().equals(Constants.E_SIMBOLO)) {
			expression.add(token);
			token = la.lexical();
			analisaFator();
		}
	}

	private void analisaFator() throws SyntacticException, SemanticException {
		if (token.getSymbol().equals(Constants.IDENTIFICADOR_SIMBOLO)) {
			int index = table.searchSymbol(token.getLexema());
			if (index != (-1)) {
				if (table.getSymbol(index) instanceof Function && (table.getSymbol(index).getType() == Constants.INTEIRO_LEXEMA || table.getSymbol(index).getType() == Constants.BOOLEANO_LEXEMA)) {
					chamadaFuncao(index);
				}
				else {
					expression.add(token);
					token = la.lexical();
				}
			} else {
				throw new SemanticException("Variável ou Função '" + token.getLexema() + "' não está definida no escopo atual. \n Linha: " + token.getLine());
			}
		} else if (token.getSymbol().equals(Constants.NUMERO_SIMBOLO)) {
			expression.add(token);
			token = la.lexical();
		} else if (token.getSymbol().equals(Constants.NAO_SIMBOLO)) {
			expression.add(token);
			token = la.lexical();
			analisaFator();
		} else if (token.getSymbol().equals(Constants.ABRE_PARENTESES_SIMBOLO)) {
			expression.add(token);
			token = la.lexical();
			analisaExpressao();
			if (token.getSymbol().equals(Constants.FECHA_PARENTESES_SIMBOLO)) {
				expression.add(token);
				token = la.lexical();
			} else {
				throw new SyntacticException(Constants.FECHA_PARENTESES_LEXEMA, Constants.FECHA_PARENTESES_SIMBOLO,
						token.getLexema(), token.getSymbol(), token.getLine());
			}
		} else if (token.getSymbol().equals(Constants.VERDADEIRO_SIMBOLO)
				|| token.getSymbol().equals(Constants.FALSO_SIMBOLO)) {
			expression.add(token);
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

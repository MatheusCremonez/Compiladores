import java.util.ArrayList;
import java.util.List;

import Constants.*;
import Exceptions.SemanticException;
import Exceptions.SyntacticException;

public class SyntacticAnalyzer {

	private String message;
	private int errorLine;

	private LexicalAnalyzer la;
	private SemanticAnalyzer semantic;
	private CodeGenerator generator;
	private Token token;
	private List<Token> expression = new ArrayList<Token>();

	// São as nominações para onde será realizado o pulo (Ex: L"0", L"1", etc)
	private int label = 0;

	// São as posições na memória que está alocado a variável
	private int position = 0;
	
	private int countVariable = 0;

	private List<Integer> variableOfAlloc = new ArrayList<Integer>();
	
	private List<Boolean> flagProcedureList = new ArrayList<Boolean>();
	private List<Boolean> flagFunctionList = new ArrayList<Boolean>();
	private List<String> nameOfFunction = new ArrayList<String>();
	private int auxLabel = 0;

	public SyntacticAnalyzer(String file) {
		flagFunctionList.add(false);
		flagProcedureList.add(false);
		
		la = new LexicalAnalyzer(file);
		semantic = new SemanticAnalyzer();
		generator = new CodeGenerator();
		syntactic();
	}

	public void syntactic() {
		try {
			analisadorSintatico();
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
		} else if (semantic.hasError()) {
			errorLine = semantic.getLine();
		}
	}

	private void analisadorSintatico() throws SyntacticException, SemanticException {
		token = la.lexical();
		if (token.getSymbol().equals(Constants.PROGRAMA_SIMBOLO)) {
			generator.createCode(Constants.START, Constants.EMPTY, Constants.EMPTY);
			token = la.lexical();
			if (token.getSymbol().equals(Constants.IDENTIFICADOR_SIMBOLO)) {
				semantic.insertProgram(token);
				token = la.lexical();
				if (token.getSymbol().equals(Constants.PONTO_VIRGULA_SIMBOLO)) {
					analisaBloco();
					if (token.getSymbol().equals(Constants.PONTO_SIMBOLO)) {
						token = la.lexical();

						if (token.getSymbol().equals(Constants.FIM_ARQUIVO)) { 

							generator.createCode(Constants.HLT, Constants.EMPTY, Constants.EMPTY);
							generator.createFile();

							setMessage("Compilação realizada com sucesso.");

							semantic.cleanTableLevel();
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
		
		if(variableOfAlloc.size() > 0 && !flagProcedureList.get(flagFunctionList.size() - 1)) {
			if(!flagFunctionList.get(flagFunctionList.size() - 1) && (variableOfAlloc.get(variableOfAlloc.size() - 1) > 0)) {
				generator.createCode(Constants.DALLOC, -1);
				variableOfAlloc.remove(variableOfAlloc.size() - 1);
			}
			else if (!flagFunctionList.get(flagFunctionList.size() - 1) && (variableOfAlloc.get(variableOfAlloc.size() - 1) == 0)) {
				variableOfAlloc.remove(variableOfAlloc.size() - 1);
			}
		}

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
		
		variableOfAlloc.add(countVariable);
		countVariable = 0;
		
		if(variableOfAlloc.get(variableOfAlloc.size() - 1) > 0) {
			generator.createCode(Constants.ALLOC, variableOfAlloc.get(variableOfAlloc.size() - 1));
		}
	}

	private void analisaVariaveis() throws SyntacticException, SemanticException {	
		do {
			if (token.getSymbol().equals(Constants.IDENTIFICADOR_SIMBOLO)) {

				semantic.searchInTableOfSymbols(token);
				semantic.insertVariable(token, position);
				countVariable++;
				position++;

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

	private void analisaTipo() throws SyntacticException {
		if (!(token.getSymbol().equals(Constants.INTEIRO_SIMBOLO))
				&& !(token.getSymbol().equals(Constants.BOOLEANO_SIMBOLO))) {
			throw new SyntacticException(Constants.INTEIRO_LEXEMA, Constants.INTEIRO_SIMBOLO, Constants.BOOLEANO_LEXEMA,
					Constants.BOOLEANO_SIMBOLO, token.getLexema(), token.getSymbol(), token.getLine());
		} else {
			semantic.insertTypeOnVariable(token);
		}
		token = la.lexical();
	}

	private void analisaSubrotinas() throws SyntacticException, SemanticException {
		int auxrot = 0, flag = 0;

		if ((token.getSymbol().equals(Constants.PROCEDIMENTO_SIMBOLO))
				|| (token.getSymbol().equals(Constants.FUNCAO_SIMBOLO))) {

			auxrot = label;
			generator.createCode(Constants.JMP, Constants.LABEL + label, Constants.EMPTY);
			label++;
			flag = 1;
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

		if (flag == 1) {
			generator.createCode(Constants.LABEL + auxrot, Constants.NULL, Constants.EMPTY);
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
			semantic.searchVariableOrFunction(aux);
			
			analisaAtribuicao(aux);
			
		} else {
			chamadaProcedimento(aux);
		}
	}

	private void analisaAtribuicao(Token attributionToken) throws SyntacticException, SemanticException {
		token = la.lexical();
		analisaExpressao();

		String aux = semantic.expressionToPostfix(expression);

		String newExpression = semantic.formatExpression(aux);
		generator.createCode(newExpression);

		String type = semantic.returnTypeOfExpression(aux);
		semantic.whoCallsMe(type, attributionToken.getLexema());

		expression.clear();
		
		if (flagFunctionList.get(flagFunctionList.size() - 1) && (nameOfFunction.get(nameOfFunction.size() - 1)).equals(attributionToken.getLexema())) {
			semantic.insertTokenOnFunctionList(attributionToken);
		}
		
		if (nameOfFunction.size() > 0) {
			if (!((nameOfFunction.get(nameOfFunction.size() - 1)).equals(attributionToken.getLexema()))) {
				generator.createCode(Constants.STR, semantic.positionOfVariable(attributionToken.getLexema()), Constants.EMPTY);	
			}
		} else {
			generator.createCode(Constants.STR, semantic.positionOfVariable(attributionToken.getLexema()), Constants.EMPTY);
		}
	}

	private void chamadaProcedimento(Token auxToken) throws SemanticException {
		semantic.searchProcedure(auxToken);
		// se houver erro, dentro do semântico lancará a exceção. Caso seja um
		// procedimento
		// válido, continuará a excecução

		int labelResult = semantic.searchProcedureLabel(auxToken);
		generator.createCode(Constants.CALL, Constants.LABEL + labelResult, Constants.EMPTY);
	}

	private void chamadaFuncao(int index) throws SemanticException {
		String symbolLexema = semantic.getLexemaOfSymbol(index);
		semantic.searchFunction(new Token(Constants.EMPTY, symbolLexema, token.getLine()));
		// se houver erro, dentro do semântico lancará a exceção. Caso seja uma funcao
		// válida, continuará a excecução

		int labelResult = semantic.searchFunctionLabel(new Token(Constants.EMPTY, symbolLexema, token.getLine()));
		generator.createCode(Constants.CALL, Constants.LABEL + labelResult, Constants.EMPTY);

		token = la.lexical();
	}

	private void analisaLeia() throws SyntacticException, SemanticException {
		generator.createCode(Constants.RD, Constants.EMPTY, Constants.EMPTY);
		token = la.lexical();
		if (token.getSymbol().equals(Constants.ABRE_PARENTESES_SIMBOLO)) {
			token = la.lexical();
			if (token.getSymbol().equals(Constants.IDENTIFICADOR_SIMBOLO)) {
				semantic.searchVariable(token);
				generator.createCode(Constants.STR, semantic.positionOfVariable(token.getLexema()), Constants.EMPTY);
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

	private void analisaEscreva() throws SyntacticException, SemanticException {
		token = la.lexical();
		if (token.getSymbol().equals(Constants.ABRE_PARENTESES_SIMBOLO)) {
			token = la.lexical();
			if (token.getSymbol().equals(Constants.IDENTIFICADOR_SIMBOLO)) {

				boolean isFunction = semantic.searchVariableOrFunction(token);

				if (isFunction) {
					int labelResult = semantic.searchFunctionLabel(token);
					generator.createCode(Constants.CALL, Constants.LABEL + labelResult, Constants.EMPTY);
				} else {
					// LDV de Variável para o PRN
					String positionOfVariable = semantic.positionOfVariable(token.getLexema());
					generator.createCode(Constants.LDV, positionOfVariable, Constants.EMPTY);
				}

				token = la.lexical();

				if (token.getSymbol().equals(Constants.FECHA_PARENTESES_SIMBOLO)) {

					generator.createCode(Constants.PRN, Constants.EMPTY, Constants.EMPTY);
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

	private void analisaEnquanto() throws SyntacticException, SemanticException {
		int auxrot1, auxrot2;

		auxrot1 = label;
		generator.createCode(Constants.LABEL + label, Constants.NULL, Constants.EMPTY);
		label++;

		token = la.lexical();
		analisaExpressao();

		String aux = semantic.expressionToPostfix(expression);
		
		String newExpression = semantic.formatExpression(aux);
		generator.createCode(newExpression);

		
		String type = semantic.returnTypeOfExpression(aux);
		semantic.whoCallsMe(type, Constants.ENQUANTO_LEXEMA);
		expression.clear();

		if (token.getSymbol().equals(Constants.FACA_SIMBOLO)) {
			auxrot2 = label;
			generator.createCode(Constants.JMPF, Constants.LABEL + label, Constants.EMPTY);
			label++;

			token = la.lexical();
			analisaComandoSimples();

			generator.createCode(Constants.JMP, Constants.LABEL + auxrot1, Constants.EMPTY);
			generator.createCode(Constants.LABEL + auxrot2, Constants.NULL, Constants.EMPTY);
		} else {
			throw new SyntacticException(Constants.FACA_LEXEMA, Constants.FACA_SIMBOLO, token.getLexema(),
					token.getSymbol(), token.getLine());
		}
	}

	private void analisaSe() throws SyntacticException, SemanticException {
		int auxrot1, auxrot2;

		auxLabel++;
		if (flagFunctionList.get(flagFunctionList.size() - 1)) {
			semantic.insertTokenOnFunctionList(new Token(token.getSymbol(), token.getLexema() + auxLabel, token.getLine()));
		}
		
		token = la.lexical();
		analisaExpressao();

		String aux = semantic.expressionToPostfix(expression);
		
		String newExpression = semantic.formatExpression(aux);
		generator.createCode(newExpression);
		
		String type = semantic.returnTypeOfExpression(aux);
		semantic.whoCallsMe(type, Constants.SE_LEXEMA);
		expression.clear();

		if (token.getSymbol().equals(Constants.ENTAO_SIMBOLO)) {
			auxrot1 = label;
			generator.createCode(Constants.JMPF, Constants.LABEL + label, Constants.EMPTY);
			label++;

			if (flagFunctionList.get(flagFunctionList.size() - 1)) {
				semantic.insertTokenOnFunctionList(new Token(token.getSymbol(), token.getLexema() + auxLabel, token.getLine()));
			}
			
			token = la.lexical();
			analisaComandoSimples();
			if (token.getSymbol().equals(Constants.SENAO_SIMBOLO)) {

				auxrot2 = label;
				generator.createCode(Constants.JMP, Constants.LABEL + label, Constants.EMPTY);
				label++;

				generator.createCode(Constants.LABEL + auxrot1, Constants.NULL, Constants.EMPTY);

				if (flagFunctionList.get(flagFunctionList.size() - 1)) {
					semantic.insertTokenOnFunctionList(new Token(token.getSymbol(), token.getLexema() + auxLabel, token.getLine()));
				}
		
				token = la.lexical();
				analisaComandoSimples();

				generator.createCode(Constants.LABEL + auxrot2, Constants.NULL, Constants.EMPTY);
			} else {
				generator.createCode(Constants.LABEL + auxrot1, Constants.NULL, Constants.EMPTY);
			}
		} else {
			throw new SyntacticException(Constants.ENTAO_LEXEMA, Constants.ENTAO_SIMBOLO, token.getLexema(),
					token.getSymbol(), token.getLine());
		}
		if (flagFunctionList.get(flagFunctionList.size() - 1)) {
			semantic.verifyFunctionList(String.valueOf(auxLabel));
		}
		auxLabel--;
	}

	private void analisaDeclaracaoProcedimento() throws SyntacticException, SemanticException {
		flagProcedureList.add(true);
		
		token = la.lexical();
		if (token.getSymbol().equals(Constants.IDENTIFICADOR_SIMBOLO)) {
			semantic.searchProcedureWithTheSameName(token);
			semantic.insertProcOrFunc(token, Constants.PROCEDIMENTO, label);

			generator.createCode(Constants.LABEL + label, Constants.NULL, Constants.EMPTY);
			label++;

			token = la.lexical();
			if (token.getSymbol().equals(Constants.PONTO_VIRGULA_SIMBOLO)) {
				analisaBloco();
			} else {
				throw new SyntacticException(Constants.PONTO_VIRGULA_LEXEMA, Constants.PONTO_VIRGULA_SIMBOLO,
						token.getLexema(), token.getSymbol(), token.getLine());
			}
		} else {
			throw new SyntacticException(Constants.IDENTIFICADOR_LEXEMA, Constants.IDENTIFICADOR_SIMBOLO,
					token.getLexema(), token.getSymbol(), token.getLine());
		}
		semantic.cleanTableLevel();

		if (variableOfAlloc.get(variableOfAlloc.size() - 1) > 0) {
			generator.createCode(Constants.DALLOC, -1);
			variableOfAlloc.remove(variableOfAlloc.size() - 1);
		}
		else {
			variableOfAlloc.remove(variableOfAlloc.size() - 1);
		}
		
		generator.createCode(Constants.RETURN, Constants.EMPTY, Constants.EMPTY);
		
		flagProcedureList.remove(flagFunctionList.size() - 1);
	}

	private void analisaDeclaracaoFuncao() throws SyntacticException, SemanticException {
		flagFunctionList.add(true);
		token = la.lexical();
		if (token.getSymbol().equals(Constants.IDENTIFICADOR_SIMBOLO)) {
			semantic.searchFunctionWithTheSameName(token);
			semantic.insertProcOrFunc(token, Constants.FUNCAO, label);

			generator.createCode(Constants.LABEL + label, Constants.NULL, Constants.EMPTY);
			label++;
			
			nameOfFunction.add(token.getLexema());
			semantic.setLine(token.getLine());

			token = la.lexical();

			if (token.getSymbol().equals(Constants.DOIS_PONTOS_SIMBOLO)) {
				token = la.lexical();

				if (token.getSymbol().equals(Constants.INTEIRO_SIMBOLO)
						|| token.getSymbol().equals(Constants.BOOLEANO_SIMBOLO)) {
					if (token.getSymbol().equals(Constants.INTEIRO_SIMBOLO)) {
						semantic.insertTypeOnFunction(Constants.INTEIRO_LEXEMA);
					} else {
						semantic.insertTypeOnFunction(Constants.BOOLEANO_LEXEMA);
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
				throw new SyntacticException(Constants.DOIS_PONTOS_LEXEMA, Constants.DOIS_PONTOS_SIMBOLO,
						token.getLexema(), token.getSymbol(), token.getLine());
			}

		} else {
			throw new SyntacticException(Constants.IDENTIFICADOR_LEXEMA, Constants.IDENTIFICADOR_SIMBOLO,
					token.getLexema(), token.getSymbol(), token.getLine());
		}
		semantic.cleanTableLevel();
		flagFunctionList.remove(flagFunctionList.size() - 1);
		semantic.thisFunctionHasReturn(nameOfFunction.get(nameOfFunction.size() - 1));
		nameOfFunction.remove(nameOfFunction.size() - 1);
		
		if(variableOfAlloc.get(variableOfAlloc.size() - 1) > 0) {
			generator.createCode(Constants.RETURNF, -1);
			variableOfAlloc.remove(variableOfAlloc.size() - 1);
		}
		else {
			generator.createCode(Constants.RETURNF, 0);
			variableOfAlloc.remove(variableOfAlloc.size() - 1);
		}
		semantic.clearFunctionList();
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
			Token aux = new Token(token.getSymbol(), token.getLexema() + "u", token.getLine()); // passando o operador
																								// unário para o
																								// semantico
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
			int index = semantic.searchSymbol(token);
			if (semantic.isValidFunction(index)) {
				expression.add(token);
				chamadaFuncao(index);
			} else {
				expression.add(token);
				token = la.lexical();
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

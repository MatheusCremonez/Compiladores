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

	// S�o as nomina��es para onde ser� realizado o pulo (Ex: L"0", L"1", etc)
	private int label = 0;

	// S�o as posi��es na mem�ria que est� alocado a vari�vel
	private int position = 0;
	
	//Vari�veis declaradas para o ALLOC
	private int countVariable = 0;

	private boolean flagFunction = false;
	private List<String> nameOfFunction = new ArrayList<String>();
	private int auxLabel = 0;

	public SyntacticAnalyzer(String file) {
		la = new LexicalAnalyzer(file);
		semantic = new SemanticAnalyzer();
		generator = new CodeGenerator();
		syntactic();
	}

	public void syntactic() {
		try {
			analisadorSintatico();
			// generator.debugCode();
			// semantic.debugTable();
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
			generator.createCode("START", Constants.EMPTY, Constants.EMPTY);
			token = la.lexical();
			if (token.getSymbol().equals(Constants.IDENTIFICADOR_SIMBOLO)) {
				semantic.insertProgram(token);
				token = la.lexical();
				if (token.getSymbol().equals(Constants.PONTO_VIRGULA_SIMBOLO)) {
					analisaBloco();
					if (token.getSymbol().equals(Constants.PONTO_SIMBOLO)) {
						token = la.lexical();

						if (token.getSymbol().equals(Constants.FIM_ARQUIVO)) { 

							generator.createCode("HLT", Constants.EMPTY, Constants.EMPTY);
							generator.createFile();

							setMessage("Compila��o realizada com sucesso.");

							semantic.cleanTableLevel();
						} else {
							throw new SyntacticException("Trecho de c�digo inesperado na linha: " + token.getLine());
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
		
		if(!flagFunction) {
			generator.createCode("DALLOC", -1);
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
		
		generator.createCode("ALLOC", countVariable);
		countVariable = 0;
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
			generator.createCode("JMP", "L" + label, "");
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
			generator.createCode("L" + auxrot, "NULL", "");
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
		
		if (flagFunction && (nameOfFunction.get(nameOfFunction.size() - 1)).equals(attributionToken.getLexema())) {
			semantic.insertTokenOnFunctionList(attributionToken);
		}
		
		if (nameOfFunction.size() > 0) {
			if (!((nameOfFunction.get(nameOfFunction.size() - 1)).equals(attributionToken.getLexema()))) {
				generator.createCode("STR", semantic.positionOfVariable(attributionToken.getLexema()), Constants.EMPTY);	
			}
		} else {
			generator.createCode("STR", semantic.positionOfVariable(attributionToken.getLexema()), Constants.EMPTY);
		}
		
		
	}

	private void chamadaProcedimento(Token auxToken) throws SemanticException {
		semantic.searchProcedure(auxToken);
		// se houver erro, dentro do sem�ntico lancar� a exce��o. Caso seja um
		// procedimento
		// v�lido, continuar� a excecu��o

		int labelResult = semantic.searchProcedureLabel(auxToken);
		generator.createCode("CALL", "L" + labelResult, "");
	}

	private void chamadaFuncao(int index) throws SemanticException {
		String symbolLexema = semantic.getLexemaOfSymbol(index);
		semantic.searchFunction(new Token("", symbolLexema, token.getLine()));
		// se houver erro, dentro do sem�ntico lancar� a exce��o. Caso seja uma funcao
		// v�lida, continuar� a excecu��o

		int labelResult = semantic.searchFunctionLabel(new Token("", symbolLexema, token.getLine()));
		generator.createCode("CALL", "L" + labelResult, "");

		token = la.lexical();
	}

	private void analisaLeia() throws SyntacticException, SemanticException {
		generator.createCode("RD", "", "");
		token = la.lexical();
		if (token.getSymbol().equals(Constants.ABRE_PARENTESES_SIMBOLO)) {
			token = la.lexical();
			if (token.getSymbol().equals(Constants.IDENTIFICADOR_SIMBOLO)) {
				semantic.searchVariable(token);
				generator.createCode("STR", semantic.positionOfVariable(token.getLexema()), Constants.EMPTY);
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
					generator.createCode("CALL", "L" + labelResult, "");
				} else {
					// LDV de Vari�vel para o PRN
					String positionOfVariable = semantic.positionOfVariable(token.getLexema());
					generator.createCode("LDV", positionOfVariable, "");
				}

				token = la.lexical();

				if (token.getSymbol().equals(Constants.FECHA_PARENTESES_SIMBOLO)) {

					generator.createCode("PRN", "", "");
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
		generator.createCode("L" + label, "NULL", "");
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
			generator.createCode("JMPF", "L" + label, "");
			label++;

			token = la.lexical();
			analisaComandoSimples();

			generator.createCode("JMP", "L" + auxrot1, "");
			generator.createCode("L" + auxrot2, "NULL", "");
		} else {
			throw new SyntacticException(Constants.FACA_LEXEMA, Constants.FACA_SIMBOLO, token.getLexema(),
					token.getSymbol(), token.getLine());
		}
	}

	private void analisaSe() throws SyntacticException, SemanticException {
		int auxrot1, auxrot2;

		auxLabel++;
		if (flagFunction) {
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
			generator.createCode("JMPF", "L" + label, "");
			label++;

			if (flagFunction) {
				semantic.insertTokenOnFunctionList(new Token(token.getSymbol(), token.getLexema() + auxLabel, token.getLine()));
			}
			
			token = la.lexical();
			analisaComandoSimples();
			if (token.getSymbol().equals(Constants.SENAO_SIMBOLO)) {

				auxrot2 = label;
				generator.createCode("JMP", "L" + label, "");
				label++;

				generator.createCode("L" + auxrot1, "NULL", "");

				if (flagFunction) {
					semantic.insertTokenOnFunctionList(new Token(token.getSymbol(), token.getLexema() + auxLabel, token.getLine()));
				}
		
				token = la.lexical();
				analisaComandoSimples();

				generator.createCode("L" + auxrot2, "NULL", "");
			} else {
				generator.createCode("L" + auxrot1, "NULL", "");
			}
		} else {
			throw new SyntacticException(Constants.ENTAO_LEXEMA, Constants.ENTAO_SIMBOLO, token.getLexema(),
					token.getSymbol(), token.getLine());
		}
		if (flagFunction) {
			semantic.verifyFunctionList(String.valueOf(auxLabel));
		}
		auxLabel--;
	}

	private void analisaDeclaracaoProcedimento() throws SyntacticException, SemanticException {
		token = la.lexical();
		if (token.getSymbol().equals(Constants.IDENTIFICADOR_SIMBOLO)) {
			semantic.searchProcedureWithTheSameName(token);
			semantic.insertProcOrFunc(token, Constants.PROCEDIMENTO, label);

			generator.createCode("L" + label, "NULL", "");
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

		generator.createCode("RETURN", "", "");
	}

	private void analisaDeclaracaoFuncao() throws SyntacticException, SemanticException {
		semantic.clearFunctionList();
		flagFunction = !flagFunction;
		token = la.lexical();
		if (token.getSymbol().equals(Constants.IDENTIFICADOR_SIMBOLO)) {
			semantic.searchFunctionWithTheSameName(token);
			semantic.insertProcOrFunc(token, Constants.FUNCAO, label);

			generator.createCode("L" + label, "NULL", "");
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
		flagFunction = !flagFunction;
		semantic.thisFunctionHasReturn(nameOfFunction.get(nameOfFunction.size() - 1));
		nameOfFunction.remove(nameOfFunction.size() - 1);
		
		System.out.println("------final table-------");
		semantic.debugTableFunction();
		System.out.println("------------------------");
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
																								// un�rio para o
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
			throw new SyntacticException("Express�o incompleta na linha: " + token.getLine());
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

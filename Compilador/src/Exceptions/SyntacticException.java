package Exceptions;

public class SyntacticException extends Exception {

	private static final long serialVersionUID = 1L;

	public SyntacticException(String message) {
		super(message);
	}

	public SyntacticException(String expectedLexema, String expectedSymbol, String receivedLexema,
			String receivedSymbol, int line) {
		super("Token esperado não encontrado na linha: " + line + ".\n" + "Encontrado: '" + receivedLexema + "' ("
				+ receivedSymbol + ").\n" + "Esperado: '" + expectedLexema + "' (" + expectedSymbol + ").");
	}

	public SyntacticException(String expectedLexema1, String expectedSymbol1, String expectedLexema2,
			String expectedSymbol2, String receivedLexema, String receivedSymbol, int line) {
		super("Token esperado não encontrado na linha: " + line + ".\n" + "Encontrado: '" + receivedLexema + "' ("
				+ receivedSymbol + ").\n" + "Esperado: '" + expectedLexema1 + "' (" + expectedSymbol1 + ").\n" + "Ou '"
				+ expectedLexema2 + "' (" + expectedSymbol2 + ").");
	}
}

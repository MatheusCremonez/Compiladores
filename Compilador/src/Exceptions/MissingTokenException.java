package Exceptions;

public class MissingTokenException extends Exception {

	private static final long serialVersionUID = 1L;

	public MissingTokenException(String expectedLexema, String expectedSymbol, String receivedLexema, String receivedSymbol, int line) {
		super("Token esperado não encontrado na linha: " + line + ".\n"
				+ "Encontrado: '" + receivedLexema + "' (" + receivedSymbol + ").\n"
				+ "Esperado: '" + expectedLexema + "' (" + expectedSymbol + ").");
	}
}

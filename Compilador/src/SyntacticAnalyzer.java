import java.util.ArrayList;
import java.util.List;

public class SyntacticAnalyzer {

	private String message;
	private List<Token> listaToken = new ArrayList<Token>();
	private LexicalAnalyzer la;

	public SyntacticAnalyzer(String file) {
		la = new LexicalAnalyzer(file);
		syntactic();
	}

	public void syntactic() {

		Token token;

		do {
			token = la.lexical();
			if (token != null) {
				listaToken.add(token);
			}
		} while (token != null);

		if (!la.error) {
			for (int i = 0; i < listaToken.size(); i++) {
				String newMessage = listaToken.get(i).getSymbol() + " " + listaToken.get(i).getLexema() + " "
						+ listaToken.get(i).getLine();
				if (i == 0)
					setMessage(newMessage);
				else
					setMessage(getMessage() + '\n' + newMessage);
			}
		} else {
			setMessage(la.getMessage());
		}
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public final String getMessage() {
		return this.message;
	}
}

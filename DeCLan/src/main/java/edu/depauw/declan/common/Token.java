package edu.depauw.declan.common;

/**
 * A Token represents one lexical unit of a DeCLan source program. A Token
 * object stores a position (line and column numbers, each starting from 1), a
 * TokenType, and a lexeme (string value -- for the fixed tokens, this would be
 * redundant and should be null, but for identifiers and numbers it specifies
 * which particular one it is).
 * 
 * @author bhoward
 */
public class Token {
	private final TokenType type;
	private final String lexeme;
	private final Position position;

	/**
	 * Construct a Token object given its components. This is package-private;
	 * tokens should be created using a TokenFactory.
	 * 
	 * @param line   the line number (starting from 1) where the token was found
	 * @param column the column number (starting from 1) where the token started
	 * @param type   the TokenType of the token
	 * @param lexeme the string value of the token
	 */
	Token(Position position, TokenType type, String lexeme) {
		this.position = position;
		this.type = type;
		this.lexeme = lexeme;
	}

	// Override the default toString() for use in development and debugging.
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder(type.toString());
		if (lexeme != null) {
			result.append(" ").append(lexeme);
		}
		result.append(" ").append(position);
		return result.toString();
	}

	public TokenType getType() {
		return type;
	}

	public String getLexeme() {
		return lexeme;
	}

	public Position getPosition() {
		return position;
	}

	/**
	 * Create a Token for a string literal. The lexeme is just the contents of the
	 * string (without surrounding quotes).
	 * 
	 * @param lexeme
	 * @param line
	 * @param column
	 * @return
	 */
	public static Token makeStringToken(String lexeme, Position position) {
		return new Token(position, TokenType.STRING, lexeme);
	}

	/**
	 * Create a Token for a numeric literal.
	 * 
	 * @param lexeme
	 * @param line
	 * @param column
	 * @return
	 */
	public static Token makeNumToken(String lexeme, Position position) {
		return new Token(position, TokenType.NUM, lexeme);
	}

	/**
	 * Create a Token that looks like an identifier. If the lexeme matches one of
	 * the reserved words, create the corresponding keyword token instead.
	 * 
	 * @param lexeme
	 * @param line
	 * @param column
	 * @return
	 */
	public static Token makeIdToken(String lexeme, Position position) {
		if (TokenType.reserved.containsKey(lexeme)) {
			return new Token(position, TokenType.reserved.get(lexeme), null);
		} else {
			return new Token(position, TokenType.ID, lexeme);
		}
	}

	/**
	 * Create a Token of a type where the lexeme is always the same.
	 * 
	 * @param type
	 * @param line
	 * @param column
	 * @return
	 */
	public static Token makeToken(TokenType type, Position position) {
		return new Token(position, type, null);
	}
}

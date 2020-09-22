package edu.depauw.declan.common.ast;

import edu.depauw.declan.common.ASTVisitor;
import edu.depauw.declan.common.Position;

public class Identifier extends AbstractASTNode implements Expression {
	private final String lexeme;
	
	public Identifier(Position start, String lexeme) {
		super(start);
		this.lexeme = lexeme;
	}

	public String getLexeme() {
		return lexeme;
	}
	
	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
	}
}

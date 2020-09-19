package edu.depauw.declan.main;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.StringReader;

import org.junit.jupiter.api.Test;

import edu.depauw.declan.common.Lexer;
import edu.depauw.declan.common.ReaderSource;
import edu.depauw.declan.common.Source;
import edu.depauw.declan.common.Token;
import edu.depauw.declan.model.ReferenceLexer;

class MyLexerOptionalTest {
	// The following test optional features
	@Test
	void testHexIntegers() {
		String input = "0H 9H 0ABCDEFH";
		compareToModel(input);
	}
	
	@Test
	void testRealNumbers() {
		String input = "0. 1.2 345.678 01.E23 4.5E+6 7.8E-09";
		compareToModel(input);
	}
	
	@Test
	void testNestedComments() {
		String input = "(**((***))**) (* \"(*\" *)\n"
				+ "\" *) (*(*(*(*here*)*)there*)*)everywhere";
		compareToModel(input);
	}
	
	@Test
	void testAdvancedErrorRecovery() {
		String input = "1F+2E-3.4E*5.E-D6";
		compareToModel(input);
	}

	void compareToModel(String input) {
		Source mySource = new ReaderSource(new StringReader(input));
		Source modelSource = new ReaderSource(new StringReader(input));

		try (Lexer myLexer = new MyLexer(mySource);
			 Lexer modelLexer = new ReferenceLexer(modelSource)) {
			while (modelLexer.hasNext()) {
				assertTrue(myLexer.hasNext(), "Not enough tokens");
				Token modelToken = modelLexer.next();
				Token myToken = myLexer.next();
				assertEquals(modelToken.getType(), myToken.getType());
				assertEquals(modelToken.getLexeme(), myToken.getLexeme());
				assertEquals(modelToken.getPosition(), myToken.getPosition());
			}

			assertFalse(myLexer.hasNext(), "Too many tokens");
		}
	}
}

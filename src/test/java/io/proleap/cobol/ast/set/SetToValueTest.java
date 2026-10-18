package io.proleap.cobol.ast.set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.Test;

import io.proleap.cobol.CobolLexer;
import io.proleap.cobol.CobolParser;

/** SET identifier ... TO value takes one sending value per TO phrase. */
public class SetToValueTest {

	private static int syntaxErrors(final String statement) {
		final CobolParser parser = new CobolParser(new CommonTokenStream(new CobolLexer(CharStreams.fromString(
				"IDENTIFICATION DIVISION. PROGRAM-ID. X. PROCEDURE DIVISION. " + statement + ".\n"))));
		parser.removeErrorListeners();
		parser.startRule();
		return parser.getNumberOfSyntaxErrors();
	}

	@Test
	public void eachToPhraseHasOneValue() {
		assertEquals(0, syntaxErrors("SET A B TO 1 C TO 2"));
		assertNotEquals(0, syntaxErrors("SET A TO 1 2"));
	}
}

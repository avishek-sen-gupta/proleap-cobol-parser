package io.proleap.cobol.ast.phrase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;

import io.proleap.cobol.CobolLexer;
import io.proleap.cobol.CobolParser;

/**
 * The body of a conditional phrase is imperative: a statement inside it cannot
 * take the outer statement's NOT phrase. Outside a phrase body a statement may
 * carry a NOT phrase alone.
 */
public class ImperativePhraseBodyTest {

	private static <T extends ParserRuleContext> T first(final ParseTree tree, final Class<T> type) {
		if (type.isInstance(tree)) {
			return type.cast(tree);
		}
		for (int i = 0; i < tree.getChildCount(); i++) {
			final T found = first(tree.getChild(i), type);
			if (found != null) {
				return found;
			}
		}
		return null;
	}

	private static ParseTree parse(final String statement) {
		final CobolParser parser = new CobolParser(new CommonTokenStream(new CobolLexer(CharStreams.fromString(
				"IDENTIFICATION DIVISION. PROGRAM-ID. X. PROCEDURE DIVISION. " + statement + ".\n"))));
		final ParseTree tree = parser.startRule();
		assertEquals(statement, 0, parser.getNumberOfSyntaxErrors());
		return tree;
	}

	@Test
	public void theOuterStatementKeepsItsNotPhrase() {
		final ParseTree call = parse(
				"CALL \"P\" ON EXCEPTION ACCEPT Y NOT ON EXCEPTION DISPLAY \"Z\" END-CALL");
		assertNotNull(first(call, CobolParser.CallStatementContext.class).notOnExceptionClause());
		assertNull(first(call, CobolParser.AcceptStatementContext.class).notOnExceptionClause());

		final ParseTree add = parse(
				"ADD 1 TO A ON SIZE ERROR COMPUTE B = 1 NOT ON SIZE ERROR DISPLAY \"Z\" END-ADD");
		assertNotNull(first(add, CobolParser.AddStatementContext.class).notOnSizeErrorPhrase());
		assertNull(first(add, CobolParser.ComputeStatementContext.class).notOnSizeErrorPhrase());
	}

	@Test
	public void aNotPhraseAloneIsAcceptedOutsideAPhraseBody() {
		assertNotNull(first(parse("DISPLAY X NOT ON EXCEPTION DISPLAY Y END-DISPLAY"),
				CobolParser.DisplayStatementContext.class).notOnExceptionClause());
		assertNotNull(first(parse("COMPUTE X = 1 NOT ON SIZE ERROR DISPLAY Y"),
				CobolParser.ComputeStatementContext.class).notOnSizeErrorPhrase());
		assertNotNull(first(parse("READ F NOT AT END DISPLAY Y END-READ"),
				CobolParser.ReadStatementContext.class).notAtEndPhrase());
	}

	@Test
	public void anInnerStatementKeepsItsOwnPairedPhrases() {
		final ParseTree call = parse("CALL \"P\" ON EXCEPTION ADD 1 TO A ON SIZE ERROR DISPLAY 1"
				+ " NOT ON SIZE ERROR DISPLAY 2 END-ADD NOT ON EXCEPTION DISPLAY 3 END-CALL");
		assertNotNull(first(call, CobolParser.CallStatementContext.class).notOnExceptionClause());
		assertNotNull(first(call, CobolParser.AddStatementContext.class).notOnSizeErrorPhrase());
	}
}

package io.proleap.cobol.ast.function;

import static org.junit.Assert.assertEquals;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;

import io.proleap.cobol.CobolLexer;
import io.proleap.cobol.CobolParser;

/**
 * A left parenthesis directly after a data name in a FUNCTION argument subscripts
 * that name; after a separator comma it opens the next argument.
 */
public class FunctionArgumentSubscriptTest {

	private static CobolParser.FunctionCallContext firstFunctionCall(final ParseTree tree) {
		if (tree instanceof CobolParser.FunctionCallContext) {
			return (CobolParser.FunctionCallContext) tree;
		}
		for (int i = 0; i < tree.getChildCount(); i++) {
			final CobolParser.FunctionCallContext found = firstFunctionCall(tree.getChild(i));
			if (found != null) {
				return found;
			}
		}
		return null;
	}

	private static int arguments(final String expression) {
		final CobolParser parser = new CobolParser(new CommonTokenStream(new CobolLexer(CharStreams.fromString(
				"IDENTIFICATION DIVISION. PROGRAM-ID. X. PROCEDURE DIVISION. COMPUTE N = " + expression + ".\n"))));
		final ParseTree tree = parser.startRule();
		assertEquals(expression, 0, parser.getNumberOfSyntaxErrors());
		return firstFunctionCall(tree).argument().size();
	}

	@Test
	public void aSubscriptStaysWithItsDataName() {
		assertEquals(1, arguments("FUNCTION MAX(TBL(I))"));
		assertEquals(1, arguments("FUNCTION MAX(TBL (I))"));
		assertEquals(1, arguments("FUNCTION SQRT(TBL(5) / 9)"));
		assertEquals(1, arguments("FUNCTION MAX(TBL OF REC (I, 2))"));
	}

	@Test
	public void separateArgumentsStaySeparate() {
		assertEquals(2, arguments("FUNCTION MAX(A, (B))"));
		assertEquals(2, arguments("FUNCTION MAX((A) (B))"));
		assertEquals(2, arguments("FUNCTION MAX(A B)"));
		assertEquals(2, arguments("FUNCTION MAX(A,B)"));
	}
}

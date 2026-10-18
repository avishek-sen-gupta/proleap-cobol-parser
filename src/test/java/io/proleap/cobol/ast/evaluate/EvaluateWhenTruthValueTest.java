package io.proleap.cobol.ast.evaluate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;

import io.proleap.cobol.CobolLexer;
import io.proleap.cobol.CobolParser;

/** WHEN TRUE / FALSE is the truth-value selection object, not a literal value. */
public class EvaluateWhenTruthValueTest {

	private static void collect(final ParseTree tree, final List<CobolParser.EvaluateConditionContext> out) {
		if (tree instanceof CobolParser.EvaluateConditionContext) {
			out.add((CobolParser.EvaluateConditionContext) tree);
		}
		for (int i = 0; i < tree.getChildCount(); i++) {
			collect(tree.getChild(i), out);
		}
	}

	private static List<CobolParser.EvaluateConditionContext> objects(final String statement) {
		final CobolParser parser = new CobolParser(new CommonTokenStream(new CobolLexer(CharStreams.fromString(
				"IDENTIFICATION DIVISION. PROGRAM-ID. X. PROCEDURE DIVISION. " + statement + ".\n"))));
		final ParseTree tree = parser.startRule();
		assertEquals(statement, 0, parser.getNumberOfSyntaxErrors());
		final List<CobolParser.EvaluateConditionContext> out = new ArrayList<>();
		collect(tree, out);
		return out;
	}

	@Test
	public void truthValuesTakeTheirOwnAlternative() {
		final List<CobolParser.EvaluateConditionContext> conditions = objects(
				"EVALUATE A ALSO B WHEN TRUE ALSO FALSE CONTINUE WHEN NOT TRUE ALSO 7 CONTINUE END-EVALUATE");
		assertEquals(4, conditions.size());
		assertNotNull(conditions.get(0).booleanLiteral().TRUE());
		assertNotNull(conditions.get(1).booleanLiteral().FALSE());
		assertNotNull(conditions.get(2).NOT());
		assertNotNull(conditions.get(2).booleanLiteral().TRUE());
		assertNull(conditions.get(3).booleanLiteral());
		assertNotNull(conditions.get(3).evaluateValue());
	}
}

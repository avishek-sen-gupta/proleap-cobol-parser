package io.proleap.cobol.ast.inspect;

import static org.junit.Assert.assertEquals;

import java.util.function.Function;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;

import io.proleap.cobol.CobolLexer;
import io.proleap.cobol.CobolParser;

/** In INSPECT, an ALL after a pattern starts a new ALL phrase, not an ALL-literal figurative pattern. */
public class InspectAllPhraseTest {

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

	private static <T extends ParserRuleContext> int count(final String statement, final Class<T> type,
			final Function<T, Integer> size) {
		final CobolParser parser = new CobolParser(new CommonTokenStream(new CobolLexer(CharStreams.fromString(
				"IDENTIFICATION DIVISION. PROGRAM-ID. X. PROCEDURE DIVISION. " + statement + ".\n"))));
		final ParseTree tree = parser.startRule();
		assertEquals(statement, 0, parser.getNumberOfSyntaxErrors());
		return size.apply(first(tree, type));
	}

	@Test
	public void tallyingAllAfterAPatternStartsAPhrase() {
		assertEquals(2, (int) count("INSPECT X TALLYING N FOR ALL \"A\" ALL \"B\"", CobolParser.InspectForContext.class,
				f -> f.inspectAllLeadings().size()));
		assertEquals(1, (int) count("INSPECT X TALLYING N FOR ALL \"A\" \"B\"", CobolParser.InspectForContext.class,
				f -> f.inspectAllLeadings().size()));
		assertEquals(2, (int) count("INSPECT X TALLYING N FOR LEADING \"A\" ALL \"B\"",
				CobolParser.InspectForContext.class, f -> f.inspectAllLeadings().size()));
	}

	@Test
	public void replacingAllAfterAPatternStartsAPhrase() {
		assertEquals(2, (int) count("INSPECT X REPLACING ALL \"A\" BY \"X\" ALL \"B\" BY \"Y\"",
				CobolParser.InspectReplacingPhraseContext.class, r -> r.inspectReplacingAllLeadings().size()));
		assertEquals(1, (int) count("INSPECT X REPLACING ALL \"A\" BY \"X\" \"B\" BY \"Y\"",
				CobolParser.InspectReplacingPhraseContext.class, r -> r.inspectReplacingAllLeadings().size()));
	}
}

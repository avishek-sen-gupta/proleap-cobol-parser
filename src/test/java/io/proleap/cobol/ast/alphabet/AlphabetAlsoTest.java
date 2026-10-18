package io.proleap.cobol.ast.alphabet;

import static org.junit.Assert.assertEquals;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;

import io.proleap.cobol.CobolLexer;
import io.proleap.cobol.CobolParser;

/** ALSO in an ALPHABET literal phrase takes exactly one literal; the next starts a new literal group. */
public class AlphabetAlsoTest {

	private static CobolParser.AlphabetClauseFormat1Context alphabet(final ParseTree tree) {
		if (tree instanceof CobolParser.AlphabetClauseFormat1Context) {
			return (CobolParser.AlphabetClauseFormat1Context) tree;
		}
		for (int i = 0; i < tree.getChildCount(); i++) {
			final CobolParser.AlphabetClauseFormat1Context found = alphabet(tree.getChild(i));
			if (found != null) {
				return found;
			}
		}
		return null;
	}

	@Test
	public void alsoTakesOneLiteral() {
		final CobolParser parser = new CobolParser(new CommonTokenStream(new CobolLexer(CharStreams.fromString(
				"IDENTIFICATION DIVISION. PROGRAM-ID. X. ENVIRONMENT DIVISION. CONFIGURATION SECTION. "
						+ "SPECIAL-NAMES. ALPHABET AL IS \"A\" ALSO \"B\" ALSO \"D\" \"C\". PROCEDURE DIVISION.\n"))));
		final ParseTree tree = parser.startRule();
		assertEquals(0, parser.getNumberOfSyntaxErrors());
		final CobolParser.AlphabetClauseFormat1Context clause = alphabet(tree);
		assertEquals(2, clause.alphabetLiterals().size());
		assertEquals(2, clause.alphabetLiterals(0).alphabetAlso().size());
		assertEquals("\"B\"", clause.alphabetLiterals(0).alphabetAlso(0).literal().getText());
		assertEquals("\"C\"", clause.alphabetLiterals(1).literal().getText());
	}
}

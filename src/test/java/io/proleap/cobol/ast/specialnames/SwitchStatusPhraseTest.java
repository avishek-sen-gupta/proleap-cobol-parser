package io.proleap.cobol.ast.specialnames;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;

import io.proleap.cobol.CobolLexer;
import io.proleap.cobol.CobolParser;

/** A switch's ON STATUS and OFF STATUS phrases each name one condition-name, in either order. */
public class SwitchStatusPhraseTest {

	private static CobolParser.EnvironmentSwitchNameSpecialNamesStatusPhraseContext phrase(final ParseTree tree) {
		if (tree instanceof CobolParser.EnvironmentSwitchNameSpecialNamesStatusPhraseContext) {
			return (CobolParser.EnvironmentSwitchNameSpecialNamesStatusPhraseContext) tree;
		}
		for (int i = 0; i < tree.getChildCount(); i++) {
			final CobolParser.EnvironmentSwitchNameSpecialNamesStatusPhraseContext found = phrase(tree.getChild(i));
			if (found != null) {
				return found;
			}
		}
		return null;
	}

	private static CobolParser.EnvironmentSwitchNameSpecialNamesStatusPhraseContext parse(final String clause) {
		final CobolParser parser = new CobolParser(new CommonTokenStream(new CobolLexer(CharStreams.fromString(
				"IDENTIFICATION DIVISION. PROGRAM-ID. X. ENVIRONMENT DIVISION. CONFIGURATION SECTION. "
						+ "SPECIAL-NAMES. " + clause + ". PROCEDURE DIVISION.\n"))));
		final ParseTree tree = parser.startRule();
		assertEquals(clause, 0, parser.getNumberOfSyntaxErrors());
		return phrase(tree);
	}

	@Test
	public void eachStatusNamesItsConditionName() {
		final CobolParser.EnvironmentSwitchNameSpecialNamesStatusPhraseContext both =
				parse("SWITCH-1 IS SW1 OFF STATUS IS SW1-OFF ON SW1-ON");
		assertEquals("SW1-ON", both.switchOnStatus().conditionName().getText());
		assertEquals("SW1-OFF", both.switchOffStatus().conditionName().getText());

		final CobolParser.EnvironmentSwitchNameSpecialNamesStatusPhraseContext on =
				parse("SWITCH-2 IS SW2 ON STATUS IS SW2-ON");
		assertEquals("SW2-ON", on.switchOnStatus().conditionName().getText());
		assertNull(on.switchOffStatus());
	}
}

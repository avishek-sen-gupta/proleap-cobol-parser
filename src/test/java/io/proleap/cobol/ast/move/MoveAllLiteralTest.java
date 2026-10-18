package io.proleap.cobol.ast.move;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;

import io.proleap.cobol.CobolLexer;
import io.proleap.cobol.CobolParser;

/** MOVE ALL literal: ALL belongs to the sending literal, the figurative constant ALL literal. */
public class MoveAllLiteralTest {

	private static CobolParser.MoveToSendingAreaContext sendingArea(final ParseTree tree) {
		if (tree instanceof CobolParser.MoveToSendingAreaContext) {
			return (CobolParser.MoveToSendingAreaContext) tree;
		}
		for (int i = 0; i < tree.getChildCount(); i++) {
			final CobolParser.MoveToSendingAreaContext found = sendingArea(tree.getChild(i));
			if (found != null) {
				return found;
			}
		}
		return null;
	}

	private static CobolParser.FigurativeConstantContext sendingFigurative(final String statement) {
		final CobolParser parser = new CobolParser(new CommonTokenStream(new CobolLexer(CharStreams.fromString(
				"IDENTIFICATION DIVISION. PROGRAM-ID. X. PROCEDURE DIVISION. " + statement + ".\n"))));
		final ParseTree tree = parser.startRule();
		assertEquals(statement, 0, parser.getNumberOfSyntaxErrors());
		return sendingArea(tree).literal().figurativeConstant();
	}

	@Test
	public void allIsPartOfTheSendingLiteral() {
		final CobolParser.FigurativeConstantContext all = sendingFigurative("MOVE ALL \"AB\" TO X");
		assertNotNull(all.ALL());
		assertEquals("\"AB\"", all.literal().getText());
		assertNotNull(sendingFigurative("MOVE ALL ZEROS TO X").ALL());
	}
}

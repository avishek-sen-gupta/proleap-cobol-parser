package io.proleap.cobol.preprocessor.fixed;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.junit.Test;

import io.proleap.cobol.asg.params.CobolParserParams;
import io.proleap.cobol.asg.params.impl.CobolParserParamsImpl;
import io.proleap.cobol.preprocessor.CobolPreprocessor.CobolSourceFormatEnum;
import io.proleap.cobol.preprocessor.impl.CobolPreprocessorImpl;

/**
 * A comment-entry paragraph header whose separator period was left out --
 * AUTHOR, INSTALLATION, DATE-WRITTEN, DATE-COMPILED, SECURITY, REMARKS -- still
 * introduces a comment entry, so the marker has to tag it. Without the tag the
 * commentary is lexed as ordinary COBOL and the parse dies on the first token of
 * it.
 *
 * The EXEC CICS block at the end is the guard on how far that relaxation
 * reaches: SECURITY there begins a continuation line in area B, not a paragraph
 * header in area A, and tagging it would rewrite a line of a real statement into
 * commentary -- a silently dropped statement rather than a parse error. The
 * period-less form is therefore recognised in area A only.
 */
public class CommentEntryParagraphNoDotTest {

	@Test
	public void test() throws Exception {
		final CobolParserParams params = new CobolParserParamsImpl();
		params.setFormat(CobolSourceFormatEnum.FIXED);

		final File inputFile = new File(
				"src/test/resources/io/proleap/cobol/preprocessor/fixed/CommentEntryParagraphNoDot.cbl");
		final String preProcessedInput = new CobolPreprocessorImpl().process(inputFile, params);

		final File expectedFile = new File(
				"src/test/resources/io/proleap/cobol/preprocessor/fixed/CommentEntryParagraphNoDot.cbl.preprocessed");
		final String expected = Files.readString(expectedFile.toPath(), StandardCharsets.UTF_8);
		assertEquals(expected, preProcessedInput);
	}
}

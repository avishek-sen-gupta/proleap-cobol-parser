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
 * A copy book is preprocessed on its own and declares no identification
 * division, so nothing in it can be a comment-entry paragraph header. A header
 * keyword written without its separator period is only a header inside an
 * identification division; here the same words are ordinary text, and tagging
 * them would turn the lines that follow into commentary -- silently dropping
 * whatever the copy book contributes.
 */
public class CommentEntryTriggerOutsideProgramTest {

	@Test
	public void test() throws Exception {
		final CobolParserParams params = new CobolParserParamsImpl();
		params.setFormat(CobolSourceFormatEnum.FIXED);

		final File inputFile = new File(
				"src/test/resources/io/proleap/cobol/preprocessor/fixed/CommentEntryTriggerOutsideProgram.cbl");
		final String preProcessedInput = new CobolPreprocessorImpl().process(inputFile, params);

		final File expectedFile = new File(
				"src/test/resources/io/proleap/cobol/preprocessor/fixed/CommentEntryTriggerOutsideProgram.cbl.preprocessed");
		final String expected = Files.readString(expectedFile.toPath(), StandardCharsets.UTF_8);
		assertEquals(expected, preProcessedInput);
	}
}

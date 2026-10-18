package io.proleap.cobol.preprocessor.copy.copyreplace.separators;

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
 * Matching treats a separator comma or semicolon as a space and ignores comment and blank lines.
 */
public class ReplaceSeparatorsTest {

	private static final String DIR = "src/test/resources/io/proleap/cobol/preprocessor/copy/copyreplace/separators";

	@Test
	public void testSeparatorsAndCommentLinesDoNotTakePartInMatching() throws Exception {
		final CobolParserParams params = new CobolParserParamsImpl();
		params.setFormat(CobolSourceFormatEnum.FIXED);

		final String preProcessedInput = new CobolPreprocessorImpl().process(new File(DIR + "/Separators.cbl"),
				params);

		final File expectedFile = new File(DIR + "/Separators.cbl.preprocessed");
		assertEquals(Files.readString(expectedFile.toPath(), StandardCharsets.UTF_8), preProcessedInput);
	}
}

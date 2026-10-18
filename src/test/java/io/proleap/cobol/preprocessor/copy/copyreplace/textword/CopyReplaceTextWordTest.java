package io.proleap.cobol.preprocessor.copy.copyreplace.textword;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;

import org.junit.Test;

import io.proleap.cobol.asg.params.CobolParserParams;
import io.proleap.cobol.asg.params.impl.CobolParserParamsImpl;
import io.proleap.cobol.preprocessor.CobolPreprocessor.CobolSourceFormatEnum;
import io.proleap.cobol.preprocessor.impl.CobolPreprocessorImpl;

/**
 * A REPLACING operand matches whole text-words only: never part of a word or of a literal.
 */
public class CopyReplaceTextWordTest {

	private static final String DIR = "src/test/resources/io/proleap/cobol/preprocessor/copy/copyreplace/textword";

	@Test
	public void testOperandMatchesWholeTextWordsOnly() throws Exception {
		final CobolParserParams params = new CobolParserParamsImpl();
		params.setCopyBookDirectories(Arrays.asList(new File(DIR + "/copybooks")));
		params.setFormat(CobolSourceFormatEnum.FIXED);

		final String preProcessedInput = new CobolPreprocessorImpl().process(new File(DIR + "/TextWord.cbl"),
				params);

		final File expectedFile = new File(DIR + "/TextWord.cbl.preprocessed");
		assertEquals(Files.readString(expectedFile.toPath(), StandardCharsets.UTF_8), preProcessedInput);
	}
}

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
 * A CBL statement needs no sequence number, and when it carries none it starts
 * in column 1 -- or anywhere before column 8. Read as if columns 1 to 6 were a
 * sequence number and column 7 an indicator, the statement loses its first
 * seven characters, and what is left of it reaches the parser as if it were
 * COBOL.
 *
 * The comment area is the guard on how far that reaches: columns 73 to 80 are
 * still not part of the statement, so the sequence number some of these lines
 * carry there has to keep being dropped.
 */
public class CblNoSequenceAreaTest {

	@Test
	public void test() throws Exception {
		final CobolParserParams params = new CobolParserParamsImpl();
		params.setFormat(CobolSourceFormatEnum.FIXED);

		final File inputFile = new File("src/test/resources/io/proleap/cobol/preprocessor/fixed/CblNoSequenceArea.cbl");
		final String preProcessedInput = new CobolPreprocessorImpl().process(inputFile, params);

		final File expectedFile = new File(
				"src/test/resources/io/proleap/cobol/preprocessor/fixed/CblNoSequenceArea.cbl.preprocessed");
		final String expected = Files.readString(expectedFile.toPath(), StandardCharsets.UTF_8);
		assertEquals(expected, preProcessedInput);
	}
}

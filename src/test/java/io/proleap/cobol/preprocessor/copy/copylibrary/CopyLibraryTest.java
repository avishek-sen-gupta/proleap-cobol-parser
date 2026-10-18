package io.proleap.cobol.preprocessor.copy.copylibrary;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map;

import org.junit.Test;

import io.proleap.cobol.asg.params.CobolParserParams;
import io.proleap.cobol.asg.params.impl.CobolParserParamsImpl;
import io.proleap.cobol.preprocessor.CobolPreprocessor.CobolSourceFormatEnum;
import io.proleap.cobol.preprocessor.impl.CobolPreprocessorImpl;

/**
 * COPY text-name OF library-name copies the text from that library; an unqualified or unknown library reads the
 * copy book directories.
 */
public class CopyLibraryTest {

	private static final String DIR = "src/test/resources/io/proleap/cobol/preprocessor/copy/copylibrary";

	@Test
	public void testQualifiedTextNameIsCopiedFromItsLibrary() throws Exception {
		final CobolParserParams params = new CobolParserParamsImpl();
		params.setCopyBookDirectories(Arrays.asList(new File(DIR + "/copybooks")));
		params.setCopyBookLibraries(Map.of("LIBA", new File(DIR + "/liba"), "LIBB", new File(DIR + "/libb")));
		params.setFormat(CobolSourceFormatEnum.FIXED);

		final String preProcessedInput = new CobolPreprocessorImpl().process(new File(DIR + "/CopyLibrary.cbl"),
				params);

		final File expectedFile = new File(DIR + "/CopyLibrary.cbl.preprocessed");
		assertEquals(Files.readString(expectedFile.toPath(), StandardCharsets.UTF_8), preProcessedInput);
	}
}

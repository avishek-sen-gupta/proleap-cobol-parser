package io.proleap.cobol.ast.fixed;

import java.io.File;

import org.junit.Test;

import io.proleap.cobol.preprocessor.CobolPreprocessor.CobolSourceFormatEnum;
import io.proleap.cobol.runner.CobolParseTestRunner;
import io.proleap.cobol.runner.impl.CobolParseTestRunnerImpl;

/**
 * The parser half of the same fix: with the CBL statements read whole, the
 * preprocessor consumes them, and what reaches the parser is a program that
 * begins with its identification division.
 */
public class CblNoSequenceAreaTest {

	@Test
	public void test() throws Exception {
		final File inputFile = new File("src/test/resources/io/proleap/cobol/ast/fixed/CblNoSequenceArea.cbl");
		final CobolParseTestRunner runner = new CobolParseTestRunnerImpl();
		runner.parseFile(inputFile, CobolSourceFormatEnum.FIXED);
	}
}

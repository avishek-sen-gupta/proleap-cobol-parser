package io.proleap.cobol.ast.fixed;

import java.io.File;

import org.junit.Test;

import io.proleap.cobol.preprocessor.CobolPreprocessor.CobolSourceFormatEnum;
import io.proleap.cobol.runner.CobolParseTestRunner;
import io.proleap.cobol.runner.impl.CobolParseTestRunnerImpl;

/**
 * The parser half of the same fix: with the comment entry tagged, each of the
 * six paragraph rules still demanded the separator period the source does not
 * have, so DOT_FS is now optional there -- as it already is in
 * programIdParagraph, for the same reason (see ProgramIdNoDotTest).
 */
public class CommentEntryParagraphNoDotTest {

	@Test
	public void test() throws Exception {
		final File inputFile = new File("src/test/resources/io/proleap/cobol/ast/fixed/CommentEntryParagraphNoDot.cbl");
		final CobolParseTestRunner runner = new CobolParseTestRunnerImpl();
		runner.parseFile(inputFile, CobolSourceFormatEnum.FIXED);
	}
}

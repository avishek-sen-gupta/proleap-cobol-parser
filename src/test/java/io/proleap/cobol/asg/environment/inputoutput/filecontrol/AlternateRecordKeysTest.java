package io.proleap.cobol.asg.environment.inputoutput.filecontrol;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import io.proleap.cobol.CobolTestBase;
import io.proleap.cobol.asg.metamodel.Program;
import io.proleap.cobol.asg.metamodel.environment.inputoutput.filecontrol.AlternateRecordKeyClause;
import io.proleap.cobol.asg.metamodel.environment.inputoutput.filecontrol.FileControlEntry;
import io.proleap.cobol.asg.runner.impl.CobolParserRunnerImpl;
import io.proleap.cobol.preprocessor.CobolPreprocessor.CobolSourceFormatEnum;

/**
 * A file may declare several ALTERNATE RECORD KEY clauses, each with or without
 * DUPLICATES; every one is kept, in the order written.
 */
public class AlternateRecordKeysTest extends CobolTestBase {

	@Test
	public void test() throws Exception {
		final File inputFile = new File(
				"src/test/resources/io/proleap/cobol/asg/environment/inputoutput/filecontrol/AlternateRecordKeys.cbl");
		final Program program = new CobolParserRunnerImpl().analyzeFile(inputFile, CobolSourceFormatEnum.TANDEM);

		final FileControlEntry fileControlEntry = program.getCompilationUnit("AlternateRecordKeys").getProgramUnit()
				.getEnvironmentDivision().getInputOutputSection().getFileControlParagraph()
				.getFileControlEntry("TEACHER");
		final List<AlternateRecordKeyClause> clauses = fileControlEntry.getAlternateRecordKeyClauses();

		assertEquals(List.of("SOMEDATANAME2", "SOMEDATANAME3", "SOMEDATANAME4"),
				clauses.stream().map(clause -> clause.getDataCall().getName()).collect(Collectors.toList()));
		assertEquals(List.of(false, true, true),
				clauses.stream().map(AlternateRecordKeyClause::isDuplicates).collect(Collectors.toList()));
	}
}

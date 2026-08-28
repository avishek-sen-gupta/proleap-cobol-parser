package io.proleap.cobol.asg.data.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.junit.Test;

import io.proleap.cobol.CobolTestBase;
import io.proleap.cobol.asg.metamodel.CompilationUnit;
import io.proleap.cobol.asg.metamodel.Program;
import io.proleap.cobol.asg.metamodel.ProgramUnit;
import io.proleap.cobol.asg.metamodel.data.DataDivision;
import io.proleap.cobol.asg.metamodel.data.file.DataRecordsClause;
import io.proleap.cobol.asg.metamodel.data.file.FileDescriptionEntry;
import io.proleap.cobol.asg.metamodel.data.file.FileSection;
import io.proleap.cobol.asg.metamodel.data.file.LabelRecordsClause;
import io.proleap.cobol.asg.runner.impl.CobolParserRunnerImpl;
import io.proleap.cobol.preprocessor.CobolPreprocessor.CobolSourceFormatEnum;

/**
 * IS and ARE are noise words in LABEL RECORDS and DATA RECORDS: the standard
 * lets either follow either of RECORD and RECORDS, and compilers accept the
 * cross pairings. The grammar used to bind each noise word to one number --
 * RECORD IS, RECORDS ARE -- and so rejected LABEL RECORDS IS STANDARD and
 * LABEL RECORD ARE STANDARD, which real programs are full of.
 *
 * Asserting on the clause type rather than merely on the parse, because the
 * noise words sit between the number and the type in the rule, and moving them
 * out of the alternation must not cost the ASG the OMITTED / STANDARD /
 * DATA_NAMES distinction it reads off that context.
 */
public class FileDescriptionEntryRecordNoiseWordsTest extends CobolTestBase {

	@Test
	public void test() throws Exception {
		final File inputFile = new File(
				"src/test/resources/io/proleap/cobol/asg/data/file/FileDescriptionEntryRecordNoiseWords.cbl");
		final Program program = new CobolParserRunnerImpl().analyzeFile(inputFile, CobolSourceFormatEnum.TANDEM);

		final CompilationUnit compilationUnit = program.getCompilationUnit("FileDescriptionEntryRecordNoiseWords");
		final ProgramUnit programUnit = compilationUnit.getProgramUnit();
		final DataDivision dataDivision = programUnit.getDataDivision();
		final FileSection fileSection = dataDivision.getFileSection();

		{
			// LABEL RECORDS IS STANDARD, DATA RECORDS IS <name>
			final FileDescriptionEntry fileDescriptionEntry = fileSection.getFileDescriptionEntry("FILEONE");
			assertNotNull(fileDescriptionEntry);

			final LabelRecordsClause labelRecordsClause = fileDescriptionEntry.getLabelRecordsClause();
			assertNotNull(labelRecordsClause);
			assertEquals(LabelRecordsClause.LabelRecordsClauseType.STANDARD,
					labelRecordsClause.getLabelRecordsClauseType());

			final DataRecordsClause dataRecordsClause = fileDescriptionEntry.getDataRecordsClause();
			assertNotNull(dataRecordsClause);
			assertEquals(1, dataRecordsClause.getDataCalls().size());
		}

		{
			// LABEL RECORD ARE STANDARD, DATA RECORD ARE <name> <name>
			final FileDescriptionEntry fileDescriptionEntry = fileSection.getFileDescriptionEntry("FILETWO");
			assertNotNull(fileDescriptionEntry);

			final LabelRecordsClause labelRecordsClause = fileDescriptionEntry.getLabelRecordsClause();
			assertNotNull(labelRecordsClause);
			assertEquals(LabelRecordsClause.LabelRecordsClauseType.STANDARD,
					labelRecordsClause.getLabelRecordsClauseType());

			final DataRecordsClause dataRecordsClause = fileDescriptionEntry.getDataRecordsClause();
			assertNotNull(dataRecordsClause);
			assertEquals(2, dataRecordsClause.getDataCalls().size());
		}

		{
			// LABEL RECORDS IS OMITTED
			final FileDescriptionEntry fileDescriptionEntry = fileSection.getFileDescriptionEntry("FILETHREE");
			assertNotNull(fileDescriptionEntry);

			final LabelRecordsClause labelRecordsClause = fileDescriptionEntry.getLabelRecordsClause();
			assertNotNull(labelRecordsClause);
			assertEquals(LabelRecordsClause.LabelRecordsClauseType.OMITTED,
					labelRecordsClause.getLabelRecordsClauseType());
		}

		{
			// LABEL RECORD IS OMITTED -- the pairing that already parsed, kept
			// here so a fix to the others cannot quietly break it.
			final FileDescriptionEntry fileDescriptionEntry = fileSection.getFileDescriptionEntry("FILEFOUR");
			assertNotNull(fileDescriptionEntry);

			final LabelRecordsClause labelRecordsClause = fileDescriptionEntry.getLabelRecordsClause();
			assertNotNull(labelRecordsClause);
			assertEquals(LabelRecordsClause.LabelRecordsClauseType.OMITTED,
					labelRecordsClause.getLabelRecordsClauseType());
		}

		{
			// LABEL RECORDS STANDARD -- no noise word at all, also already valid.
			final FileDescriptionEntry fileDescriptionEntry = fileSection.getFileDescriptionEntry("FILEFIVE");
			assertNotNull(fileDescriptionEntry);

			final LabelRecordsClause labelRecordsClause = fileDescriptionEntry.getLabelRecordsClause();
			assertNotNull(labelRecordsClause);
			assertEquals(LabelRecordsClause.LabelRecordsClauseType.STANDARD,
					labelRecordsClause.getLabelRecordsClauseType());
		}
	}
}

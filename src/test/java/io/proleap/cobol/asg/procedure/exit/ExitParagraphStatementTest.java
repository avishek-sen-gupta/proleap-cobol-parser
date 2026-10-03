package io.proleap.cobol.asg.procedure.exit;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import io.proleap.cobol.CobolTestBase;
import io.proleap.cobol.asg.metamodel.CompilationUnit;
import io.proleap.cobol.asg.metamodel.Program;
import io.proleap.cobol.asg.metamodel.ProgramUnit;
import io.proleap.cobol.asg.metamodel.procedure.Paragraph;
import io.proleap.cobol.asg.metamodel.procedure.ProcedureDivision;
import io.proleap.cobol.asg.metamodel.procedure.Section;
import io.proleap.cobol.asg.metamodel.procedure.StatementTypeEnum;
import io.proleap.cobol.asg.metamodel.procedure.exit.ExitStatement;
import io.proleap.cobol.asg.metamodel.procedure.exit.ExitStatement.ExitStatementType;
import io.proleap.cobol.asg.metamodel.procedure.ifstmt.IfStatement;
import io.proleap.cobol.asg.metamodel.procedure.move.MoveStatement;
import io.proleap.cobol.asg.runner.impl.CobolParserRunnerImpl;
import io.proleap.cobol.preprocessor.CobolPreprocessor.CobolSourceFormatEnum;

public class ExitParagraphStatementTest extends CobolTestBase {

	@Test
	public void test() throws Exception {
		final File inputFile = new File(
				"src/test/resources/io/proleap/cobol/asg/procedure/exit/ExitParagraphStatement.cbl");
		final Program program = new CobolParserRunnerImpl().analyzeFile(inputFile, CobolSourceFormatEnum.TANDEM);

		final CompilationUnit compilationUnit = program.getCompilationUnit("ExitParagraphStatement");
		final ProgramUnit programUnit = compilationUnit.getProgramUnit();
		final ProcedureDivision procedureDivision = programUnit.getProcedureDivision();
		final Section section = procedureDivision.getSection("MAIN");
		final Paragraph first = section.getParagraph("P1");
		final Paragraph second = section.getParagraph("P2");

		{
			// EXIT PARAGRAPH, inside an IF
			final IfStatement ifStatement = (IfStatement) first.getStatements().get(0);
			final ExitStatement exitStatement = (ExitStatement) ifStatement.getThen().getStatements().get(0);
			assertEquals(StatementTypeEnum.EXIT, exitStatement.getStatementType());
			assertEquals(ExitStatementType.PARAGRAPH, exitStatement.getExitStatementType());
		}

		{
			// PARAGRAPH is still usable as a data name
			final MoveStatement moveStatement = (MoveStatement) first.getStatements().get(1);
			assertEquals(StatementTypeEnum.MOVE, moveStatement.getStatementType());
		}

		{
			// EXIT SECTION
			final ExitStatement exitStatement = (ExitStatement) second.getStatements().get(0);
			assertEquals(ExitStatementType.SECTION, exitStatement.getExitStatementType());
		}
	}
}

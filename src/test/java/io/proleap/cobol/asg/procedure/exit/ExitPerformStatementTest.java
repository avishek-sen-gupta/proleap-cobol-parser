package io.proleap.cobol.asg.procedure.exit;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import io.proleap.cobol.CobolTestBase;
import io.proleap.cobol.asg.metamodel.CompilationUnit;
import io.proleap.cobol.asg.metamodel.Program;
import io.proleap.cobol.asg.metamodel.ProgramUnit;
import io.proleap.cobol.asg.metamodel.procedure.ProcedureDivision;
import io.proleap.cobol.asg.metamodel.procedure.StatementTypeEnum;
import io.proleap.cobol.asg.metamodel.procedure.exit.ExitStatement;
import io.proleap.cobol.asg.metamodel.procedure.exit.ExitStatement.ExitStatementType;
import io.proleap.cobol.asg.metamodel.procedure.perform.PerformInlineStatement;
import io.proleap.cobol.asg.metamodel.procedure.perform.PerformStatement;
import io.proleap.cobol.asg.runner.impl.CobolParserRunnerImpl;
import io.proleap.cobol.preprocessor.CobolPreprocessor.CobolSourceFormatEnum;

/**
 * EXIT PERFORM and EXIT PERFORM CYCLE terminate, respectively continue an
 * inline PERFORM. Both forms were missing from the grammar, which accepted
 * EXIT PROGRAM only.
 */
public class ExitPerformStatementTest extends CobolTestBase {

	@Test
	public void test() throws Exception {
		final File inputFile = new File(
				"src/test/resources/io/proleap/cobol/asg/procedure/exit/ExitPerformStatement.cbl");
		final Program program = new CobolParserRunnerImpl().analyzeFile(inputFile, CobolSourceFormatEnum.TANDEM);

		final CompilationUnit compilationUnit = program.getCompilationUnit("ExitPerformStatement");
		final ProgramUnit programUnit = compilationUnit.getProgramUnit();
		final ProcedureDivision procedureDivision = programUnit.getProcedureDivision();
		assertEquals(3, procedureDivision.getStatements().size());

		{
			// EXIT PERFORM
			final PerformStatement performStatement = (PerformStatement) procedureDivision.getStatements().get(0);
			final PerformInlineStatement performInlineStatement = performStatement.getPerformInlineStatement();
			assertEquals(2, performInlineStatement.getStatements().size());

			final ExitStatement exitStatement = (ExitStatement) performInlineStatement.getStatements().get(1);
			assertEquals(StatementTypeEnum.EXIT, exitStatement.getStatementType());
			assertEquals(ExitStatementType.PERFORM, exitStatement.getExitStatementType());
		}

		{
			// EXIT PERFORM CYCLE
			final PerformStatement performStatement = (PerformStatement) procedureDivision.getStatements().get(1);
			final PerformInlineStatement performInlineStatement = performStatement.getPerformInlineStatement();
			assertEquals(2, performInlineStatement.getStatements().size());

			final ExitStatement exitStatement = (ExitStatement) performInlineStatement.getStatements().get(1);
			assertEquals(ExitStatementType.PERFORM_CYCLE, exitStatement.getExitStatementType());
		}

		{
			// EXIT PROGRAM
			final ExitStatement exitStatement = (ExitStatement) procedureDivision.getStatements().get(2);
			assertEquals(ExitStatementType.PROGRAM, exitStatement.getExitStatementType());
		}
	}
}

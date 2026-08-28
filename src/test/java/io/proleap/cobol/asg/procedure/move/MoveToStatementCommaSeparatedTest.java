package io.proleap.cobol.asg.procedure.move;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import io.proleap.cobol.CobolTestBase;
import io.proleap.cobol.asg.metamodel.CompilationUnit;
import io.proleap.cobol.asg.metamodel.Program;
import io.proleap.cobol.asg.metamodel.ProgramUnit;
import io.proleap.cobol.asg.metamodel.procedure.ProcedureDivision;
import io.proleap.cobol.asg.metamodel.procedure.move.MoveStatement;
import io.proleap.cobol.asg.metamodel.procedure.move.MoveToStatement;
import io.proleap.cobol.asg.runner.impl.CobolParserRunnerImpl;
import io.proleap.cobol.preprocessor.CobolPreprocessor.CobolSourceFormatEnum;

/**
 * A comma is a separator that may stand between the receiving areas of a MOVE.
 * The lexer hides the two-character separator ', ', so a comma written without
 * the space after it reaches the parser as a token of its own and used to be
 * rejected.
 */
public class MoveToStatementCommaSeparatedTest extends CobolTestBase {

	@Test
	public void test() throws Exception {
		final File inputFile = new File(
				"src/test/resources/io/proleap/cobol/asg/procedure/move/MoveToStatementCommaSeparated.cbl");
		final Program program = new CobolParserRunnerImpl().analyzeFile(inputFile, CobolSourceFormatEnum.TANDEM);

		final CompilationUnit compilationUnit = program.getCompilationUnit("MoveToStatementCommaSeparated");
		final ProgramUnit programUnit = compilationUnit.getProgramUnit();
		final ProcedureDivision procedureDivision = programUnit.getProcedureDivision();
		assertEquals(3, procedureDivision.getStatements().size());

		{
			// MOVE 'Y' TO SOMEFLAGONE,SOMEFLAGTWO,SOMEFLAGTHREE
			final MoveStatement moveStatement = (MoveStatement) procedureDivision.getStatements().get(0);
			final MoveToStatement moveToStatement = moveStatement.getMoveToStatement();
			assertEquals(3, moveToStatement.getReceivingAreaCalls().size());
			assertEquals("SOMEFLAGONE", moveToStatement.getReceivingAreaCalls().get(0).getName());
			assertEquals("SOMEFLAGTWO", moveToStatement.getReceivingAreaCalls().get(1).getName());
			assertEquals("SOMEFLAGTHREE", moveToStatement.getReceivingAreaCalls().get(2).getName());
		}

		{
			// MOVE 'N' TO SOMEFLAGONE, SOMEFLAGTWO
			final MoveStatement moveStatement = (MoveStatement) procedureDivision.getStatements().get(1);
			assertEquals(2, moveStatement.getMoveToStatement().getReceivingAreaCalls().size());
		}

		{
			// MOVE 'N' TO SOMEFLAGONE SOMEFLAGTWO
			final MoveStatement moveStatement = (MoveStatement) procedureDivision.getStatements().get(2);
			assertEquals(2, moveStatement.getMoveToStatement().getReceivingAreaCalls().size());
		}
	}
}

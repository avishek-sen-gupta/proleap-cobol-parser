package io.proleap.cobol.asg.procedure.call;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;

import org.junit.Test;

import io.proleap.cobol.CobolTestBase;
import io.proleap.cobol.asg.metamodel.CompilationUnit;
import io.proleap.cobol.asg.metamodel.Program;
import io.proleap.cobol.asg.metamodel.procedure.ProcedureDivision;
import io.proleap.cobol.asg.metamodel.procedure.StatementTypeEnum;
import io.proleap.cobol.asg.metamodel.procedure.call.CallStatement;
import io.proleap.cobol.asg.metamodel.procedure.display.DisplayStatement;
import io.proleap.cobol.asg.runner.impl.CobolParserRunnerImpl;
import io.proleap.cobol.preprocessor.CobolPreprocessor.CobolSourceFormatEnum;

public class CallStatementExceptionDisplayTest extends CobolTestBase {

	@Test
	public void test() throws Exception {
		final File inputFile = new File(
				"src/test/resources/io/proleap/cobol/asg/procedure/call/CallStatementExceptionDisplay.cbl");
		final Program program = new CobolParserRunnerImpl().analyzeFile(inputFile, CobolSourceFormatEnum.TANDEM);

		final CompilationUnit compilationUnit = program.getCompilationUnit("CallStatementExceptionDisplay");
		final ProcedureDivision procedureDivision = compilationUnit.getProgramUnit().getProcedureDivision();
		assertEquals(1, procedureDivision.getStatements().size());

		final CallStatement callStatement = (CallStatement) procedureDivision.getStatements().get(0);
		assertEquals(StatementTypeEnum.CALL, callStatement.getStatementType());
		assertEquals(1, callStatement.getOnExceptionClause().getStatements().size());
		assertEquals(2, callStatement.getNotOnExceptionClause().getStatements().size());

		final DisplayStatement inner = (DisplayStatement) callStatement.getOnExceptionClause().getStatements().get(0);
		assertNull(inner.getNotOnExceptionClause());
		assertEquals(StatementTypeEnum.DISPLAY,
				callStatement.getNotOnExceptionClause().getStatements().get(1).getStatementType());
	}
}

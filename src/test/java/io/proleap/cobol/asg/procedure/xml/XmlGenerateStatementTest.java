package io.proleap.cobol.asg.procedure.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;

import org.junit.Test;

import io.proleap.cobol.CobolTestBase;
import io.proleap.cobol.asg.metamodel.CompilationUnit;
import io.proleap.cobol.asg.metamodel.Program;
import io.proleap.cobol.asg.metamodel.ProgramUnit;
import io.proleap.cobol.asg.metamodel.call.Call.CallType;
import io.proleap.cobol.asg.metamodel.procedure.ProcedureDivision;
import io.proleap.cobol.asg.metamodel.procedure.StatementTypeEnum;
import io.proleap.cobol.asg.metamodel.procedure.xml.XmlGenerateStatement;
import io.proleap.cobol.asg.runner.impl.CobolParserRunnerImpl;
import io.proleap.cobol.preprocessor.CobolPreprocessor.CobolSourceFormatEnum;

/**
 * XML GENERATE converts a data item into an XML document. The statement was
 * missing from the grammar altogether, so a program using it was rejected at
 * the FROM of its first occurrence.
 */
public class XmlGenerateStatementTest extends CobolTestBase {

	@Test
	public void test() throws Exception {
		final File inputFile = new File(
				"src/test/resources/io/proleap/cobol/asg/procedure/xml/XmlGenerateStatement.cbl");
		final Program program = new CobolParserRunnerImpl().analyzeFile(inputFile, CobolSourceFormatEnum.TANDEM);

		final CompilationUnit compilationUnit = program.getCompilationUnit("XmlGenerateStatement");
		final ProgramUnit programUnit = compilationUnit.getProgramUnit();
		final ProcedureDivision procedureDivision = programUnit.getProcedureDivision();
		assertEquals(2, procedureDivision.getStatements().size());

		{
			// XML GENERATE SOMEXMLDOC FROM SOMERECORD
			final XmlGenerateStatement xmlGenerateStatement = (XmlGenerateStatement) procedureDivision.getStatements()
					.get(0);
			assertEquals(StatementTypeEnum.XML_GENERATE, xmlGenerateStatement.getStatementType());

			assertNotNull(xmlGenerateStatement.getReceiverCall());
			assertEquals(CallType.DATA_DESCRIPTION_ENTRY_CALL, xmlGenerateStatement.getReceiverCall().getCallType());
			assertEquals("SOMEXMLDOC", xmlGenerateStatement.getReceiverCall().getName());

			assertNotNull(xmlGenerateStatement.getFromCall());
			assertEquals(CallType.DATA_DESCRIPTION_ENTRY_CALL, xmlGenerateStatement.getFromCall().getCallType());
			assertEquals("SOMERECORD", xmlGenerateStatement.getFromCall().getName());

			assertNull(xmlGenerateStatement.getCountCall());
			assertNull(xmlGenerateStatement.getOnExceptionClause());
			assertNull(xmlGenerateStatement.getNotOnExceptionClause());
		}

		{
			// COUNT IN, ON EXCEPTION, NOT ON EXCEPTION, END-XML
			final XmlGenerateStatement xmlGenerateStatement = (XmlGenerateStatement) procedureDivision.getStatements()
					.get(1);
			assertEquals(StatementTypeEnum.XML_GENERATE, xmlGenerateStatement.getStatementType());

			assertNotNull(xmlGenerateStatement.getCountCall());
			assertEquals("SOMEXMLLENGTH", xmlGenerateStatement.getCountCall().getName());

			assertNotNull(xmlGenerateStatement.getOnExceptionClause());
			assertEquals(1, xmlGenerateStatement.getOnExceptionClause().getStatements().size());

			assertNotNull(xmlGenerateStatement.getNotOnExceptionClause());
			assertEquals(1, xmlGenerateStatement.getNotOnExceptionClause().getStatements().size());
		}
	}
}

package io.proleap.cobol.asg.environment.specialnames;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.stream.Collectors;

import org.junit.Test;

import io.proleap.cobol.CobolTestBase;
import io.proleap.cobol.asg.metamodel.CompilationUnit;
import io.proleap.cobol.asg.metamodel.Program;
import io.proleap.cobol.asg.metamodel.ProgramUnit;
import io.proleap.cobol.asg.metamodel.environment.specialnames.SpecialNamesParagraph;
import io.proleap.cobol.asg.metamodel.procedure.use.AfterOn;
import io.proleap.cobol.asg.metamodel.procedure.use.UseAfterStatement;
import io.proleap.cobol.asg.runner.impl.CobolParserRunnerImpl;
import io.proleap.cobol.preprocessor.CobolPreprocessor.CobolSourceFormatEnum;

/** Every CLASS clause is kept, and USE AFTER ... ON EXTEND has its type. */
public class ClassClausesUseExtendTest extends CobolTestBase {

	@Test
	public void test() throws Exception {
		final File inputFile = new File(
				"src/test/resources/io/proleap/cobol/asg/environment/specialnames/ClassClausesUseExtend.cbl");
		final Program program = new CobolParserRunnerImpl().analyzeFile(inputFile, CobolSourceFormatEnum.TANDEM);
		final CompilationUnit compilationUnit = program.getCompilationUnit("ClassClausesUseExtend");
		final ProgramUnit programUnit = compilationUnit.getProgramUnit();

		final SpecialNamesParagraph specialNames = programUnit.getEnvironmentDivision().getSpecialNamesParagraph();
		assertEquals("VOWEL DIGIT", specialNames.getClassClauses().stream()
				.map(c -> c.getClassCall().getName()).collect(Collectors.joining(" ")));

		final UseAfterStatement use = programUnit.getProcedureDivision().getDeclaratives().getDeclaratives().get(0)
				.getUseStament().getUseAfterStatement();
		assertEquals(AfterOn.AfterOnType.EXTEND, use.getAfterOn().getAfterOnType());
	}
}

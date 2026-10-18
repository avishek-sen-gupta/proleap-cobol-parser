package io.proleap.cobol.asg;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import io.proleap.cobol.CobolTestBase;
import io.proleap.cobol.asg.metamodel.CompilationUnit;
import io.proleap.cobol.asg.metamodel.Program;
import io.proleap.cobol.asg.metamodel.ProgramUnit;
import io.proleap.cobol.asg.metamodel.identification.ProgramIdParagraph.Attribute;
import io.proleap.cobol.asg.runner.impl.CobolParserRunnerImpl;
import io.proleap.cobol.preprocessor.CobolPreprocessor.CobolSourceFormatEnum;

/**
 * A compilation unit holding programs in sequence (batch) and programs contained in
 * other programs (nested, ended by END PROGRAM): the tree of program units.
 */
public class ProgramUnitTreeTest extends CobolTestBase {

	private static List<String> names(final List<ProgramUnit> units) {
		return units.stream().map(unit -> unit.getIdentificationDivision().getProgramIdParagraph().getName())
				.collect(Collectors.toList());
	}

	private static ProgramUnit named(final List<ProgramUnit> units, final String name) {
		return units.stream()
				.filter(unit -> unit.getIdentificationDivision().getProgramIdParagraph().getName().equals(name))
				.findFirst().orElseThrow();
	}

	@Test
	public void test() throws Exception {
		final File inputFile = new File("src/test/resources/io/proleap/cobol/asg/ProgramUnitTree.cbl");
		final Program program = new CobolParserRunnerImpl().analyzeFile(inputFile, CobolSourceFormatEnum.TANDEM);
		final CompilationUnit compilationUnit = program.getCompilationUnit("ProgramUnitTree");

		final List<ProgramUnit> topLevel = compilationUnit.getTopLevelProgramUnits();
		final ProgramUnit outer = named(topLevel, "OUTER");
		final ProgramUnit middle = named(outer.getContainedProgramUnits(), "MIDDLE");
		final ProgramUnit inner = named(middle.getContainedProgramUnits(), "INNER");
		final ProgramUnit sibling = named(outer.getContainedProgramUnits(), "SIBLING");
		final ProgramUnit batched = named(topLevel, "BATCHED");

		assertEquals(List.of("OUTER", "BATCHED"), names(topLevel));
		assertEquals(List.of("MIDDLE", "SIBLING"), names(outer.getContainedProgramUnits()));
		assertEquals(List.of("INNER"), names(middle.getContainedProgramUnits()));
		assertEquals(List.of(), names(inner.getContainedProgramUnits()));
		assertEquals(List.of(), names(sibling.getContainedProgramUnits()));
		assertEquals(List.of(), names(batched.getContainedProgramUnits()));
		assertEquals(List.of("OUTER", "MIDDLE", "INNER", "SIBLING", "BATCHED"),
				names(compilationUnit.getProgramUnits()));
		assertEquals(outer, compilationUnit.getProgramUnit());
		assertEquals(Attribute.COMMON, middle.getIdentificationDivision().getProgramIdParagraph().getAttribute());
		assertEquals(Attribute.INITIAL, inner.getIdentificationDivision().getProgramIdParagraph().getAttribute());
		assertEquals(null, outer.getIdentificationDivision().getProgramIdParagraph().getAttribute());
	}
}

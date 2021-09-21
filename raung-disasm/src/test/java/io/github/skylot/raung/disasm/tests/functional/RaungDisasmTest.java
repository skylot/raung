package io.github.skylot.raung.disasm.tests.functional;

import java.nio.file.Paths;
import java.util.Collections;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import io.github.skylot.raung.disasm.RaungDisasm;

class RaungDisasmTest {

	@Test
	@Disabled
	public void testUsage() {
		RaungDisasm.create()
				.inputs(Collections.emptyList())
				.output(null)
				.ignoreDebugInfo()
				.execute();
	}

	@Test
	@Disabled
	public void testUsageSingle() {
		RaungDisasm.create()
				.input(Paths.get("inputDir"))
				.output(Paths.get("outputDir"))
				.execute();
	}
}

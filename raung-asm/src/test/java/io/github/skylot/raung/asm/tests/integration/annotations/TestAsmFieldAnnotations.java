package io.github.skylot.raung.asm.tests.integration.annotations;

import org.junit.jupiter.api.Test;

import io.github.skylot.raung.asm.tests.api.IntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class TestAsmFieldAnnotations extends IntegrationTest {

	@Test
	public void test() {
		assertThat(asmCls())
				.containsOnlyOnce("public I field1")
				.containsOnlyOnce(
						"@Lio/github/skylot/raung/disasm/tests/integration/TestFieldAnnotations$TestCls$A;(value=2, s=\"A\") // invisible");
	}
}

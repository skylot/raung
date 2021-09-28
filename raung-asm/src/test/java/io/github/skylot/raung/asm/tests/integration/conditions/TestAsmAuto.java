package io.github.skylot.raung.asm.tests.integration.conditions;

import org.junit.jupiter.api.Test;

import io.github.skylot.raung.asm.tests.api.IntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class TestAsmAuto extends IntegrationTest {

	@Test
	public void test() {
		assertThat(asmCls())
				.containsOnlyOnce("FRAME SAME1 I");
	}
}

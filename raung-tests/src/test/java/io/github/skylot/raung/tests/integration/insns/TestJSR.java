package io.github.skylot.raung.tests.integration.insns;

import org.junit.jupiter.api.Test;

import io.github.skylot.raung.tests.api.IntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class TestJSR extends IntegrationTest {

	@Test
	public void test() {
		assertThat(runChecksForRaung())
				.contains("jsr :L3", "ret 5");
	}
}

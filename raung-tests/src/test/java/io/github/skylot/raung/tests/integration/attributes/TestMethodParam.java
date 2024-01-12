package io.github.skylot.raung.tests.integration.attributes;

import org.junit.jupiter.api.Test;

import io.github.skylot.raung.tests.api.IntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class TestMethodParam extends IntegrationTest {

	private static class TestCls {
		private String docName;

		public void setDocName(final String docName) {
			this.docName = docName;
		}
	}

	@Test
	public void test() {
		runChecksFor(TestCls.class);
	}

	@Test
	public void testAsm() {
		assertThat(runChecksForRaung())
				.containsOnlyOnce(".param final docName");
	}
}

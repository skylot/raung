package io.github.skylot.raung.tests.integration;

import org.junit.jupiter.api.Test;

import io.github.skylot.raung.tests.api.IntegrationTest;

public class TestParser extends IntegrationTest {

	public static class TestCls {
		// field name can be read as access flag
		public final int constructor = 1;
	}

	@Test
	public void test() {
		runChecksFor(TestCls.class);
	}
}

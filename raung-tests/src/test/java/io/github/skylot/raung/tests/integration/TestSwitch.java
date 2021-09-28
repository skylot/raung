package io.github.skylot.raung.tests.integration;

import org.junit.jupiter.api.Test;

import io.github.skylot.raung.tests.api.IntegrationTest;

public class TestSwitch extends IntegrationTest {

	public static class TestCls {

		public int test(int i) {
			switch (i) {
				case 1:
					return 1;
				case 7:
					return 7;
				default:
					return 42;
			}
		}
	}

	@Test
	public void test() {
		runChecksFor(TestCls.class);
	}
}

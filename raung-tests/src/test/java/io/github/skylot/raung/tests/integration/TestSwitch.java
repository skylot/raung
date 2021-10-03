package io.github.skylot.raung.tests.integration;

import org.junit.jupiter.api.Test;

import io.github.skylot.raung.tests.api.IntegrationTest;

public class TestSwitch extends IntegrationTest {

	public static class TestCls {

		public int testLookupSwitch(int i) {
			switch (i) {
				case 1:
					return 1;
				case 7:
					return 7;
				case 11:
					return 11;
				default:
					return 42;
			}
		}

		public int testTableSwitch(int i) {
			switch (i) {
				case 1:
					return 0;
				case 2:
					return 1;
				case 3:
					return 2;
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

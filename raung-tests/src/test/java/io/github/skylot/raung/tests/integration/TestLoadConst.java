package io.github.skylot.raung.tests.integration;

import org.junit.jupiter.api.Test;

import io.github.skylot.raung.tests.api.IntegrationTest;

public class TestLoadConst extends IntegrationTest {

	public static class TestCls {
		public static Class<?>[] testTypes() {
			return new Class[] { Integer.class, Float[].class };
		}

		@SuppressWarnings("UnnecessaryBoxing")
		public static String testSimpleTypes() {
			return Float.valueOf(3.0f) + " " + Double.valueOf(7.0)
					+ Byte.valueOf((byte) 11) + Short.valueOf((short) 17)
					+ Character.valueOf((char) 21) + '1';
		}

		public static double[] testEdgeCasesDouble() {
			return new double[] {
					Double.NaN,
					Double.MAX_VALUE,
					Double.MAX_VALUE,
					Double.POSITIVE_INFINITY,
					Double.NEGATIVE_INFINITY,
			};
		}
	}

	@Test
	public void test() {
		runChecksFor(TestCls.class);
	}
}

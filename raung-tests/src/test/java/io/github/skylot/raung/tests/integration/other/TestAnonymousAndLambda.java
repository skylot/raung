package io.github.skylot.raung.tests.integration.other;

import org.junit.jupiter.api.Test;

import io.github.skylot.raung.tests.api.IntegrationTest;

public class TestAnonymousAndLambda extends IntegrationTest {

	public static class TestCls {
		@SuppressWarnings("Convert2Lambda")
		public Runnable testAnonymous() {
			return new Runnable() {
				@Override
				public void run() {
					System.out.println("run");
				}
			};
		}

		public Runnable testLambda() {
			return () -> System.out.println("run");
		}
	}

	@Test
	public void test() {
		runChecksWithInnerClasses(TestAnonymousAndLambda.class);
	}
}

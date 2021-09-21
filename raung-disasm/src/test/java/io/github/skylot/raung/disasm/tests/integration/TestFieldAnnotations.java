package io.github.skylot.raung.disasm.tests.integration;

import org.junit.jupiter.api.Test;

import io.github.skylot.raung.disasm.tests.api.IntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class TestFieldAnnotations extends IntegrationTest {

	public static class TestCls {
		@A(value = { 2, 3 }, s = "A", b = @B(value = E.E2, arr = { E.E1 }))
		public int field1 = 7;

		public static @interface A {
			String s();

			int[] value();

			B b();
		}

		public static @interface B {
			E value();

			E[] arr() default {};
		}

		public static enum E {
			E1, E2
		}
	}

	@Test
	public void test() {
		assertThat(disasmCls(TestCls.class))
				.containsOnlyOnce(".field public field1 I")
				.containsOnlyOnce(".end field");
	}
}

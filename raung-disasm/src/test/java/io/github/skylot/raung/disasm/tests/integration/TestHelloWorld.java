package io.github.skylot.raung.disasm.tests.integration;

import org.junit.jupiter.api.Test;

import io.github.skylot.raung.disasm.tests.api.IntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class TestHelloWorld extends IntegrationTest {

	public static class TestCls {
		public static void main(String[] args) {
			System.out.println("Hello, World!");
		}
	}

	@Test
	public void test() {
		assertThat(disasmCls(TestCls.class))
				.containsOnlyOnce("getstatic java/lang/System out Ljava/io/PrintStream;")
				.containsOnlyOnce("ldc \"Hello, World!\"")
				.containsOnlyOnce("invokevirtual java/io/PrintStream println (Ljava/lang/String;)V");
	}
}

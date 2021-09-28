package io.github.skylot.raung.asm.tests.integration.other;

import org.junit.jupiter.api.Test;

import io.github.skylot.raung.asm.tests.api.IntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class TestAsmHelloWorld extends IntegrationTest {

	@Test
	public void test() {
		assertThat(asmCls())
				.containsOnlyOnce("GETSTATIC java/lang/System.out : Ljava/io/PrintStream;")
				.containsOnlyOnce("LDC \"Hello, World!\"")
				.containsOnlyOnce("INVOKEVIRTUAL java/io/PrintStream.println (Ljava/lang/String;)V");
	}
}

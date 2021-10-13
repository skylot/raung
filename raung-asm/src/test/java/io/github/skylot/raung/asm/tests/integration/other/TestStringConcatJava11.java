package io.github.skylot.raung.asm.tests.integration.other;

import org.junit.jupiter.api.Test;

import io.github.skylot.raung.asm.tests.api.IntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class TestStringConcatJava11 extends IntegrationTest {

	@Test
	public void test() {
		assertThat(asmCls())
				.containsOnlyOnce("INVOKEDYNAMIC makeConcatWithConstants(Ljava/lang/String;)Ljava/lang/String;")
				.containsOnlyOnce("java/lang/invoke/StringConcatFactory.makeConcatWithConstants")
				.containsOnlyOnce("\"test\"");
	}
}

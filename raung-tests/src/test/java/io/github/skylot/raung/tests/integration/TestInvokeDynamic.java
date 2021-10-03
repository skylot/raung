package io.github.skylot.raung.tests.integration;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import io.github.skylot.raung.tests.api.IntegrationTest;

public class TestInvokeDynamic extends IntegrationTest {

	public static class TestCls {
		public String test(Stream<String> strings) {
			return strings.map(String::trim).collect(Collectors.joining());
		}
	}

	@Test
	public void test() {
		runChecksFor(TestCls.class);
	}
}

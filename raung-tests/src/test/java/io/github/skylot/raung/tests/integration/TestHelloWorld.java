package io.github.skylot.raung.tests.integration;

import org.junit.jupiter.api.Test;

import io.github.skylot.raung.tests.api.IntegrationTest;

public class TestHelloWorld extends IntegrationTest {

	public static class TestCls {
		public static void main(String[] args) {
			System.out.println("Hello, World!");
		}
	}

	@Test
	public void test() {
		runChecksFor(TestCls.class);
	}
}

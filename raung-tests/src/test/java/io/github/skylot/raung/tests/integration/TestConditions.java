package io.github.skylot.raung.tests.integration;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import io.github.skylot.raung.tests.api.IntegrationTest;

public class TestConditions extends IntegrationTest {

	public static class TestCls {
		private Set<Object> firstSet = new HashSet<>();
		private Set<Object> secondSet = new HashSet<>();

		public boolean contains(Object o) {
			if (this.firstSet.contains(o)) {
				return true;
			}
			if (this.secondSet.contains(o)) {
				return true;
			}
			return false;
		}
	}

	@Test
	public void test() {
		runChecksFor(TestCls.class);
	}
}

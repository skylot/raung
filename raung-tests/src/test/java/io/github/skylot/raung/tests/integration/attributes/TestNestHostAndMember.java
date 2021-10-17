package io.github.skylot.raung.tests.integration.attributes;

import org.junit.jupiter.api.Test;

import io.github.skylot.raung.tests.api.IntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class TestNestHostAndMember extends IntegrationTest {

	@Test
	public void testHost() {
		assertThat(runChecksForRaung("TestNestHost"))
				.containsOnlyOnce(".nest host");
	}

	@Test
	public void testMember() {
		assertThat(runChecksForRaung("TestNestMember"))
				.containsOnlyOnce(".nest member");
	}
}

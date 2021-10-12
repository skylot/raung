package io.github.skylot.raung.tests.integration;

import org.junit.jupiter.api.Test;

import io.github.skylot.raung.tests.api.IntegrationTest;

@SuppressWarnings("AvoidEscapedUnicodeCharacters")
public class TestStringEscape extends IntegrationTest {
	public static class TestCls {
		static final String FORMAT = "\n\r\t\b\f\\\"' ";
		static final String EMOJI = "😀😂👍\uD83C\uDF5E\uD83C\uDF54\uD83C\uDF69\uD83C\uDF6B\uD83C\uDF61\uD83C\uDF50";
		static final String RANDOM = "❶⚙ⴗ░⢈⛂⋰Ⰴ⢂⪞⅗⢋❤⺾ⓓ\u209E⏮☎❍\u2E57⬸ⶭ⬶†⧘\u2EFCⵓⱞ\u2FDC⎋ⵠ⟂⋳⾆ⲛ≻◴⩨⪨\u2BDF❭⚥⩜┼"
				+ "\u2BD3⛑⊼Ⓢ↩Ⱜ⭿⬘⪙\u2B97⬒\u2BD7☚\u2E69⩤⨲Ⰺ⌘⻖Ⱋ☕\u2E59ℌ\u2E53⛵⟙⎕⅏☞⺨◕⅀✀⑁⬄ⷊ⡶⓯◫◻⩊⦹⌧⡋Ⲯ∸ ↟⸉■⚲ⰹ↞⤄⫵⣰";
	}

	@Test
	public void test() {
		runChecksFor(TestCls.class);
	}
}

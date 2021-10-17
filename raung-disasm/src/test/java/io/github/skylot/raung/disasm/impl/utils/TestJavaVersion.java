package io.github.skylot.raung.disasm.impl.utils;

import org.junit.jupiter.api.Test;

import static io.github.skylot.raung.disasm.impl.utils.JavaVersion.JAVA_17;
import static io.github.skylot.raung.disasm.impl.utils.JavaVersion.JAVA_18;
import static io.github.skylot.raung.disasm.impl.utils.JavaVersion.JAVA_1_1;
import static io.github.skylot.raung.disasm.impl.utils.JavaVersion.JAVA_8;
import static io.github.skylot.raung.disasm.impl.utils.JavaVersion.PREVIEW_VERSION;
import static io.github.skylot.raung.disasm.impl.utils.JavaVersion.getNameStr;
import static org.assertj.core.api.Assertions.assertThat;

class TestJavaVersion {

	@Test
	public void various() {
		assertThat(getNameStr(JAVA_1_1.getRawVersion())).isEqualTo("Java 1.1");
		assertThat(getNameStr(JAVA_8.getRawVersion())).isEqualTo("Java 8");
		assertThat(getNameStr(JAVA_17.getRawVersion() | PREVIEW_VERSION)).isEqualTo("Java 17 preview");
		assertThat(getNameStr(JAVA_18.getRawVersion() + 1)).isEqualTo("Java 19 (approximated)");
		assertThat(getNameStr((JAVA_18.getRawVersion() + 2) | PREVIEW_VERSION)).isEqualTo("Java 20 (approximated) preview");

		assertThat(JAVA_1_1.getMajor()).isEqualTo(45);
		assertThat(JAVA_1_1.getMinor()).isEqualTo(3);
	}
}

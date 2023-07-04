package io.github.skylot.raung.disasm.impl.utils;

import org.junit.jupiter.api.Test;

import io.github.skylot.raung.common.utils.JavaVersion;

import static io.github.skylot.raung.common.utils.JavaVersion.JAVA_17;
import static io.github.skylot.raung.common.utils.JavaVersion.JAVA_1_1;
import static io.github.skylot.raung.common.utils.JavaVersion.JAVA_21;
import static io.github.skylot.raung.common.utils.JavaVersion.JAVA_8;
import static io.github.skylot.raung.common.utils.JavaVersion.PREVIEW_VERSION;
import static io.github.skylot.raung.common.utils.JavaVersion.getNameStr;
import static org.assertj.core.api.Assertions.assertThat;

class TestJavaVersion {

	@Test
	public void various() {
		assertThat(getNameStr(JAVA_1_1.getRawVersion())).isEqualTo("Java 1.1");
		assertThat(getNameStr(JAVA_8.getRawVersion())).isEqualTo("Java 8");
		assertThat(getNameStr(JAVA_17.getRawVersion() | PREVIEW_VERSION)).isEqualTo("Java 17 preview");
		assertThat(getNameStr(JAVA_21.getRawVersion() + 1)).isEqualTo("Java 22 (approximated)");
		assertThat(getNameStr((JAVA_21.getRawVersion() + 2) | PREVIEW_VERSION)).isEqualTo("Java 23 (approximated) preview");

		assertThat(JAVA_1_1.getMajor()).isEqualTo(45);
		assertThat(JAVA_1_1.getMinor()).isEqualTo(3);

		assertThat(getNameStr(JavaVersion.getRaw(JAVA_17.getMajor(), 1))).isEqualTo("Java 17 (with unknown minor: 1)");
	}
}

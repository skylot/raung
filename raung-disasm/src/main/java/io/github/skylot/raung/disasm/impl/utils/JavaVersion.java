package io.github.skylot.raung.disasm.impl.utils;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

public enum JavaVersion {
	JAVA_1_0_2(45, "1.0.2"),
	JAVA_1_1(3 << 16 | 45, "1.1"),
	JAVA_1_2(46, "1.2"),
	JAVA_1_3(47, "1.3"),
	JAVA_1_4(48, "1.4"),
	JAVA_1_5(49, "5.0"),
	JAVA_6(50, "6"),
	JAVA_7(51, "7"),
	JAVA_8(52, "8"),
	JAVA_9(53, "9"),
	JAVA_10(54, "10"),
	JAVA_11(55, "11"),
	JAVA_12(56, "12"),
	JAVA_13(57, "13"),
	JAVA_14(58, "14"),
	JAVA_15(59, "15"),
	JAVA_16(60, "16"),
	JAVA_17(61, "17"),
	JAVA_18(62, "18");

	private final int rawVersion;
	private final String name;

	JavaVersion(int rawVersion, String name) {
		this.rawVersion = rawVersion;
		this.name = name;
	}

	public int getRawVersion() {
		return rawVersion;
	}

	public String getName() {
		return name;
	}

	public int getMajor() {
		return rawVersion & 0xFFFF;
	}

	public int getMinor() {
		return (rawVersion & 0xFFFF0000) >> 16;
	}

	private static final Map<Integer, JavaVersion> MAP;

	static {
		JavaVersion[] values = values();
		Map<Integer, JavaVersion> map = new HashMap<>(values.length);
		for (JavaVersion value : values) {
			map.put(value.rawVersion, value);
		}
		MAP = map;
	}

	public static final int PREVIEW_VERSION = 0xFFFF0000;

	public static boolean isPreview(int rawVersion) {
		return (rawVersion & PREVIEW_VERSION) == PREVIEW_VERSION;
	}

	public static String getNameStr(int rawVersion) {
		StringBuilder sb = new StringBuilder("Java ");
		boolean preview = isPreview(rawVersion);
		int searchVersion = preview ? rawVersion & 0xFFFF : rawVersion;
		JavaVersion version = MAP.get(searchVersion);
		if (version != null) {
			sb.append(version.name);
		} else {
			// try approximation :)
			int verDiff = searchVersion - JAVA_18.rawVersion;
			if (verDiff > 0) {
				sb.append(18 + verDiff).append(" (approximated)");
			} else {
				sb.append("unknown version");
			}
		}
		if (preview) {
			sb.append(" preview");
		}
		return sb.toString();
	}

	@Nullable
	public static JavaVersion get(int rawVersion) {
		return MAP.get(rawVersion);
	}
}

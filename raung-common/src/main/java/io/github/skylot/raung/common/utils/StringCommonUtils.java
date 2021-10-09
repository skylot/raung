package io.github.skylot.raung.common.utils;

public class StringCommonUtils {

	public static String repeat(char ch, int count) {
		if (count <= 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder(count);
		for (int i = 0; i < count; i++) {
			sb.append(ch);
		}
		return sb.toString();
	}
}

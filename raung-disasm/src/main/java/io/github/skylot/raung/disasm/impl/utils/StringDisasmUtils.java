package io.github.skylot.raung.disasm.impl.utils;

import org.jetbrains.annotations.Nullable;

public class StringDisasmUtils {

	public static String escapeString(String str) {
		int len = str.length();
		if (len == 0) {
			return "\"\"";
		}
		StringBuilder sb = new StringBuilder();
		sb.append('"');
		int offset = 0;
		while (offset < len) {
			int codePoint = str.codePointAt(offset);
			processCharInsideString(codePoint, sb);
			offset += Character.charCount(codePoint);
		}
		sb.append('"');
		return sb.toString();
	}

	private static void processCharInsideString(int codePoint, StringBuilder sb) {
		String str = getSpecialStringForChar(codePoint);
		if (str != null) {
			sb.append(str);
			return;
		}
		if (isPrintable(codePoint)) {
			sb.appendCodePoint(codePoint);
		} else {
			sb.append("\\u").append(String.format("%04x", codePoint));
		}
	}

	private static boolean isPrintable(int codePoint) {
		if (Character.isISOControl(codePoint)) {
			return false;
		}
		if (Character.isWhitespace(codePoint)) {
			// don't print whitespaces other than standard one
			return codePoint == ' ';
		}
		switch (Character.getType(codePoint)) {
			case Character.CONTROL:
			case Character.FORMAT:
			case Character.PRIVATE_USE:
			case Character.SURROGATE:
			case Character.UNASSIGNED:
				return false;
		}
		return true;
	}

	@Nullable
	private static String getSpecialStringForChar(int c) {
		switch (c) {
			case '\n':
				return "\\n";
			case '\r':
				return "\\r";
			case '\t':
				return "\\t";
			case '\b':
				return "\\b";
			case '\f':
				return "\\f";
			case '\'':
				return "'";
			case '"':
				return "\\\"";
			case '\\':
				return "\\\\";

			default:
				return null;
		}
	}
}

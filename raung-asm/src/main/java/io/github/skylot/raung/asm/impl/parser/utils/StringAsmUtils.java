package io.github.skylot.raung.asm.impl.parser.utils;

import io.github.skylot.raung.asm.impl.utils.RaungAsmTokenException;

public class StringAsmUtils {

	public static String unescapeString(String str) {
		int len = str.length();
		if (len == 0) {
			return "";
		}
		if (str.indexOf('\\') == -1) {
			return str;
		}
		StringBuilder sb = new StringBuilder();
		boolean escape = false;
		int offset = 0;
		while (offset < len) {
			int codePoint = str.codePointAt(offset);
			if (escape) {
				escape = false;
				if (codePoint == 'u') {
					offset += processUnicodeChar(str, offset, len, sb);
				} else {
					char c = processSingleChar(codePoint);
					if (c == 0) {
						String ch = new StringBuilder().appendCodePoint(codePoint).toString();
						throw new RaungAsmTokenException("Unexpected escape for char: " + ch, offset + 1);
					}
					sb.append(c);
					offset++;
				}
			} else {
				if (codePoint == '\\') {
					escape = true;
					offset++;
				} else {
					sb.appendCodePoint(codePoint);
					offset += Character.charCount(codePoint);
				}
			}
		}
		return sb.toString();
	}

	private static char processSingleChar(int codePoint) {
		switch (codePoint) {
			case 'n':
				return '\n';
			case 'r':
				return '\r';
			case 't':
				return '\t';
			case 'b':
				return '\b';
			case 'f':
				return '\f';
			case '\'':
				return '\'';
			case '\"':
				return '\"';
			case '\\':
				return '\\';
			default:
				return 0;
		}
	}

	private static int processUnicodeChar(String str, int offset, int len, StringBuilder sb) {
		int start = offset + 1;
		if (start + 4 > len) {
			throw new RaungAsmTokenException("Truncated unicode sequence", start);
		}
		String unicodeIdStr = str.substring(start, start + 4);
		try {
			int unicodeCodePoint = Integer.parseInt(unicodeIdStr, 16);
			sb.appendCodePoint(unicodeCodePoint);
			return 5;
		} catch (NumberFormatException e) {
			throw new RaungAsmTokenException("Malformed unicode number: " + unicodeIdStr, start + 1);
		}
	}
}

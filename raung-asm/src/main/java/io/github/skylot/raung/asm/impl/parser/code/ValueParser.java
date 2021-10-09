package io.github.skylot.raung.asm.impl.parser.code;

import org.objectweb.asm.Type;

import io.github.skylot.raung.asm.impl.parser.utils.StringAsmUtils;
import io.github.skylot.raung.asm.impl.utils.RaungAsmException;

public class ValueParser {

	public static Object process(String token) {
		char firstChar = token.charAt(0);
		if (firstChar == '"') {
			// String
			return StringAsmUtils.unescapeString(token.substring(1, token.length() - 1));
		}
		if (firstChar == 'L') {
			// Object
			return Type.getObjectType(token.substring(1, token.length() - 1));
		}
		if (firstChar == '-' || firstChar == '+' || Character.isDigit(firstChar)) {
			if (token.endsWith("L")) {
				return Long.parseLong(token.substring(0, token.length() - 1));
			}
			if (token.endsWith("f")) {
				return Float.parseFloat(token.substring(0, token.length() - 1));
			}
			if (token.contains(".")) {
				return Double.parseDouble(token);
			}
			return Integer.parseInt(token);
		}
		int dotIdx = token.indexOf('.');
		if (dotIdx != -1) {
			if (dotIdx == token.length()) {
				throw new RaungAsmException("Unknown value format: " + token);
			}
			return parseSpecialValue(token.substring(0, dotIdx), token.substring(dotIdx + 1));
		}
		if (token.equals("true")) {
			return Boolean.TRUE;
		}
		if (token.equals("false")) {
			return Boolean.FALSE;
		}
		// TODO: type ??
		return Type.getObjectType(token);
	}

	private static Object parseSpecialValue(String type, String value) {
		switch (type) {
			case "Double":
				return parseSpecialDouble(value);

			default:
				throw new RaungAsmException("Unknown value type: " + type + "." + value);
		}
	}

	private static Object parseSpecialDouble(String value) {
		switch (value) {
			case "NaN":
				return Double.NaN;
			case "MAX_VALUE":
				return Double.MAX_VALUE;
			case "MIN_VALUE":
				return Double.MIN_VALUE;
			case "POSITIVE_INFINITY":
				return Double.POSITIVE_INFINITY;
			case "NEGATIVE_INFINITY":
				return Double.NEGATIVE_INFINITY;

			default:
				throw new RaungAsmException("Unknown Double special value: " + value);
		}
	}
}

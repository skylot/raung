package io.github.skylot.raung.asm.impl.parser.code;

public class ValueParser {

	public static Object process(String valueToken) {
		char firstChar = valueToken.charAt(0);
		if (firstChar == '"') {
			// String
			return valueToken.substring(1, valueToken.length() - 1);
		}
		if (firstChar == '-' || Character.isDigit(firstChar)) {
			if (valueToken.contains(".")) {
				return Double.parseDouble(valueToken);
			}
			if (valueToken.endsWith("L")) {
				return Long.parseLong(valueToken.substring(0, valueToken.length() - 1));
			}
			return Integer.parseInt(valueToken);
		}
		// TODO: parse other types (add type descriptors?)
		return valueToken;
	}
}

package io.github.skylot.raung.disasm.impl.utils;

import java.lang.reflect.Array;

public class RaungTypes {

	public static String format(Object value) {
		if (value == null) {
			return "null";
		}
		Class<?> cls = value.getClass();
		if (cls.isArray()) {
			int length = Array.getLength(value);
			if (length == 0) {
				return "{ }";
			}
			StringBuilder sb = new StringBuilder();
			sb.append("{ ").append(Array.get(value, 0));
			for (int i = 1; i < length; i++) {
				sb.append(", ").append(Array.get(value, i));
			}
			sb.append(" }");
			return sb.toString();
		}
		if (cls.equals(String.class)) {
			String str = ((String) value);
			str = str.replace("\"", "\\\"");
			str = str.replace("\n", "\\n");
			return String.format("\"%s\"", str);
		}
		if (cls.equals(Long.class)) {
			return String.valueOf(value) + 'L';
		}
		// TODO:
		return String.valueOf(value);
	}
}

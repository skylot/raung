package io.github.skylot.raung.disasm.impl.utils;

import java.lang.reflect.Array;

public class RaungTypes {

	public static String format(Object value) {
		if (value == null) {
			return "null";
		}
		Class<?> cls = value.getClass();
		if (cls.isPrimitive()) {
			return String.valueOf(value);
		}
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
			// TODO: escape strings
			return '"' + (String) value + '"';
		}
		// TODO:
		return String.valueOf(value);
	}
}

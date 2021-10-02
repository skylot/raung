package io.github.skylot.raung.disasm.impl.utils;

import java.lang.reflect.Array;

import org.objectweb.asm.ConstantDynamic;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Type;

public class RaungTypes {

	public static String format(Object value) {
		if (value == null) {
			return "null";
		}
		if (value instanceof String) {
			return StringDisasmUtils.escapeString(((String) value));
		}
		if (value instanceof Type) {
			return formatType(value);
		}
		if (value instanceof Number) {
			if (value instanceof Integer) {
				return value.toString();
			}
			if (value instanceof Float) {
				return String.valueOf(value) + 'f';
			}
			if (value instanceof Long) {
				return String.valueOf(value) + 'L';
			}
			if (value instanceof Double) {
				return formatDouble((Double) value);
			}
			return value.toString();
		}
		if (value.getClass().isArray()) {
			return formatConstArray(value);
		}
		if (value instanceof Handle) {
			return "Handle:" + value;
		}
		if (value instanceof ConstantDynamic) {
			return "ConstantDynamic:" + value;
		}
		throw new RaungDisasmException("Unexpected type of constant value: " + value
				+ ", class: " + value.getClass().getName());
	}

	private static String formatType(Object value) {
		Type type = (Type) value;
		switch (type.getSort()) {
			case Type.OBJECT:
				return type.getDescriptor();
			case Type.ARRAY:
				return type.toString();
			case Type.METHOD:
				return "Method:" + type;
			default:
				throw new RaungDisasmException("Unexpected type of constant value: " + value
						+ ", class: " + value.getClass().getName());
		}
	}

	private static String formatConstArray(Object value) {
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

	private static String formatDouble(double value) {
		if (Double.isNaN(value)) {
			return "Double.NaN";
		}
		if (value == Double.POSITIVE_INFINITY) {
			return "Double.POSITIVE_INFINITY";
		}
		if (value == Double.NEGATIVE_INFINITY) {
			return "Double.NEGATIVE_INFINITY";
		}
		if (value == Double.MIN_VALUE) {
			return "Double.MIN_VALUE";
		}
		if (value == Double.MAX_VALUE) {
			return "Double.MAX_VALUE";
		}
		return Double.toString(value);
	}
}

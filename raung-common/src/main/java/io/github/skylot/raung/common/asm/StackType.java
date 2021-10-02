package io.github.skylot.raung.common.asm;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;

public enum StackType {
	TOP(Opcodes.TOP, "Top"),
	INTEGER(Opcodes.INTEGER, "int"),
	FLOAT(Opcodes.FLOAT, "float"),
	DOUBLE(Opcodes.DOUBLE, "double"),
	LONG(Opcodes.LONG, "long"),
	NULL(Opcodes.NULL, "null"),
	UNINIT_THIS(Opcodes.UNINITIALIZED_THIS, "UninitializedThis");

	private final Integer value;
	private final String name;

	StackType(Integer value, String name) {
		this.value = value;
		this.name = name;
	}

	public Integer getValue() {
		return value;
	}

	public String getName() {
		return name;
	}

	private static final Map<Integer, StackType> VALUES_MAP;
	private static final Map<String, StackType> NAMES_MAP;

	static {
		StackType[] values = values();
		Map<Integer, StackType> valuesMap = new IdentityHashMap<>(values.length);
		Map<String, StackType> namesMap = new HashMap<>(values.length);
		for (StackType value : values) {
			valuesMap.put(value.value, value);
			namesMap.put(value.name, value);
		}
		VALUES_MAP = valuesMap;
		NAMES_MAP = namesMap;
	}

	public static StackType getByName(String name) {
		return NAMES_MAP.get(name);
	}

	@Nullable
	public static StackType getByValue(Integer value) {
		return VALUES_MAP.get(value);
	}
}

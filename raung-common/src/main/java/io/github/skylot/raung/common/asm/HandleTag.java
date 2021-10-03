package io.github.skylot.raung.common.asm;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.Opcodes;

public enum HandleTag {

	GETFIELD(Opcodes.H_GETFIELD, "get-field"),
	GETSTATIC(Opcodes.H_GETSTATIC, "get-static"),
	PUTFIELD(Opcodes.H_PUTFIELD, "put-field"),
	PUTSTATIC(Opcodes.H_PUTSTATIC, "put-static"),
	INVOKEVIRTUAL(Opcodes.H_INVOKEVIRTUAL, "invoke-virtual"),
	INVOKESTATIC(Opcodes.H_INVOKESTATIC, "invoke-static"),
	INVOKESPECIAL(Opcodes.H_INVOKESPECIAL, "invoke-special"),
	NEWINVOKESPECIAL(Opcodes.H_NEWINVOKESPECIAL, "new-invoke-special"),
	INVOKEINTERFACE(Opcodes.H_INVOKEINTERFACE, "invoke-interface");

	private final int tag;
	private final String name;

	HandleTag(int tag, String name) {
		this.tag = tag;
		this.name = name;
	}

	public int getTag() {
		return tag;
	}

	public String getName() {
		return name;
	}

	private static final HandleTag[] VALUES_ARRAY;
	private static final Map<String, HandleTag> NAMES_MAP;

	static {
		HandleTag[] values = values();
		HandleTag[] valuesArr = new HandleTag[10];
		Map<String, HandleTag> namesMap = new HashMap<>(values.length);
		for (HandleTag value : values) {
			valuesArr[value.tag] = value;
			namesMap.put(value.name, value);
		}
		VALUES_ARRAY = valuesArr;
		NAMES_MAP = namesMap;
	}

	public static HandleTag getByName(String name) {
		return NAMES_MAP.get(name);
	}

	public static HandleTag getByValue(int value) {
		return VALUES_ARRAY[value];
	}
}

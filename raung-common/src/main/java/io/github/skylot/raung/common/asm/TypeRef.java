package io.github.skylot.raung.common.asm;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.TypeReference;

import static io.github.skylot.raung.common.asm.TypeRefFormat.FORMAL_PARAM_INDEX;
import static io.github.skylot.raung.common.asm.TypeRefFormat.NO_ARGS;
import static io.github.skylot.raung.common.asm.TypeRefFormat.SUPER_TYPE_INDEX;
import static io.github.skylot.raung.common.asm.TypeRefFormat.TYPE_ARGUMENT_INDEX;
import static io.github.skylot.raung.common.asm.TypeRefFormat.TYPE_PARAM_BOUND_INDEX;
import static io.github.skylot.raung.common.asm.TypeRefFormat.TYPE_PARAM_INDEX;

public enum TypeRef {
	CLASS_TYPE_PARAMETER(TypeReference.CLASS_TYPE_PARAMETER, "class_type_parameter", TYPE_PARAM_INDEX),
	CLASS_TYPE_PARAMETER_BOUND(TypeReference.CLASS_TYPE_PARAMETER_BOUND, "class_type_parameter_bound", TYPE_PARAM_BOUND_INDEX),
	METHOD_TYPE_PARAMETER(TypeReference.METHOD_TYPE_PARAMETER, "method_type_parameter", TYPE_PARAM_INDEX),
	METHOD_TYPE_PARAMETER_BOUND(TypeReference.METHOD_TYPE_PARAMETER_BOUND, "method_type_parameter_bound", TYPE_PARAM_BOUND_INDEX),
	METHOD_FORMAL_PARAMETER(TypeReference.METHOD_FORMAL_PARAMETER, "method_formal_parameter", FORMAL_PARAM_INDEX),
	METHOD_INVOCATION_TYPE_ARGUMENT(TypeReference.METHOD_INVOCATION_TYPE_ARGUMENT, "method_invocation_type_argument", TYPE_ARGUMENT_INDEX),
	CLASS_EXTENDS(TypeReference.CLASS_EXTENDS, "class_extends", SUPER_TYPE_INDEX),
	CAST(TypeReference.CAST, "cast", TYPE_ARGUMENT_INDEX),
	FIELD(TypeReference.FIELD, "field", NO_ARGS),
	METHOD_RETURN(TypeReference.METHOD_RETURN, "method_return", NO_ARGS),
	NEW(TypeReference.NEW, "new", NO_ARGS),
	LOCAL_VARIABLE(TypeReference.LOCAL_VARIABLE, "local_variable", NO_ARGS);

	private final int value;
	private final String name;
	private final TypeRefFormat format;

	TypeRef(int value, String name, TypeRefFormat format) {
		this.value = value;
		this.name = name;
		this.format = format;
	}

	public int getValue() {
		return value;
	}

	public String getName() {
		return name;
	}

	public TypeRefFormat getFormat() {
		return format;
	}

	private static final TypeRef[] ARR_BY_VALUE;
	private static final Map<String, TypeRef> MAP_BY_NAME;

	static {
		TypeRef[] values = values();
		TypeRef[] arr = new TypeRef[0x50];
		Map<String, TypeRef> map = new HashMap<>(values.length);
		for (TypeRef ref : values) {
			arr[ref.value] = ref;
			map.put(ref.name, ref);
		}
		ARR_BY_VALUE = arr;
		MAP_BY_NAME = map;
	}

	public static TypeRef getByValue(int value) {
		return ARR_BY_VALUE[value];
	}

	@Nullable
	public static TypeRef getByName(String name) {
		return MAP_BY_NAME.get(name);
	}
}

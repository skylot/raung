package io.github.skylot.raung.common;

public enum JavaOpCodeFormat {
	UNKNOWN,
	NO_ARGS,
	INT,
	TYPE,
	FIELD,
	METHOD,
	VAR,
	JUMP,

	// special cases
	NEW_ARRAY,
	IINC,
	LDC,
}

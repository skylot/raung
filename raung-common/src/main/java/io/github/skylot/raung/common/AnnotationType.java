package io.github.skylot.raung.common;

public enum AnnotationType {
	NORMAL("annotation"),
	TYPE("type_annotation"),
	PARAM("param_annotation"),
	DEFAULT("default"),
	SUB("sub_annotation"),
	INSN("insn_annotation"),
	ARRAY("array"),
	;

	private final String name;

	AnnotationType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}

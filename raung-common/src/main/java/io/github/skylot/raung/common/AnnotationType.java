package io.github.skylot.raung.common;

public enum AnnotationType {
	NORMAL("annotation"),
	TYPE("typeannotation"),
	PARAM("paramannotation"),
	DEFAULT("default"),
	SUB("subannotation"),
	INSN("insnannotaion"),
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

package io.github.skylot.raung.asm.impl.parser.data;

public class FieldData extends CommonData {
	private String type;
	private Object value;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}

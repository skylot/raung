package io.github.skylot.raung.disasm.impl.visitors.data;

public class LocalVar {
	private final int index;
	private final String name;
	private final String type;
	private final String signature;

	public LocalVar(int index, String name, String type, String signature) {
		this.index = index;
		this.name = name;
		this.type = type;
		this.signature = signature;
	}

	public int getIndex() {
		return index;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String getSignature() {
		return signature;
	}
}

package io.github.skylot.raung.asm.impl.parser.data;

import org.jetbrains.annotations.Nullable;

public class RaungLocalVar {
	private final int number;
	private final String name;
	private final String type;
	@Nullable
	private final String signature;

	private final RaungLabel startLabel;

	private boolean visited;

	public RaungLocalVar(int number, String name, String type, @Nullable String signature, RaungLabel startLabel) {
		this.number = number;
		this.name = name;
		this.type = type;
		this.signature = signature;
		this.startLabel = startLabel;
	}

	public int getNumber() {
		return number;
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

	public RaungLabel getStartLabel() {
		return startLabel;
	}

	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}
}

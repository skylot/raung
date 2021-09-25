package io.github.skylot.raung.asm.impl.parser.data;

import org.objectweb.asm.Label;

public class RaungLabel extends Label {
	private final String name;

	public RaungLabel(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "Label{" + name + '}';
	}
}

package io.github.skylot.raung.asm.impl.parser.data;

import org.objectweb.asm.Label;

public class RaungLabel extends Label {
	private final String name;
	private int pos = -1;

	public static RaungLabel makeNew(MethodData mth, String labelName) {
		RaungLabel label = new RaungLabel(labelName);
		mth.addLabel(label);
		return label;
	}

	public static RaungLabel ref(MethodData mth, String labelName) {
		RaungLabel existLabel = mth.getLabel(labelName);
		if (existLabel != null) {
			return existLabel;
		}
		return makeNew(mth, labelName);
	}

	private RaungLabel(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}

	@Override
	public String toString() {
		return "Label{" + name + " at " + pos + '}';
	}
}

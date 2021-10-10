package io.github.skylot.raung.asm.impl.asm;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AnnotationNode;

import io.github.skylot.raung.asm.impl.parser.data.TypeRefPathData;

public class InsnAnnotationNode extends AnnotationNode {
	private final boolean visible;
	private final TypeRefPathData pathData;

	public InsnAnnotationNode(String type, boolean visible, TypeRefPathData pathData) {
		super(Opcodes.ASM9, type);
		this.visible = visible;
		this.pathData = pathData;
	}

	public String getType() {
		return desc;
	}

	public boolean isVisible() {
		return visible;
	}

	public TypeRefPathData getPathData() {
		return pathData;
	}
}

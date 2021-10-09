package io.github.skylot.raung.disasm.impl.visitors;

import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.TypePath;

import io.github.skylot.raung.disasm.impl.utils.RaungWriter;

public class RaungFieldVisitor extends FieldVisitor {
	private final RaungWriter writer;
	private final RaungClassVisitor clsVisitor;
	private boolean closeField;

	public RaungFieldVisitor(RaungClassVisitor clsVisitor, boolean closeField) {
		super(clsVisitor.getApi());
		this.clsVisitor = clsVisitor;
		this.writer = clsVisitor.getWriter();
		this.closeField = closeField;
	}

	@Override
	public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
		this.closeField = true;
		return RaungAnnotationVisitor.buildAnnotation(clsVisitor, descriptor, visible);
	}

	@Override
	public AnnotationVisitor visitTypeAnnotation(int typeRef, @Nullable TypePath typePath, String descriptor, boolean visible) {
		this.closeField = true;
		return RaungAnnotationVisitor.buildTypeAnnotation(clsVisitor, typeRef, typePath, descriptor, visible);
	}

	@Override
	public void visitAttribute(Attribute attribute) {
		this.closeField = true;
		writer.startLine("# TODO: field attribute: " + attribute);
	}

	@Override
	public void visitEnd() {
		writer.decreaseIndent();
		if (closeField) {
			writer.startLine(".end field");
		}
	}
}

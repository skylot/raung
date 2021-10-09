package io.github.skylot.raung.asm.impl.parser.data;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.TypePath;

import io.github.skylot.raung.asm.impl.asm.RaungAsmWriter;

public class FieldData extends CommonData {
	private final ClassData classData;

	private String type;
	private Object value;

	private FieldVisitor fieldVisitor;

	public FieldData(ClassData classData) {
		this.classData = classData;
	}

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

	public boolean isVisited() {
		return fieldVisitor != null;
	}

	public FieldVisitor getAsmVisitor() {
		if (this.fieldVisitor == null) {
			this.fieldVisitor = RaungAsmWriter.visitField(classData.visitCls(), this);
		}
		return this.fieldVisitor;
	}

	@Override
	public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
		return getAsmVisitor().visitAnnotation(descriptor, visible);
	}

	@Override
	public AnnotationVisitor visitTypeAnnotation(int ref, TypePath path, String descriptor, boolean visible) {
		return getAsmVisitor().visitTypeAnnotation(ref, path, descriptor, visible);
	}
}

package io.github.skylot.raung.asm.impl.parser.data;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.TypePath;

public abstract class CommonData {
	private int accessFlags;
	private String name;
	private String signature;

	public abstract AnnotationVisitor visitAnnotation(String descriptor, boolean visible);

	public abstract AnnotationVisitor visitTypeAnnotation(int ref, TypePath path, String descriptor, boolean visible);

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public int getAccessFlags() {
		return accessFlags;
	}

	public void setAccessFlags(int accessFlags) {
		this.accessFlags = accessFlags;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}

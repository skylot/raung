package io.github.skylot.raung.asm.impl.parser.data;

import java.util.ArrayList;
import java.util.List;

public class CommonData {
	private int accessFlags;
	private String name;

	private String signature;
	private List<AnnotationData> annotations = new ArrayList<>();

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public List<AnnotationData> getAnnotations() {
		return annotations;
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

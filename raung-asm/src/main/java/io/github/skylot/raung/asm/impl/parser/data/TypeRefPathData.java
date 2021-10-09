package io.github.skylot.raung.asm.impl.parser.data;

import org.objectweb.asm.TypePath;
import org.objectweb.asm.TypeReference;

public class TypeRefPathData {
	private TypeReference typeRef;
	private TypePath path;

	public TypeReference getTypeRef() {
		return typeRef;
	}

	public void setTypeRef(TypeReference typeRef) {
		this.typeRef = typeRef;
	}

	public TypePath getPath() {
		return path;
	}

	public void setPath(TypePath path) {
		this.path = path;
	}
}

package io.github.skylot.raung.asm.impl.parser.data;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassWriter;

import io.github.skylot.raung.asm.impl.parser.RaungAsmWriter;

public class ClassData extends CommonData {
	private final ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

	private int version;
	private String superCls;
	private List<String> interfaces = new ArrayList<>();
	private String source;

	private List<FieldData> fields = new ArrayList<>();
	private List<MethodData> methods = new ArrayList<>();

	private boolean visited;

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getSuperCls() {
		if (superCls == null) {
			return "java/lang/Object";
		}
		return superCls;
	}

	public void setSuperCls(String superCls) {
		this.superCls = superCls;
	}

	public List<String> getInterfaces() {
		return interfaces;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public List<FieldData> getFields() {
		return fields;
	}

	public List<MethodData> getMethods() {
		return methods;
	}

	public ClassWriter getAsmClassWriter() {
		return cw;
	}

	public byte[] getBytes() {
		return cw.toByteArray();
	}

	public void visitCls() {
		RaungAsmWriter.visitCls(this, cw);
	}

	public void markAsVisited() {
		this.visited = true;
	}

	public boolean isVisited() {
		return visited;
	}
}

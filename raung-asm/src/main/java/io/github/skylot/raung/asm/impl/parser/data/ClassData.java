package io.github.skylot.raung.asm.impl.parser.data;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassWriter;

import io.github.skylot.raung.asm.impl.asm.RaungAsmWriter;

public class ClassData extends CommonData {
	private ClassWriter classWriter;

	private int version;
	private String superCls;
	private final List<String> interfaces = new ArrayList<>();
	private String source;
	private AutoOption auto = AutoOption.DISABLE;

	private final List<FieldData> fields = new ArrayList<>();
	private final List<MethodData> methods = new ArrayList<>();

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

	public byte[] getBytes() {
		return classWriter.toByteArray();
	}

	public boolean isVisited() {
		return classWriter != null;
	}

	public AutoOption getAuto() {
		return auto;
	}

	public void setAuto(AutoOption auto) {
		this.auto = auto;
	}

	public ClassWriter visitCls() {
		if (classWriter == null) {
			classWriter = RaungAsmWriter.visitCls(this);
		}
		return classWriter;
	}
}

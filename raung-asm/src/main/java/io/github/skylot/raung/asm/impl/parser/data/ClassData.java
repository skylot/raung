package io.github.skylot.raung.asm.impl.parser.data;

import java.util.ArrayList;
import java.util.List;

public class ClassData extends CommonData {
	private int version;
	private String superCls;
	private List<String> interfaces = new ArrayList<>();
	private String source;

	private List<FieldData> fields = new ArrayList<>();

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
}

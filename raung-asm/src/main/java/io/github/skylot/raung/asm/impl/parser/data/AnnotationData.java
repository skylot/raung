package io.github.skylot.raung.asm.impl.parser.data;

import java.util.LinkedHashMap;
import java.util.Map;

public class AnnotationData {
	private String type;
	private boolean visible;
	private Map<String, Object> values = new LinkedHashMap<>();

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public Map<String, Object> getValues() {
		return values;
	}
}

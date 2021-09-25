package io.github.skylot.raung.disasm.impl.visitors.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.objectweb.asm.Label;

public class LabelData {
	private final Label label;
	private final String name;
	private int insnRef;
	private int useCount;
	private List<LocalVar> startVars;
	private List<LocalVar> endVars;

	public LabelData(Label label, String name) {
		this.label = label;
		this.name = name;
	}

	public Label getLabel() {
		return label;
	}

	public String getName() {
		return name;
	}

	public int getInsnRef() {
		return insnRef;
	}

	public void setInsnRef(int insnRef) {
		this.insnRef = insnRef;
	}

	public int getUseCount() {
		return useCount;
	}

	public void addUse() {
		this.useCount++;
	}

	public void setUseCount(int useCount) {
		this.useCount = useCount;
	}

	public List<LocalVar> getStartVars() {
		return startVars == null ? Collections.emptyList() : startVars;
	}

	public void addStartVars(LocalVar var) {
		if (startVars == null) {
			startVars = new ArrayList<>();
		}
		startVars.add(var);
	}

	public List<LocalVar> getEndVars() {
		return endVars == null ? Collections.emptyList() : endVars;
	}

	public void addEndVar(LocalVar var) {
		if (endVars == null) {
			endVars = new ArrayList<>();
		}
		endVars.add(var);
	}
}

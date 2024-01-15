package io.github.skylot.raung.disasm.impl.visitors.data;

import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Label;

import io.github.skylot.raung.disasm.impl.utils.ListUtils;

public class LabelData {
	private final Label label;
	private final String name;
	private int insnRef;
	private int useCount;
	private @Nullable List<LocalVar> startVars;
	private @Nullable List<LocalVar> endVars;
	private @Nullable List<TryCatchBlock> catches;
	private @Nullable List<Integer> lines;

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

	public LabelData addUse() {
		this.useCount++;
		return this;
	}

	public List<LocalVar> getStartVars() {
		return ListUtils.fromNullable(startVars);
	}

	public void addStartVars(LocalVar var) {
		this.startVars = ListUtils.addToNullable(startVars, var);
	}

	public List<LocalVar> getEndVars() {
		return ListUtils.fromNullable(endVars);
	}

	public void addEndVar(LocalVar var) {
		this.endVars = ListUtils.addToNullable(endVars, var);
	}

	public List<TryCatchBlock> getCatches() {
		return ListUtils.fromNullable(catches);
	}

	public void addCatch(TryCatchBlock block) {
		this.catches = ListUtils.addToNullable(catches, block);
	}

	public List<Integer> getLines() {
		return ListUtils.fromNullable(lines);
	}

	public void setLine(int line) {
		this.lines = ListUtils.addToNullable(lines, line);
	}

	@Override
	public String toString() {
		return name + " at " + insnRef;
	}
}

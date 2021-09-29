package io.github.skylot.raung.asm.impl.parser.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.MethodVisitor;

import io.github.skylot.raung.asm.impl.asm.RaungAsmWriter;

public class MethodData extends CommonData {
	private final ClassData classData;

	private String descriptor;
	private final List<String> throwsList = new ArrayList<>();
	private int maxStack = 0;
	private int maxLocals = 0;

	private MethodVisitor methodVisitor;
	private int insnsCount = 0;

	private Map<Integer, RaungLocalVar> localVars;
	private final List<RaungLabel> labels = new ArrayList<>();
	private final Map<String, RaungLabel> labelsMap = new HashMap<>();

	public MethodData(ClassData classData) {
		this.classData = classData;
	}

	public int getMaxStack() {
		return maxStack;
	}

	public void setMaxStack(int maxStack) {
		this.maxStack = maxStack;
	}

	public int getMaxLocals() {
		return maxLocals;
	}

	public void setMaxLocals(int maxLocals) {
		this.maxLocals = maxLocals;
	}

	public ClassData getClassData() {
		return classData;
	}

	public MethodVisitor getAsmMethodVisitor() {
		if (methodVisitor == null) {
			this.methodVisitor = RaungAsmWriter.visitMethod(this);
		}
		return methodVisitor;
	}

	public String getDescriptor() {
		return descriptor;
	}

	public void setDescriptor(String descriptor) {
		this.descriptor = descriptor;
	}

	public List<String> getThrows() {
		return throwsList;
	}

	public void addThrow(String type) {
		this.throwsList.add(type);
	}

	public int getInsnsCount() {
		return insnsCount;
	}

	public void addInsn() {
		insnsCount++;
	}

	public void addLocalVar(RaungLocalVar localVar) {
		if (localVars == null) {
			localVars = new HashMap<>();
		}
		localVars.put(localVar.getNumber(), localVar);
	}

	@Nullable
	public RaungLocalVar getLocalVar(int number) {
		if (this.localVars == null) {
			return null;
		}
		return this.localVars.get(number);
	}

	public Collection<RaungLocalVar> getLocalVars() {
		if (this.localVars == null) {
			return Collections.emptyList();
		}
		return this.localVars.values();
	}

	public void addLabel(RaungLabel label) {
		this.labels.add(label);
		this.labelsMap.put(label.getName(), label);
	}

	@Nullable
	public RaungLabel getLabel(String name) {
		return this.labelsMap.get(name);
	}

	@Nullable
	public RaungLabel getLabel(int pos) {
		for (RaungLabel label : this.labels) {
			if (label.getPos() == pos) {
				return label;
			}
		}
		return null;
	}

	public List<RaungLabel> getLabels() {
		return labels;
	}
}

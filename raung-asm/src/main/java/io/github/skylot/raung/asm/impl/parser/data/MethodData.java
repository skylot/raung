package io.github.skylot.raung.asm.impl.parser.data;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.MethodVisitor;

public class MethodData extends CommonData {
	private final ClassData classData;

	private String descriptor;
	private List<String> exceptions = new ArrayList<>();
	private int maxStack = 0;
	private int maxLocals = 0;

	private MethodVisitor methodVisitor;
	private int insnsCount = 0;

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

	public MethodVisitor getMethodVisitor() {
		return methodVisitor;
	}

	public void setMethodVisitor(MethodVisitor methodVisitor) {
		this.methodVisitor = methodVisitor;
	}

	public String getDescriptor() {
		return descriptor;
	}

	public void setDescriptor(String descriptor) {
		this.descriptor = descriptor;
	}

	public List<String> getExceptions() {
		return exceptions;
	}

	public int getInsnsCount() {
		return insnsCount;
	}

	public void addInsn() {
		insnsCount++;
	}
}

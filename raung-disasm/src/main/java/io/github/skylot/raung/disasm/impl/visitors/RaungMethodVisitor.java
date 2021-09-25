package io.github.skylot.raung.disasm.impl.visitors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.TypePath;

import io.github.skylot.raung.common.JavaOpCodes;
import io.github.skylot.raung.disasm.impl.utils.RaungTypes;
import io.github.skylot.raung.disasm.impl.utils.RaungWriter;
import io.github.skylot.raung.disasm.impl.visitors.data.LabelData;
import io.github.skylot.raung.disasm.impl.visitors.data.LocalVar;

public class RaungMethodVisitor extends MethodVisitor {
	private final RaungClassVisitor classVisitor;
	private final RaungWriter writer;

	private final RaungWriter tempWriter = new RaungWriter();

	private final List<String> insns = new ArrayList<>();
	private final Map<Label, LabelData> labels = new IdentityHashMap<>();

	public RaungMethodVisitor(RaungClassVisitor classVisitor) {
		super(classVisitor.getApi());
		this.classVisitor = classVisitor;
		this.writer = classVisitor.getWriter();
	}

	@Override
	public void visitParameter(String name, int access) {
	}

	@Override
	public AnnotationVisitor visitAnnotationDefault() {
		return null;
	}

	@Override
	public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
		return null;
	}

	@Override
	public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
		return null;
	}

	@Override
	public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
		return null;
	}

	@Override
	public void visitAnnotableParameterCount(int parameterCount, boolean visible) {
	}

	@Override
	public AnnotationVisitor visitParameterAnnotation(int parameter, String descriptor, boolean visible) {
		return null;
	}

	@Override
	public void visitAttribute(Attribute attribute) {
	}

	@Override
	public void visitCode() {
	}

	@Override
	public void visitFrame(int type, int numLocal, Object[] local, int numStack, Object[] stack) {
	}

	@Override
	public void visitInsn(int opcode) {
		insns.add(JavaOpCodes.getName(opcode));
	}

	@Override
	public void visitIntInsn(int opcode, int operand) {
		insns.add(formatInsn(opcode).add(operand).getCode());
	}

	@Override
	public void visitVarInsn(int opcode, int var) {
		insns.add(formatInsn(opcode).add(var).getCode());
	}

	@Override
	public void visitTypeInsn(int opcode, String type) {
		insns.add(formatInsn(opcode).add(type).getCode());
	}

	@Override
	public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
		insns.add(formatInsn(opcode).add(owner).space().add(name).space().add(descriptor).getCode());
	}

	@Override
	public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
		RaungWriter rw = formatInsn(opcode).add(owner).space().add(name).space().add(descriptor);
		if (isInterface) {
			rw.add("  # interface");
		}
		insns.add(rw.getCode());
	}

	@Override
	public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
		insns.add(String.format("invoke-dynamic %s %s %s %s",
				name, descriptor, bootstrapMethodHandle.toString(), Arrays.toString(bootstrapMethodArguments)));
	}

	@Override
	public void visitJumpInsn(int opcode, Label label) {
		LabelData ld = getLabelData(label);
		ld.addUse();
		insns.add(formatInsn(opcode).add(ld.getName()).getCode());
	}

	@Override
	public void visitLdcInsn(Object value) {
		boolean wide = value instanceof Long || value instanceof Double;
		String insn = wide ? "ldc2_w" : "ldc";
		insns.add(tmpWriter().add(insn).space().add(RaungTypes.format(value)).getCode());
	}

	@Override
	public void visitIincInsn(int var, int increment) {
		insns.add(tmpWriter().add("iinc").space().add(var).space().add(increment).getCode());
	}

	@Override
	public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
	}

	@Override
	public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
	}

	@Override
	public void visitMultiANewArrayInsn(String descriptor, int numDimensions) {
	}

	@Override
	public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
	}

	@Override
	public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
		return null;
	}

	@Override
	public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {
		if (addDebugInfo()) {
			LocalVar var = new LocalVar(index, name, descriptor, signature);
			getLabelData(start).addStartVars(var);
			getLabelData(end).addEndVar(var);
		}
	}

	@Override
	public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index,
			String descriptor, boolean visible) {
		return null;
	}

	@Override
	public void visitLineNumber(int line, Label start) {
		if (addDebugInfo()) {
			insns.add(".line " + line);
		}
	}

	@Override
	public void visitLabel(Label label) {
		getLabelData(label).setInsnRef(insns.size() - 1);
	}

	@Override
	public void visitMaxs(int maxStack, int maxLocals) {
		writer.startLine(".max stack").space().add(maxStack);
		writer.startLine(".max locals").space().add(maxLocals);
		writer.newLine();
	}

	@Override
	public void visitEnd() {
		Map<Integer, LabelData> labels = this.labels.values().stream()
				.filter(ld -> ld.getUseCount() != 0
						|| !ld.getStartVars().isEmpty()
						|| !ld.getEndVars().isEmpty())
				.collect(Collectors.toMap(LabelData::getInsnRef, ld -> ld));
		int insnsCount = insns.size();
		for (int i = 0; i < insnsCount; i++) {
			LabelData labelData = labels.get(i);
			if (labelData != null) {
				if (labelData.getUseCount() != 0) {
					writer.decreaseIndent();
					writer.startLine().add(labelData.getName());
					writer.increaseIndent();
				}
				for (LocalVar startVar : labelData.getStartVars()) {
					writer.startLine(".local")
							.space().add(startVar.getIndex())
							.space().addString(startVar.getName())
							.space().add(startVar.getType());
					if (startVar.getSignature() != null) {
						writer.space().add(startVar.getSignature());
					}
				}
			}
			String insn = insns.get(i);
			writer.startLine(insn);
			if (labelData != null && !labelData.getEndVars().isEmpty() && i != insnsCount - 1) {
				for (LocalVar endVar : labelData.getEndVars()) {
					writer.startLine(".end local ").add(endVar.getIndex()).add(" # ").addString(endVar.getName());
				}
			}
		}
		writer.decreaseIndent();
		writer.decreaseIndent();
		writer.startLine(".end method");
	}

	private LabelData getLabelData(Label label) {
		return labels.computeIfAbsent(label, l -> new LabelData(label, String.format(":L%d", labels.size())));
	}

	private RaungWriter tmpWriter() {
		return tempWriter.clear();
	}

	private RaungWriter formatInsn(int opcode) {
		return tmpWriter().add(JavaOpCodes.getName(opcode)).space();
	}

	private boolean addDebugInfo() {
		return !classVisitor.getArgs().isIgnoreDebugInfo();
	}
}

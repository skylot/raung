package io.github.skylot.raung.disasm.impl.visitors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.TypePath;

import io.github.skylot.raung.common.Directive;
import io.github.skylot.raung.common.JavaOpCodes;
import io.github.skylot.raung.common.RaungAccessFlags;
import io.github.skylot.raung.common.asm.StackType;
import io.github.skylot.raung.disasm.impl.utils.RaungDisasmException;
import io.github.skylot.raung.disasm.impl.utils.RaungTypes;
import io.github.skylot.raung.disasm.impl.utils.RaungWriter;
import io.github.skylot.raung.disasm.impl.utils.TypeRefUtils;
import io.github.skylot.raung.disasm.impl.visitors.data.LabelData;
import io.github.skylot.raung.disasm.impl.visitors.data.LocalVar;
import io.github.skylot.raung.disasm.impl.visitors.data.TryCatchBlock;

public class RaungMethodVisitor extends MethodVisitor {
	private final RaungClassVisitor classVisitor;
	private final RaungWriter writer;

	private final RaungWriter tempWriter = new RaungWriter();

	private final List<String> insns = new ArrayList<>();
	private final Map<Label, LabelData> labels = new IdentityHashMap<>();
	private final Map<Integer, RaungWriter> insnAttachments = new HashMap<>();

	private int catchCount;

	public RaungMethodVisitor(RaungClassVisitor classVisitor) {
		super(classVisitor.getApi());
		this.classVisitor = classVisitor;
		this.writer = classVisitor.getWriter();
	}

	@Override
	public void visitParameter(String name, int access) {
		writer.startLine(".param").space().addString(name)
				.space().add(RaungAccessFlags.format(access, RaungAccessFlags.Scope.PARAM));
	}

	@Override
	public AnnotationVisitor visitAnnotationDefault() {
		return RaungAnnotationVisitor.buildDefaultValueVisitor(this.classVisitor);
	}

	@Override
	public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
		return RaungAnnotationVisitor.buildAnnotation(this.classVisitor, descriptor, visible);
	}

	@Override
	public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
		return RaungAnnotationVisitor.buildTypeAnnotation(this.classVisitor, typeRef, typePath, descriptor, visible);
	}

	@Override
	public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
		RaungWriter rw = new RaungWriter().setIndent(writer.getIndent());
		RaungAnnotationVisitor av = RaungAnnotationVisitor.buildInsnAnnotation(classVisitor, rw, typeRef, typePath, descriptor, visible);
		// attach annotation to last instruction
		insnAttachments.put(insns.size() - 1, rw);
		return av;
	}

	@Override
	public void visitAnnotableParameterCount(int parameterCount, boolean visible) {
		// ignore
	}

	@Override
	public AnnotationVisitor visitParameterAnnotation(int parameter, String descriptor, boolean visible) {
		return RaungAnnotationVisitor.buildParamAnnotation(this.classVisitor, parameter, descriptor, visible);
	}

	@Override
	public void visitAttribute(Attribute attribute) {
		// TODO
		writer.startLine("# attribute " + attribute);
	}

	@Override
	public void visitCode() {
	}

	@Override
	public void visitFrame(int type, int numLocal, Object[] local, int numStack, Object[] stack) {
		if (classVisitor.getArgs().isAutoFrames()) {
			return;
		}
		RaungWriter rw = tmpWriter();
		rw.add(Directive.STACK);
		switch (type) {
			case Opcodes.F_SAME:
				rw.add("same");
				break;

			case Opcodes.F_SAME1:
				rw.add("same1").space().add(formatStackType(stack[0]));
				break;

			case Opcodes.F_CHOP:
				rw.add("chop").space().add(numLocal);
				break;

			case Opcodes.F_APPEND:
				rw.add("append");
				rw.setIndent(writer.getIndent() + 2);
				for (int i = 0; i < numLocal; i++) {
					rw.startLine(formatStackType(local[i]));
				}
				rw.setIndent(writer.getIndent());
				rw.startLine(".end stack");
				break;

			case Opcodes.F_FULL:
				rw.add("full");
				rw.setIndent(writer.getIndent() + 2);
				for (int i = 0; i < numStack; i++) {
					rw.startLine("stack ").add(i).space().add(formatStackType(stack[i]));
				}
				for (int i = 0; i < numLocal; i++) {
					rw.startLine("local ").add(i).space().add(formatStackType(local[i]));
				}
				rw.setIndent(writer.getIndent());
				rw.startLine(".end stack");
				break;

			default:
				throw new RaungDisasmException("Unexpected frame type: " + type);
		}
		insns.add(rw.getCode());
	}

	private String formatStackType(Object value) {
		if (value instanceof String) {
			return (String) value;
		}
		if (value instanceof Integer) {
			StackType type = StackType.getByValue((Integer) value);
			if (type == null) {
				throw new RaungDisasmException("Unknown primitive stack type: " + value);
			}
			return type.getName();
		}
		if (value instanceof Label) {
			Label lbl = (Label) value;
			LabelData labelData = getLabelData(lbl);
			labelData.addUse();
			return "Uninitialized " + labelData.getName();
		}
		throw new IllegalArgumentException("Unknown stack type: " + value + ", class: " + value.getClass().getName());
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
		RaungWriter rw = formatInsn(opcode);
		if (isInterface && opcode != Opcodes.INVOKEINTERFACE) {
			rw.add("interface ");
		}
		rw.add(owner).space().add(name).space().add(descriptor);
		insns.add(rw.getCode());
	}

	@Override
	public void visitInvokeDynamicInsn(String name, String descriptor, Handle mthHandle, Object... args) {
		RaungWriter rw = tmpWriter().add("invokedynamic").space().add(name).space().add(descriptor);
		rw.setIndent(writer.getIndent() + 2);
		rw.startLine(RaungTypes.formatHandle(mthHandle));
		for (int i = 0; i < args.length; i++) {
			rw.startLine(".arg").space().add(i).space().add(RaungTypes.format(args[i]));
		}
		rw.setIndent(writer.getIndent());
		rw.startLine(".end invokedynamic");
		insns.add(rw.getCode());
	}

	@Override
	public void visitJumpInsn(int opcode, Label label) {
		LabelData ld = getLabelData(label).addUse();
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
		String switchType = classVisitor.getArgs().isAutoSwitch() ? "switch" : "tableswitch";
		RaungWriter rw = tmpWriter().add(switchType);
		rw.setIndent(writer.getIndent() + 1);
		int l = 0;
		for (int i = min; i <= max; i++) {
			LabelData label = getLabelData(labels[l++]).addUse();
			rw.startLine().add(i).space().add(label.getName());
		}
		rw.startLine("default ").add(getLabelData(dflt).addUse().getName());
		rw.setIndent(writer.getIndent());
		rw.startLine(".end ").add(switchType);
		insns.add(rw.getCode());
	}

	@Override
	public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
		String switchType = classVisitor.getArgs().isAutoSwitch() ? "switch" : "lookupswitch";
		RaungWriter rw = tmpWriter().add(switchType);
		rw.setIndent(writer.getIndent() + 1);
		int len = keys.length;
		for (int i = 0; i < len; i++) {
			LabelData label = getLabelData(labels[i]).addUse();
			rw.startLine().add(keys[i]).space().add(label.getName());
		}
		rw.startLine("default ").add(getLabelData(dflt).addUse().getName());
		rw.setIndent(writer.getIndent());
		rw.startLine(".end ").add(switchType);
		insns.add(rw.getCode());
	}

	@Override
	public void visitMultiANewArrayInsn(String descriptor, int numDimensions) {
		insns.add(tmpWriter().add("multianewarray").space().add(descriptor).space().add(numDimensions).getCode());
	}

	@Override
	public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
		LabelData startLabel = getLabelData(start).addUse();
		LabelData endLabel = getLabelData(end).addUse();
		LabelData handlerLabel = getLabelData(handler).addUse();
		TryCatchBlock tryCatchBlock = new TryCatchBlock(catchCount++, startLabel, endLabel, handlerLabel, type);
		endLabel.addCatch(tryCatchBlock);
	}

	@Override
	public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
		// TODO
		writer.startLine("# try-catch annotation " + TypeRefUtils.formatPath(typeRef, typePath) + " " + descriptor + " " + visible);
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
		// TODO: support local variable annotations
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
		LabelData ld = getLabelData(label);
		ld.setInsnRef(insns.size());
	}

	@Override
	public void visitMaxs(int maxStack, int maxLocals) {
		if (classVisitor.getArgs().isAutoMax()) {
			return;
		}
		writer.startLine(".max stack").space().add(maxStack);
		writer.startLine(".max locals").space().add(maxLocals);
		writer.newLine();
	}

	@Override
	public void visitEnd() {
		Map<Integer, LabelData> labelsMap = labels.values().stream()
				.filter(LabelData::isUsed)
				.collect(Collectors.toMap(LabelData::getInsnRef, ld -> ld));
		int insnsCount = insns.size();
		for (int i = 0; i < insnsCount; i++) {
			LabelData labelData = labelsMap.get(i);
			if (labelData != null) {
				handleLabelDataBeforeInsn(labelData);
			}
			addInsnAttachments(i);
			writer.startLine(insns.get(i));
			if (labelData != null) {
				handleLabelDataAfterInsn(labelData, i == insnsCount - 1);
			}
		}
		writer.setIndent(0);
		writer.startLine(".end method");
	}

	private void addInsnAttachments(int insnOffset) {
		RaungWriter rw = insnAttachments.get(insnOffset);
		if (rw != null) {
			writer.add(rw);
		}
	}

	private void handleLabelDataBeforeInsn(LabelData labelData) {
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
		if (!labelData.getCatches().isEmpty()) {
			for (TryCatchBlock catchBlock : labelData.getCatches()) {
				String type = catchBlock.getType();
				writer.startLine(Directive.CATCH);
				if (classVisitor.getArgs().isSaveCatchNumber()) {
					writer.add('@').add(catchBlock.getId()).space();
				}
				writer.add(type == null ? "all" : type)
						.space().add(catchBlock.getStart().getName())
						.space().add("..")
						.space().add(catchBlock.getEnd().getName())
						.space().add("goto").space().add(catchBlock.getHandler().getName());
			}
		}
	}

	private void handleLabelDataAfterInsn(LabelData labelData, boolean lastInsn) {
		if (!lastInsn && !labelData.getEndVars().isEmpty()) {
			for (LocalVar endVar : labelData.getEndVars()) {
				writer.startLine(".end local ").add(endVar.getIndex()).add(" # ").addString(endVar.getName());
			}
		}
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

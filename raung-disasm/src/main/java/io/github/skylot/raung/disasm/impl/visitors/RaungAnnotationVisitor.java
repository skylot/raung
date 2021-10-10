package io.github.skylot.raung.disasm.impl.visitors;

import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.TypePath;

import io.github.skylot.raung.common.AnnotationType;
import io.github.skylot.raung.common.Directive;
import io.github.skylot.raung.disasm.impl.utils.RaungTypes;
import io.github.skylot.raung.disasm.impl.utils.RaungWriter;
import io.github.skylot.raung.disasm.impl.utils.TypeRefUtils;

public class RaungAnnotationVisitor extends AnnotationVisitor {

	public static RaungAnnotationVisitor buildAnnotation(RaungClassVisitor clsVisitor, String descriptor, boolean visible) {
		RaungWriter writer = clsVisitor.getWriter();
		RaungAnnotationVisitor annotationVisitor = new RaungAnnotationVisitor(clsVisitor, writer, AnnotationType.NORMAL);
		writer.startLine(Directive.ANNOTATION).add(visible ? "runtime" : "build").space().add(descriptor);
		writer.increaseIndent();
		return annotationVisitor;
	}

	public static AnnotationVisitor buildTypeAnnotation(RaungClassVisitor clsVisitor,
			int typeRef, TypePath typePath, String descriptor, boolean visible) {
		RaungWriter writer = clsVisitor.getWriter();
		RaungAnnotationVisitor annotationVisitor = new RaungAnnotationVisitor(clsVisitor, writer, AnnotationType.TYPE);
		writer.startLine(Directive.TYPE_ANNOTATION).add(visible ? "runtime" : "build").space().add(descriptor);
		writer.increaseIndent();
		String ref = TypeRefUtils.formatPath(typeRef, typePath);
		if (!ref.isEmpty()) {
			writer.startLine(".ref").space().add(ref);
		}
		return annotationVisitor;
	}

	public static AnnotationVisitor buildParamAnnotation(RaungClassVisitor clsVisitor, int parameter, String descriptor, boolean visible) {
		RaungWriter writer = clsVisitor.getWriter();
		RaungAnnotationVisitor av = new RaungAnnotationVisitor(clsVisitor, writer, AnnotationType.PARAM);
		writer.startLine(Directive.PARAM_ANNOTATION).add(parameter).space()
				.add(visible ? "runtime" : "build").space().add(descriptor);
		writer.increaseIndent();
		return av;
	}

	public static AnnotationVisitor buildDefaultValueVisitor(RaungClassVisitor classVisitor) {
		RaungWriter writer = classVisitor.getWriter();
		RaungAnnotationVisitor av = new RaungAnnotationVisitor(classVisitor, writer, AnnotationType.DEFAULT);
		writer.startLine(Directive.ANNOTATION_DEFAULT_VALUE);
		writer.increaseIndent();
		return av;
	}

	public static RaungAnnotationVisitor buildInsnAnnotation(RaungClassVisitor clsVisitor, RaungWriter writer,
			int typeRef, TypePath typePath, String descriptor, boolean visible) {
		RaungAnnotationVisitor annotationVisitor = new RaungAnnotationVisitor(clsVisitor, writer, AnnotationType.INSN);
		writer.startLine(Directive.INSN_ANNOTATION).add(visible ? "runtime" : "build").space().add(descriptor);
		writer.increaseIndent();
		String ref = TypeRefUtils.formatPath(typeRef, typePath);
		if (!ref.isEmpty()) {
			writer.startLine(".ref").space().add(ref);
		}
		return annotationVisitor;
	}

	private final RaungClassVisitor clsVisitor;
	private final AnnotationType type;
	private final RaungWriter writer;

	private RaungAnnotationVisitor(RaungClassVisitor clsVisitor, RaungWriter writer, AnnotationType type) {
		super(clsVisitor.getApi());
		this.clsVisitor = clsVisitor;
		this.writer = writer;
		this.type = type;
	}

	@Override
	public void visit(@Nullable String name, Object value) {
		startAssign(name).add(RaungTypes.format(value));
	}

	@Override
	public void visitEnum(@Nullable String name, String descriptor, String value) {
		startAssign(name).add(Directive.ENUM).add(descriptor).space().add(value);
	}

	@Override
	public AnnotationVisitor visitAnnotation(@Nullable String name, String descriptor) {
		startAssign(name)
				.add('.').add(AnnotationType.SUB.getName()).space().add(descriptor)
				.increaseIndent();
		return new RaungAnnotationVisitor(this.clsVisitor, writer, AnnotationType.SUB);
	}

	@Override
	public AnnotationVisitor visitArray(@Nullable String name) {
		startAssign(name)
				.add('.').add(AnnotationType.ARRAY.getName())
				.increaseIndent();
		return new RaungAnnotationVisitor(this.clsVisitor, writer, AnnotationType.ARRAY);
	}

	@Override
	public void visitEnd() {
		writer.decreaseIndent()
				.startLine(".end ").add(this.type.getName());
	}

	private RaungWriter startAssign(String name) {
		writer.startLine();
		if (name != null) {
			writer.add(name).add(" = ");
		}
		return writer;
	}
}

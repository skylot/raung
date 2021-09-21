package io.github.skylot.raung.disasm.impl.visitors;

import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.AnnotationVisitor;

import io.github.skylot.raung.common.DirectiveToken;
import io.github.skylot.raung.disasm.impl.utils.RaungTypes;
import io.github.skylot.raung.disasm.impl.utils.RaungWriter;

public class RaungAnnotationVisitor extends AnnotationVisitor {

	private final RaungClassVisitor clsVisitor;
	private final String endDirective;

	public RaungAnnotationVisitor(RaungClassVisitor clsVisitor) {
		super(clsVisitor.getApi());
		this.clsVisitor = clsVisitor;
		this.endDirective = ".end annotation";
	}

	public RaungAnnotationVisitor(RaungClassVisitor clsVisitor, String endDirective) {
		super(clsVisitor.getApi());
		this.clsVisitor = clsVisitor;
		this.endDirective = endDirective;
	}

	@Override
	public void visit(@Nullable String name, Object value) {
		startAssign(name).add(RaungTypes.format(value));
	}

	@Override
	public void visitEnum(@Nullable String name, String descriptor, String value) {
		startAssign(name).add(DirectiveToken.ENUM).add(value).space().add(descriptor);
	}

	@Override
	public AnnotationVisitor visitAnnotation(@Nullable String name, String descriptor) {
		startAssign(name)
				.add(".subannotation").space().add(descriptor)
				.increaseIndent();
		return new RaungAnnotationVisitor(this.clsVisitor, ".end subannotation");
	}

	@Override
	public AnnotationVisitor visitArray(@Nullable String name) {
		startAssign(name)
				.add('{')
				.increaseIndent();
		return new RaungAnnotationVisitor(this.clsVisitor, "}");
	}

	@Override
	public void visitEnd() {
		clsVisitor.getWriter()
				.decreaseIndent()
				.startLine(endDirective);
	}

	private RaungWriter startAssign(String name) {
		RaungWriter writer = clsVisitor.getWriter();
		writer.startLine();
		if (name != null) {
			writer.add(name).add(" = ");
		}
		return writer;
	}
}

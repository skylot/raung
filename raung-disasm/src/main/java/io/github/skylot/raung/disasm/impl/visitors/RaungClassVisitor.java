package io.github.skylot.raung.disasm.impl.visitors;

import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.ModuleVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.RecordComponentVisitor;
import org.objectweb.asm.TypePath;

import io.github.skylot.raung.common.DirectiveToken;
import io.github.skylot.raung.common.RaungAccessFlags;
import io.github.skylot.raung.disasm.impl.utils.RaungWriter;

import static io.github.skylot.raung.common.RaungAccessFlags.Scope.CLASS;
import static io.github.skylot.raung.common.RaungAccessFlags.Scope.FIELD;
import static io.github.skylot.raung.common.RaungAccessFlags.Scope.METHOD;

public class RaungClassVisitor extends ClassVisitor {

	private final RaungWriter writer = new RaungWriter();

	public RaungClassVisitor() {
		super(Opcodes.ASM9);
	}

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		writer.add(DirectiveToken.VERSION.token()).space().add(version);
		writer.startLine(DirectiveToken.CLASS).add(RaungAccessFlags.format(access, CLASS)).space().add(name);
		writer.startLine(DirectiveToken.SUPER).add(superName);
		for (String iface : interfaces) {
			writer.startLine(".implements ").add(iface);
		}
		if (signature != null) {
			writer.startLine(DirectiveToken.SIGNATURE).add(signature);
		}
	}

	@Override
	public void visitSource(String source, String debug) {
		writer.startLine(DirectiveToken.SOURCE).add('"').add(source).add('"');
	}

	@Override
	public ModuleVisitor visitModule(String name, int access, String version) {
		return null;
	}

	@Override
	public void visitNestHost(String nestHost) {
	}

	@Override
	public void visitOuterClass(String owner, String name, String descriptor) {
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
	public void visitAttribute(Attribute attribute) {
	}

	@Override
	public void visitNestMember(String nestMember) {
	}

	@Override
	public void visitPermittedSubclass(String permittedSubclass) {
	}

	@Override
	public void visitInnerClass(String name, String outerName, String innerName, int access) {
	}

	@Override
	public RecordComponentVisitor visitRecordComponent(String name, String descriptor, String signature) {
		return null;
	}

	@Override
	public FieldVisitor visitField(int access, String name, String descriptor, @Nullable String signature, Object value) {
		writer.startLine()
				.startLine(DirectiveToken.FIELD)
				.add(RaungAccessFlags.format(access, FIELD)).space()
				.add(name).space()
				.add(descriptor);

		if (value != null) {
			writer.add(" = ").add(String.valueOf(value));
		}
		boolean closeField = false;
		writer.increaseIndent();
		if (signature != null) {
			writer.startLine(DirectiveToken.SIGNATURE).add(signature);
			closeField = true;
		}
		return new RaungFieldVisitor(this, closeField);
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String descriptor, @Nullable String signature, String[] exceptions) {
		writer.startLine()
				.startLine(DirectiveToken.METHOD)
				.add(RaungAccessFlags.format(access, METHOD)).space()
				.add(name).add(descriptor);
		if (signature != null) {
			writer.increaseIndent();
			writer.startLine(DirectiveToken.SIGNATURE).add(signature);
			writer.decreaseIndent();
		}
		writer.startLine(".end method");
		return null;
	}

	@Override
	public void visitEnd() {
	}

	public int getApi() {
		return api;
	}

	public RaungWriter getWriter() {
		return writer;
	}

	public String getResult() {
		return writer.getCode();
	}
}

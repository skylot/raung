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

import io.github.skylot.raung.common.Directive;
import io.github.skylot.raung.common.RaungAccessFlags;
import io.github.skylot.raung.disasm.impl.RaungDisasmBuilder;
import io.github.skylot.raung.disasm.impl.utils.JavaVersion;
import io.github.skylot.raung.disasm.impl.utils.RaungTypes;
import io.github.skylot.raung.disasm.impl.utils.RaungWriter;

import static io.github.skylot.raung.common.RaungAccessFlags.Scope.CLASS;
import static io.github.skylot.raung.common.RaungAccessFlags.Scope.FIELD;
import static io.github.skylot.raung.common.RaungAccessFlags.Scope.METHOD;

public class RaungClassVisitor extends ClassVisitor {

	private final RaungWriter writer = new RaungWriter();
	private final RaungDisasmBuilder args;

	private String clsFullName;

	public RaungClassVisitor(RaungDisasmBuilder args) {
		super(Opcodes.ASM9);
		this.args = args;
	}

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		writer.add(Directive.VERSION.token()).space().add(version)
				.add("  # ").add(JavaVersion.getNameStr(version));
		writer.startLine(Directive.CLASS).add(RaungAccessFlags.format(access, CLASS)).add(name);
		if (!superName.equals("java/lang/Object")) {
			writer.startLine(Directive.SUPER).add(superName);
		}
		for (String iface : interfaces) {
			writer.startLine(Directive.IMPLEMENTS).add(iface);
		}
		if (signature != null) {
			writer.startLine(Directive.SIGNATURE).add(signature);
		}
		this.clsFullName = name;

		if (args.isAutoFrames()) {
			writer.startLine(".auto frames");
		} else if (args.isAutoMax()) {
			writer.startLine(".auto maxs");
		}
	}

	@Override
	public void visitSource(String source, String debug) {
		writer.startLine(Directive.SOURCE).add('"').add(source).add('"');
	}

	@Override
	public ModuleVisitor visitModule(String name, int access, String version) {
		writer.startLine("# TODO: module");
		return null;
	}

	@Override
	public void visitNestHost(String nestHost) {
		writer.newLine().startLine(Directive.NEST).add("host").space().add(nestHost);
	}

	@Override
	public void visitNestMember(String nestMember) {
		writer.newLine().startLine(Directive.NEST).add("member").space().add(nestMember);
	}

	@Override
	public void visitOuterClass(String owner, String name, String descriptor) {
		writer.newLine()
				.startLine(Directive.OUTERCLASS)
				.add(owner).space().add(name).space().add(descriptor);
	}

	@Override
	public void visitInnerClass(String name, String outerName, String innerName, int access) {
		writer.startLine()
				.startLine(Directive.INNERCLASS)
				.add(RaungAccessFlags.format(access, CLASS))
				.add(innerName).space()
				.add(outerName);
		if (!name.equals(clsFullName)) {
			writer.space().add(name);
		}
	}

	@Override
	public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
		return RaungAnnotationVisitor.buildAnnotation(this, descriptor, visible);
	}

	@Override
	public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
		return RaungAnnotationVisitor.buildTypeAnnotation(this, typeRef, typePath, descriptor, visible);
	}

	@Override
	public void visitAttribute(Attribute attribute) {
		writer.startLine("# TODO: class attribute: " + attribute);
	}

	@Override
	public void visitPermittedSubclass(String permittedSubclass) {
		writer.startLine("# TODO: class permitted subclass: " + permittedSubclass);
	}

	@Override
	public RecordComponentVisitor visitRecordComponent(String name, String descriptor, String signature) {
		writer.startLine("# TODO: record component: " + name + " " + descriptor + " " + signature);
		return null;
	}

	@Override
	public FieldVisitor visitField(int access, String name, String descriptor, @Nullable String signature, Object value) {
		writer.startLine()
				.startLine(Directive.FIELD)
				.add(RaungAccessFlags.format(access, FIELD))
				.add(name).space()
				.add(descriptor);
		if (value != null) {
			writer.add(" = ").add(RaungTypes.format(value));
		}
		boolean closeField = false;
		writer.increaseIndent();
		if (signature != null) {
			writer.startLine(Directive.SIGNATURE).add(signature);
			closeField = true;
		}
		return new RaungFieldVisitor(this, closeField);
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String descriptor,
			@Nullable String signature, @Nullable String[] exceptions) {
		writer.newLine()
				.startLine(Directive.METHOD)
				.add(RaungAccessFlags.format(access, METHOD))
				.add(name).add(descriptor)
				.setIndent(2);
		if (exceptions != null) {
			for (String exc : exceptions) {
				writer.startLine(Directive.THROW).add(exc);
			}
		}
		if (signature != null) {
			writer.startLine(Directive.SIGNATURE).add(signature);
		}
		return new RaungMethodVisitor(this);
	}

	@Override
	public void visitEnd() {
		writer.newLine();
	}

	public int getApi() {
		return api;
	}

	public RaungWriter getWriter() {
		return writer;
	}

	public RaungDisasmBuilder getArgs() {
		return args;
	}

	public String getClsFullName() {
		return clsFullName;
	}

	public String getResult() {
		return writer.getCode();
	}
}

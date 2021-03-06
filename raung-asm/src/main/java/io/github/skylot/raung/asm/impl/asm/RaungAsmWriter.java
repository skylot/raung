package io.github.skylot.raung.asm.impl.asm;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import io.github.skylot.raung.asm.impl.parser.data.ClassData;
import io.github.skylot.raung.asm.impl.parser.data.FieldData;
import io.github.skylot.raung.asm.impl.parser.data.MethodData;
import io.github.skylot.raung.asm.impl.parser.data.TypeRefPathData;

public class RaungAsmWriter {

	public static ClassData buildCls(ClassData cls) {
		ClassWriter cw = cls.visitCls();
		cw.visitEnd();
		return cls;
	}

	public static ClassWriter visitCls(ClassData cls) {
		ClassWriter cw = new RaungClassWriter(cls);
		int version = cls.getVersion() == 0 ? 52 : cls.getVersion();
		cw.visit(version, cls.getAccessFlags(), cls.getName(),
				cls.getSignature(), cls.getSuperCls(),
				cls.getInterfaces().toArray(new String[0]));
		String source = cls.getSource();
		if (source != null) {
			cw.visitSource(source, null);
		}
		return cw;
	}

	public static MethodVisitor visitMethod(MethodData mth) {
		ClassWriter cw = mth.getClassData().visitCls();
		return cw.visitMethod(mth.getAccessFlags(), mth.getName(), mth.getDescriptor(),
				mth.getSignature(), mth.getThrows().toArray(new String[0]));
	}

	public static FieldVisitor visitField(ClassWriter cw, FieldData field) {
		return cw.visitField(field.getAccessFlags(), field.getName(),
				field.getType(), field.getSignature(), field.getValue());
	}

	public static void attachInsnAnnotation(MethodData methodData, InsnAnnotationNode insnAnn) {
		TypeRefPathData pathData = insnAnn.getPathData();
		int typeRef = pathData.getTypeRef().getValue();
		AnnotationVisitor av = methodData.getAsmMethodVisitor()
				.visitInsnAnnotation(typeRef, pathData.getPath(), insnAnn.getType(), insnAnn.isVisible());
		insnAnn.accept(av);
	}
}

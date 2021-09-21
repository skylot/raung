package io.github.skylot.raung.asm.impl.parser;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;

import io.github.skylot.raung.asm.impl.parser.data.AnnotationData;
import io.github.skylot.raung.asm.impl.parser.data.ClassData;
import io.github.skylot.raung.asm.impl.parser.data.FieldData;

public class RaungAsmWriter {

	public static byte[] writeCls(ClassData classData) {
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		write(cw, classData);
		cw.visitEnd();
		return cw.toByteArray();
	}

	private static void write(ClassWriter cw, ClassData cls) {
		cw.visit(cls.getVersion(), cls.getAccessFlags(), cls.getName(),
				cls.getSignature(), cls.getSuperCls(), cls.getInterfaces().toArray(new String[0]));

		for (FieldData field : cls.getFields()) {
			FieldVisitor fv = cw.visitField(field.getAccessFlags(), field.getName(),
					field.getType(), field.getSignature(), field.getValue());
			addFieldAnnotations(field, fv);
			fv.visitEnd();
		}
	}

	private static void addFieldAnnotations(FieldData field, FieldVisitor fv) {
		for (AnnotationData ann : field.getAnnotations()) {
			AnnotationVisitor av = fv.visitAnnotation(ann.getType(), ann.isVisible());
			ann.getValues().forEach(av::visit);
			av.visitEnd();
		}
	}
}

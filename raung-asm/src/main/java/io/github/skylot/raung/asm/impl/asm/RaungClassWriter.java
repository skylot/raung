package io.github.skylot.raung.asm.impl.asm;

import org.objectweb.asm.ClassWriter;

import io.github.skylot.raung.asm.impl.parser.data.ClassData;
import io.github.skylot.raung.asm.impl.utils.RaungAsmException;

public class RaungClassWriter extends ClassWriter {

	public RaungClassWriter(ClassData cls) {
		super(cls.getAuto().getAsmComputeFlag());
	}

	/**
	 * Default implementation will try to load classes with `Class.forName()`
	 */
	@Override
	protected String getCommonSuperClass(String type1, String type2) {
		if (isJavaStdLib(type1) && isJavaStdLib(type2)) {
			return super.getCommonSuperClass(type1, type2);
		}
		// TODO: try to search and load these classes to get class hierarchy
		throw new RaungAsmException("Can't calculate common super class for types: " + type1 + " and " + type2
				+ ". Try to remove '.auto frames' and manually set '.max' and '.stack' directives.");
	}

	private boolean isJavaStdLib(String type) {
		return type.startsWith("java/");
	}
}

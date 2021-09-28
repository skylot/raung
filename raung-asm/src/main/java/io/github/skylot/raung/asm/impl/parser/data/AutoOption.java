package io.github.skylot.raung.asm.impl.parser.data;

import org.objectweb.asm.ClassWriter;

public enum AutoOption {
	DISABLE(0),
	MAXS(ClassWriter.COMPUTE_MAXS),
	FRAMES(ClassWriter.COMPUTE_FRAMES);

	private final int asmComputeFlag;

	AutoOption(int asmComputeFlag) {
		this.asmComputeFlag = asmComputeFlag;
	}

	public int getAsmComputeFlag() {
		return asmComputeFlag;
	}
}

package io.github.skylot.raung.asm.impl.utils;

public class RaungAsmTokenException extends RuntimeException {
	private final int offsetInToken;

	public RaungAsmTokenException(String message, int offset) {
		super(message);
		this.offsetInToken = offset;
	}

	public int getOffsetInToken() {
		return offsetInToken;
	}
}

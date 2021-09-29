package io.github.skylot.raung.disasm.impl.visitors.data;

import org.jetbrains.annotations.Nullable;

public class TryCatchBlock {
	private final LabelData start;
	private final LabelData end;
	private final LabelData handler;
	@Nullable
	private final String type;

	public TryCatchBlock(LabelData start, LabelData end, LabelData handler, @Nullable String type) {
		this.start = start;
		this.end = end;
		this.handler = handler;
		this.type = type;
	}

	public LabelData getStart() {
		return start;
	}

	public LabelData getEnd() {
		return end;
	}

	public LabelData getHandler() {
		return handler;
	}

	@Nullable
	public String getType() {
		return type;
	}
}

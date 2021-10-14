package io.github.skylot.raung.disasm.impl.visitors.data;

import org.jetbrains.annotations.Nullable;

public class TryCatchBlock {
	private final int id;
	private final LabelData start;
	private final LabelData end;
	private final LabelData handler;
	@Nullable
	private final String type;

	public TryCatchBlock(int id, LabelData start, LabelData end, LabelData handler, @Nullable String type) {
		this.id = id;
		this.start = start;
		this.end = end;
		this.handler = handler;
		this.type = type;
	}

	public int getId() {
		return id;
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

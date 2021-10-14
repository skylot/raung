package io.github.skylot.raung.asm.impl.parser.data;

public class TryCatchBlock implements Comparable<TryCatchBlock> {
	private final int id;
	private final RaungLabel start;
	private final RaungLabel end;
	private final RaungLabel handler;
	private final String type;

	public TryCatchBlock(int id, RaungLabel start, RaungLabel end, RaungLabel handler, String type) {
		this.id = id;
		this.start = start;
		this.end = end;
		this.handler = handler;
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public RaungLabel getStart() {
		return start;
	}

	public RaungLabel getEnd() {
		return end;
	}

	public RaungLabel getHandler() {
		return handler;
	}

	public String getType() {
		return type;
	}

	@Override
	public int compareTo(TryCatchBlock other) {
		return Integer.compare(this.id, other.id);
	}

	@Override
	public String toString() {
		return "TryCatchBlock{id=" + id
				+ ", start=" + start + ", end=" + end
				+ ", handler=" + handler + ", type='" + type + "'}";
	}
}

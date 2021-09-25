package io.github.skylot.raung.disasm.impl;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import io.github.skylot.raung.disasm.api.IRaungDisasm;
import io.github.skylot.raung.disasm.impl.utils.RaungDisasmException;

public class RaungDisasmBuilder implements IRaungDisasm {

	private List<Path> inputs;
	private Path output;

	private boolean ignoreDebugInfo = false;

	@Override
	public IRaungDisasm inputs(List<Path> inputs) {
		this.inputs = inputs;
		return this;
	}

	@Override
	public IRaungDisasm input(Path input) {
		if (this.inputs == null) {
			this.inputs = new ArrayList<>();
		}
		this.inputs.add(input);
		return this;
	}

	@Override
	public IRaungDisasm output(Path out) {
		this.output = out;
		return this;
	}

	@Override
	public IRaungDisasm ignoreDebugInfo() {
		this.ignoreDebugInfo = true;
		return this;
	}

	public List<Path> getInputs() {
		return inputs;
	}

	public Path getOutput() {
		return output;
	}

	public boolean isIgnoreDebugInfo() {
		return ignoreDebugInfo;
	}

	@Override
	public void execute() {
		RaungDisasmExecutor.process(this);
	}

	@Override
	public String executeForSingleClass(Path input) {
		return RaungDisasmExecutor.processSingleClass(this, input);
	}

	@Override
	public String executeForInputStream(InputStream input) {
		try {
			return RaungDisasmExecutor.processInputStream(this, input);
		} catch (Exception e) {
			throw new RaungDisasmException("Failed to process input stream", e);
		}
	}

	@Override
	public String toString() {
		return "RaungDisasmArgs{"
				+ "input=" + inputs
				+ ", output=" + output
				+ ", ignoreDebugInfo=" + ignoreDebugInfo
				+ '}';
	}
}

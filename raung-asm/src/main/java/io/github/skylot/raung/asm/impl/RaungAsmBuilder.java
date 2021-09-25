package io.github.skylot.raung.asm.impl;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import io.github.skylot.raung.asm.api.IRaungAsm;
import io.github.skylot.raung.asm.impl.utils.RaungAsmException;

public class RaungAsmBuilder implements IRaungAsm {
	private List<Path> inputs;
	private Path output;

	@Override
	public IRaungAsm inputs(List<Path> inputs) {
		this.inputs = inputs;
		return this;
	}

	@Override
	public IRaungAsm input(Path input) {
		if (this.inputs == null) {
			this.inputs = new ArrayList<>();
		}
		this.inputs.add(input);
		return this;
	}

	@Override
	public IRaungAsm output(Path out) {
		this.output = out;
		return this;
	}

	public List<Path> getInputs() {
		return inputs;
	}

	public Path getOutput() {
		return output;
	}

	@Override
	public void execute() {
		RaungAsmExecutor.process(this);
	}

	@Override
	public byte[] executeForSingleClass(Path input) {
		return RaungAsmExecutor.processSingleClass(this, input);
	}

	@Override
	public byte[] executeForInputStream(InputStream input) {
		try {
			return RaungAsmExecutor.processInputStream(this, input, null);
		} catch (Exception e) {
			throw new RaungAsmException("Failed to process input stream", e);
		}
	}

	@Override
	public String toString() {
		return "RaungAsmArgs{input=" + inputs + ", output=" + output + '}';
	}
}

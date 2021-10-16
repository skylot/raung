package io.github.skylot.raung.disasm.impl.utils;

import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.skylot.raung.disasm.impl.RaungDisasmBuilder;

public class ValidateDisasmArgs {
	private static final Logger LOG = LoggerFactory.getLogger(ValidateDisasmArgs.class);

	public static void process(RaungDisasmBuilder args) {
		if (args.getInputs().isEmpty()) {
			throw new RaungDisasmException("Empty inputs");
		}
		for (Path input : args.getInputs()) {
			if (!Files.exists(input)) {
				throw new RaungDisasmException("Input not found: " + input.toAbsolutePath());
			}
		}
		processOptions(args);
	}

	public static void processOptions(RaungDisasmBuilder args) {
		if (args.isAutoFrames()) {
			args.autoMax(true);
		}
		LOG.debug("Effective args: {}", args);
	}
}

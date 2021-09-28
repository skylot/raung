package io.github.skylot.raung.disasm.impl.utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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
		Path output = args.getOutput();
		if (output == null) {
			args.output(getOutDirFromInputs(args.getInputs()));
		} else {
			if (Files.isRegularFile(output)) {
				throw new RaungDisasmException("Output already exists as a regular file. Expect directory");
			}
		}
		processOptions(args);
	}

	public static void processOptions(RaungDisasmBuilder args) {
		LOG.debug("Effective args: {}", args);
	}

	private static Path getOutDirFromInputs(List<Path> inputs) {
		Path input = inputs.get(0);
		Path inputParent = input.getParent();
		Path parentDir = inputParent == null ? Paths.get(".") : inputParent;
		String fileName = input.getFileName().toString();
		int extPos = fileName.lastIndexOf('.');
		if (extPos == -1) {
			return parentDir.resolve(fileName + "-raung");
		}
		return parentDir.resolve(fileName.substring(0, extPos));
	}
}

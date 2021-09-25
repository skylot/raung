package io.github.skylot.raung.asm.impl.utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.skylot.raung.asm.impl.RaungAsmBuilder;
import io.github.skylot.raung.common.utils.FileUtils;

public class ValidateAsmArgs {
	private static final Logger LOG = LoggerFactory.getLogger(ValidateAsmArgs.class);

	public static void process(RaungAsmBuilder args) {
		if (args.getInputs().isEmpty()) {
			throw new RaungAsmException("Empty inputs");
		}
		for (Path input : args.getInputs()) {
			if (!Files.exists(input)) {
				throw new RaungAsmException("Input not found: " + input.toAbsolutePath());
			}
		}
		Path output = args.getOutput();
		if (output == null) {
			args.output(getOutDirFromInputs(args.getInputs()));
		}
		LOG.debug("Effective args: {}", args);
	}

	private static Path getOutDirFromInputs(List<Path> inputs) {
		Path input = inputs.get(0);
		Path parent = FileUtils.getParentDir(input);
		String fileName = input.getFileName().toString();
		Path candidate = parent.resolve(fileName + ".jar");
		if (!Files.exists(candidate)) {
			return candidate;
		}
		return parent.resolve(fileName + "-raung.jar");
	}
}

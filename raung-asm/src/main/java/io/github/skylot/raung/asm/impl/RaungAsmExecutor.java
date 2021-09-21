package io.github.skylot.raung.asm.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.jetbrains.annotations.Nullable;

import io.github.skylot.raung.asm.impl.parser.RaungAsmWriter;
import io.github.skylot.raung.asm.impl.parser.RaungParser;
import io.github.skylot.raung.asm.impl.utils.RaungAsmException;

public class RaungAsmExecutor {
	public static void process(RaungAsmBuilder args) {

	}

	public static byte[] processSingleClass(Path inputPath) {
		try (InputStream input = Files.newInputStream(inputPath);
				RaungParser parser = new RaungParser(input, inputPath.toString())) {
			return RaungAsmWriter.writeCls(parser.parse());
		} catch (IOException e) {
			throw new RaungAsmException("Failed to assemble input", e);
		}
	}

	public static byte[] processInputStream(InputStream input, @Nullable String fileName) {
		try (RaungParser parser = new RaungParser(input, fileName)) {
			return RaungAsmWriter.writeCls(parser.parse());
		} catch (IOException e) {
			throw new RaungAsmException("Failed to assemble input", e);
		}
	}
}

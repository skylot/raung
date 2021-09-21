package io.github.skylot.raung.disasm.impl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.objectweb.asm.ClassReader;

import io.github.skylot.raung.disasm.impl.utils.RaungDisasmException;
import io.github.skylot.raung.disasm.impl.visitors.RaungClassVisitor;

public class RaungDisasmExecutor {

	public static void process(RaungDisasmBuilder args) {
		Path clsFile = args.getInputs().get(0);
		String code = processSingleClass(clsFile);
		System.out.println(code);
	}

	public static String processSingleClass(Path clsFile) {
		try (InputStream in = new BufferedInputStream(Files.newInputStream(clsFile, StandardOpenOption.READ))) {
			return processInputStream(in);
		} catch (IOException e) {
			throw new RaungDisasmException("Failed to process class file: " + clsFile, e);
		}
	}

	public static String processInputStream(InputStream in) throws IOException {
		ClassReader reader = new ClassReader(in);
		RaungClassVisitor visitor = new RaungClassVisitor();
		reader.accept(visitor, ClassReader.SKIP_FRAMES);
		return visitor.getResult();
	}
}

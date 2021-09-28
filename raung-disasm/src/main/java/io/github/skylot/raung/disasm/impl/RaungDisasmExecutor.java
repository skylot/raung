package io.github.skylot.raung.disasm.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

import org.objectweb.asm.ClassReader;

import io.github.skylot.raung.common.utils.FileUtils;
import io.github.skylot.raung.common.utils.ZipUtils;
import io.github.skylot.raung.disasm.impl.utils.RaungDisasmException;
import io.github.skylot.raung.disasm.impl.utils.ValidateDisasmArgs;
import io.github.skylot.raung.disasm.impl.visitors.RaungClassVisitor;

public class RaungDisasmExecutor {

	public static void process(RaungDisasmBuilder args) {
		ValidateDisasmArgs.process(args);
		List<Path> inputs = args.getInputs();
		for (Path input : FileUtils.expandDirs(inputs)) {
			runForFile(args, input);
		}
	}

	public static String processSingleClass(RaungDisasmBuilder args, Path clsFile) {
		FileUtils.checkInputFile(clsFile);
		ValidateDisasmArgs.processOptions(args);
		return runForSingleClass(args, clsFile).getResult();
	}

	public static String processInputStream(RaungDisasmBuilder args, InputStream in) throws IOException {
		ValidateDisasmArgs.processOptions(args);
		return runForInputStream(args, in).getResult();
	}

	private static RaungClassVisitor runForSingleClass(RaungDisasmBuilder args, Path clsFile) {
		try (InputStream in = new BufferedInputStream(Files.newInputStream(clsFile, StandardOpenOption.READ))) {
			return runForInputStream(args, in);
		} catch (IOException e) {
			throw new RaungDisasmException("Failed to process class file: " + clsFile, e);
		}
	}

	private static RaungClassVisitor runForInputStream(RaungDisasmBuilder args, InputStream in) throws IOException {
		ClassReader reader = new ClassReader(in);
		RaungClassVisitor visitor = new RaungClassVisitor(args);
		reader.accept(visitor, 0); // TODO: add option for skip frames (if '.auto frames' will be used)
		return visitor;
	}

	private static void runForFile(RaungDisasmBuilder args, Path inputFile) {
		String fileName = inputFile.getFileName().toString();
		if (fileName.endsWith(".class")) {
			saveResult(args, runForSingleClass(args, inputFile));
			return;
		}
		if (fileName.endsWith(".jar") || fileName.endsWith(".zip")) {
			runForZip(args, inputFile);
		}
	}

	private static void runForZip(RaungDisasmBuilder args, Path zipPath) {
		ZipUtils.visitZipEntries(zipPath.toFile(), (zip, entry) -> {
			String entryName = entry.getName();
			if (entryName.endsWith(".class")) {
				try (InputStream in = ZipUtils.getInputStreamForEntry(zip, entry)) {
					saveResult(args, runForInputStream(args, in));
				} catch (Exception e) {
					throw new RuntimeException("Error process zip entry: " + entry.getName(), e);
				}
			}
			return null;
		});
	}

	private static void saveResult(RaungDisasmBuilder args, RaungClassVisitor rcv) {
		String fileName = rcv.getClsFullName().replace('/', File.separatorChar) + ".raung";
		FileUtils.saveFile(args.getOutput(), fileName, rcv.getResult());
	}
}

package io.github.skylot.raung.disasm.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Objects;

import org.objectweb.asm.ClassReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.skylot.raung.common.utils.FileUtils;
import io.github.skylot.raung.common.utils.ZipUtils;
import io.github.skylot.raung.disasm.impl.utils.RaungDisasmException;
import io.github.skylot.raung.disasm.impl.utils.ValidateDisasmArgs;
import io.github.skylot.raung.disasm.impl.visitors.RaungClassVisitor;

public class RaungDisasmExecutor {
	private static final Logger LOG = LoggerFactory.getLogger(RaungDisasmExecutor.class);

	public static void process(RaungDisasmBuilder args) {
		ValidateDisasmArgs.process(args);
		processFiles(args);
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

	private static void processFiles(RaungDisasmBuilder args) {
		Path output = args.getOutput();
		boolean isOutputRaungFile = Objects.equals(FileUtils.getExt(output), "raung");
		List<Path> inputFiles = FileUtils.expandDirs(args.getInputs());
		if (inputFiles.size() == 1 && inputFiles.get(0).getFileName().toString().endsWith(".class")) {
			// single class file as input
			Path inputFile = inputFiles.get(0);
			RaungClassVisitor rcv = runForSingleClass(args, inputFile);
			if (output == null) {
				// save single class disasm to '.raung' file with same base name
				String fileName = inputFile.getFileName().toString();
				String baseFileName = fileName.substring(0, fileName.length() - 6) + ".raung";
				Path raungFile = inputFile.toAbsolutePath().getParent().resolve(baseFileName);
				LOG.info("Saving to {}", raungFile);
				FileUtils.saveFile(raungFile, rcv.getResult());
				return;
			}
			if (isOutputRaungFile) {
				FileUtils.saveFile(output, rcv.getResult());
				return;
			}
			// output is directory
			saveResult(args, rcv);
			return;
		}
		// save multiple results to directory
		if (isOutputRaungFile || Files.isRegularFile(output)) {
			throw new RaungDisasmException("Expect output to be directory, got file: " + output);
		}
		for (Path input : inputFiles) {
			runForFile(args, input);
		}
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
		Path resDir = args.getOutput().resolve("resources");
		ZipUtils.visitZipEntries(zipPath.toFile(), (zip, entry) -> {
			String entryName = entry.getName();
			if (entryName.endsWith(".class")) {
				try (InputStream in = ZipUtils.getInputStreamForEntry(zip, entry)) {
					saveResult(args, runForInputStream(args, in));
				} catch (Exception e) {
					LOG.error("Error process zip entry: {}", entry.getName(), e);
				}
			} else if (!entry.isDirectory()) {
				// save resources
				try (InputStream in = ZipUtils.getInputStreamForEntry(zip, entry)) {
					FileUtils.saveInputStream(resDir, entryName, in);
				} catch (Exception e) {
					LOG.error("Error process zip entry: {}", entry.getName(), e);
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

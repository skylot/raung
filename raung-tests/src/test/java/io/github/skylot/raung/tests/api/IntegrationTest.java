package io.github.skylot.raung.tests.api;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.assertj.core.util.diff.DiffUtils;
import org.assertj.core.util.diff.Patch;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;

import io.github.skylot.raung.asm.RaungAsm;
import io.github.skylot.raung.disasm.RaungDisasm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class IntegrationTest {

	protected String runChecksFor(Class<?> cls) {
		try {
			Path classFile = locateClassFile(cls);
			String disasm = disasmFromFile(classFile);
			printCode(disasm);
			byte[] bytes = assembleClass(disasm);

			compareResults("Raung diff", disasm, disasmFromBytes(bytes));
			compareResults("ASM diff", disasmWithASM(classFile), disasmWithASM(bytes));
			checkClassWithAsm(bytes);
			return disasm;
		} catch (Exception e) {
			fail("Check failed", e);
			return null;
		}
	}

	private String disasmFromFile(Path input) {
		return RaungDisasm.create().executeForSingleClass(input);
	}

	private String disasmFromBytes(byte[] bytes) throws IOException {
		try (InputStream input = new ByteArrayInputStream(bytes)) {
			return RaungDisasm.create().executeForInputStream(input);
		}
	}

	private byte[] assembleClass(String code) throws IOException {
		try (InputStream input = new ByteArrayInputStream(code.getBytes(StandardCharsets.UTF_8))) {
			return RaungAsm.create().executeForInputStream(input);
		}
	}

	private void checkClassWithAsm(byte[] bytes) {
		StringWriter results = new StringWriter();
		CheckClassAdapter.verify(new ClassReader(bytes), false, new PrintWriter(results));
		assertThat(results.toString()).isEmpty();
	}

	private static String disasmWithASM(byte[] bytes) throws IOException {
		return disasmWithASM(new ByteArrayInputStream(bytes));
	}

	private static String disasmWithASM(Path file) throws IOException {
		return disasmWithASM(Files.newInputStream(file));
	}

	private static String disasmWithASM(InputStream in) throws IOException {
		StringWriter out = new StringWriter();
		TraceClassVisitor tcv = new TraceClassVisitor(new PrintWriter(out));
		new ClassReader(in).accept(tcv, 0);
		return out.toString();
	}

	private Path locateClassFile(Class<?> cls) {
		try {
			Path path = Paths.get(cls.getProtectionDomain().getCodeSource().getLocation().toURI());
			if (Files.isDirectory(path)) {
				String clsFile = cls.getName().replace('.', '/') + ".class";
				Path file = path.resolve(clsFile);
				System.out.println("Class file location: " + file);
				return file;
			}
			return path;
		} catch (Exception e) {
			fail("Failed to find class file location: " + cls.getName(), e);
			return null;
		}
	}

	private void printCode(String code) {
		System.out.println("===================================");
		System.out.println(code);
		System.out.println("===================================");
	}

	private void compareResults(String message, String firstStr, String secondStr) {
		if (!firstStr.equals(secondStr)) {
			// print short diff
			printDiff(message, firstStr, secondStr);
			// Intellij Idea will suggest to view nice full diff
			assertEquals(firstStr, secondStr, message);
		}
	}

	private void printDiff(String message, String first, String second) {
		Patch<String> diff = DiffUtils.diff(toLines(first), toLines(second));
		System.out.println("-------------- " + message + " --------------");
		diff.getDeltas().forEach(System.out::println);
		System.out.println("--------------------------------------");
	}

	private List<String> toLines(String str) {
		List<String> list = new ArrayList<>();
		Collections.addAll(list, str.split("\n"));
		return list;
	}
}

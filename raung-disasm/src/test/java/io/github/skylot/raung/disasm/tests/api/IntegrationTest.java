package io.github.skylot.raung.disasm.tests.api;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import io.github.skylot.raung.disasm.impl.RaungDisasmExecutor;

import static org.junit.jupiter.api.Assertions.fail;

public class IntegrationTest {

	protected String disasmCls(Class<?> cls) {
		try {
			Path input = locateClassFile(cls);
			String code = disasmSingleClass(input);
			printCode(code);
			return code;
		} catch (Exception e) {
			fail("Class disasm failed", e);
			return null;
		}
	}

	private String disasmSingleClass(Path input) {
		return RaungDisasmExecutor.processSingleClass(input);
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
}

package io.github.skylot.raung.tests.integration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import io.github.skylot.raung.tests.api.IntegrationTest;

public class TestTryCatch extends IntegrationTest {

	public static class TestCls {
		private static final Object MKDIR_SYNC = new Object();

		public static void makeDirs(Path dir) {
			if (dir != null) {
				synchronized (MKDIR_SYNC) {
					try {
						createDirs(dir);
					} catch (IOException e) {
						throw new RuntimeException("Can't create directory " + dir);
					}
				}
			}
		}

		private static void createDirs(Path dir) throws IOException {
			Files.createDirectories(dir);
		}
	}

	@Test
	public void test() {
		runChecksFor(TestCls.class);
	}
}

package io.github.skylot.raung.common.utils;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtils {
	private static final Logger LOG = LoggerFactory.getLogger(FileUtils.class);

	public static List<Path> expandDirs(List<Path> paths) {
		List<Path> files = new ArrayList<>(paths.size());
		for (Path path : paths) {
			if (Files.isDirectory(path)) {
				expandDir(path, files);
			} else {
				files.add(path);
			}
		}
		return files;
	}

	private static void expandDir(Path dir, List<Path> files) {
		try (Stream<Path> walk = Files.walk(dir, FileVisitOption.FOLLOW_LINKS)) {
			walk.filter(Files::isRegularFile).forEach(files::add);
		} catch (Exception e) {
			LOG.error("Failed to list files in directory: {}", dir, e);
		}
	}

	public static Path getParentDir(Path path) {
		Path parent = path.getParent();
		if (parent == null) {
			return Paths.get(".");
		}
		return parent;
	}

	public static void makeDirsForFile(Path file) {
		if (file != null) {
			makeDirs(file.getParent());
		}
	}

	private static final Object MKDIR_SYNC = new Object();

	public static void makeDirs(@Nullable Path dir) {
		if (dir != null) {
			synchronized (MKDIR_SYNC) {
				try {
					Files.createDirectories(dir);
				} catch (IOException e) {
					throw new RuntimeException("Can't create directory " + dir);
				}
			}
		}
	}

	public static void saveFile(Path output, String fileName, String code) {
		Path file = output.resolve(fileName);
		makeDirsForFile(file);
		try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
			writer.write(code);
		} catch (Exception e) {
			throw new RuntimeException("File save failed: " + file.toAbsolutePath(), e);
		}
	}

	@Nullable
	public static String getExt(Path path) {
		String name = path.getFileName().toString();
		int extIdx = name.lastIndexOf('.');
		if (extIdx == -1 || extIdx == name.length() - 1) {
			return null;
		}
		return name.substring(extIdx + 1);
	}
}

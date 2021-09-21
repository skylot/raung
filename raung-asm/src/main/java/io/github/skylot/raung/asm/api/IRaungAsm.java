package io.github.skylot.raung.asm.api;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

public interface IRaungAsm {

	/**
	 * List of files or directories (will preform recursive search for '.raung' files).
	 */
	IRaungAsm inputs(List<Path> inputs);

	/**
	 * Single file or directory (will preform recursive search for '.raung' files).
	 * Can be called several times if needed.
	 */
	IRaungAsm input(Path input);

	/**
	 * Directory or single file (only for single input).
	 * Optional - current dir will be used if not set.
	 */
	IRaungAsm output(Path out);

	/**
	 * Preform assemble according to provided options.
	 */
	void execute();

	/**
	 * Single mode: process specified input file and return result as byte array.
	 * Any set inputs or output will be ignored.
	 */
	byte[] executeForSingleClass(Path input);

	/**
	 * Single mode: process specified input stream and return result as byte array.
	 * Any set inputs or output will be ignored.
	 */
	byte[] executeForInputStream(InputStream input);
}

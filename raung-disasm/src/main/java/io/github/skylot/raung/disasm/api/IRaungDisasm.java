package io.github.skylot.raung.disasm.api;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

public interface IRaungDisasm {

	/**
	 * List of files or directories (will preform recursive search for .jar or .class files).
	 */
	IRaungDisasm inputs(List<Path> inputs);

	/**
	 * Single file or directory (will preform recursive search for .jar or .class files).
	 * Can be called several times if needed.
	 */
	IRaungDisasm input(Path input);

	/**
	 * Directory or single file (only for single input).
	 * Optional - current dir will be used if not set.
	 */
	IRaungDisasm output(Path out);

	/**
	 * Set predefined set of options, see {@link RaungDisasmPreset}.
	 * Caution: this method will overwrite all previously set options (except 'inputs' and 'output')
	 */
	IRaungDisasm preset(RaungDisasmPreset preset);

	/**
	 * Don't add debug information (var names and line numbers)
	 */
	IRaungDisasm ignoreDebugInfo();

	/**
	 * Add optional catch number (allow preserving try-catches order).
	 * Default to false;
	 */
	IRaungDisasm saveCatchNumber(boolean save);

	/**
	 * Remove '.max' add '.auto maxs'
	 */
	IRaungDisasm autoMax(boolean auto);

	/**
	 * Remove '.max' and '.stack' add '.auto frames'
	 */
	IRaungDisasm autoFrames(boolean auto);

	/**
	 * Use switch with type autodetect: 'table' or 'lookup'
	 */
	IRaungDisasm autoSwitch(boolean auto);

	/**
	 * Preform disassemble according to provided options.
	 */
	void execute();

	/**
	 * Single mode: process specified input file and return result as string.
	 * Any set inputs or output will be ignored.
	 */
	String executeForSingleClass(Path input);

	/**
	 * Single mode: process specified input stream and return result as string.
	 * Any set inputs or output will be ignored.
	 */
	String executeForInputStream(InputStream input);

	/**
	 * Single mode: process specified input class file bytes and return result as string.
	 * Any set inputs or output will be ignored.
	 */
	String executeForBytes(byte[] bytes);
}

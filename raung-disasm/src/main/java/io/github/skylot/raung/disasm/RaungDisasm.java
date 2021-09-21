package io.github.skylot.raung.disasm;

import io.github.skylot.raung.disasm.api.IRaungDisasm;
import io.github.skylot.raung.disasm.impl.RaungDisasmBuilder;

public class RaungDisasm {

	/**
	 * API entry point. Check {@link IRaungDisasm} for available methods.
	 * Usage example:
	 * <br>
	 *
	 * <pre>
	 * RaungDisasm.create()
	 * 		.input(Paths.get("inputDirOrFile"))
	 * 		.output(Paths.get("outputDir"))
	 * 		.execute();
	 * </pre>
	 */
	public static IRaungDisasm create() {
		return new RaungDisasmBuilder();
	}
}

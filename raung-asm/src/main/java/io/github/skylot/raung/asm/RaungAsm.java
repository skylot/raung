package io.github.skylot.raung.asm;

import io.github.skylot.raung.asm.api.IRaungAsm;
import io.github.skylot.raung.asm.impl.RaungAsmBuilder;

public class RaungAsm {
	/**
	 * API entry point. Check {@link IRaungAsm} for available methods.
	 * Usage example:
	 * <br>
	 *
	 * <pre>
	 * RaungAsm.create()
	 * 		.input(Paths.get("inputDirOrFile"))
	 * 		.output(Paths.get("outputDir"))
	 * 		.execute();
	 * </pre>
	 */
	public static IRaungAsm create() {
		return new RaungAsmBuilder();
	}
}

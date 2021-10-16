package io.github.skylot.raung.cli;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import picocli.AutoComplete;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import io.github.skylot.raung.asm.RaungAsm;
import io.github.skylot.raung.disasm.RaungDisasm;
import io.github.skylot.raung.disasm.api.IRaungDisasm;
import io.github.skylot.raung.disasm.api.RaungDisasmPreset;

@Command(
		name = "raung",
		description = "Assembler/disassembler for java bytecode",
		subcommands = {
				RaungArgs.AsmArgs.class,
				RaungArgs.DisasmArgs.class,
				AutoComplete.GenerateCompletion.class
		},
		mixinStandardHelpOptions = true
)
public class RaungArgs implements Callable<Integer> {
	private static final Logger LOG = LoggerFactory.getLogger(RaungArgs.class);

	@Command(
			name = "assemble",
			aliases = { "a", "asm" },
			description = "Assemble .class or .jar from .raung files",
			mixinStandardHelpOptions = true,
			sortOptions = false
	)
	@SuppressWarnings({ "FieldCanBeLocal", "FieldMayBeFinal", "unused" })
	public static class AsmArgs implements Runnable {
		@Parameters(paramLabel = "INPUTS", arity = "1..*", description = "input directories or *.raung files")
		private List<Path> inputs;

		@Option(names = { "-o", "--output" }, description = "Output *.jar, *.class file or directory")
		private Path output;

		public void run() {
			RaungAsm.create()
					.inputs(inputs)
					.output(output)
					.execute();
			LOG.info("done");
		}
	}

	@Command(
			name = "disassemble",
			aliases = { "d", "dis" },
			description = "Disassemble .class or .jar to .raung files",
			mixinStandardHelpOptions = true,
			sortOptions = false,
			usageHelpWidth = 200 // avoid wrapping for long descriptions
	)
	@SuppressWarnings({ "FieldCanBeLocal", "FieldMayBeFinal", "unused" })
	public static class DisasmArgs implements Runnable {
		@Parameters(paramLabel = "INPUTS", arity = "1..*", description = "*.jar or *.class files")
		private List<Path> files;

		@Option(names = { "-o", "--output" }, description = "Output directory")
		private Path output;

		@Option(names = { "-p", "--preset" }, description = "Options preset name:\n${sys:RAUNG_DISASM_PRESETS_LIST}")
		private String preset = RaungDisasmPreset.DEFAULT.getName();

		@Option(names = { "-d", "--no-debug-info" }, description = "Don't add debug info")
		private boolean debugInfo = true;

		@Option(names = { "--auto-max" }, description = "Use '.auto maxs' and remove '.max' directives")
		private Boolean autoMax;

		@Option(names = { "--auto-frames" }, description = "Use '.auto frames' and remove '.max' and '.stack' directives")
		private Boolean autoFrames;

		@Option(names = { "--auto-switches" }, description = "Use auto switch instead 'table' and 'lookup'")
		private Boolean autoSwitch;

		@Option(names = { "--save-catch-numbers" }, description = "Save optional catch number to preserve order on rebuild")
		private Boolean catchNumber;

		@Option(names = { "-v", "--verbose" }, description = "Verbose logging")
		private boolean verbose = false;

		public static void initVars() {
			System.setProperty("RAUNG_DISASM_PRESETS_LIST", RaungDisasmPreset.buildFormattedList());
		}

		public void run() {
			applyLogLevel(verbose);
			IRaungDisasm disasm = RaungDisasm.create()
					.inputs(files)
					.output(output)
					.preset(RaungDisasmPreset.getByName(preset));
			if (!debugInfo) {
				disasm.ignoreDebugInfo();
			}
			set(autoMax, disasm::autoMax);
			set(autoFrames, disasm::autoFrames);
			set(autoSwitch, disasm::autoSwitch);
			set(catchNumber, disasm::saveCatchNumber);

			disasm.execute();
			LOG.info("done");
		}
	}

	@Spec
	CommandSpec spec;

	@Override
	public Integer call() {
		// empty input -> just print help
		spec.commandLine().usage(System.out);
		return 1;
	}

	public static CommandLine buildParser() {
		DisasmArgs.initVars();
		CommandLine cmd = new CommandLine(new RaungArgs());
		CommandLine gen = cmd.getSubcommands().get("generate-completion");
		gen.getCommandSpec().usageMessage().hidden(true);
		return cmd;
	}

	private static void applyLogLevel(boolean verbose) {
		if (verbose) {
			((ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).setLevel(Level.DEBUG);
		}
	}

	private static <T> void set(@Nullable T value, Function<T, IRaungDisasm> method) {
		if (value != null) {
			method.apply(value);
		}
	}
}

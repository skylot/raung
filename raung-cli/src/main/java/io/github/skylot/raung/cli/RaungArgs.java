package io.github.skylot.raung.cli;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
			sortOptions = false
	)
	@SuppressWarnings({ "FieldCanBeLocal", "FieldMayBeFinal", "unused" })
	public static class DisasmArgs implements Runnable {
		@Parameters(paramLabel = "FILES", arity = "1..*", description = "*.jar or *.class files")
		private List<Path> files;

		@Option(names = { "-o", "--output" }, description = "Output directory")
		private Path output;

		@Option(names = { "-d", "--no-debug-info" }, description = "Don't add debug info")
		private boolean debugInfo = true;

		@Option(names = { "--save-catch-numbers" }, description = "Save optional catch number to preserve order or rebuild")
		private boolean catchNumber = false;

		public void run() {
			IRaungDisasm disasm = RaungDisasm.create()
					.inputs(files)
					.output(output)
					.saveCatchNumber(catchNumber);
			if (!debugInfo) {
				disasm.ignoreDebugInfo();
			}
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
		CommandLine cmd = new CommandLine(new RaungArgs());
		CommandLine gen = cmd.getSubcommands().get("generate-completion");
		gen.getCommandSpec().usageMessage().hidden(true);
		return cmd;
	}
}

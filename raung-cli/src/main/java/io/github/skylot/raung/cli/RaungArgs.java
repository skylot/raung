package io.github.skylot.raung.cli;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;

import picocli.AutoComplete;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

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

	@Command(
			name = "assemble",
			aliases = { "a", "asm" },
			description = "Assemble .class or .jar from .raung files",
			mixinStandardHelpOptions = true
	)
	public static class AsmArgs {

		@Option(names = { "-a", "--algorithm" }, description = "MD5, SHA-1, SHA-256, ...")
		private String algorithm = "MD5";
	}

	@Command(
			name = "disassemble",
			aliases = { "d", "dis" },
			description = "Disassemble .class or .jar to .raung files",
			mixinStandardHelpOptions = true,
			sortOptions = false
	)
	public static class DisasmArgs implements Runnable {
		@Parameters(paramLabel = "FILES", arity = "1..*", description = "one or more .jar or .class files")
		private List<Path> files;

		@Option(names = { "-o", "--output" }, required = true, description = "Output directory")
		private Path output;

		@Option(names = { "-d", "--no-debug-info" }, description = "Don't add debug info")
		private boolean debugInfo = true;

		public void run() {
			IRaungDisasm disasm = RaungDisasm.create()
					.inputs(files)
					.output(output);
			if (!debugInfo) {
				disasm.ignoreDebugInfo();
			}
			disasm.execute();
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

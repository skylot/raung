package io.github.skylot.raung.cli;

public class RaungCLI {

	public static void main(String[] args) {
		int exitCode = RaungArgs.buildParser().execute(args);
		System.exit(exitCode);
	}
}

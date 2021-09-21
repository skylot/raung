plugins {
	id("raung.java-common")

	application
}

dependencies {
	implementation(project(":raung-asm"))
	implementation(project(":raung-disasm"))

	implementation("info.picocli:picocli:4.6.1")
}

application {
	mainClass.set("io.github.skylot.raung.cli.RaungCLI")
	applicationName = "raung"
}

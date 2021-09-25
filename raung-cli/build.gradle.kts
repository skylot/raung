plugins {
	id("raung.java-common")

	application
}

dependencies {
	implementation(project(":raung-asm"))
	implementation(project(":raung-disasm"))

	implementation("info.picocli:picocli:4.6.1")
	implementation("ch.qos.logback:logback-classic:1.2.6")
}

application {
	mainClass.set("io.github.skylot.raung.cli.RaungCLI")
	applicationName = "raung"
}

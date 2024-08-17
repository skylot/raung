plugins {
	id("raung.java-common")

	application
}

dependencies {
	implementation(project(":raung-asm"))
	implementation(project(":raung-disasm"))

	implementation("info.picocli:picocli:4.7.6")
	implementation("ch.qos.logback:logback-classic:1.5.7")
}

application {
	mainClass.set("io.github.skylot.raung.cli.RaungCLI")
	applicationName = "raung"
}

plugins {
	id("raung.java-common")

	application

	id("org.graalvm.buildtools.native") version "0.9.4"
}

dependencies {
	implementation(project(":raung-asm"))
	implementation(project(":raung-disasm"))

	implementation("info.picocli:picocli:4.6.1")
	annotationProcessor("info.picocli:picocli-codegen:4.6.1")

	implementation("org.slf4j:slf4j-simple:2.0.0-alpha5")
}

application {
	mainClass.set("io.github.skylot.raung.cli.RaungCLI")
	applicationName = "raung"
}

tasks.withType<JavaCompile> {
	val compilerArgs = options.compilerArgs
	compilerArgs.add("-Aproject=${project.group}/${project.name}")
}

nativeBuild {
	imageName.set("raung")
	buildArgs.add("-H:IncludeResources=simplelogger.properties")
	useFatJar.set(true)
}

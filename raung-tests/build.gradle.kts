plugins {
	id("raung.java-common")
}

dependencies {
	testImplementation(project(":raung-asm"))
	testImplementation(project(":raung-disasm"))

	testCompileOnly("org.jetbrains:annotations:24.1.0")
	testImplementation("commons-io:commons-io:2.16.1")
}

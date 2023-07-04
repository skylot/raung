plugins {
	id("raung.java-common")
}

dependencies {
	testImplementation(project(":raung-asm"))
	testImplementation(project(":raung-disasm"))

	testCompileOnly("org.jetbrains:annotations:24.0.1")
}

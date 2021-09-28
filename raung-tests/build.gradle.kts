plugins {
	id("raung.java-common")
}

dependencies {
	testImplementation(project(":raung-asm"))
	testImplementation(project(":raung-disasm"))
}

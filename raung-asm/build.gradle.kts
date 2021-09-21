plugins {
	id("raung.java-library")
}

dependencies {
	implementation(project(":raung-common"))

	api("org.ow2.asm:asm-util:9.2")
}

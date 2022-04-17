plugins {
	id("raung.java-library")
}

dependencies {
	api(project(":raung-common"))

	api("org.ow2.asm:asm-util:9.3")
}

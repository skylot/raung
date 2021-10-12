plugins {
	id("raung.java-common")

	`java-library`
	`maven-publish`
}

dependencies {
	api("org.slf4j:slf4j-api:1.7.32")
}

group = "io.github.skylot"
version = "0.0.1"

java {
	withJavadocJar()
	withSourcesJar()
}
publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			artifactId = project.name
			from(components["java"])
			versionMapping {
				usage("java-api") {
					fromResolutionOf("runtimeClasspath")
				}
				usage("java-runtime") {
					fromResolutionResult()
				}
			}
			pom {
				name.set("Raung")
				description.set("Assembler/disassembler for java bytecode")
				url.set("https://github.com/skylot/raung")
				licenses {
					license {
						name.set("MIT License")
						url.set("https://github.com/skylot/raung/blob/main/LICENSE")
					}
				}
				developers {
					developer {
						id.set("skylot")
						name.set("Skylot")
						email.set("skylot@gmail.com")
					}
				}
				scm {
					connection.set("scm:git:git://github.com/skylot/raung.git")
					url.set("https://github.com/skylot/raung")
				}
			}
		}
	}
}

tasks.javadoc {
	if (JavaVersion.current().isJava9Compatible) {
		(options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
	}
}

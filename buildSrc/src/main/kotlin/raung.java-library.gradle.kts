plugins {
	id("raung.java-common")

	`java-library`
	`maven-publish`
	signing
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
				name.set(project.name)
				description.set("Assembler/disassembler for java bytecode")
				url.set("https://github.com/skylot/raung")
				licenses {
					license {
						name.set("MIT License")
						url.set("http://www.opensource.org/licenses/mit-license.php")
					}
				}
				developers {
					developer {
						id.set("skylot")
						name.set("Skylot")
						email.set("skylot@gmail.com")
						url.set("https://github.com/skylot")
					}
				}
				scm {
					connection.set("scm:git:git://github.com/skylot/raung.git")
					developerConnection.set("scm:git:ssh://github.com:skylot/raung.git")
					url.set("https://github.com/skylot/raung")
				}
			}
		}
	}
	repositories {
		maven {
			// change URLs to point to your repos, e.g. http://my.org/repo
			val releasesRepoUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
			val snapshotsRepoUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
			url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
			credentials {
				username = project.properties["ossrhUser"].toString()
				password = project.properties["ossrhPassword"].toString()
			}
		}
	}
}

signing {
	sign(publishing.publications["mavenJava"])
}

tasks.javadoc {
	if (JavaVersion.current().isJava9Compatible) {
		(options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
	}
}

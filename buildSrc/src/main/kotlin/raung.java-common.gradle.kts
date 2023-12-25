plugins {
	java
	checkstyle
}

group = "io.github.skylot.raung"
version = System.getenv("RAUNG_VERSION") ?: "dev"

java {
	sourceCompatibility = JavaVersion.VERSION_11
	targetCompatibility = JavaVersion.VERSION_11
}

repositories {
	mavenCentral()
}

dependencies {
	testImplementation("ch.qos.logback:logback-classic:1.4.14")
	testImplementation("org.assertj:assertj-core:3.24.2")

	testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
	useJUnitPlatform()
}

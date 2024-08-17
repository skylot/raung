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
	testImplementation("ch.qos.logback:logback-classic:1.5.7")
	testImplementation("org.assertj:assertj-core:3.26.3")

	testImplementation("org.junit.jupiter:junit-jupiter:5.11.0")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
	useJUnitPlatform()
}

plugins {
	java
	checkstyle
}

group = "io.github.skylot.raung"
version = System.getenv("RAUNG_VERSION") ?: "dev"

java {
	sourceCompatibility = JavaVersion.VERSION_1_8
	targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
	mavenCentral()
}

dependencies {
	testImplementation("ch.qos.logback:logback-classic:1.4.8")
	testImplementation("org.assertj:assertj-core:3.24.2")

	testImplementation("org.junit.jupiter:junit-jupiter:5.9.3")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
	useJUnitPlatform()
}

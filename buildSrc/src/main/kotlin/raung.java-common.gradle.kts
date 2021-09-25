plugins {
	java
	checkstyle
}

group = "io.github.skylot.raung"
version = "0.0.1"

java {
	sourceCompatibility = JavaVersion.VERSION_1_8
	targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
	mavenLocal()
	mavenCentral()
}

dependencies {

	testImplementation("ch.qos.logback:logback-classic:1.2.6")
	testImplementation("org.assertj:assertj-core:3.21.0")

	testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.test {
	useJUnitPlatform()
}

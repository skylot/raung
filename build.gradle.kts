import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import java.util.*

plugins {
	id("com.github.ben-manes.versions") version "0.50.0"
	id("se.patrikerdes.use-latest-versions") version "0.2.18"
	id("com.diffplug.spotless") version "6.23.3"
}

repositories {
	mavenCentral()
	google()
}

// TODO: move to `raung.java-common` base configuration
allprojects {
	apply(plugin = "java")
	apply(plugin = "checkstyle")
	apply(plugin = "com.github.ben-manes.versions")
	apply(plugin = "se.patrikerdes.use-latest-versions")
	apply(plugin = "com.diffplug.spotless")

	spotless {
		java {
			importOrderFile("${rootDir}/config/code-formatter/eclipse.importorder")
			eclipse().configFile("${rootDir}/config/code-formatter/eclipse.xml")
			removeUnusedImports()
			encoding("UTF-8")
			trimTrailingWhitespace()
			endWithNewline()
		}
		format("misc") {
			target("**/*.gradle.kts", "**/*.md", "**/.gitignore")
			targetExclude(".gradle/**", ".idea/**", "*/build/**")
			indentWithTabs()
			trimTrailingWhitespace()
			endWithNewline()
		}
	}
}

tasks.withType<DependencyUpdatesTask> {
	rejectVersionIf {
		isNonStable(candidate.version)
	}
}

fun isNonStable(version: String): Boolean {
	val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase(Locale.getDefault()).contains(it) }
	val regex = "^[0-9,.v-]+(-r)?$".toRegex()
	val isStable = stableKeyword || regex.matches(version)
	return isStable.not()
}


var libProjects = listOf(":raung-common", ":raung-asm", ":raung-disasm")

tasks.register("publishLocal") {
	group = "raung"
	description = "Publish library packages into Maven local repo"

	libProjects.forEach {
		dependsOn(tasks.getByPath("$it:publishToMavenLocal"))
	}
}
tasks.register("publish") {
	group = "raung"
	description = "Publish library packages into Maven Central repo"

	libProjects.forEach {
		dependsOn(tasks.getByPath("$it:publish"))
	}
}

tasks.register("dist", Copy::class) {
	group = "raung"
	description = "Build raung-cli distribution package"

	from(tasks.getByPath(":raung-cli:distZip"))
	into(layout.buildDirectory.dir("dist"))
}

tasks.register("cleanBuild", Delete::class) {
	group = "raung"
	description = "Remove all build directories"

	delete(layout.buildDirectory)
}

tasks.getByName("clean").dependsOn("cleanBuild")

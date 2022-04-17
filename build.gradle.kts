import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
	id("com.github.ben-manes.versions") version "0.42.0"
	id("com.diffplug.spotless") version "6.4.2"
}

fun isNonStable(version: String): Boolean {
	val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.toUpperCase().contains(it) }
	val regex = "^[0-9,.v-]+(-r)?$".toRegex()
	val isStable = stableKeyword || regex.matches(version)
	return isStable.not()
}

tasks.withType<DependencyUpdatesTask> {
	rejectVersionIf {
		isNonStable(candidate.version)
	}
}

spotless {
	java {
		target("**/*.java")

		importOrderFile("config/code-formatter/eclipse.importorder")
		eclipse().configFile("config/code-formatter/eclipse.xml")
		if (JavaVersion.current() < JavaVersion.VERSION_16) {
			removeUnusedImports()
		} else {
			// google-format broken on java 16+ (https://github.com/diffplug/spotless/issues/834)
			println("Warning! Unused imports remove is disabled for Java 16+")
		}
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

var libProjects = listOf(":raung-common", ":raung-asm", ":raung-disasm")

tasks.register("publishLocal") {
	group = "raung"
	description = "Public library packages into Maven local repo"

	libProjects.forEach {
		dependsOn(tasks.getByPath("$it:publishToMavenLocal"))
	}
}
tasks.register("publish") {
	group = "raung"
	description = "Public library packages into Maven Central repo"

	libProjects.forEach {
		dependsOn(tasks.getByPath("$it:publish"))
	}
}

tasks.register("dist", Copy::class) {
	group = "raung"
	description = "Build raung-cli distribution package"

	from(tasks.getByPath(":raung-cli:distZip"))
	into(layout.buildDirectory)
}

tasks.register("clean", Delete::class) {
	group = "raung"
	description = "Remove all build directories"

	delete(layout.buildDirectory)
	subprojects.forEach { sp -> dependsOn(sp.tasks.named("clean")) }
}

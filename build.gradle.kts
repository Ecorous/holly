import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	application

	kotlin("jvm")
	kotlin("plugin.serialization")

	id("com.github.johnrengelman.shadow")
}

group = "org.ecorous.holly"
version = "0.1.0"

repositories {
	google()
	mavenCentral()

	maven {
		name = "Sonatype Snapshots (Legacy)"
		url = uri("https://oss.sonatype.org/content/repositories/snapshots")
	}

	maven {
		name = "Sonatype Snapshots"
		url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots")
	}
}

dependencies {
	implementation(libs.kord.extensions)
	implementation(libs.kotlin.stdlib)
	implementation(libs.kx.ser)

	// Logging dependencies
	implementation(libs.groovy)
	implementation(libs.jansi)
	implementation(libs.logback)
	implementation(libs.logback.groovy)
	implementation(libs.logging)

	// Database dependencies
	implementation(libs.bundles.exposed)
	implementation(libs.postgresql)
}

application {
	mainClass.set("org.ecorous.holly.AppKt")
}

tasks.withType<KotlinCompile> {
	// Current LTS version of Java
	kotlinOptions.jvmTarget = "17"

	kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
}

tasks.jar {
	manifest {
		attributes(
			"Main-Class" to "org.ecorous.holly.AppKt"
		)
	}
}

tasks.build {
	dependsOn(tasks["shadowJar"])
}

java {
	// Current LTS version of Java
	sourceCompatibility = JavaVersion.VERSION_17
	targetCompatibility = JavaVersion.VERSION_17
}


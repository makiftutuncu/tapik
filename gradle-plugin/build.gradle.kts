plugins {
	`kotlin-dsl`
	`java-gradle-plugin`
	`maven-publish`
	kotlin("plugin.serialization") version "2.2.0"
}

group = "dev.akif.tapik"
version = "0.1.0"

repositories {
	mavenCentral()
}

kotlin {
	jvmToolchain(21)
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-compiler-embeddable:2.2.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.2.0")
    implementation("org.ow2.asm:asm:9.6")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
}

gradlePlugin {
	plugins {
		create("tapikGradlePlugin") {
			id = "dev.akif.tapik.gradle"
			implementationClass = "dev.akif.tapik.gradle.TapikGradlePlugin"
			displayName = "Tapik Gradle Plugin"
			description = "Tapik code generator Gradle plugin"
			tags.set(listOf("tapik", "codegen"))
		}
	}
}

publishing {
	publications { /* created automatically by java-gradle-plugin; keep for mavenLocal */ }
}

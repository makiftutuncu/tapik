plugins {
	`kotlin-dsl`
	`java-gradle-plugin`
	`maven-publish`
	kotlin("plugin.serialization") version "2.2.0"
}

group = "dev.akif.tapik"

repositories {
	mavenCentral()
}

kotlin {
	jvmToolchain(21)
}

tasks.test {
	useJUnitPlatform()
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation(project(":core"))
    implementation(libs.kotlinCompilerEmbeddable)
    implementation(libs.kotlinStdlib)
    implementation(libs.asm)
    implementation(libs.asmTree)
    implementation(libs.kotlinSerializationJson)

    testImplementation(gradleTestKit())
    testImplementation(libs.bundles.testCommon)
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

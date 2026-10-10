import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("org.jetbrains.kotlin.jvm")
    `java-gradle-plugin`
    `maven-publish`
}

val tapikVersion = extra["tapikVersion"] as String

dependencies {
    implementation("dev.akif:tapik-common-plugin:$tapikVersion")

    testImplementation(platform("io.kotest:kotest-bom:6.2.4"))
    testImplementation("io.kotest:kotest-runner-junit5-jvm")
    testImplementation("io.kotest:kotest-assertions-core-jvm")
    testImplementation(gradleTestKit())
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
    withSourcesJar()
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_25)
        javaParameters.set(true)
    }
}

gradlePlugin {
    plugins {
        create("tapik") {
            id = "dev.akif.tapik.plugin.gradle"
            implementationClass = "dev.akif.tapik.plugin.gradle.TapikGradlePlugin"
            displayName = "tapik Gradle plugin"
            description = "Generates artifacts from type-safe tapik API contracts"
            tags.set(listOf("tapik", "code-generation", "kotlin"))
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

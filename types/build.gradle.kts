import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("buildsrc.convention.kotlin-jvm")
    alias(libs.plugins.ktlint)
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.named<KotlinCompile>("compileKotlin") {
    dependsOn(":code-generator:generate-types")
}

import org.gradle.kotlin.dsl.named
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("buildsrc.convention.kotlin-jvm")
    alias(libs.plugins.ktlint)
}

dependencies {
    api(project(":codec"))
    api(project(":types"))
    implementation(libs.arrowCore)
    testImplementation(kotlin("test"))
}

tasks.named<KotlinCompile>("compileKotlin") {
    dependsOn(":code-generator:generate-core")
}

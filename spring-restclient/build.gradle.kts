import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("buildsrc.convention.kotlin-jvm")
    alias(libs.plugins.ktlint)
}

dependencies {
    api(project(":core"))
    api(project(":jackson"))
    implementation(libs.arrowCore)
    implementation(libs.springWeb)
    testImplementation(kotlin("test"))
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.compilerOptions {
    freeCompilerArgs.set(listOf("-Xcontext-parameters"))
}

tasks.named<KotlinCompile>("compileKotlin") {
    dependsOn(":code-generator:generate-spring-restclient")
}

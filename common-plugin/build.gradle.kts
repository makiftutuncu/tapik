plugins {
    `kotlin-dsl`
    alias(libs.plugins.dokka)
}

repositories {
    mavenCentral()
}

val javaVersion = providers.gradleProperty("javaVersion").map(String::toInt).get()

kotlin {
    jvmToolchain(javaVersion)
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
    implementation(libs.kotlinStdlib)
    implementation(libs.asm)
    implementation(libs.asmTree)
    runtimeClasspath(libs.kotlinCompilerEmbeddable)
    testImplementation(libs.bundles.testCommon)
}

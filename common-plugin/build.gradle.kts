plugins {
    `kotlin-dsl`
    id("buildlogic.convention.kotlin-dsl")
    alias(libs.plugins.dokka)
}

dependencies {
    implementation(project(":core"))
    implementation(libs.kotlinStdlib)
    implementation(libs.asm)
    implementation(libs.asmTree)
    runtimeClasspath(libs.kotlinCompilerEmbeddable)
    testImplementation(libs.bundles.testCommon)
    testRuntimeOnly(project(":spring-restclient"))
    testRuntimeOnly(project(":spring-webmvc"))
}

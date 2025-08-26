plugins {
    id("buildsrc.convention.kotlin-jvm")
    alias(libs.plugins.ktlint)
}

dependencies {
    api(project(":core"))
    implementation(libs.arrowCore)
    implementation(libs.jacksonKotlin)
    testImplementation(kotlin("test"))
}

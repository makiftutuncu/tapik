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

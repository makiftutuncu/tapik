plugins {
    id("buildsrc.convention.kotlin-jvm")
    alias(libs.plugins.ktlint)
}

dependencies {
    api(project(":types"))
    implementation(libs.arrowCore)
    testImplementation(kotlin("test"))
}

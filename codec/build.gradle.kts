plugins {
    id("buildsrc.convention.kotlin-jvm")
    alias(libs.plugins.ktlint)
}

dependencies {
    api(project(":tuple"))
    implementation(libs.arrowCore)
    testImplementation(kotlin("test"))
}

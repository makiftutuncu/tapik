plugins {
    id("buildsrc.convention.kotlin-jvm")
    alias(libs.plugins.ktlint)
}

dependencies {
    implementation(libs.arrowCore)
    testImplementation(kotlin("test"))
}

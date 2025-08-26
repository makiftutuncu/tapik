plugins {
    id("buildsrc.convention.kotlin-jvm")
    alias(libs.plugins.ktlint)
}

dependencies {
    implementation(project(":core"))
    implementation(project(":jackson"))
    implementation(project(":spring-restclient"))
    implementation(libs.arrowCore)
    implementation(libs.springWeb)
}

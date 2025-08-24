plugins {
    id("buildsrc.convention.kotlin-jvm")
}

dependencies {
    api(project(":core"))
    api(project(":jackson"))
    implementation(libs.arrowCore)
    implementation(libs.springWeb)
    testImplementation(kotlin("test"))
}

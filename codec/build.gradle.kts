plugins {
    id("buildsrc.convention.kotlin-jvm")
}

dependencies {
    api(project(":tuple"))
    implementation(libs.arrowCore)
    testImplementation(kotlin("test"))
}

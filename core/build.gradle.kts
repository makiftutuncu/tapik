plugins {
    id("buildsrc.convention.kotlin-jvm")
}

dependencies {
    api(project(":codec"))
    api(project(":tuple"))
    implementation(libs.bundles.arrow)
    testImplementation(kotlin("test"))
}

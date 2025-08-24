plugins {
    id("buildsrc.convention.kotlin-jvm")
}

dependencies {
    api(project(":core"))
    implementation(libs.arrowCore)
    implementation(libs.jacksonKotlin)
    testImplementation(kotlin("test"))
}

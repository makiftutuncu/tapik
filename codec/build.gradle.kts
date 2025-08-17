plugins {
    id("buildsrc.convention.kotlin-jvm")
}

dependencies {
    implementation(libs.bundles.arrow)
    testImplementation(kotlin("test"))
}

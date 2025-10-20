plugins {
    id("buildsrc.convention.kotlin-jvm")
    alias(libs.plugins.ktlint)
}

dependencies {
    api(project(":codec"))
    implementation(libs.arrowCore)
    testImplementation(libs.bundles.testCommon)
}

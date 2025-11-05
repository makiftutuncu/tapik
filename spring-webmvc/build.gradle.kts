plugins {
    id("buildsrc.convention.kotlin-jvm")
    alias(libs.plugins.dokka)
    alias(libs.plugins.ktlint)
}

dependencies {
    api(project(":core"))
    implementation(libs.springWeb)
    testImplementation(libs.bundles.testCommon)
}

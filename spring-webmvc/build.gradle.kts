plugins {
    id("buildsrc.convention.kotlin-jvm")
    alias(libs.plugins.dokka)
    alias(libs.plugins.ktlint)
}

dependencies {
    api(project(":core"))
    compileOnly(project(":common-plugin"))
    implementation(libs.springWeb)
    testImplementation(libs.bundles.testCommon)
    testImplementation(project(":common-plugin"))
}

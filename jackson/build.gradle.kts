plugins {
    id("buildlogic.convention.kotlin-jvm")
    alias(libs.plugins.dokka)
    alias(libs.plugins.ktlint)
}

dependencies {
    api(project(":core"))
    implementation(libs.arrowCore)
    implementation(libs.jacksonKotlin)
    testImplementation(libs.bundles.testCommon)
}

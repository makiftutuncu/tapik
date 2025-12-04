plugins {
    id("buildlogic.convention.kotlin-jvm")
    alias(libs.plugins.dokka)
    alias(libs.plugins.ktlint)
}

dependencies {
    api(libs.arrowCore)
    testImplementation(libs.bundles.testCommon)
}

plugins {
    id("buildlogic.convention.kotlin-jvm")
    alias(libs.plugins.dokka)
    alias(libs.plugins.ktlint)
}

dependencies {
    api(project(":core"))
    api(libs.springWeb)
    api(libs.springWebMvc)
    testImplementation(libs.bundles.testCommon)
}

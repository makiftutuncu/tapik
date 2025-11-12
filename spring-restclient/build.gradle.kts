plugins {
    id("buildlogic.convention.kotlin-jvm")
    alias(libs.plugins.dokka)
    alias(libs.plugins.ktlint)
}

dependencies {
    api(project(":core"))
    api(project(":jackson"))
    compileOnly(project(":common-plugin"))
    implementation(libs.arrowCore)
    implementation(libs.springWeb)
    testImplementation(libs.bundles.testCommon)
    testImplementation(libs.mockk)
    testImplementation(libs.wiremock)
    testImplementation(project(":common-plugin"))
}

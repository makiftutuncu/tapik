plugins {
    id("buildlogic.convention.kotlin-jvm")
    `java-test-fixtures`
    alias(libs.plugins.dokka)
    alias(libs.plugins.ktlint)
}

dependencies {
    api(project(":codec"))
    implementation(libs.arrowCore)
    testImplementation(libs.bundles.testCommon)
}

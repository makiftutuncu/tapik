plugins {
    id("buildlogic.convention.kotlin-jvm")
    `java-test-fixtures`
    alias(libs.plugins.dokka)
    alias(libs.plugins.ktlint)
}

val sourceSets = the<SourceSetContainer>()

dependencies {
    api(project(":codec"))
    implementation(libs.arrowCore)
    testImplementation(libs.bundles.testCommon)
}

tasks.named<Jar>("jar") {
    from(sourceSets.named("main").map { it.output })
}

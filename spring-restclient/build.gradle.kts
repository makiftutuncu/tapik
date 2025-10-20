plugins {
    id("buildsrc.convention.kotlin-jvm")
    alias(libs.plugins.ktlint)
}

val integrationTest by sourceSets.creating {
    compileClasspath += sourceSets.main.get().output + configurations["testCompileClasspath"]
    runtimeClasspath += output + compileClasspath + configurations["testRuntimeClasspath"]
}

val integrationTestImplementation by configurations.getting
val integrationTestRuntimeOnly by configurations.getting
val testImplementation by configurations.getting
val testRuntimeOnly by configurations.getting

configurations[integrationTest.implementationConfigurationName].extendsFrom(testImplementation)
configurations[integrationTest.runtimeOnlyConfigurationName].extendsFrom(testRuntimeOnly)

dependencies {
    api(project(":core"))
    api(project(":jackson"))
    implementation(libs.arrowCore)
    implementation(libs.springWeb)
    testImplementation(libs.bundles.testCommon)
    testImplementation(libs.mockk)
    integrationTestImplementation(libs.wiremock)
}

val integrationTestTask =
    tasks.register<Test>("integrationTest") {
        description = "Runs Spring RestClient integration tests."
        group = "verification"

        testClassesDirs = integrationTest.output.classesDirs
        classpath = integrationTest.runtimeClasspath
        shouldRunAfter(tasks.test)
    }

tasks.named("check") {
    dependsOn(integrationTestTask)
}

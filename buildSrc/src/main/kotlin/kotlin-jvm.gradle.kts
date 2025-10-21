package buildsrc.convention

import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    kotlin("jvm")
}

kotlin {
    val javaVersion = providers.gradleProperty("javaVersion").map(String::toInt).get()
    jvmToolchain(javaVersion)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()

    testLogging {
        events(
            TestLogEvent.FAILED,
            TestLogEvent.PASSED,
            TestLogEvent.SKIPPED
        )
    }
}

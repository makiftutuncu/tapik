import buildlogic.convention.configureKotlinAndJavaToolchains
import org.gradle.api.tasks.testing.Test

configureKotlinAndJavaToolchains()

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

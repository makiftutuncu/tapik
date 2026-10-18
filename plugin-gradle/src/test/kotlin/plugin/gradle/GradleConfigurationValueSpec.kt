package dev.akif.tapik.plugin.gradle

import dev.akif.tapik.common.plugin.ListConfigurationValue
import dev.akif.tapik.common.plugin.ObjectConfigurationValue
import dev.akif.tapik.common.plugin.ScalarConfigurationValue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class GradleConfigurationValueSpec : FunSpec({
    test("translate the complete ordered Gradle configuration tree") {
        val values =
            linkedMapOf(
                "enabled" to ScalarGradleConfigurationValue("true"),
                "formats" to
                    ListGradleConfigurationValue(
                        listOf(
                            ScalarGradleConfigurationValue("json"),
                            ObjectGradleConfigurationValue(
                                linkedMapOf("pretty" to ScalarGradleConfigurationValue("false"))
                            )
                        )
                    )
            )

        values.toTargetConfiguration().values shouldBe
            linkedMapOf(
                "enabled" to ScalarConfigurationValue("true"),
                "formats" to
                    ListConfigurationValue(
                        listOf(
                            ScalarConfigurationValue("json"),
                            ObjectConfigurationValue(
                                linkedMapOf("pretty" to ScalarConfigurationValue("false"))
                            )
                        )
                    )
            )
    }
})

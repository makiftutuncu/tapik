package dev.akif.tapik.plugin.gradle

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.maps.shouldContainExactly
import org.gradle.api.Action
import org.gradle.testfixtures.ProjectBuilder

class GradleExecutionConfigurationSpec : FunSpec({
    test("omit unset optional built-in target configuration") {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply(TapikGradlePlugin::class.java)
        val extension = project.extensions.getByType(TapikExtension::class.java)
        extension.openApi("docs", Action { execution -> execution.version("1.0.0") })
        extension.springRestClient("client", Action {})
        extension.springWebMvc("server", Action {})

        extension.orderedExecutions.get()[0].generationConfiguration() shouldContainExactly
            linkedMapOf("version" to ScalarGradleConfigurationValue("1.0.0"))
        extension.orderedExecutions.get()[1].generationConfiguration() shouldContainExactly emptyMap()
        extension.orderedExecutions.get()[2].generationConfiguration() shouldContainExactly emptyMap()
    }

    test("translate explicitly configured built-in target values") {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply(TapikGradlePlugin::class.java)
        val extension = project.extensions.getByType(TapikExtension::class.java)
        extension.openApi(
            "docs",
            Action { execution ->
                execution.version("2.0.0")
                execution.format(OpenApiFormat.JSON)
                execution.pretty(false)
                execution.componentNaming(OpenApiComponentNaming.QUALIFIED)
                execution.output("contract.json")
            }
        )
        extension.springRestClient(
            "client",
            Action { execution ->
                execution.packageName("com.example.client")
                execution.clientSuffix("Remote")
            }
        )
        extension.springWebMvc(
            "server",
            Action { execution ->
                execution.packageName("com.example.server")
                execution.serverSuffix("Handler")
                execution.controllerSuffix("Controller")
            }
        )

        extension.orderedExecutions.get()[0].generationConfiguration() shouldContainExactly
            linkedMapOf(
                "version" to ScalarGradleConfigurationValue("2.0.0"),
                "format" to ScalarGradleConfigurationValue("json"),
                "pretty" to ScalarGradleConfigurationValue("false"),
                "componentNaming" to ScalarGradleConfigurationValue("qualified"),
                "output" to ScalarGradleConfigurationValue("contract.json")
            )
        extension.orderedExecutions.get()[1].generationConfiguration() shouldContainExactly
            linkedMapOf(
                "packageName" to ScalarGradleConfigurationValue("com.example.client"),
                "clientSuffix" to ScalarGradleConfigurationValue("Remote")
            )
        extension.orderedExecutions.get()[2].generationConfiguration() shouldContainExactly
            linkedMapOf(
                "packageName" to ScalarGradleConfigurationValue("com.example.server"),
                "serverSuffix" to ScalarGradleConfigurationValue("Handler"),
                "controllerSuffix" to ScalarGradleConfigurationValue("Controller")
            )
    }
})

package dev.akif.tapik.plugin.gradle

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.testfixtures.ProjectBuilder

class TapikExtensionSpec : FunSpec({
    test("provide typed target conventions") {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply(TapikGradlePlugin::class.java)
        val extension = project.extensions.getByType(TapikExtension::class.java)

        extension.outputRoot.get().asFile shouldBe project.layout.buildDirectory.dir("generated/tapik").get().asFile
        extension.mode.isPresent shouldBe false

        extension.openApi("docs", Action { execution -> execution.version("1.0.0") })
        extension.springRestClient("client", Action {})
        extension.springWebMvc("server", Action {})

        val executions = extension.orderedExecutions.get()
        executions.map { execution -> execution.name } shouldContainExactly listOf("docs", "client", "server")

        val openApi = executions[0] as OpenApiExecution
        openApi.targetId shouldBe "openapi"
        openApi.format.get() shouldBe OpenApiFormat.YAML
        openApi.pretty.get() shouldBe true
        openApi.componentNaming.get() shouldBe OpenApiComponentNaming.SIMPLE
        openApi.output.isPresent shouldBe false

        val restClient = executions[1] as SpringRestClientExecution
        restClient.packageName.get() shouldBe "dev.akif.tapik.generated"
        restClient.clientSuffix.get() shouldBe "Client"

        val webMvc = executions[2] as SpringWebMvcExecution
        webMvc.packageName.get() shouldBe "dev.akif.tapik.generated"
        webMvc.serverSuffix.get() shouldBe "Server"
        webMvc.controllerSuffix.get() shouldBe "GeneratedController"
    }

    test("apply typed overrides and preserve execution declaration order") {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply(TapikGradlePlugin::class.java)
        val extension = project.extensions.getByType(TapikExtension::class.java)

        extension.outputRoot(project.layout.projectDirectory.dir("generated-contracts"))
        extension.mode(Mode.COMPILED_CONTRACT)
        extension.springWebMvc(
            "server",
            Action { execution ->
                execution.includeApis("Books", "Authors")
                execution.excludeApis("Internal")
                execution.packageName("com.example.generated")
                execution.serverSuffix("Handler")
                execution.controllerSuffix("GeneratedController")
            }
        )
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
        extension.target(
            "company",
            "company-docs",
            Action { execution ->
                execution.value("enabled", true)
                execution.list("formats", "json", "yaml")
                execution.objectValue(
                    "metadata",
                    Action { value -> value.value("owner", "platform") }
                )
            }
        )

        extension.outputRoot.get().asFile shouldBe project.layout.projectDirectory.dir("generated-contracts").asFile
        extension.mode.get() shouldBe Mode.COMPILED_CONTRACT
        extension.orderedExecutions.get().map { execution -> execution.name } shouldContainExactly
            listOf("server", "docs", "company")

        val server = extension.orderedExecutions.get()[0] as SpringWebMvcExecution
        server.includeApis.get().toList() shouldContainExactly listOf("Books", "Authors")
        server.excludeApis.get().toList() shouldContainExactly listOf("Internal")
        server.packageName.get() shouldBe "com.example.generated"
        server.serverSuffix.get() shouldBe "Handler"

        val docs = extension.orderedExecutions.get()[1] as OpenApiExecution
        docs.version.get() shouldBe "2.0.0"
        docs.format.get() shouldBe OpenApiFormat.JSON
        docs.pretty.get() shouldBe false
        docs.componentNaming.get() shouldBe OpenApiComponentNaming.QUALIFIED
        docs.output.get() shouldBe "contract.json"

        val company = extension.orderedExecutions.get()[2] as CustomTargetExecution
        company.targetId shouldBe "company-docs"
        company.configuration.get() shouldContainExactly
            linkedMapOf(
                "enabled" to ScalarGradleConfigurationValue("true"),
                "formats" to
                    ListGradleConfigurationValue(
                        listOf(
                            ScalarGradleConfigurationValue("json"),
                            ScalarGradleConfigurationValue("yaml")
                        )
                    ),
                "metadata" to
                    ObjectGradleConfigurationValue(
                        linkedMapOf("owner" to ScalarGradleConfigurationValue("platform"))
                    )
            )
    }

    test("create executions only for declared targets and allow repeated target types") {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply(TapikGradlePlugin::class.java)
        val extension = project.extensions.getByType(TapikExtension::class.java)

        extension.orderedExecutions.get() shouldBe emptyList()

        extension.springRestClient(
            "internal",
            Action { execution -> execution.includeApis("Internal") }
        )
        extension.springRestClient(
            "public",
            Action { execution -> execution.includeApis("Public") }
        )

        val executions = extension.orderedExecutions.get()
        executions.map { execution -> execution.name } shouldContainExactly listOf("internal", "public")
        executions.map { execution -> execution.targetId } shouldContainExactly
            listOf("spring-restclient", "spring-restclient")
    }

    test("reject invalid configuration before generation") {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply(TapikGradlePlugin::class.java)
        val extension = project.extensions.getByType(TapikExtension::class.java)

        shouldThrow<IllegalArgumentException> { extension.openApi(" ", Action {}) }
        shouldThrow<IllegalArgumentException> { extension.target("custom", " ", Action {}) }

        extension.openApi("docs", Action { execution -> execution.version("1") })
        shouldThrow<IllegalArgumentException> { extension.springRestClient("docs", Action {}) }

        extension.mode(Mode.COMPILED_CONTRACT)
        extension.mode(Mode.COMPILED_CONTRACT)
        shouldThrow<IllegalArgumentException> { extension.mode(Mode.SAME_PROJECT) }
    }

    test("validate mode API selection and required typed options") {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply(TapikGradlePlugin::class.java)
        val extension = project.extensions.getByType(TapikExtension::class.java)
        val task = project.tasks.named("tapikGenerate", TapikGenerateTask::class.java).get()

        shouldThrow<GradleException> { task.validateConfiguration() }

        extension.openApi(
            Action { execution ->
                execution.includeApis("Books", " ")
                execution.excludeApis("Books")
            }
        )
        shouldThrow<GradleException> { task.validateConfiguration() }

        extension.mode(Mode.SAME_PROJECT)
        shouldThrow<GradleException> { task.validateConfiguration() }
    }

    test("register the generation task lazily") {
        val project = ProjectBuilder.builder().build()
        var realized = false
        project.tasks.withType(TapikGenerateTask::class.java).configureEach { realized = true }

        project.pluginManager.apply(TapikGradlePlugin::class.java)

        realized shouldBe false
        project.tasks.named("tapikGenerate", TapikGenerateTask::class.java).get()
        realized shouldBe true
    }
})

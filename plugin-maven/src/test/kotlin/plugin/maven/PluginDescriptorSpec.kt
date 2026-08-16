package dev.akif.tapik.plugin.maven

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import org.w3c.dom.Document
import org.w3c.dom.NodeList
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

class PluginDescriptorSpec : FunSpec({
    test("describe one generic generation goal") {
        val descriptor = requireNotNull(PluginDescriptorSpec::class.java.getResourceAsStream("/META-INF/maven/plugin.xml"))
        val document = descriptor.use { DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(it) }

        document.text("/plugin/groupId") shouldBe "dev.akif"
        document.text("/plugin/artifactId") shouldBe "tapik-plugin-maven"
        document.text("/plugin/goalPrefix") shouldBe "tapik"
        document.text("/plugin/mojos/mojo[goal='generate']/phase") shouldBe "process-classes"
        document.text("/plugin/mojos/mojo[goal='generate']/implementation") shouldBe
            "dev.akif.tapik.plugin.maven.GenerateMojo"
        document.text("/plugin/mojos/mojo[goal='generate']/requiresDependencyResolution") shouldBe "compile+runtime"
        document.text("/plugin/mojos/mojo[goal='generate']/threadSafe") shouldBe "true"
        document.texts("/plugin/mojos/mojo[goal='generate']/parameters/parameter/name") shouldContainExactlyInAnyOrder
            listOf("project", "target", "outputDirectory", "targetConfiguration")
    }
})

private fun Document.text(expression: String): String =
    XPathFactory.newInstance().newXPath().evaluate(expression, this)

private fun Document.texts(expression: String): List<String> {
    val nodes = XPathFactory.newInstance().newXPath().evaluate(expression, this, XPathConstants.NODESET) as NodeList
    return List(nodes.length) { index -> nodes.item(index).textContent }
}

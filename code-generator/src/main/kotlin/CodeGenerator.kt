package dev.akif.tapik

import dev.akif.tapik.generators.AliasesGenerator
import dev.akif.tapik.generators.method.BuildResponseMethodGenerator
import dev.akif.tapik.generators.method.DecodeHeadersMethodGenerator
import dev.akif.tapik.generators.method.EncodeHeadersMethodGenerator
import dev.akif.tapik.generators.method.BuildURIMethodGenerator
import dev.akif.tapik.generators.type.ResponseGenerator
import dev.akif.tapik.generators.type.SelectionsGenerator
import dev.akif.tapik.generators.type.TupleGenerator
import kotlin.system.exitProcess

val requiredArguments = setOf(
    setOf("--module", "-m"),
    setOf("--limit", "-l"),
)

fun main(args: Array<String>) {
    val arguments = args
        .takeIf { it.isNotEmpty() }
        ?.toList()
        ?.windowed(size = 2, step = 2, partialWindows = true)
        ?.associateBy({ (k, _) -> k.trim() }) { it.getOrNull(1)?.trim().orEmpty() }
        .orEmpty()

    val missing = requiredArguments.any { alternatives ->
        alternatives.all { it !in arguments }.also {
            if (it) {
                println(
                    """Missing argument ${alternatives.joinToString(" | ")}

                    Usage: java dev.akif.tapik.CodeGeneratorKt ${alternatives.joinToString(" | ") { "$it <value>" }} [--verbose | -v]

                    """.trimIndent()
                )
            }
        }
    }
    if (missing) {
        exitProcess(1)
    }

    val module = requireNotNull((arguments["--module"] ?: arguments["-m"])) { "Invalid module: $arguments" }
    val limit = requireNotNull((arguments["--limit"] ?: arguments["-l"])?.toIntOrNull()) { "Invalid limit: $arguments" }
    val verbose = "--verbose" in arguments || "-v" in arguments

    val generators = buildList {
        add(AliasesGenerator)
        add(BuildResponseMethodGenerator)
        add(BuildURIMethodGenerator)
        add(DecodeHeadersMethodGenerator)
        add(EncodeHeadersMethodGenerator)
        add(ResponseGenerator)
        add(SelectionsGenerator)
        add(TupleGenerator)
    }

    generators.filter { it.module == module }.forEach { it.run(limit, verbose) }
}

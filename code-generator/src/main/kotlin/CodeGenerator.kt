package dev.akif.tapik

import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val arguments = args
        .takeIf { it.isNotEmpty() }
        ?.toList()
        ?.windowed(size = 2, step = 2, partialWindows = true)
        ?.associateBy({ (k, _) -> k.trim() }) { it.getOrNull(1)?.trim().orEmpty() }
        .orEmpty()

    if ("--limit" !in arguments && "-l" !in arguments) {
        println(
            """

            Usage: java dev.akif.tapik.CodeGeneratorKt [--limit <limit> | -l <limit>] [--verbose | -v]

            """.trimIndent()
        )
        exitProcess(1)
    }

    val limit = requireNotNull((arguments["--limit"] ?: arguments["-l"])?.toIntOrNull()) { "Invalid limit: $arguments" }
    val verbose = "--verbose" in arguments || "-v" in arguments

    val generators = listOf(TupleGenerator)

    generators.forEach { it.run(limit, verbose) }
}

package dev.akif.tapik.common.format

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import java.lang.ref.WeakReference

class WeakScopedFormatCacheSpec : FunSpec({
    test("reuse values only within the same scope configuration and type") {
        val cache = WeakScopedFormatCache<Any, Any, String, Any>(FormatCacheKeyEquality.STRUCTURAL)
        val scope = Any()
        val configuration = Any()

        cache.getOrPut(scope, configuration, "type", ::Any) shouldBeSameInstanceAs
            cache.getOrPut(scope, configuration, "type", ::Any)
        cache.getOrPut(scope, configuration, "type", ::Any) shouldNotBeSameInstanceAs
            cache.getOrPut(Any(), configuration, "type", ::Any)
        cache.getOrPut(scope, configuration, "type", ::Any) shouldNotBeSameInstanceAs
            cache.getOrPut(scope, Any(), "type", ::Any)
    }

    test("do not retain an unreachable scope configuration type or value") {
        val cache = WeakScopedFormatCache<Any, Any, Any, ScopedCachedValue>(FormatCacheKeyEquality.STRUCTURAL)
        val references = cache.createWeaklyReferencedEntry()

        awaitScopedGarbageCollection(references)

        references.map(WeakReference<*>::get) shouldBe listOf(null, null, null, null)
    }
})

private data class ScopedCachedValue(
    val configuration: Any,
    val type: Any
)

private fun WeakScopedFormatCache<Any, Any, Any, ScopedCachedValue>.createWeaklyReferencedEntry():
    List<WeakReference<*>> {
    val scope = Any()
    val configuration = Any()
    val type = Any()
    val value = getOrPut(scope, configuration, type) { ScopedCachedValue(configuration, type) }
    return listOf(WeakReference(scope), WeakReference(configuration), WeakReference(type), WeakReference(value))
}

private fun awaitScopedGarbageCollection(references: List<WeakReference<*>>) {
    repeat(100) {
        if (references.all { reference -> reference.get() == null }) return
        System.gc()
        Thread.sleep(10)
    }
}

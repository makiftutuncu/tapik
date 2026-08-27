package dev.akif.tapik.common.format

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import java.lang.ref.WeakReference
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

class WeakFormatCacheSpec : FunSpec({
    test("reuse a live value for concurrent configuration and equal type lookups") {
        val cache = WeakFormatCache<Any, EqualKey, Any>(FormatCacheKeyEquality.STRUCTURAL)
        val configuration = Any()
        val start = CountDownLatch(1)
        val executor = Executors.newFixedThreadPool(8)
        val constructions = AtomicInteger()

        try {
            val results =
                List(64) {
                    executor.submit<Any> {
                        start.await()
                        cache.getOrPut(configuration, EqualKey("type")) {
                            constructions.incrementAndGet()
                            Any()
                        }
                    }
                }

            start.countDown()

            val values = results.map { result -> result.get() }
            values.all { value -> value === values.first() } shouldBe true
            constructions.get() shouldBe 1
        } finally {
            executor.shutdownNow()
        }
    }

    test("always compare configurations by identity") {
        val cache = WeakFormatCache<EqualKey, EqualKey, Any>(FormatCacheKeyEquality.STRUCTURAL)
        val type = EqualKey("type")
        val first = cache.getOrPut(EqualKey("configuration"), type, ::Any)
        val second = cache.getOrPut(EqualKey("configuration"), type, ::Any)

        first shouldNotBeSameInstanceAs second
    }

    test("support identity type keys") {
        val cache = WeakFormatCache<Any, EqualKey, Any>(FormatCacheKeyEquality.IDENTITY)
        val configuration = Any()
        val type = EqualKey("type")

        cache.getOrPut(configuration, type, ::Any) shouldBeSameInstanceAs
            cache.getOrPut(configuration, type, ::Any)
        cache.getOrPut(configuration, type, ::Any) shouldNotBeSameInstanceAs
            cache.getOrPut(configuration, EqualKey("type"), ::Any)
    }

    test("do not retain an unreachable configuration type or value") {
        val cache = WeakFormatCache<Any, Any, CachedValue>(FormatCacheKeyEquality.STRUCTURAL)
        val references = cache.createWeaklyReferencedEntry()

        awaitGarbageCollection(references)

        references.map(WeakReference<*>::get) shouldBe listOf(null, null, null)
        cache.size shouldBe 0
    }
})

private data class CachedValue(
    val configuration: Any,
    val type: Any
)

private data class EqualKey(
    val value: String
)

private fun WeakFormatCache<Any, Any, CachedValue>.createWeaklyReferencedEntry(): List<WeakReference<*>> {
    val configuration = Any()
    val type = Any()
    val value = getOrPut(configuration, type) { CachedValue(configuration, type) }
    return listOf(WeakReference(configuration), WeakReference(type), WeakReference(value))
}

private fun awaitGarbageCollection(references: List<WeakReference<*>>) {
    repeat(100) {
        if (references.all { reference -> reference.get() == null }) {
            return
        }
        System.gc()
        Thread.sleep(10)
    }
}

package dev.akif.tapik.format.kotlinx

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import java.lang.ref.WeakReference
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

class WeakIdentityPairCacheSpec : FunSpec({
    test("reuse a live value for concurrent identity-pair lookups") {
        val cache = WeakIdentityPairCache<Any, Any, Any>()
        val first = Any()
        val second = Any()
        val start = CountDownLatch(1)
        val executor = Executors.newFixedThreadPool(8)
        val constructions = AtomicInteger()

        try {
            val results =
                List(64) {
                    executor.submit<Any> {
                        start.await()
                        cache.getOrPut(first, second) {
                            constructions.incrementAndGet()
                            Any()
                        }
                    }
                }

            start.countDown()

            val values = results.map { it.get() }
            values.all { it === values.first() } shouldBe true
            constructions.get() shouldBe 1
        } finally {
            executor.shutdownNow()
        }
    }

    test("distinguish equal identity-pair instances") {
        val cache = WeakIdentityPairCache<EqualKey, EqualKey, Any>()
        val first = EqualKey("first")
        val second = EqualKey("second")
        val equalFirst = EqualKey("first")
        val equalSecond = EqualKey("second")
        val firstPairValue = cache.getOrPut(first, second, ::Any)
        val secondPairValue = cache.getOrPut(equalFirst, equalSecond, ::Any)

        firstPairValue shouldNotBeSameInstanceAs secondPairValue
    }

    test("do not retain an unreachable identity pair or value") {
        val cache = WeakIdentityPairCache<Any, Any, CachedValue>()
        val references = cache.createWeaklyReferencedEntry()

        awaitGarbageCollection(references)

        references.map(WeakReference<*>::get) shouldBe listOf(null, null, null)
        cache.size shouldBe 0
    }
})

private data class CachedValue(
    val first: Any,
    val second: Any
)

private data class EqualKey(
    val value: String
)

private fun WeakIdentityPairCache<Any, Any, CachedValue>.createWeaklyReferencedEntry(): List<WeakReference<*>> {
    val first = Any()
    val second = Any()
    val value = getOrPut(first, second) { CachedValue(first, second) }
    return listOf(WeakReference(first), WeakReference(second), WeakReference(value))
}

private fun awaitGarbageCollection(references: List<WeakReference<*>>) {
    repeat(100) {
        if (references.all { it.get() == null }) {
            return
        }
        System.gc()
        Thread.sleep(10)
    }
}

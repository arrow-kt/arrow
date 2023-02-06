package arrow.fx.coroutines

import arrow.core.Either
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.should
import io.kotest.matchers.string.shouldStartWith
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

class ParZip3JvmTest : StringSpec({
  "parZip 3 returns to original context" {
    val zipCtxName = "parZip3"
    val zipCtx = Resource.fromExecutor { Executors.newFixedThreadPool(3, NamedThreadFactory { zipCtxName }) }

      single.zip(zipCtx).use { (_single, _zipCtx) ->
        withContext(_single) {
          threadName() shouldStartWith singleThreadName

          val (s1, s2, s3) = parZip(
            _zipCtx,
            { Thread.currentThread().name },
            { Thread.currentThread().name },
            { Thread.currentThread().name }) { a, b, c -> Triple(a, b, c) }

          s1 shouldStartWith zipCtxName
          s2 shouldStartWith zipCtxName
          s3 shouldStartWith zipCtxName
          threadName() shouldStartWith singleThreadName
        }
      }
  }

  "parZip 3 returns to original context on failure" {
    val zipCtxName = "parZip3"
    val zipCtx = Resource.fromExecutor { Executors.newFixedThreadPool(3, NamedThreadFactory { zipCtxName }) }

    checkAll(Arb.int(1..3), Arb.throwable()) { choose, e ->
      single.zip(zipCtx).use { (_single, _zipCtx) ->
        withContext(_single) {
          threadName() shouldStartWith singleThreadName

          Either.catch {
            when (choose) {
              1 -> parZip(
                _zipCtx,
                { e.suspend() },
                { awaitCancellation() },
                { awaitCancellation() }
              ) { _, _, _ -> Unit }
              2 -> parZip(
                _zipCtx,
                { awaitCancellation() },
                { e.suspend() },
                { awaitCancellation() }
              ) { _, _, _ -> Unit }
              else -> parZip(
                _zipCtx,
                { awaitCancellation() },
                { awaitCancellation() },
                { e.suspend() }
              ) { _, _, _ -> Unit }
            }
          } should leftException(e)

          threadName() shouldStartWith singleThreadName
        }
      }
    }
  }

  "parZip 3 finishes on single thread" {
    checkAll(Arb.string()) {
      val res = single.use { ctx ->
        parZip(
          ctx,
          { Thread.currentThread().name },
          { Thread.currentThread().name },
          { Thread.currentThread().name }) { a, b, c -> listOf(a, b, c) }
      }
      assertSoftly {
        res.forEach { it shouldStartWith "single" }
      }
    }
  }
})

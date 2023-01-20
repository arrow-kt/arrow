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

class ParZip2JvmTest : StringSpec({
    "parZip 2 returns to original context" {
      val zipCtxName = "parZip2"
      val zipCtx = Resource.fromExecutor { Executors.newFixedThreadPool(2, NamedThreadFactory { zipCtxName }) }

        single.zip(zipCtx).use { (_single, _zipCtx) ->
          withContext(_single) {
            threadName() shouldStartWith singleThreadName

            val (s1, s2) = parZip(
              _zipCtx,
              { Thread.currentThread().name },
              { Thread.currentThread().name }) { a, b -> Pair(a, b) }

            s1 shouldStartWith zipCtxName
            s2 shouldStartWith zipCtxName
            threadName() shouldStartWith singleThreadName
          }
        }
    }

    "parZip 2 returns to original context on failure" {
      val zipCtxName = "parZip2"
      val zipCtx = Resource.fromExecutor { Executors.newFixedThreadPool(2, NamedThreadFactory { zipCtxName }) }

      checkAll(Arb.int(1..2), Arb.throwable()) { choose, e ->
        single.zip(zipCtx).use { (_single, _zipCtx) ->
          withContext(_single) {
            threadName() shouldStartWith singleThreadName

            Either.catch {
              when (choose) {
                1 -> parZip(_zipCtx, { e.suspend() }, { awaitCancellation() }) { _, _ -> Unit }
                else -> parZip(_zipCtx, { awaitCancellation() }, { e.suspend() }) { _, _ -> Unit }
              }
            } should leftException(e)

            threadName() shouldStartWith singleThreadName
          }
        }
      }
    }

    "parZip 2 finishes on single thread" {
      checkAll(Arb.string()) {
        val res = single.use { ctx ->
          parZip(ctx, { Thread.currentThread().name }, { Thread.currentThread().name }) { a, b -> listOf(a, b) }
        }
        assertSoftly {
          res.forEach { it shouldStartWith "single" }
        }
      }
    }
  }
)

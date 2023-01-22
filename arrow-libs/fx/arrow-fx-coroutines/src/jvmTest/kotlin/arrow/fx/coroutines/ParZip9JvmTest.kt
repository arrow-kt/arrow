package arrow.fx.coroutines

import arrow.core.Either
import arrow.core.Tuple9
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.should
import io.kotest.matchers.string.shouldStartWith
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

class ParZip9JvmTest : StringSpec({
  val threadName: suspend CoroutineScope.() -> String =
    { Thread.currentThread().name }

  "parZip 9 returns to original context" {
    val zipCtxName = "parZip9"
    val zipCtx = Resource.fromExecutor { Executors.newFixedThreadPool(9, NamedThreadFactory { zipCtxName }) }

      single.zip(zipCtx).use { (_single, _zipCtx) ->
        withContext(_single) {
          threadName() shouldStartWith singleThreadName

          val (s1, s2, s3, s4, s5, s6, s7, s8, s9) = parZip(
            _zipCtx, threadName, threadName, threadName, threadName, threadName, threadName, threadName, threadName, threadName
          ) { a, b, c, d, e, f, g, h, i ->
            Tuple9(a, b, c, d, e, f, g, h, i)
          }

          s1 shouldStartWith zipCtxName
          s2 shouldStartWith zipCtxName
          s3 shouldStartWith zipCtxName
          s4 shouldStartWith zipCtxName
          s5 shouldStartWith zipCtxName
          s6 shouldStartWith zipCtxName
          s7 shouldStartWith zipCtxName
          s8 shouldStartWith zipCtxName
          s9 shouldStartWith zipCtxName
          threadName() shouldStartWith singleThreadName
        }
      }

  }

  "parZip 9 returns to original context on failure" {
    val zipCtxName = "parZip9"
    val zipCtx = Resource.fromExecutor { Executors.newFixedThreadPool(9, NamedThreadFactory { zipCtxName }) }

    checkAll(Arb.int(1..9), Arb.throwable()) { choose, e ->
      single.zip(zipCtx).use { (_single, _zipCtx) ->
        withContext(_single) {
          threadName() shouldStartWith singleThreadName

          Either.catch {
            when (choose) {
              1 -> parZip(
                _zipCtx,
                { e.suspend() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() }
              ) { _, _, _, _, _, _, _, _, _ -> Unit }
              2 -> parZip(
                _zipCtx,
                { awaitCancellation() },
                { e.suspend() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() }
              ) { _, _, _, _, _, _, _, _, _ -> Unit }
              3 -> parZip(
                _zipCtx,
                { awaitCancellation() },
                { awaitCancellation() },
                { e.suspend() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() }
              ) { _, _, _, _, _, _, _, _, _ -> Unit }
              4 -> parZip(
                _zipCtx,
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { e.suspend() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() }
              ) { _, _, _, _, _, _, _, _, _ -> Unit }
              5 -> parZip(
                _zipCtx,
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { e.suspend() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() }
              ) { _, _, _, _, _, _, _, _, _ -> Unit }
              6 -> parZip(
                _zipCtx,
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { e.suspend() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() }
              ) { _, _, _, _, _, _, _, _, _ -> Unit }
              7 -> parZip(
                _zipCtx,
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { e.suspend() },
                { awaitCancellation() },
                { awaitCancellation() }
              ) { _, _, _, _, _, _, _, _, _ -> Unit }
              8 -> parZip(
                _zipCtx,
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { e.suspend() },
                { awaitCancellation() }
              ) { _, _, _, _, _, _, _, _, _ -> Unit }
              else -> parZip(
                _zipCtx,
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { e.suspend() }
              ) { _, _, _, _, _, _, _, _, _ -> Unit }
            }
          } should leftException(e)
          threadName() shouldStartWith singleThreadName
        }
      }
    }
  }

  "parZip 9 finishes on single thread" {
    checkAll(Arb.string()) {
      val res = single.use { ctx ->
        parZip(
          ctx,
          threadName,
          threadName,
          threadName,
          threadName,
          threadName,
          threadName,
          threadName,
          threadName,
          threadName
        ) { a, b, c, d, e, f, g, h, i ->
          listOf(a, b, c, d, e, f, g, h, i)
        }
      }
      assertSoftly {
        res.forEach { it shouldStartWith "single" }
      }
    }
  }
})

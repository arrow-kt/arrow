package arrow.fx.coroutines

import arrow.core.Either
import arrow.core.Tuple6
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.should
import io.kotest.matchers.string.shouldStartWith
import io.kotest.mpp.NamedThreadFactory
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import java.util.concurrent.Executors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.withContext

class ParZip6JvmTest : StringSpec({
  val threadName: suspend CoroutineScope.() -> String =
    { Thread.currentThread().name }

  "parZip 6 returns to original context" {
    val zipCtxName = "parZip6"
    resourceScope {
      val zipCtx = executor { Executors.newFixedThreadPool(6, NamedThreadFactory(zipCtxName)) }

      withContext(single()) {
        threadName() shouldStartWith "single"

        val (s1, s2, s3, s4, s5, s6) = parZip(
          zipCtx, threadName, threadName, threadName, threadName, threadName, threadName
        ) { a, b, c, d, e, f ->
          Tuple6(a, b, c, d, e, f)
        }

        s1 shouldStartWith zipCtxName
        s2 shouldStartWith zipCtxName
        s3 shouldStartWith zipCtxName
        s4 shouldStartWith zipCtxName
        s5 shouldStartWith zipCtxName
        s6 shouldStartWith zipCtxName
        threadName() shouldStartWith "single"
      }
    }
  }

  "parZip 6 returns to original context on failure" {
    val zipCtxName = "parZip6"
    resourceScope {
    val zipCtx = executor { Executors.newFixedThreadPool(6, NamedThreadFactory(zipCtxName)) }

    checkAll(Arb.int(1..6), Arb.throwable()) { choose, e ->
        withContext(single()) {
          threadName() shouldStartWith "single"

          Either.catch {
            when (choose) {
              1 -> parZip(
                zipCtx,
                { e.suspend() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() }
              ) { _, _, _, _, _, _ -> Unit }

              2 -> parZip(
                zipCtx,
                { awaitCancellation() },
                { e.suspend() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() }
              ) { _, _, _, _, _, _ -> Unit }

              3 -> parZip(
                zipCtx,
                { awaitCancellation() },
                { awaitCancellation() },
                { e.suspend() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() }
              ) { _, _, _, _, _, _ -> Unit }

              4 -> parZip(
                zipCtx,
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { e.suspend() },
                { awaitCancellation() },
                { awaitCancellation() }
              ) { _, _, _, _, _, _ -> Unit }

              5 -> parZip(
                zipCtx,
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { e.suspend() },
                { awaitCancellation() }
              ) { _, _, _, _, _, _ -> Unit }

              else -> parZip(
                zipCtx,
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { e.suspend() }
              ) { _, _, _, _, _, _ -> Unit }
            }
          } should leftException(e)
          threadName() shouldStartWith "single"
        }
      }
    }
  }

  "parZip 6 finishes on single thread" {
    checkAll(Arb.string()) {
      val res = resourceScope {
        val ctx = singleThreadContext("single")
        parZip(ctx, threadName, threadName, threadName, threadName, threadName, threadName) { a, b, c, d, e, f ->
          listOf(a, b, c, d, e, f)
        }
      }
      assertSoftly {
        res.forEach { it shouldStartWith "single" }
      }
    }
  }
})

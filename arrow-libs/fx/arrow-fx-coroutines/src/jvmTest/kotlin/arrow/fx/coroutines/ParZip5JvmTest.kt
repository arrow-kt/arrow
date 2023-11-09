package arrow.fx.coroutines

import arrow.core.Either
import arrow.core.Tuple5
import io.kotest.assertions.assertSoftly
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
import kotlin.test.Test

class ParZip5JvmTest {
  val threadName: suspend CoroutineScope.() -> String =
    { Thread.currentThread().name }

  @Test fun parZip5ReturnsToOriginalContext() = runTestUsingDefaultDispatcher {
    val zipCtxName = "parZip5"
    resourceScope {
      val zipCtx = executor { Executors.newFixedThreadPool(5, NamedThreadFactory(zipCtxName)) }

      withContext(single()) {
        threadName() shouldStartWith "single"

        val (s1, s2, s3, s4, s5) = parZip(
          zipCtx, threadName, threadName, threadName, threadName, threadName
        ) { a, b, c, d, e -> Tuple5(a, b, c, d, e) }

        s1 shouldStartWith zipCtxName
        s2 shouldStartWith zipCtxName
        s3 shouldStartWith zipCtxName
        s4 shouldStartWith zipCtxName
        s5 shouldStartWith zipCtxName
        threadName() shouldStartWith "single"
      }
    }
  }

  @Test fun parZip5ReturnsToOriginalContextOnFailure() = runTestUsingDefaultDispatcher {
    val zipCtxName = "parZip5"
    resourceScope {
      val zipCtx = executor { Executors.newFixedThreadPool(5, NamedThreadFactory(zipCtxName)) }

      checkAll(Arb.int(1..5), Arb.throwable()) { choose, e ->
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
                { awaitCancellation() }
              ) { _, _, _, _, _ -> Unit }

              2 -> parZip(
                zipCtx,
                { awaitCancellation() },
                { e.suspend() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() }
              ) { _, _, _, _, _ -> Unit }

              3 -> parZip(
                zipCtx,
                { awaitCancellation() },
                { awaitCancellation() },
                { e.suspend() },
                { awaitCancellation() },
                { awaitCancellation() }
              ) { _, _, _, _, _ -> Unit }

              4 -> parZip(
                zipCtx,
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { e.suspend() },
                { awaitCancellation() }
              ) { _, _, _, _, _ -> Unit }

              else -> parZip(
                zipCtx,
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { e.suspend() }
              ) { _, _, _, _, _ -> Unit }
            }
          } should leftException(e)

          threadName() shouldStartWith "single"
        }
      }
    }
  }

  @Test fun parZip5FinishesOnSingleThread() = runTestUsingDefaultDispatcher {
    checkAll(Arb.string()) {
      val res = resourceScope {
        val ctx = singleThreadContext("single")
        parZip(ctx, threadName, threadName, threadName, threadName, threadName) { a, b, c, d, e ->
          listOf(
            a,
            b,
            c,
            d,
            e
          )
        }
      }
      assertSoftly {
        res.forEach { it shouldStartWith "single" }
      }
    }
  }
}

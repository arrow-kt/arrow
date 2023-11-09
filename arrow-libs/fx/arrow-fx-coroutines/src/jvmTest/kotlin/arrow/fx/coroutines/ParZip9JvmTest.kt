package arrow.fx.coroutines

import arrow.core.Either
import arrow.core.Tuple9
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.should
import io.kotest.matchers.string.shouldStartWith
import io.kotest.mpp.NamedThreadFactory
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.withContext
import kotlin.test.Test
import java.util.concurrent.Executors

class ParZip9JvmTest {
  val threadName: suspend CoroutineScope.() -> String =
    { Thread.currentThread().name }

  @Test fun parZip9ReturnsToOriginalContext() = runTestUsingDefaultDispatcher {
    val zipCtxName = "parZip9"
    resourceScope {
      val zipCtx = executor { Executors.newFixedThreadPool(9, NamedThreadFactory(zipCtxName)) }
      withContext(single()) {
        threadName() shouldStartWith "single"

        val (s1, s2, s3, s4, s5, s6, s7, s8, s9) = parZip(
          zipCtx,
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
        threadName() shouldStartWith "single"
      }
    }

  }

  @Test fun parZip9ReturnsToOriginalContextOnFailure() = runTestUsingDefaultDispatcher {
    val zipCtxName = "parZip9"
    resourceScope {
      val zipCtx = executor { Executors.newFixedThreadPool(9, NamedThreadFactory(zipCtxName)) }

      checkAll(Arb.int(1..9), Arb.throwable()) { choose, e ->
        withContext(single()) {
          threadName() shouldStartWith "single"

          Either.catch {
            when (choose) {
              1 -> parZip(
                zipCtx,
                { throw e },
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
                zipCtx,
                { awaitCancellation() },
                { throw e },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() }
              ) { _, _, _, _, _, _, _, _, _ -> Unit }

              3 -> parZip(
                zipCtx,
                { awaitCancellation() },
                { awaitCancellation() },
                { throw e },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() }
              ) { _, _, _, _, _, _, _, _, _ -> Unit }

              4 -> parZip(
                zipCtx,
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { throw e },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() }
              ) { _, _, _, _, _, _, _, _, _ -> Unit }

              5 -> parZip(
                zipCtx,
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { throw e },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() }
              ) { _, _, _, _, _, _, _, _, _ -> Unit }

              6 -> parZip(
                zipCtx,
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { throw e },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() }
              ) { _, _, _, _, _, _, _, _, _ -> Unit }

              7 -> parZip(
                zipCtx,
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { throw e },
                { awaitCancellation() },
                { awaitCancellation() }
              ) { _, _, _, _, _, _, _, _, _ -> Unit }

              8 -> parZip(
                zipCtx,
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { throw e },
                { awaitCancellation() }
              ) { _, _, _, _, _, _, _, _, _ -> Unit }

              else -> parZip(
                zipCtx,
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { throw e }
              ) { _, _, _, _, _, _, _, _, _ -> Unit }
            }
          } should leftException(e)
          threadName() shouldStartWith "single"
        }
      }
    }
  }

  @Test fun parZip9FinishesOnSingleThread() = runTestUsingDefaultDispatcher {
    checkAll(Arb.string()) {
      val res = resourceScope {
        parZip(
          single(),
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
}

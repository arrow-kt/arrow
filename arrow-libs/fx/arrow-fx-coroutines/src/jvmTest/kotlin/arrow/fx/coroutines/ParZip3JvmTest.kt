package arrow.fx.coroutines

import arrow.core.Either
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.should
import io.kotest.matchers.string.shouldStartWith
import io.kotest.mpp.NamedThreadFactory
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import java.util.concurrent.Executors
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.withContext
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds

class ParZip3JvmTest {
  @Test fun parZip3ReturnsToOriginalContext() = runTestUsingDefaultDispatcher {
    val zipCtxName = "parZip3"
    resourceScope {
      val zipCtx = executor { Executors.newFixedThreadPool(3, NamedThreadFactory(zipCtxName)) }
      withContext(single()) {
        Thread.currentThread().name shouldStartWith "single"
        val (s1, s2, s3) = parZip(
          zipCtx,
          { Thread.currentThread().name },
          { Thread.currentThread().name },
          { Thread.currentThread().name }) { a, b, c -> Triple(a, b, c) }

        s1 shouldStartWith zipCtxName
        s2 shouldStartWith zipCtxName
        s3 shouldStartWith zipCtxName
        Thread.currentThread().name shouldStartWith "single"
      }
    }
  }

  @Test fun parZip3ReturnsToOriginalContextOnFailure() = runTestUsingDefaultDispatcher(20.seconds) {
    val zipCtxName = "parZip3"
    resourceScope {
    val zipCtx = executor { Executors.newFixedThreadPool(3, NamedThreadFactory(zipCtxName)) }

    checkAll(Arb.int(1..3), Arb.throwable()) { choose, e ->
        withContext(single()) {
          Thread.currentThread().name shouldStartWith "single"

          Either.catch {
            when (choose) {
              1 -> parZip(
                zipCtx,
                { throw e },
                { awaitCancellation() },
                { awaitCancellation() }
              ) { _, _, _ -> Unit }

              2 -> parZip(
                zipCtx,
                { awaitCancellation() },
                { throw e },
                { awaitCancellation() }
              ) { _, _, _ -> Unit }

              else -> parZip(
                zipCtx,
                { awaitCancellation() },
                { awaitCancellation() },
                { throw e }
              ) { _, _, _ -> Unit }
            }
          } should leftException(e)

          Thread.currentThread().name shouldStartWith "single"
        }
      }
    }
  }

  @Test fun parZip3FinishesOnSingleThread() = runTestUsingDefaultDispatcher {
    checkAll(Arb.string()) {
      val res = resourceScope {
        val ctx = singleThreadContext("single")
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
}

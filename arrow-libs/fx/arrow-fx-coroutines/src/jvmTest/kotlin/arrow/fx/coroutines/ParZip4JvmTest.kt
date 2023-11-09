package arrow.fx.coroutines

import arrow.core.Either
import arrow.core.Tuple4
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

class ParZip4JvmTest {
    @Test fun parZip4ReturnsToOriginalContext() = runTestUsingDefaultDispatcher {
      val zipCtxName = "parZip4"
      resourceScope {
        val zipCtx = executor { Executors.newFixedThreadPool(4, NamedThreadFactory(zipCtxName)) }

          withContext(single()) {
            Thread.currentThread().name shouldStartWith "single"

            val (s1, s2, s3, s4) = parZip(
              zipCtx,
              { Thread.currentThread().name },
              { Thread.currentThread().name },
              { Thread.currentThread().name },
              { Thread.currentThread().name }
            ) { a, b, c, d -> Tuple4(a, b, c, d) }

            s1 shouldStartWith zipCtxName
            s2 shouldStartWith zipCtxName
            s3 shouldStartWith zipCtxName
            s4 shouldStartWith zipCtxName
            Thread.currentThread().name shouldStartWith "single"
          }
        }
    }

    @Test fun parZip4ReturnsToOriginalContextOnFailure() = runTestUsingDefaultDispatcher {
      val zipCtxName = "parZip4"
      resourceScope {
      val zipCtx = executor { Executors.newFixedThreadPool(4, NamedThreadFactory(zipCtxName)) }

      checkAll(Arb.int(1..4), Arb.throwable()) { choose, e ->
          withContext(single()) {
            Thread.currentThread().name shouldStartWith "single"

            Either.catch {
              when (choose) {
                1 -> parZip(
                  zipCtx,
                  { e.suspend() },
                  { awaitCancellation() },
                  { awaitCancellation() },
                  { awaitCancellation() }
                ) { _, _, _, _ -> Unit }

                2 -> parZip(
                  zipCtx,
                  { awaitCancellation() },
                  { e.suspend() },
                  { awaitCancellation() },
                  { awaitCancellation() }
                ) { _, _, _, _ -> Unit }

                3 -> parZip(
                  zipCtx,
                  { awaitCancellation() },
                  { awaitCancellation() },
                  { e.suspend() },
                  { awaitCancellation() }
                ) { _, _, _, _ -> Unit }

                else -> parZip(
                  zipCtx,
                  { awaitCancellation() },
                  { awaitCancellation() },
                  { awaitCancellation() },
                  { e.suspend() }
                ) { _, _, _, _ -> Unit }
              }
            } should leftException(e)

            Thread.currentThread().name shouldStartWith "single"
          }
        }
      }
    }

    @Test fun parZip4FinishesOnSingleThread() = runTestUsingDefaultDispatcher {
      checkAll(Arb.string()) {
        val res = resourceScope {
          val ctx = singleThreadContext("single")
          parZip(
            ctx,
            { Thread.currentThread().name },
            { Thread.currentThread().name },
            { Thread.currentThread().name },
            { Thread.currentThread().name }) { a, b, c, d -> listOf(a, b, c, d) }
        }
        assertSoftly {
          res.forEach { it shouldStartWith "single" }
        }
      }
    }
  }

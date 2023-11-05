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
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.test.Test

class ParZip2JvmTest {
  @Test fun parZip2ReturnsToOriginalContext() = runTest {
    val zipCtxName = "parZip2"
    resourceScope {
      val zipCtx = executor { Executors.newFixedThreadPool(2, NamedThreadFactory(zipCtxName)) }
      withContext(single()) {
        Thread.currentThread().name shouldStartWith "single"

        val (s1, s2) = parZip(
          zipCtx,
          { Thread.currentThread().name },
          { Thread.currentThread().name }) { a, b -> Pair(a, b) }

        s1 shouldStartWith zipCtxName
        s2 shouldStartWith zipCtxName
        Thread.currentThread().name shouldStartWith "single"
      }
    }
  }

  @Test fun parZip2ReturnsToOriginalContextOnFailure() = runTest {
    val zipCtxName = "parZip2"
    resourceScope {
      val zipCtx = executor { Executors.newFixedThreadPool(2, NamedThreadFactory(zipCtxName)) }
      checkAll(Arb.int(1..2), Arb.throwable()) { choose, e ->
        withContext(single()) {
          Thread.currentThread().name shouldStartWith "single"

          Either.catch {
            when (choose) {
              1 -> parZip(zipCtx, { e.suspend() }, { awaitCancellation() }) { _, _ -> Unit }
              else -> parZip(zipCtx, { awaitCancellation() }, { e.suspend() }) { _, _ -> Unit }
            }
          } should leftException(e)

          Thread.currentThread().name shouldStartWith "single"
        }
      }
    }
  }

  @Test fun parZip2FinishesOnSingleThread() = runTest {
    checkAll(Arb.string()) {
      val res = resourceScope {
        val ctx = singleThreadContext("single")
        parZip(ctx, { Thread.currentThread().name }, { Thread.currentThread().name }) { a, b -> listOf(a, b) }
      }
      assertSoftly {
        res.forEach { it shouldStartWith "single" }
      }
    }
  }
}

suspend fun parallelCtx(
  nThreads: Int,
  mapCtxName: String,
  use: suspend (CoroutineContext, CoroutineContext) -> Unit,
): Unit = resourceScope {
  use(singleThreadContext("single"), fixedThreadPoolContext(nThreads, mapCtxName))
}

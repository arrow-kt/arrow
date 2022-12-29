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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.withContext

class ParMap8JvmTest : StringSpec({
  val mapCtxName = "parMap8"
  val threadName: suspend CoroutineScope.() -> String =
    { Thread.currentThread().name }

  "parMapN 8 returns to original context" {
      parallelCtx(7, mapCtxName) { _single, _mapCtx ->
        withContext(_single) {
          threadName() shouldStartWith "single"
  
          val result = parZip(
            _mapCtx, threadName, threadName, threadName, threadName, threadName, threadName, threadName, threadName
          ) { a, b, c, d, e, f, g, h ->
            listOf(a, b, c, d, e, f, g, h)
          }

          result[0] shouldStartWith mapCtxName
          result[1] shouldStartWith mapCtxName
          result[2] shouldStartWith mapCtxName
          result[3] shouldStartWith mapCtxName
          result[4] shouldStartWith mapCtxName
          result[5] shouldStartWith mapCtxName
          result[6] shouldStartWith mapCtxName
          result[7] shouldStartWith mapCtxName
          threadName() shouldStartWith "single"
        }
      }

  }

  "parMapN 8 returns to original context on failure" {
    checkAll(Arb.int(1..8), Arb.throwable()) { choose, e ->
      parallelCtx(7, mapCtxName) { _single, _mapCtx ->
        withContext(_single) {
          threadName() shouldStartWith "single"
  
          Either.catch {
            when (choose) {
              1 -> parZip(
                _mapCtx,
                { e.suspend() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() }
              ) { _, _, _, _, _, _, _, _ -> Unit }
      
              2 -> parZip(
                _mapCtx,
                { awaitCancellation() },
                { e.suspend() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() }
              ) { _, _, _, _, _, _, _, _ -> Unit }
      
              3 -> parZip(
                { awaitCancellation() },
                { awaitCancellation() },
                { e.suspend() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() }
              ) { _, _, _, _, _, _, _ -> Unit }
      
              4 -> parZip(
                _mapCtx,
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { e.suspend() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() }
              ) { _, _, _, _, _, _, _, _ -> Unit }
      
              5 -> parZip(
                _mapCtx,
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { e.suspend() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() }
              ) { _, _, _, _, _, _, _, _ -> Unit }
      
              6 -> parZip(
                _mapCtx,
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { e.suspend() },
                { awaitCancellation() },
                { awaitCancellation() }
              ) { _, _, _, _, _, _, _, _ -> Unit }
      
              7 -> parZip(
                _mapCtx,
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { e.suspend() },
                { awaitCancellation() }
              ) { _, _, _, _, _, _, _, _ -> Unit }
      
              else -> parZip(
                _mapCtx,
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { awaitCancellation() },
                { e.suspend() }
              ) { _, _, _, _, _, _, _, _ -> Unit }
            }
          } should leftException(e)
          threadName() shouldStartWith "single"
        }
      }
    }
  }

  "parMapN 8 finishes on single thread" {
    checkAll(Arb.string()) {
      val res = resourceScope {
        val ctx = singleThreadContext("single")
        parZip(
          ctx,
          threadName,
          threadName,
          threadName,
          threadName,
          threadName,
          threadName,
          threadName,
          threadName
        ) { a, b, c, d, e, f, g, h ->
          listOf(a, b, c, d, e, f, g, h)
        }
      }
      assertSoftly {
        res.forEach { it shouldStartWith "single" }
      }
    }
  }
})

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
import kotlin.coroutines.CoroutineContext

class ParMap2JvmTest : StringSpec({
    val mapCtxName = "parMap2"
    
    "parMapN 2 returns to original context" {
        parallelCtx(2, mapCtxName) { _single, _mapCtx ->
          withContext(_single) {
            Thread.currentThread().name shouldStartWith "single"

            val (s1, s2) = parZip(
              _mapCtx,
              { Thread.currentThread().name },
              { Thread.currentThread().name }) { a, b -> Pair(a, b) }

            s1 shouldStartWith mapCtxName
            s2 shouldStartWith mapCtxName
            Thread.currentThread().name shouldStartWith "single"
          }
        }
    }
    
    "parMapN 2 returns to original context on failure" {
      checkAll(Arb.int(1..2), Arb.throwable()) { choose, e ->
        parallelCtx(2, mapCtxName) { _single, _mapCtx ->
          withContext(_single) {
            Thread.currentThread().name shouldStartWith "single"
  
            Either.catch {
              when (choose) {
                1 -> parZip(_mapCtx, { e.suspend() }, { awaitCancellation() }) { _, _ -> Unit }
                else -> parZip(_mapCtx, { awaitCancellation() }, { e.suspend() }) { _, _ -> Unit }
              }
            } should leftException(e)
  
            Thread.currentThread().name shouldStartWith "single"
          }
        }
      }
    }
    
    "parMapN 2 finishes on single thread" {
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
)

suspend fun parallelCtx(
  nThreads: Int,
  mapCtxName: String,
  use: suspend (CoroutineContext, CoroutineContext) -> Unit,
): Unit = resourceScope {
  use(singleThreadContext("single"), fixedThreadPoolContext(nThreads, mapCtxName))
}

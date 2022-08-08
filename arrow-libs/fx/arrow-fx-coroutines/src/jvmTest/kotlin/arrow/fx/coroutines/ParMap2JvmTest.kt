package arrow.fx.coroutines

import arrow.core.Either
import arrow.core.continuations.AtomicRef
import arrow.fx.coroutines.ResourceScope
import arrow.fx.coroutines.resource
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.should
import io.kotest.matchers.string.shouldStartWith
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

class ParMap2JvmTest : ArrowFxSpec(
  spec = {
    val mapCtxName = "parMap2"
    
    "parMapN 2 returns to original context" {
      
      checkAll {
        parallelCtx(2, mapCtxName).use { (_single, _mapCtx) ->
          withContext(_single) {
            threadName() shouldStartWith singleThreadName
            
            val (s1, s2) = parZip(
              _mapCtx,
              { Thread.currentThread().name },
              { Thread.currentThread().name }) { a, b -> Pair(a, b) }
            
            s1 shouldStartWith mapCtxName
            s2 shouldStartWith mapCtxName
            threadName() shouldStartWith singleThreadName
          }
        }
      }
    }
    
    "parMapN 2 returns to original context on failure" {
      checkAll(Arb.int(1..2), Arb.throwable()) { choose, e ->
        parallelCtx(2, mapCtxName).use { (_single, _mapCtx) ->
          withContext(_single) {
            threadName() shouldStartWith singleThreadName
            
            Either.catch {
              when (choose) {
                1 -> parZip(_mapCtx, { e.suspend() }, { never<Nothing>() }) { _, _ -> Unit }
                else -> parZip(_mapCtx, { never<Nothing>() }, { e.suspend() }) { _, _ -> Unit }
              }
            } should leftException(e)
            
            threadName() shouldStartWith singleThreadName
          }
        }
      }
    }
    
    "parMapN 2 finishes on single thread" {
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

suspend fun ResourceScope.newFixedThreadPool(
  nThreads: Int,
  mapCtxName: String
): CoroutineContext =
  executor {
    val count = AtomicRef(0)
    Executors.newFixedThreadPool(nThreads) { r ->
      Thread(r, "$mapCtxName-${count.get()}")
        .apply { isDaemon = true }
    }
  }

fun parallelCtx(
  nThreads: Int,
  mapCtxName: String
): Resource<Pair<CoroutineContext, CoroutineContext>> = resource {
  Pair(single.bind(), newFixedThreadPool(nThreads, mapCtxName))
}

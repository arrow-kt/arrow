package arrow.fx.coroutines

import arrow.core.Either
import arrow.core.Tuple4
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

class ParMap4JvmTest : StringSpec({
    val mapCtxName = "parMap4"
    
    "parMapN 4 returns to original context" {
        parallelCtx(4, mapCtxName) { _single, _mapCtx ->
          withContext(_single) {
            Thread.currentThread().name shouldStartWith "single"
  
            val (s1, s2, s3, s4) = parZip(
              _mapCtx,
              { Thread.currentThread().name },
              { Thread.currentThread().name },
              { Thread.currentThread().name },
              { Thread.currentThread().name }
            ) { a, b, c, d -> Tuple4(a, b, c, d) }
  
            s1 shouldStartWith mapCtxName
            s2 shouldStartWith mapCtxName
            s3 shouldStartWith mapCtxName
            s4 shouldStartWith mapCtxName
            Thread.currentThread().name shouldStartWith "single"
          }
        }
    }

    "parMapN 4 returns to original context on failure" {
      checkAll(Arb.int(1..4), Arb.throwable()) { choose, e ->
        parallelCtx(4, mapCtxName) { _single, _mapCtx ->
          withContext(_single) {
            Thread.currentThread().name shouldStartWith "single"
  
            Either.catch {
              when (choose) {
                1 -> parZip(
                  _mapCtx,
                  { e.suspend() },
                  { awaitCancellation() },
                  { awaitCancellation() },
                  { awaitCancellation() }
                ) { _, _, _, _ -> Unit }
      
                2 -> parZip(
                  _mapCtx,
                  { awaitCancellation() },
                  { e.suspend() },
                  { awaitCancellation() },
                  { awaitCancellation() }
                ) { _, _, _, _ -> Unit }
      
                3 -> parZip(
                  _mapCtx,
                  { awaitCancellation() },
                  { awaitCancellation() },
                  { e.suspend() },
                  { awaitCancellation() }
                ) { _, _, _, _ -> Unit }
      
                else -> parZip(
                  _mapCtx,
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

    "parMapN 4 finishes on single thread" {
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
)

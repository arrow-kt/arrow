package arrow.fx.coroutines

import arrow.core.Either
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.should
import io.kotest.matchers.string.shouldStartWith
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import kotlinx.coroutines.withContext

class ParMap3JvmTest : ArrowFxSpec(spec = {
  "parMapN 3 returns to original context" {
    val mapCtxName = "parMap3"

    checkAll {
      parallelCtx(3, mapCtxName) { _single, _mapCtx ->
        withContext(_single) {
          Thread.currentThread().name shouldStartWith "single"
  
          val (s1, s2, s3) = parZip(
            _mapCtx,
            { Thread.currentThread().name },
            { Thread.currentThread().name },
            { Thread.currentThread().name }) { a, b, c -> Triple(a, b, c) }
  
          s1 shouldStartWith mapCtxName
          s2 shouldStartWith mapCtxName
          s3 shouldStartWith mapCtxName
          Thread.currentThread().name shouldStartWith "single"
        }
      }
    }
  }

  "parMapN 3 returns to original context on failure" {
    val mapCtxName = "parMap3"

    checkAll(Arb.int(1..3), Arb.throwable()) { choose, e ->
      parallelCtx(3, mapCtxName) { _single, _mapCtx ->
        withContext(_single) {
          Thread.currentThread().name shouldStartWith "single"
  
          Either.catch {
            when (choose) {
              1 -> parZip(
                _mapCtx,
                { e.suspend() },
                { never<Nothing>() },
                { never<Nothing>() }
              ) { _, _, _ -> Unit }
      
              2 -> parZip(
                _mapCtx,
                { never<Nothing>() },
                { e.suspend() },
                { never<Nothing>() }
              ) { _, _, _ -> Unit }
      
              else -> parZip(
                _mapCtx,
                { never<Nothing>() },
                { never<Nothing>() },
                { e.suspend() }
              ) { _, _, _ -> Unit }
            }
          } should leftException(e)
  
          Thread.currentThread().name shouldStartWith "single"
        }
      }
    }
  }

  "parMapN 3 finishes on single thread" {
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
})

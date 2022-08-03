package arrow.fx.coroutines

import arrow.core.Either
import arrow.core.Tuple4
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.should
import io.kotest.matchers.string.shouldStartWith
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

class ParMap4JvmTest : ArrowFxSpec(
  spec = {
    val mapCtxName = "parMap4"
    
    "parMapN 4 returns to original context" {
      checkAll {
        parallelCtx(4, mapCtxName).use { (_single, _mapCtx) ->
          withContext(_single) {
            threadName() shouldStartWith singleThreadName

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
            threadName() shouldStartWith singleThreadName
          }
        }
      }
    }

    "parMapN 4 returns to original context on failure" {
      checkAll(Arb.int(1..4), Arb.throwable()) { choose, e ->
        parallelCtx(4, mapCtxName).use { (_single, _mapCtx) ->
          withContext(_single) {
            threadName() shouldStartWith singleThreadName

            Either.catch {
              when (choose) {
                1 -> parZip(
                  _mapCtx,
                  { e.suspend() },
                  { never<Nothing>() },
                  { never<Nothing>() },
                  { never<Nothing>() }
                ) { _, _, _, _ -> Unit }
                2 -> parZip(
                  _mapCtx,
                  { never<Nothing>() },
                  { e.suspend() },
                  { never<Nothing>() },
                  { never<Nothing>() }
                ) { _, _, _, _ -> Unit }
                3 -> parZip(
                  _mapCtx,
                  { never<Nothing>() },
                  { never<Nothing>() },
                  { e.suspend() },
                  { never<Nothing>() }
                ) { _, _, _, _ -> Unit }
                else -> parZip(
                  _mapCtx,
                  { never<Nothing>() },
                  { never<Nothing>() },
                  { never<Nothing>() },
                  { e.suspend() }
                ) { _, _, _, _ -> Unit }
              }
            } should leftException(e)

            threadName() shouldStartWith singleThreadName
          }
        }
      }
    }

    "parMapN 4 finishes on single thread" {
      checkAll(Arb.string()) {
        val res = single.use { ctx ->
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

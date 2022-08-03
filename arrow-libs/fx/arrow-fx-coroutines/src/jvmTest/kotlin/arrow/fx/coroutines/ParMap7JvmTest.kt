package arrow.fx.coroutines

import arrow.core.Either
import arrow.core.Tuple7
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.should
import io.kotest.matchers.string.shouldStartWith
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

class ParMap7JvmTest : ArrowFxSpec(
  spec = {
    val mapCtxName = "parMap7"
    val threadName: suspend CoroutineScope.() -> String =
      { Thread.currentThread().name }

    "parMapN 7 returns to original context" {
      checkAll {
        parallelCtx(7, mapCtxName).use { (_single, _mapCtx) ->
          withContext(_single) {
            threadName() shouldStartWith singleThreadName

            val (s1, s2, s3, s4, s5, s6, s7) = parZip(
              _mapCtx, threadName, threadName, threadName, threadName, threadName, threadName, threadName
            ) { a, b, c, d, e, f, g ->
              Tuple7(a, b, c, d, e, f, g)
            }

            s1 shouldStartWith mapCtxName
            s2 shouldStartWith mapCtxName
            s3 shouldStartWith mapCtxName
            s4 shouldStartWith mapCtxName
            s5 shouldStartWith mapCtxName
            s6 shouldStartWith mapCtxName
            s7 shouldStartWith mapCtxName
            threadName() shouldStartWith singleThreadName
          }
        }
      }
    }

    "parMapN 7 returns to original context on failure" {
      checkAll(Arb.int(1..7), Arb.throwable()) { choose, e ->
        parallelCtx(7, mapCtxName).use { (_single, _mapCtx) ->
          withContext(_single) {
            threadName() shouldStartWith singleThreadName

            Either.catch {
              when (choose) {
                1 -> parZip(
                  _mapCtx,
                  { e.suspend() },
                  { never<Nothing>() },
                  { never<Nothing>() },
                  { never<Nothing>() },
                  { never<Nothing>() },
                  { never<Nothing>() },
                  { never<Nothing>() }
                ) { _, _, _, _, _, _, _ -> Unit }
                2 -> parZip(
                  _mapCtx,
                  { never<Nothing>() },
                  { e.suspend() },
                  { never<Nothing>() },
                  { never<Nothing>() },
                  { never<Nothing>() },
                  { never<Nothing>() },
                  { never<Nothing>() }
                ) { _, _, _, _, _, _, _ -> Unit }
                3 -> parZip(
                  _mapCtx,
                  { never<Nothing>() },
                  { never<Nothing>() },
                  { e.suspend() },
                  { never<Nothing>() },
                  { never<Nothing>() },
                  { never<Nothing>() },
                  { never<Nothing>() }
                ) { _, _, _, _, _, _, _ -> Unit }
                4 -> parZip(
                  _mapCtx,
                  { never<Nothing>() },
                  { never<Nothing>() },
                  { never<Nothing>() },
                  { e.suspend() },
                  { never<Nothing>() },
                  { never<Nothing>() },
                  { never<Nothing>() }
                ) { _, _, _, _, _, _, _ -> Unit }
                5 -> parZip(
                  _mapCtx,
                  { never<Nothing>() },
                  { never<Nothing>() },
                  { never<Nothing>() },
                  { never<Nothing>() },
                  { e.suspend() },
                  { never<Nothing>() },
                  { never<Nothing>() }
                ) { _, _, _, _, _, _, _ -> Unit }
                6 -> parZip(
                  _mapCtx,
                  { never<Nothing>() },
                  { never<Nothing>() },
                  { never<Nothing>() },
                  { never<Nothing>() },
                  { never<Nothing>() },
                  { e.suspend() },
                  { never<Nothing>() }
                ) { _, _, _, _, _, _, _ -> Unit }
                else -> parZip(
                  _mapCtx,
                  { never<Nothing>() },
                  { never<Nothing>() },
                  { never<Nothing>() },
                  { never<Nothing>() },
                  { never<Nothing>() },
                  { never<Nothing>() },
                  { e.suspend() }
                ) { _, _, _, _, _, _, _ -> Unit }
              }
            } should leftException(e)
            threadName() shouldStartWith singleThreadName
          }
        }
      }
    }

    "parMapN 7 finishes on single thread" {
      checkAll(Arb.string()) {
        val res = single.use { ctx ->
          parZip(
            ctx,
            threadName,
            threadName,
            threadName,
            threadName,
            threadName,
            threadName,
            threadName
          ) { a, b, c, d, e, f, g ->
            listOf(a, b, c, d, e, f, g)
          }
        }
        assertSoftly {
          res.forEach { it shouldStartWith "single" }
        }
      }
    }
  }
)

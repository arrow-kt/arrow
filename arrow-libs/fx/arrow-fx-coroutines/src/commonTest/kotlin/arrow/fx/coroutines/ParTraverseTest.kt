package arrow.fx.coroutines

import arrow.atomic.Atomic
import arrow.atomic.update
import arrow.core.Either
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

@ExperimentalTime
class ParTraverseTest : ArrowFxSpec(
  spec = {

    "parTraverse can traverse effect full computations" {
      val ref = Atomic(0)
      (0 until 100).parMap {
        ref.update { it + 1 }
      }
      ref.value shouldBe 100
    }

    "parTraverse runs in parallel" {
      val promiseA = CompletableDeferred<Unit>()
      val promiseB = CompletableDeferred<Unit>()
      val promiseC = CompletableDeferred<Unit>()

      listOf(
        suspend {
          promiseA.await()
          promiseC.complete(Unit)
        },
        suspend {
          promiseB.await()
          promiseA.complete(Unit)
        },
        suspend {
          promiseB.complete(Unit)
          promiseC.await()
        }
      ).parSequence()
    }

    "parTraverse results in the correct error" {
      checkAll(
        Arb.int(min = 10, max = 20),
        Arb.int(min = 1, max = 9),
        Arb.throwable()
      ) { n, killOn, e ->
        Either.catch {
          (0 until n).parMap { i ->
            if (i == killOn) throw e else Unit
          }
        } should leftException(e)
      }
    }

    "parTraverse identity is identity" {
      checkAll(Arb.list(Arb.int())) { l ->
        l.parMap { it } shouldBe l
      }
    }

    "parTraverse stack-safe" {
      val count = 20_000
      val l = (0 until count).parMap { it }
      l shouldBe (0 until count).toList()
    }

    // TODO: Fix
    // "parTraverseN can traverse effect full computations" {
    //   val ref = Atomic(0)
    //   (0 until 100).parMapN(5) {
    //     ref.update { it + 1 }
    //   }
    //   ref.value shouldBe 100
    // }

    // TODO: Fix
    // "parTraverseN(3) runs in (3) parallel" {
    //   val promiseA = CompletableDeferred<Unit>()
    //   val promiseB = CompletableDeferred<Unit>()
    //   val promiseC = CompletableDeferred<Unit>()
    //
    //   listOf(
    //     suspend {
    //       promiseA.await()
    //       promiseC.complete(Unit)
    //     },
    //     suspend {
    //       promiseB.await()
    //       promiseA.complete(Unit)
    //     },
    //     suspend {
    //       promiseB.complete(Unit)
    //       promiseC.await()
    //     }
    //   ).parSequenceN(3)
    // }

    // TODO: Fix
    // "parTraverseN(1) times out running 3 tasks" {
    //   val promiseA = CompletableDeferred<Unit>()
    //   val promiseB = CompletableDeferred<Unit>()
    //   val promiseC = CompletableDeferred<Unit>()
    //
    //   withTimeoutOrNull(10.milliseconds) {
    //     listOf(
    //       suspend {
    //         promiseA.await()
    //         promiseC.complete(Unit)
    //       },
    //       suspend {
    //         promiseB.await()
    //         promiseA.complete(Unit)
    //       },
    //       suspend {
    //         promiseB.complete(Unit)
    //         promiseC.await()
    //       }
    //     ).parSequenceN(1)
    //   } shouldBe null
    // }

    // TODO: Fix
    // "parTraverseN identity is identity" {
    //   checkAll(Arb.list(Arb.int())) { l ->
    //     l.parTraverseN(5) { it } shouldBe l
    //   }
    // }

    // TODO: Fix
    // "parTraverseN results in the correct error" {
    //   checkAll(
    //     Arb.int(min = 10, max = 20),
    //     Arb.int(min = 1, max = 9),
    //     Arb.throwable()
    //   ) { n, killOn, e ->
    //     Either.catch {
    //       (0 until n).parTraverseN(3) { i ->
    //         if (i == killOn) throw e else Unit
    //       }
    //     } should leftException(e)
    //   }
    // }

    // TODO: Fix
    // "parTraverseN stack-safe" {
    //   val count = 20_000
    //   val l = (0 until count).parTraverseN(20) { it }
    //   l shouldBe (0 until count).toList()
    // }

    // TODO: Fix
    // "parSequenceN can traverse effect full computations" {
    //   val ref = Atomic(0)
    //   (0 until 100)
    //     .map { suspend { ref.update { it + 1 } } }
    //     .parSequenceN(5)
    //
    //   ref.value shouldBe 100
    // }
  }
)

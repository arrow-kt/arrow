package arrow.fx.coroutines

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import kotlinx.coroutines.CompletableDeferred

//todo(#2728): @marc check if this test is still valid after removing traverse
/*
class parMapEitherTest : ArrowFxSpec(
  spec = {
    "parMapEither can traverse effect full computations" {
      val ref = Atomic(0)
      (0 until 100).parMapEither {
        ref.update { it + 1 }.right()
      }
      ref.get() shouldBe 100
    }

    "parMapEither runs in parallel" {
      val promiseA = CompletableDeferred<Unit>()
      val promiseB = CompletableDeferred<Unit>()
      val promiseC = CompletableDeferred<Unit>()

      listOf(
        suspend {
          promiseA.await()
          promiseC.complete(Unit).right()
        },
        suspend {
          promiseB.await()
          promiseA.complete(Unit).right()
        },
        suspend {
          promiseB.complete(Unit)
          promiseC.await().right()
        }
      ).parMapEither { it() }
    }

    "parMapEither results in the correct left" {
      checkAll(
        Arb.int(min = 10, max = 20),
        Arb.int(min = 1, max = 9),
        Arb.string()
      ) { n, killOn, e ->
        (0 until n).parMapEither { i ->
          if (i == killOn) e.left() else Unit.right()
        } shouldBe e.left()
      }
    }

    //todo(#2728): @marc check if this test is still valid after removing traverse
    */
/*"parMapEither identity is identity" {
      checkAll(Arb.list(Arb.either(Arb.string(), Arb.int()))) { l ->
        val containsError = l.any(Either<String, Int>::isLeft)
        val res = l.parMapEither { it }

        if (containsError) l.contains<Either<String, Any>>(res) shouldBe true
        else res shouldBe l.sequence()
      }
    }*//*


    "parMapEither results in the correct error" {
      checkAll(
        Arb.int(min = 10, max = 20),
        Arb.int(min = 1, max = 9),
        Arb.throwable()
      ) { n, killOn, e ->
        Either.catch {
          (0 until n).parMapEither { i ->
            if (i == killOn) throw e else Unit.right()
          }
        } should leftException(e)
      }
    }

    "parMapEither stack-safe" {
      val count = 20_000
      val l = (0 until count).parMapEither { it.right() }
      l shouldBe (0 until count).toList().right()
    }
  }
)
*/

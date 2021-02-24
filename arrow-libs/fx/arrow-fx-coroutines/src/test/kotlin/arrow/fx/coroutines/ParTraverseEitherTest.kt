package arrow.fx.coroutines

import arrow.core.Either
import arrow.core.extensions.either.applicative.applicative
import arrow.core.extensions.list.traverse.sequence
import arrow.core.left
import arrow.core.right
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.string

class ParTraverseEitherTest : ArrowFxSpec(
  spec = {
    "parTraverseEither can traverse effect full computations" {
      val ref = Atomic(0)
      (0 until 100).parTraverseEither {
        ref.update { it + 1 }.right()
      }
      ref.get() shouldBe 100
    }

    "parTraverseEither runs in parallel" {
      val promiseA = Promise<Unit>()
      val promiseB = Promise<Unit>()
      val promiseC = Promise<Unit>()

      listOf(
        suspend {
          promiseA.get()
          promiseC.complete(Unit).right()
        },
        suspend {
          promiseB.get()
          promiseA.complete(Unit).right()
        },
        suspend {
          promiseB.complete(Unit)
          promiseC.get().right()
        }
      ).parTraverseEither { it() }
    }

    "parTraverseEither results in the correct left" {
      checkAll(
        Arb.int(min = 10, max = 20),
        Arb.int(min = 1, max = 9),
        Arb.string()
      ) { n, killOn, e ->
        (0 until n).parTraverseEither { i ->
          if (i == killOn) e.left() else Unit.right()
        } shouldBe e.left()
      }
    }

    "parTraverseEither identity is identity" {
      checkAll(Arb.list(Arb.either(Arb.string(), Arb.int()))) { l ->
        val containsError = l.any(Either<String, Int>::isLeft)
        val res = l.parTraverseEither { it }

        if (containsError) l.contains(res) shouldBe true
        else res shouldBe l.sequence(Either.applicative())
      }
    }

    "parTraverseEither results in the correct error" {
      checkAll(
        Arb.int(min = 10, max = 20),
        Arb.int(min = 1, max = 9),
        Arb.throwable()
      ) { n, killOn, e ->
        Either.catch {
          (0 until n).parTraverseEither { i ->
            if (i == killOn) throw e else Unit.right()
          }
        } should leftException(e)
      }
    }

    "parTraverseEither stack-safe" {
      val count = 20_000
      val l = (0 until count).parTraverseEither { it.right() }
      l shouldBe (0 until count).toList().right()
    }

    "parTraverseEither finishes on single thread " { // 100 is same default length as Arb.list
      checkAll(Arb.int(min = Int.MIN_VALUE, max = 100)) { i ->
        single.use { ctx ->
          (0 until i).parTraverseEither(ctx) { Thread.currentThread().name.right() }
        } shouldBe (0 until i).map { "single" }.right()
      }
    }
  }
)

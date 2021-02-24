package arrow.fx.coroutines

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.Validated
import arrow.core.extensions.list.traverse.sequence
import arrow.core.extensions.nonemptylist.semigroup.semigroup
import arrow.core.extensions.validated.applicative.applicative
import arrow.core.extensions.validated.bifunctor.mapLeft
import arrow.core.identity
import arrow.core.invalidNel
import arrow.core.validNel
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.string

class ParTraverseValidatedTest : ArrowFxSpec(
  spec = {
    "parTraverseValidated can traverse effect full computations" {
      val ref = Atomic(0)
      (0 until 100).parTraverseValidated(NonEmptyList.semigroup()) {
        ref.update { it + 1 }.validNel()
      }
      ref.get() shouldBe 100
    }

    "parTraverseValidated runs in parallel" {
      val promiseA = Promise<Unit>()
      val promiseB = Promise<Unit>()
      val promiseC = Promise<Unit>()

      listOf(
        suspend {
          promiseA.get()
          promiseC.complete(Unit).validNel()
        },
        suspend {
          promiseB.get()
          promiseA.complete(Unit).validNel()
        },
        suspend {
          promiseB.complete(Unit)
          promiseC.get().validNel()
        }
      ).parTraverseValidated(NonEmptyList.semigroup()) { it() }
    }

    "parTraverseValidated results in the correct left" {
      checkAll(
        Arb.int(min = 10, max = 20),
        Arb.int(min = 1, max = 9),
        Arb.string()
      ) { n, killOn, e ->
        (0 until n).parTraverseValidated(NonEmptyList.semigroup()) { i ->
          if (i == killOn) e.invalidNel() else Unit.validNel()
        } shouldBe e.invalidNel()
      }
    }

    "parTraverseValidated identity is identity" {
      checkAll(Arb.list(Arb.validatedNel(Arb.int(), Arb.int()))) { l ->
        val res = l.parTraverseValidated(NonEmptyList.semigroup(), ::identity)
        res shouldBe l.sequence(Validated.applicative(NonEmptyList.semigroup()))
          // TODO Fix with Arrow Core Iterable<A>.traverseValidated
          .mapLeft { NonEmptyList.fromListUnsafe(it.reversed()) }
      }
    }

    "parTraverseValidated results in the correct error" {
      checkAll(
        Arb.int(min = 10, max = 20),
        Arb.int(min = 1, max = 9),
        Arb.throwable()
      ) { n, killOn, e ->
        Either.catch {
          (0 until n).parTraverseValidated(NonEmptyList.semigroup()) { i ->
            if (i == killOn) throw e else Unit.validNel()
          }
        } should leftException(e)
      }
    }

    "parTraverseValidated stack-safe" {
      val count = 20_000
      val l = (0 until count).parTraverseValidated(NonEmptyList.semigroup()) { it.validNel() }
      l shouldBe (0 until count).toList().validNel()
    }

    "parTraverseValidated finishes on single thread " { // 100 is same default length as Arb.list
      checkAll(Arb.int(min = Int.MIN_VALUE, max = 100)) { i ->
        single.use { ctx ->
          (0 until i).parTraverseValidated(ctx, NonEmptyList.semigroup()) { Thread.currentThread().name.validNel() }
        } shouldBe (0 until i).map { "single" }.validNel()
      }
    }
  }
)

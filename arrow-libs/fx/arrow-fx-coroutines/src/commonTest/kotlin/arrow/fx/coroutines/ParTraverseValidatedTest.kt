package arrow.fx.coroutines

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.Validated
import arrow.core.invalidNel
import arrow.core.sequence
import arrow.core.validNel
import arrow.typeclasses.Semigroup
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.coroutines.CompletableDeferred

class ParTraverseValidatedTest : StringSpec({
    "parTraverseValidated can traverse effect full computations" {
      val ref = Atomic(0)
      (0 until 100).parTraverseValidated(Semigroup.nonEmptyList()) {
        ref.update { it + 1 }.validNel()
      }
      ref.get() shouldBe 100
    }

    "parTraverseValidated runs in parallel" {
      val promiseA = CompletableDeferred<Unit>()
      val promiseB = CompletableDeferred<Unit>()
      val promiseC = CompletableDeferred<Unit>()

      listOf(
        suspend {
          promiseA.await()
          promiseC.complete(Unit).validNel()
        },
        suspend {
          promiseB.await()
          promiseA.complete(Unit).validNel()
        },
        suspend {
          promiseB.complete(Unit)
          promiseC.await().validNel()
        }
      ).parTraverseValidated(Semigroup.nonEmptyList()) { it() }
    }

    "parTraverseValidated results in the correct left" {
      checkAll(
        Arb.int(min = 10, max = 20),
        Arb.int(min = 1, max = 9),
        Arb.string()
      ) { n, killOn, e ->
        (0 until n).parTraverseValidated(Semigroup.nonEmptyList()) { i ->
          if (i == killOn) e.invalidNel() else Unit.validNel()
        } shouldBe e.invalidNel()
      }
    }

    "parTraverseValidated identity is identity" {
      checkAll(Arb.list(Arb.validatedNel(Arb.int(), Arb.int()))) { l ->
        val res: Validated<NonEmptyList<Int>, List<Int>> = l.parTraverseValidated(Semigroup.nonEmptyList()) { it }
        res shouldBe l.sequence(Semigroup.nonEmptyList())
      }
    }

    "parTraverseValidated results in the correct error" {
      checkAll(
        Arb.int(min = 10, max = 20),
        Arb.int(min = 1, max = 9),
        Arb.throwable()
      ) { n, killOn, e ->
        Either.catch {
          (0 until n).parTraverseValidated(Semigroup.nonEmptyList()) { i ->
            if (i == killOn) throw e else Unit.validNel()
          }
        } should leftException(e)
      }
    }

    "parTraverseValidated stack-safe" {
      val count = 20_000
      val l = (0 until count).parTraverseValidated(Semigroup.nonEmptyList()) { it.validNel() }
      l shouldBe (0 until count).toList().validNel()
    }
  }
)

package arrow.fx.coroutines

import arrow.atomic.Atomic
import arrow.atomic.update
import arrow.core.Either
import arrow.core.raise.result
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.result.shouldBeFailure
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.coroutines.CompletableDeferred

class ParTraverseResultTest : StringSpec({
    "parTraverseResult can traverse effect full computations" {
      val ref = Atomic(0)
      (0 until 100).parTraverseResult {
        Result.success(ref.update { it + 1 })
      }
      ref.get() shouldBe 100
    }

    "parTraverseResult runs in parallel" {
      val promiseA = CompletableDeferred<Unit>()
      val promiseB = CompletableDeferred<Unit>()
      val promiseC = CompletableDeferred<Unit>()

      listOf(
        suspend {
          promiseA.await()
          Result.success(promiseC.complete(Unit))
        },
        suspend {
          promiseB.await()
          Result.success(promiseA.complete(Unit))
        },
        suspend {
          promiseB.complete(Unit)
          Result.success(promiseC.await())
        }
      ).parTraverseResult { it() }
    }

    "parTraverseResult results in the correct left" {
      checkAll(
        Arb.int(min = 10, max = 20),
        Arb.int(min = 1, max = 9)
      ) { n, killOn ->
        (0 until n).parTraverseResult { i ->
          if (i == killOn) Result.failure(RuntimeException()) else Result.success(Unit)
        }.shouldBeFailure<RuntimeException>()
      }
    }

    "parTraverseResult identity is identity" {
      checkAll(Arb.list(Arb.result(Arb.int()))) { l ->
        val res = l.parTraverseResult { it }
        if (l.any { it.isFailure }) l.shouldContain(res)
        else res shouldBe result { l.map { it.bind() } }
      }
    }

    "parTraverseResult results in the correct error" {
      checkAll(
        Arb.int(min = 10, max = 20),
        Arb.int(min = 1, max = 9),
        Arb.string().orNull()
      ) { n, killOn, msg ->
        (0 until n).parTraverseResult { i ->
          if (i == killOn) throw RuntimeException(msg) else Result.success(Unit)
        }.exceptionOrNull().shouldBeTypeOf<RuntimeException>().message shouldBe msg
      }
    }

    "parTraverseResult stack-safe" {
      val count = 20_000
      val l = (0 until count).parTraverseResult { Result.success(it) }
      l shouldBe Result.success((0 until count).toList())
    }
  }
)

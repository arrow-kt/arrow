package arrow.fx.coroutines

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.positiveInts
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.reduce
import kotlin.time.ExperimentalTime

@ExperimentalTime
class FlowTest : ArrowFxSpec(
  spec = {

    "Retry - flow fails" {
      checkAll(Arb.int(), Arb.positiveInts(10)) { a, n ->
        var counter = 0
        val e = shouldThrow<RuntimeException> {
          flow {
            emit(a)
            if (++counter <= 11) throw RuntimeException("Bang!")
          }.retry(Schedule.recurs(n))
            .collect()
        }
        e.message shouldBe "Bang!"
      }
    }

    "Retry - flow succeeds" {
      checkAll(Arb.int(), Arb.int(5, 10)) { a, n ->
        var counter = 0
        val sum = flow {
          emit(a)
          if (++counter <= 5) throw RuntimeException("Bang!")
        }.retry(Schedule.recurs(n))
          .reduce { acc, int -> acc + int }

        sum shouldBe a * 6
      }
    }
  }
)

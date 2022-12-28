package arrow.fx.resilience

import arrow.fx.coroutines.ArrowFxSpec
import arrow.fx.coroutines.assertThrowable
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.positiveInts
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.reduce

@ExperimentalTime
class FlowTest : ArrowFxSpec(
  spec = {

    "Retry - flow fails" {
      val bang = RuntimeException("Bang!")

      checkAll(Arb.int(), Arb.positiveInts(10)) { a, n ->
        var counter = 0
        val e = assertThrowable {
          flow {
            emit(a)
            if (++counter <= 11) throw bang
          }.retry(Schedule.recurs(n))
            .collect()
        }
        e shouldBe bang
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

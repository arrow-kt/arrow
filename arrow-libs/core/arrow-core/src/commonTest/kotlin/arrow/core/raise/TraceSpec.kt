package arrow.core.raise

import arrow.core.right
import arrow.typeclasses.Semigroup
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.coroutines.CompletableDeferred

@OptIn(ExperimentalTraceApi::class)
class TraceSpec : StringSpec({
  "trace is empty when no errors" {
    checkAll(Arb.int()) { i ->
      either<Nothing, Int> {
        traced({ i }) { unreachable() }
      } shouldBe i.right()
    }
  }

  "trace is empty with exception" {
    checkAll(Arb.string()) { msg ->
      val error = RuntimeException(msg)
      shouldThrow<RuntimeException> {
        either<Nothing, Int> {
          traced({ throw error }) { unreachable() }
        }
      }.message shouldBe msg
    }
  }

  "Can trace a typed error" {
    either<RuntimeException, Nothing> {
      traced({ raise(RuntimeException("")) }) { traced ->
        // Remove first 2 lines:
        // arrow.core.raise.RaiseCancellationException
        //	at arrow.core.raise.DefaultRaise.raise(Fold.kt:187)
        val trace = traced.stackTraceToString().lines().drop(2)

        // Remove first line:
        // java.lang.RuntimeException:
        val exceptionTrace = traced.raised.stackTraceToString().lines().drop(1)

        trace shouldBe exceptionTrace
      }
    }
  }

  "nested tracing - identity" {
    val inner = CompletableDeferred<String>()
    ior(String::plus) {
      traced({
        traced({ raise("") }) { traced ->
          inner.complete(traced.stackTraceToString())
        }
      }) { traced ->
        inner.await() shouldBe traced.stackTraceToString()
      }
    }
  }
})

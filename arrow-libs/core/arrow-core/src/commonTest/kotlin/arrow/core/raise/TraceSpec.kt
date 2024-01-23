package arrow.core.raise

import arrow.core.right
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
        traced({ i }) { _,_ -> unreachable() }
      } shouldBe i.right()
    }
  }

  "trace is empty with exception" {
    checkAll(Arb.string()) { msg ->
      val error = RuntimeException(msg)
      shouldThrow<RuntimeException> {
        either<Nothing, Int> {
          traced({ throw error }) { _,_ -> unreachable() }
        }
      }.message shouldBe msg
    }
  }

  "nested tracing - identity" {
    val inner = CompletableDeferred<String>()
    ior(String::plus) {
      traced({
        traced({ raise("") }) { traced, _ ->
          inner.complete(traced.stackTraceToString())
        }
      }) { traced, _ ->
        inner.await() shouldBe traced.stackTraceToString()
      }
    }
  }

  "nested tracing - different types" {
    either {
      traced<Any?, _>({
        traced<String, _> ({
          raise(Unit)
        }) { _, _ -> unreachable() }
      }) { _, unit -> unit shouldBe Unit }
    }
  }
})

package arrow.core.raise

import arrow.core.right
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlin.test.Test
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalTraceApi::class, DelicateRaiseApi::class)
class TraceSpec {
  @Test fun traceIsEmptyWhenNoErrors() = runTest {
    checkAll(Arb.int()) { i ->
      either<Nothing, Int> {
        traced({ i }) { _,_ -> unreachable() }
      } shouldBe i.right()
    }
  }

  @Test fun traceIsEmptyWithException() = runTest {
    checkAll(Arb.string()) { msg ->
      val error = RuntimeException(msg)
      shouldThrow<RuntimeException> {
        either<Nothing, Int> {
          traced({ throw error }) { _,_ -> unreachable() }
        }
      }.message shouldBe msg
    }
  }

  @Test fun nestedTracingIdentity() = runTest {
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

  @Test fun nestedTracingDifferentTypes() = runTest {
    either {
      traced<Any?, _>({
        traced<String, _> ({
          raise(Unit)
        }) { _, _ -> unreachable() }
      }) { _, unit -> unit shouldBe Unit }
    }
  }

  @Test
  fun withErrorMaintainsTrace() = runTest {
    val inner = CompletableDeferred<String>()
    merge {
      traced({
        withError({ str: String -> str.length }) {
          val e = shouldThrow<Traced> {
            raise("")
          }
          inner.complete(e.stackTraceToString())
          throw e
        }
      }) { traced, _ ->
        inner.await() shouldBe traced.stackTraceToString()
      }
    } shouldBe 0
  }
}

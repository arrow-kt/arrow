package arrow.core.raise

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

@OptIn(ExperimentalTraceApi::class)
class TraceJvmSpec : StringSpec({
  "Can trace a typed error" {
    either<RuntimeException, Nothing> {
      traced({ raise(RuntimeException("")) }) { trace, raised ->
        // Remove first 2 lines:
        // arrow.core.raise.RaiseCancellationException
        //	at arrow.core.raise.DefaultRaise.raise(Fold.kt:187)
        val stackTrace = trace.stackTraceToString().lines().drop(2)

        // Remove first line:
        // java.lang.RuntimeException:
        val exceptionStackTrace = raised.stackTraceToString().lines().drop(1)

        stackTrace shouldBe exceptionStackTrace
      }
    }
  }
})

package arrow.core.raise

import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlinx.coroutines.test.runTest
  
@OptIn(ExperimentalTraceApi::class)
class TraceJvmSpec {
  @Test fun canTraceATypedError() = runTest {
    val _ = merge {
      traced({ raise(RuntimeException("")) }) { traced, raised ->
        // Remove first 2 lines:
        // arrow.core.raise.RaiseCancellationException
        //	at arrow.core.raise.DefaultRaise.raise(Fold.kt:187)
        val trace = traced.stackTraceToString().lines().drop(2)

        // Remove first line:
        // java.lang.RuntimeException:
        val exceptionTrace = raised.stackTraceToString().lines().drop(1)

        trace shouldBe exceptionTrace
      }
    }
  }

  @Test fun canTraceAMappedError() = runTest {
    val _ = merge {
      traced({
        withError({ raised: Exception ->
          // Remove first line:
          // java.lang.RuntimeException:
          raised.stackTraceToString().lines().drop(1)
        }) {
          raise(RuntimeException(""))
        }
      }) { traced, exceptionTrace ->
        // Remove first 2 lines:
        // arrow.core.raise.RaiseCancellationException
        //	at arrow.core.raise.DefaultRaise.raise(Fold.kt:187)
        val trace = traced.stackTraceToString().lines().drop(2)
        trace shouldBe exceptionTrace
      }
    }
  }
}

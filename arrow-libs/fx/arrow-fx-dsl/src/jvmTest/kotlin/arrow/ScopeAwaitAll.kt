package arrow

import arrow.await.AwaitAllScope
import arrow.await.awaitAll
import arrow.scoped.ScopingScope
import arrow.scoped.coroutineScope
import arrow.scoped.scoped
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class ScopeAwaitAll {
  @Test
  fun customScopeAwaitAll() = runTest {
    val ex = CancellationException("Boom!")
    scoped {
      awaitAll {
        try {
          parallel(ex)
          fail("Should not finish success")
        } catch (e: CancellationException) {
          assertEquals(ex, e)
        } catch (e: Throwable) {
          fail("Cannot reach this place")
        }
      }
    }
  }
}

context(ScopingScope, AwaitAllScope)
suspend fun parallel(ex: Throwable): List<Int> {
  val scope = coroutineScope()
  return (0..10)
    .map { index ->
      if (index == 10) scope.async {
        println("index: $index")
        delay(100); throw ex
      } else scope.async { println(index); awaitCancellation() }
    }.map { it.await() }
}

package arrow.effects.suspended.fx

import arrow.effects.internal.Platform
import java.util.concurrent.Executor
import kotlin.concurrent.getOrSet
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine
import kotlin.coroutines.suspendCoroutine

//TODO REMOVE
object TrampolineRunLoop {

  @PublishedApi
  internal val maxStackSize = 127

  suspend inline operator fun <A> invoke(noinline f: suspend () -> A): A {
    val currentIterations = iterations.get()
    return if (currentIterations <= maxStackSize) {
      iterations.set(currentIterations + 1)
      f()
    } else {
      //println("reached iterations: $currentIterations, trampolining...")
      iterations.set(0)
      suspendCoroutine { cont ->
        Platform.trampoline {
          f.startCoroutine(Continuation(EmptyCoroutineContext) {
            cont.resumeWith(it)
          })
        }
      }
    }
  }

  @PublishedApi
  internal val iterations: ThreadLocal<Int> = ThreadLocal.withInitial { 0 }

}

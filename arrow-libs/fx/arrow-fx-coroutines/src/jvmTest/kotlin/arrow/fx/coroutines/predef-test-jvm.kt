package arrow.fx.coroutines

import arrow.core.continuations.AtomicRef
import java.util.concurrent.ThreadFactory
import kotlin.coroutines.CoroutineContext

const val singleThreadName: String = "single"

val single: Resource<CoroutineContext> = singleThreadContext(singleThreadName)

val threadName: suspend () -> String =
  { Thread.currentThread().name }

class NamedThreadFactory(private val mkName: (Int) -> String) : ThreadFactory {
  private val count = AtomicRef(0)
  override fun newThread(r: Runnable): Thread =
    Thread(r, mkName(count.get()))
      .apply { isDaemon = true }
}

package arrow.fx.coroutines

import kotlinx.atomicfu.atomic
import java.util.concurrent.ThreadFactory

val singleThreadName = "single"
val single = Resource.singleThreadContext(singleThreadName)

val threadName: suspend () -> String =
  { Thread.currentThread().name }

class NamedThreadFactory(private val mkName: (Int) -> String) : ThreadFactory {
  private val count = atomic(0)
  override fun newThread(r: Runnable): Thread =
    Thread(r, mkName(count.value))
      .apply { isDaemon = true }
}

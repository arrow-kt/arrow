package arrow.fx.coroutines

import arrow.core.continuations.AtomicRef
import arrow.core.test.concurrency.deprecateArrowTestModules
import java.util.concurrent.ThreadFactory
import kotlin.coroutines.CoroutineContext

@Deprecated(deprecateArrowTestModules)
public val singleThreadName: String = "single"

@Deprecated(deprecateArrowTestModules)
public val single: Resource<CoroutineContext> = Resource.singleThreadContext(singleThreadName)

@Deprecated(deprecateArrowTestModules)
public val threadName: suspend () -> String =
  { Thread.currentThread().name }

@Deprecated(deprecateArrowTestModules)
public class NamedThreadFactory(private val mkName: (Int) -> String) : ThreadFactory {
  private val count = AtomicRef(0)
  override fun newThread(r: Runnable): Thread =
    Thread(r, mkName(count.get()))
      .apply { isDaemon = true }
}

@file:OptIn(ExperimentalAtomicApi::class)

package arrow.fx.coroutines

import java.util.concurrent.ThreadFactory
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.fetchAndIncrement

private val namedThreadCount = AtomicInt(0)

class NamedThreadFactory(val name: String): ThreadFactory {
  override fun newThread(r: Runnable): Thread? =
    Thread(r, "$name-${namedThreadCount.fetchAndIncrement()}").apply {
      uncaughtExceptionHandler = Thread.UncaughtExceptionHandler { _, e ->
        e.printStackTrace()
      }
    }
}

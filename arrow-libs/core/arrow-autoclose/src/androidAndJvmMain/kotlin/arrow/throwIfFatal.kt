@file:Suppress("API_NOT_AVAILABLE")

package arrow

import kotlin.coroutines.cancellation.CancellationException

@PublishedApi @IgnorableReturnValue
internal actual fun Throwable.throwIfFatal(): Throwable =
  when (this) {
    is VirtualMachineError, is ThreadDeath, is InterruptedException, is LinkageError, is CancellationException -> throw this
    else -> this
  }

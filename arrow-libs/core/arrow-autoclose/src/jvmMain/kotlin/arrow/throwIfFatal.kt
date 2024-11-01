package arrow

import kotlin.coroutines.cancellation.CancellationException

@PublishedApi
internal actual fun Throwable.throwIfFatal(): Throwable =
  when(this) {
    is VirtualMachineError, is ThreadDeath, is InterruptedException, is LinkageError, is CancellationException -> throw this
    else -> this
  }

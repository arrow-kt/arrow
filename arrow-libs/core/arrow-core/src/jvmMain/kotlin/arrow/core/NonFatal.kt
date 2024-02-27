@file:JvmMultifileClass
@file:JvmName("NonFatalKt")
package arrow.core

import kotlin.coroutines.cancellation.CancellationException

public actual fun NonFatal(t: Throwable): Boolean =
  when (t) {
    is VirtualMachineError, is ThreadDeath, is InterruptedException, is LinkageError, is CancellationException -> false
    else -> true
  }

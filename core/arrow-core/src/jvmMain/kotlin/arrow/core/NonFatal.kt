@file:JvmMultifileClass
@file:JvmName("NonFatalKt")
package arrow.core

import arrow.continuations.generic.ControlThrowable
import kotlin.coroutines.cancellation.CancellationException

public actual fun NonFatal(t: Throwable): Boolean =
  when (t) {
    is VirtualMachineError, is ThreadDeath, is InterruptedException, is LinkageError, is ControlThrowable, is CancellationException -> false
    else -> true
  }

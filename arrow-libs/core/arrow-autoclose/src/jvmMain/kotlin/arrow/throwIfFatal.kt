package arrow

@PublishedApi
internal actual fun Throwable.throwIfFatal(): Throwable =
  when(this) {
    is VirtualMachineError, is ThreadDeath, is InterruptedException, is LinkageError -> throw this
    else -> this
  }

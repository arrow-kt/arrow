package arrow

@PublishedApi
internal actual fun Throwable.throwIfFatal(): Throwable = this

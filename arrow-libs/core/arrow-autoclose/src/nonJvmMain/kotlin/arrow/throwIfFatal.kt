package arrow

import kotlin.coroutines.cancellation.CancellationException

@PublishedApi
internal actual fun Throwable.throwIfFatal(): Throwable = if (this is CancellationException) throw this else this

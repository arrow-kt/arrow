package arrow.core.raise

import kotlin.coroutines.cancellation.CancellationException

internal actual open class CancellationExceptionNoTrace : CancellationException(RaiseCancellationExceptionCaptured)

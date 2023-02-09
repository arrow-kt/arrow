package arrow.core.raise

import kotlin.coroutines.cancellation.CancellationException

public actual open class CancellationExceptionNoTrace : CancellationException("Raised Continuation")

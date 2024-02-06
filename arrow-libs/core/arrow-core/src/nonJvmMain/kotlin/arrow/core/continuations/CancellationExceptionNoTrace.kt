package arrow.core.continuations

import kotlin.coroutines.cancellation.CancellationException

public actual open class CancellationExceptionNoTrace : CancellationException("Shifted Continuation")

package arrow.core

import kotlin.coroutines.cancellation.CancellationException

@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
@RequiresOptIn("This declaration is public only to allow other arrow libraries to use it", RequiresOptIn.Level.ERROR)
public annotation class InternalArrowApi

/**
 * [ControlCancellationException] is a _delicate_ api, and should be used with care.
 * It denotes a short-circuiting exception.
 * Exceptions of this type are deprioritized w.r.t. exception suppression.
 *
 * @see mergeSuppressed
 */
@InternalArrowApi
public open class ControlCancellationException: CancellationException {
    public constructor()
    public constructor(message: String?)
}

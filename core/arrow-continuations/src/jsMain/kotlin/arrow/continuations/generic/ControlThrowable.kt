package arrow.continuations.generic

/**
 * A [Throwable] class intended for control flow.
 * Instance of [ControlThrowable.kt] should **not** be caught,
 * and `arrow.core.NonFatal` does not catch this [Throwable].
 * Thus by extension `Either.catch` and `Validated.catch` also don't catch [ControlThrowable.kt].
 */
public actual open class ControlThrowable : Throwable()

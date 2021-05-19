package arrow.continuations.generic

/**
 * A [Throwable] class intended for control flow.
 * Instance of [ControlThrowable] should **not** be caught,
 * and `arrow.core.NonFatal` does not catch this [Throwable].
 * Thus by extension `Either.catch` and `Validated.catch` also don't catch [ControlThrowable].
 */
open class ControlThrowable : Throwable() {
  override fun fillInStackTrace(): Throwable = this
}

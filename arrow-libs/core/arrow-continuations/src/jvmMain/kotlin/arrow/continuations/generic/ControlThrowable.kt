
package arrow.continuations.generic

/**
 * A [Throwable] class intended for control flow.
 * Instance of [ControlThrowable] should **not** be caught,
 * and `arrow.core.NonFatal` does not catch this [Throwable].
 * By extension, `Either.catch` and `Raise.catch` also don't catch [ControlThrowable].
 */
@Deprecated(deprecateArrowContinuation)
public actual open class ControlThrowable : Throwable() {
  override fun fillInStackTrace(): Throwable = this
}

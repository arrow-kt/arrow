package arrow.continuations.generic

/**
 * A [Throwable] class intended for control flow.
 * Instance of [ControlThrowable] should **not** be caught,
 * and `arrow.core.NonFatal` does not catch this [Throwable].
 * By extension, `Either.catch` and `Raise.catch` also don't catch [ControlThrowable].
 */
@Deprecated(deprecateArrowContinuation)
public expect open class ControlThrowable() : Throwable

internal const val deprecateArrowContinuation: String =
  "arrow.continuation is being discontinued and will be removed in the next version in favor of the Effect/ EagerEffect Runtime. If you depend on low-level APIs as in arrow.continuation, feel free to write us in the Kotlin Slack channel for guidance."

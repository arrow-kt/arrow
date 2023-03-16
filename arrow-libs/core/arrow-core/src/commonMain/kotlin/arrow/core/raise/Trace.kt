@file:JvmMultifileClass
@file:JvmName("RaiseKt")
package arrow.core.raise

import kotlin.coroutines.cancellation.CancellationException
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

@RequiresOptIn("This API is experimental, and may change in the future.")
public annotation class ExperimentalTraceApi

/** Tracing result of `R`. Allows to inspect `R`, and the traces from where it was raised. */
@ExperimentalTraceApi
@JvmInline
public value class Trace(private val exception: CancellationException) {
  /**
   * Returns the stacktrace as a [String]
   *
   * Note, the first line in the stacktrace will be the `RaiseCancellationException`.
   * The users call to `raise` can found in the_second line of the stacktrace.
   */
  public fun stackTraceToString(): String = exception.stackTraceToString()

  /**
   * Prints the stacktrace.
   *
   * Note, the first line in the stacktrace will be the `RaiseCancellationException`.
   * The users call to `raise` can found in the_second line of the stacktrace.
   */
  public fun printStackTrace(): Unit =
    exception.printStackTrace()

  /**
   * Returns the suppressed exceptions that occurred during cancellation of the surrounding coroutines,
   *
   * For example when working with `Resource`, or `bracket`:
   * When consuming a `Resource` fails due to [Raise.raise] it results in `ExitCase.Cancelled`,
   * if the finalizer then results in a `Throwable` it will be added as a `suppressedException` to the [CancellationException].
   */
  public fun suppressedExceptions(): List<Throwable> =
    exception.suppressedExceptions
}

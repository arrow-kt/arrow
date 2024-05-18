@file:JvmMultifileClass
@file:JvmName("RaiseKt")
package arrow.core.raise

import kotlin.coroutines.cancellation.CancellationException
import kotlin.jvm.JvmInline
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

@MustBeDocumented
@RequiresOptIn("This API is experimental, and may change in the future.")
@Retention(AnnotationRetention.BINARY)
public annotation class ExperimentalTraceApi

/** Tracing result. Allows to inspect the traces from where raise was called. */
@ExperimentalTraceApi
@OptIn(DelicateRaiseApi::class)
@JvmInline
public value class Trace
@PublishedApi internal constructor(private val exception: Traced) {
  /**
   * Returns the stacktrace as a [String]
   *
   * Note, the first line in the stacktrace will be the `RaiseCancellationException`.
   * The users call to `raise` can found in the_second line of the stacktrace.
   */
  public fun stackTraceToString(): String = exception.originalTrace?.stackTraceToString() ?: exception.stackTraceToString()

  /**
   * Prints the stacktrace.
   *
   * Note, the first line in the stacktrace will be the `RaiseCancellationException`.
   * The users call to `raise` can found in the_second line of the stacktrace.
   */
  public fun printStackTrace(): Unit =
    exception.originalTrace?.printStackTrace() ?: exception.printStackTrace()

  /**
   * Returns the suppressed exceptions that occurred during cancellation of the surrounding coroutines,
   *
   * For example when working with `Resource`, or `bracket`:
   * When consuming a `Resource` fails due to [Raise.raise] it results in `ExitCase.Cancelled`,
   * if the finalizer then results in a `Throwable` it will be added as a `suppressedException` to the [CancellationException].
   */
  public fun suppressedExceptions(): List<Throwable> =
    exception.originalTrace?.suppressedExceptions().orEmpty() + exception.suppressedExceptions
}

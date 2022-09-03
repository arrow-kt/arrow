@file:JvmMultifileClass
@file:JvmName("Effect")
@file:OptIn(ExperimentalTypeInference::class)

package arrow.core.continuations

import kotlin.coroutines.cancellation.CancellationException
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

/** Tracing result of `R`. Allows to inspect `R`, and the traces from where it was shifted. */
public data class Traced<R>(private val exception: CancellationException, private val shifted: R) {
  /**
   * Returns the stacktrace as a [String]
   *
   * Note, the first line in the stacktrace will be the `ShiftCancellationException`.
   * The users call to `shift` can found in the_second line of the stacktrace.
   */
  public fun stackTraceToString(): String = exception.stackTraceToString()
  
  /**
   * Prints the stacktrace.
   *
   * Note, the first line in the stacktrace will be the `ShiftCancellationException`.
   * The users call to `shift` can found in the_second line of the stacktrace.
   */
  public fun printStackTrace(): Unit =
    exception.printStackTrace()
  
  /**
   * Returns the suppressed exceptions that occurred during cancellation of the surrounding coroutines,
   *
   * For example when working with `Resource`, or `bracket`:
   * When consuming a `Resource` fails due to [Shift.shift] it results in `ExitCase.Cancelled`,
   * if the finalizer then results in a `Throwable` it will be added as a `suppressedException` to the [CancellationException].
   */
  public fun suppressedExceptions(): List<Throwable> =
    exception.suppressedExceptions
}

public fun <R, A> Effect<R, A>.traced(recover: Shift<R>.(traces: Traced<R>) -> Unit): Effect<R, A> =
  effect { traced({ bind() }, recover) }

public fun <R, A> EagerEffect<R, A>.traced(recover: Shift<R>.(traces: Traced<R>) -> Unit): EagerEffect<R, A> =
  eagerEffect { traced({ bind() }, recover) }

/**
 * Inspect a [Traced] value of [R].
 *
 * Tracing [R] can be useful to know where certain errors, or failures are coming from.
 * Let's say you have a `ValidationError`, but it might be shifted from many places in the project.
 *
 * You would have to manually trace where this error is coming from,
 * instead [Traced] offers you ways to inspect the actual stacktrace of where the shifted value occurred.
 *
 * ```kotlin
 * public fun main() {
 *   val error = effect<String, Int> { shift("error") }
 *   error.traced { traced -> traced.printStackTrace() }
 *     .fold({ require(it == "error") }, { error("impossible") })
 * }
 * ```
 * ```text
 * arrow.core.continuations.ShiftCancellationException: Shifted Continuation
 *   at arrow.core.continuations.DefaultShift.shift(Fold.kt:77)
 *   at MainKtKt$main$error$1.invoke(MainKt.kt:6)
 *   at MainKtKt$main$error$1.invoke(MainKt.kt:6)
 *   at arrow.core.continuations.Shift$DefaultImpls.bind(Shift.kt:22)
 *   at arrow.core.continuations.DefaultShift.bind(Fold.kt:74)
 *   at arrow.core.continuations.Effect__TracingKt$traced$2.invoke(Tracing.kt:46)
 *   at arrow.core.continuations.Effect__TracingKt$traced$2.invoke(Tracing.kt:46)
 *   at arrow.core.continuations.Effect__FoldKt.fold(Fold.kt:92)
 *   at arrow.core.continuations.Effect.fold(Unknown Source)
 *   at MainKtKt.main(MainKt.kt:8)
 *   at MainKtKt.main(MainKt.kt)
 * ```
 *
 * NOTE:
 * This implies a performance penalty of creating a stacktrace when calling [Shift.shift],
 * but **this only occurs** when composing `traced`.
 * The stacktrace creation is disabled if no `traced` calls are made within the function composition.
 */
public inline fun <R, A> Shift<R>.traced(
  @BuilderInference program: Shift<R>.() -> A,
  recover: Shift<R>.(traces: Traced<R>) -> Unit,
): A {
  val nested = DefaultShift(true)
  return try {
    program.invoke(nested)
  } catch (e: ShiftCancellationException) {
    val r: R = e.shiftedOrRethrow(nested)
    recover(Traced(e, r))
    shift(r)
  }
}

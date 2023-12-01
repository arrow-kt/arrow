@file:JvmMultifileClass
@file:JvmName("RaiseKt")
@file:OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)

package arrow.core.raise

import arrow.atomic.AtomicBoolean
import arrow.core.Either
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract
import kotlin.coroutines.cancellation.CancellationException
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

/**
 * `invoke` the [Effect] and [fold] the result:
 *  - _success_ [transform] result of [A] to a value of [B].
 *  - _raised_ [recover] from `raised` value of [Error] to a value of [B].
 *  - _exception_ [catch] from [Throwable] by transforming value into [B].
 *
 * This method should never be wrapped in `try`/`catch` as it will not throw any unexpected errors,
 * it will only result in [CancellationException], or fatal exceptions such as `OutOfMemoryError`.
 */
public suspend fun <Error, A, B> Effect<Error, A>.fold(
  catch: suspend (throwable: Throwable) -> B,
  recover: suspend (error: Error) -> B,
  transform: suspend (value: A) -> B,
): B {
  contract {
    callsInPlace(catch, AT_MOST_ONCE)
    callsInPlace(recover, AT_MOST_ONCE)
    callsInPlace(transform, AT_MOST_ONCE)
  }
  val a = recover<Error, A>({ invoke(this) }, { return recover(it) }, { return catch(it) })
  return catch({ transform(a) }) { catch(it) }
}

/**
 * `invoke` the [Effect] and [fold] the result:
 *  - _success_ [transform] result of [A] to a value of [B].
 *  - _raised_ [recover] from `raised` value of [Error] to a value of [B].
 *
 * This function re-throws any exceptions thrown within the [Effect].
 */
public suspend fun <Error, A, B> Effect<Error, A>.fold(
  recover: suspend (error: Error) -> B,
  transform: suspend (value: A) -> B,
): B {
  contract {
    callsInPlace(recover, AT_MOST_ONCE)
    callsInPlace(transform, AT_MOST_ONCE)
  }
  return fold({ throw it }, recover, transform)
}

/**
 * `invoke` the [EagerEffect] and [fold] the result:
 *  - _success_ [transform] result of [A] to a value of [B].
 *  - _raised_ [recover] from `raised` value of [Error] to a value of [B].
 *  - _exception_ [catch] from [Throwable] by transforming value into [B].
 *
 * This method should never be wrapped in `try`/`catch` as it will not throw any unexpected errors,
 * it will only result in [CancellationException], or fatal exceptions such as `OutOfMemoryError`.
 */
public inline fun <Error, A, B> EagerEffect<Error, A>.fold(
  catch: (throwable: Throwable) -> B,
  recover: (error: Error) -> B,
  transform: (value: A) -> B,
): B {
  contract {
    callsInPlace(catch, AT_MOST_ONCE)
    callsInPlace(recover, AT_MOST_ONCE)
    callsInPlace(transform, AT_MOST_ONCE)
  }
  val a = recover<Error, A>({ invoke(this) }, { return recover(it) }, { return catch(it) })
  return catch({ transform(a) }) { catch(it) }
}

/**
 * `invoke` the [EagerEffect] and [fold] the result:
 *  - _success_ [transform] result of [A] to a value of [B].
 *  - _raised_ [recover] from `raised` value of [Error] to a value of [B].
 *
 * This function re-throws any exceptions thrown within the [Effect].
 */
public inline fun <Error, A, B> EagerEffect<Error, A>.fold(recover: (error: Error) -> B, transform: (value: A) -> B): B {
  contract {
    callsInPlace(recover, AT_MOST_ONCE)
    callsInPlace(transform, AT_MOST_ONCE)
  }
  return fold({ throw it }, recover, transform)
}

/**
 * Inspect a [Trace] value of [Error].
 *
 * Tracing [Error] can be useful to know where certain errors, or failures are coming from.
 * Let's say you have a `DomainError`, but it might be raised from many places in the project.
 *
 * You would have to manually _trace_ where this error is coming from,
 * instead [Trace] offers you ways to inspect the actual stacktrace of where the raised value occurred.
 *
 * Beware that tracing can only track the [Raise.bind] or [Raise.raise] call that resulted in the [Error] value,
 * and not any location of where the [Error], or [Either.Left] value was created.
 *
 * ```kotlin
 * public fun main() {
 *   val error = effect<String, Int> { raise("error") }
 *   error.traced { (trace, _: String) -> trace.printStackTrace() }
 *     .fold({ require(it == "error") }, { error("impossible") })
 * }
 * ```
 * ```text
 * arrow.core.continuations.RaiseCancellationException: Raised Continuation
 *   at arrow.core.continuations.DefaultRaise.raise(Fold.kt:77)
 *   at MainKtKt$main$error$1.invoke(MainKt.kt:6)
 *   at MainKtKt$main$error$1.invoke(MainKt.kt:6)
 *   at arrow.core.continuations.Raise$DefaultImpls.bind(Raise.kt:22)
 *   at arrow.core.continuations.DefaultRaise.bind(Fold.kt:74)
 *   at arrow.core.continuations.Effect__TracingKt$traced$2.invoke(Traced.kt:46)
 *   at arrow.core.continuations.Effect__TracingKt$traced$2.invoke(Traced.kt:46)
 *   at arrow.core.continuations.Effect__FoldKt.fold(Fold.kt:92)
 *   at arrow.core.continuations.Effect.fold(Unknown Source)
 *   at MainKtKt.main(MainKt.kt:8)
 *   at MainKtKt.main(MainKt.kt)
 * ```
 *
 * NOTE:
 * This implies a performance penalty of creating a stacktrace when calling [Raise.raise],
 * but **this only occurs** when composing `traced`.
 * The stacktrace creation is disabled if no `traced` calls are made within the function composition.
 */
@ExperimentalTraceApi
public inline fun <Error, A> Raise<Error>.traced(
  @BuilderInference block: Raise<Error>.() -> A,
  trace: (trace: Trace, error: Error) -> Unit
): A {
  contract {
    callsInPlace(block, AT_MOST_ONCE)
    callsInPlace(trace, AT_MOST_ONCE)
  }
  val isOuterTraced = this is DefaultRaise && isTraced
  val nested: DefaultRaise = if (isOuterTraced) this as DefaultRaise else DefaultRaise(true)
  return try {
    block.invoke(nested)
  } catch (e: RaiseCancellationException) {
    val r: Error = e.raisedOrRethrow(nested)
    trace(Trace(e), r)
    if (isOuterTraced) throw e else raise(r)
  }
}

/** Returns the raised value, rethrows the CancellationException if not our scope */
@PublishedApi
@Suppress("UNCHECKED_CAST")
internal fun <R> CancellationException.raisedOrRethrow(raise: DefaultRaise): R =
  when {
    this is RaiseCancellationExceptionNoTrace && this.raise === raise -> raised as R
    this is RaiseCancellationException && this.raise === raise -> raised as R
    else -> throw this
  }

/** Serves as both purposes of a scope-reference token, and a default implementation for Raise. */
@PublishedApi
internal class DefaultRaise(@PublishedApi internal val isTraced: Boolean) : Raise<Any?> {
  private val isActive = AtomicBoolean(true)

  @PublishedApi
  internal fun complete(): Boolean = isActive.getAndSet(false)
  override fun raise(r: Any?): Nothing = when {
    isActive.value -> throw if (isTraced) RaiseCancellationException(r, this) else RaiseCancellationExceptionNoTrace(r, this)
    else -> throw RaiseLeakedException()
  }
}

/** CancellationException is required to cancel coroutines when raising from within them. */
private class RaiseCancellationExceptionNoTrace(val raised: Any?, val raise: Raise<Any?>) :
  CancellationExceptionNoTrace()

private class RaiseCancellationException(val raised: Any?, val raise: Raise<Any?>) : CancellationException()

internal expect open class CancellationExceptionNoTrace() : CancellationException

private class RaiseLeakedException : IllegalStateException(
  """
  raise or bind was called outside of its DSL scope, and the DSL Scoped operator was leaked
  This is kind of usage is incorrect, make sure all calls to raise or bind occur within the lifecycle of effect { }, either { } or similar builders.
 
  See: Effect documentation for additional information.
  """.trimIndent()
)

internal const val RaiseCancellationExceptionCaptured: String =
  "kotlin.coroutines.cancellation.CancellationException should never get cancelled. Always re-throw it if captured." +
    "This swallows the exception of Arrow's Raise, and leads to unexpected behavior." +
    "When working with Arrow prefer Either.catch or arrow.core.raise.catch to automatically rethrow CancellationException."

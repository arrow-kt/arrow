@file:JvmMultifileClass
@file:JvmName("RaiseKt")
@file:OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)

package arrow.core.raise

import arrow.atomic.AtomicBoolean
import arrow.core.Either
import arrow.core.nonFatalOrThrow
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
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
  return fold({ invoke() }, { catch(it) }, { recover(it) }, { transform(it) })
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
  return fold({ invoke(this) }, catch, recover, transform)
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
 * The most general way to execute a computation using [Raise].
 * Depending on the outcome of the block, one of the two lambdas is run:
 * - _success_ [transform] result of [A] to a value of [B].
 * - _raised_ [recover] from `raised` value of [Error] to a value of [B].
 *
 * This function re-throws any exceptions thrown within the [Raise] block.
 */
@JvmName("_foldOrThrow")
public inline fun <Error, A, B> fold(
  @BuilderInference block: Raise<Error>.() -> A,
  recover: (error: Error) -> B,
  transform: (value: A) -> B,
): B {
  contract {
    callsInPlace(block, AT_MOST_ONCE)
    callsInPlace(recover, AT_MOST_ONCE)
    callsInPlace(transform, AT_MOST_ONCE)
  }
  return fold(block, { throw it }, recover, transform)
}

/**
 * The most general way to execute a computation using [Raise].
 * Depending on the outcome of the block, one of the three lambdas is run:
 * - _success_ [transform] result of [A] to a value of [B].
 * - _raised_ [recover] from `raised` value of [Error] to a value of [B].
 * - _exception_ [catch] from [Throwable] by transforming value into [B].
 *
 * This method should never be wrapped in `try`/`catch` as it will not throw any unexpected errors,
 * it will only result in [CancellationException], or fatal exceptions such as `OutOfMemoryError`.
 */
@OptIn(DelicateRaiseApi::class)
@JvmName("_fold")
public inline fun <Error, A, B> fold(
  @BuilderInference block: Raise<Error>.() -> A,
  catch: (throwable: Throwable) -> B,
  recover: (error: Error) -> B,
  transform: (value: A) -> B,
): B {
  contract {
    callsInPlace(block, AT_MOST_ONCE)
    callsInPlace(catch, AT_MOST_ONCE)
    callsInPlace(recover, AT_MOST_ONCE)
    callsInPlace(transform, AT_MOST_ONCE)
  }
  val raise = DefaultRaise(false)
  return try {
    val res = block(raise)
    raise.complete()
    transform(res)
  } catch (e: RaiseCancellationException) {
    raise.complete()
    recover(e.raisedOrRethrow(raise))
  } catch (e: Throwable) {
    raise.complete()
    catch(e.nonFatalOrThrow())
  }
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
 * arrow.core.raise.RaiseCancellationException: Raised Continuation
 *   at arrow.core.raise.DefaultRaise.raise(Fold.kt:77)
 *   at MainKtKt$main$error$1.invoke(MainKt.kt:6)
 *   at MainKtKt$main$error$1.invoke(MainKt.kt:6)
 *   at arrow.core.raise.Raise$DefaultImpls.bind(Raise.kt:22)
 *   at arrow.core.raise.DefaultRaise.bind(Fold.kt:74)
 *   at arrow.core.raise.Effect__TracingKt$traced$2.invoke(Traced.kt:46)
 *   at arrow.core.raise.Effect__TracingKt$traced$2.invoke(Traced.kt:46)
 *   at arrow.core.raise.Effect__FoldKt.fold(Fold.kt:92)
 *   at arrow.core.raise.Effect.fold(Unknown Source)
 *   at MainKtKt.main(MainKt.kt:8)
 *   at MainKtKt.main(MainKt.kt)
 * ```
 *
 * NOTE:
 * This implies a performance penalty of creating a stacktrace when calling [Raise.raise],
 * but **this only occurs** when composing `traced`.
 * The stacktrace creation is disabled if no `traced` calls are made within the function composition.
 */
@OptIn(DelicateRaiseApi::class)
@ExperimentalTraceApi
public inline fun <Error, A> Raise<Error>.traced(
  @BuilderInference block: Raise<Error>.() -> A,
  trace: (trace: Trace, error: Error) -> Unit
): A {
  contract {
    callsInPlace(trace, AT_MOST_ONCE)
    callsInPlace(block, EXACTLY_ONCE)
  }
  return withErrorTraced({ t, error -> error.also { trace(t, error) } }, block)
}

@OptIn(DelicateRaiseApi::class)
@ExperimentalTraceApi
public inline fun <Error, OtherError, A> Raise<Error>.withErrorTraced(
  transform: (Trace, OtherError) -> Error,
  block: Raise<OtherError>.() -> A
): A {
  contract {
    callsInPlace(transform, AT_MOST_ONCE)
    callsInPlace(block, EXACTLY_ONCE)
  }
  val nested = DefaultRaise(true)
  return try {
    block(nested).also { nested.complete() }
  } catch (e: Traced) {
    nested.complete()
    val error = transform(Trace(e), e.raisedOrRethrow(nested))
    // If our outer Raise happens to be traced
    // Then we want the stack trace to match the inner one
    try {
      raise(error)
    } catch (rethrown: Traced) {
      throw rethrown.withCause(e)
    }
  }
}

@PublishedApi
@DelicateRaiseApi
internal fun Traced.withCause(cause: Traced): Traced =
  Traced(raised, raise, cause)

/** Returns the raised value, rethrows the CancellationException if not our scope */
@PublishedApi
@DelicateRaiseApi
@Suppress("UNCHECKED_CAST")
internal fun <R> CancellationException.raisedOrRethrow(raise: DefaultRaise): R =
  when {
    this is RaiseCancellationException && this.raise === raise -> raised as R
    else -> throw this
  }

/** Serves as both purposes of a scope-reference token, and a default implementation for Raise. */
@PublishedApi
internal class DefaultRaise(@PublishedApi internal val isTraced: Boolean) : Raise<Any?> {
  private val isActive = AtomicBoolean(true)

  @PublishedApi
  internal fun complete(): Boolean = isActive.getAndSet(false)

  @OptIn(DelicateRaiseApi::class)
  override fun raise(r: Any?): Nothing = when {
    isActive.value -> throw if (isTraced) Traced(r, this) else NoTrace(r, this)
    else -> throw RaiseLeakedException()
  }
}

@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
@RequiresOptIn(RaiseCancellationExceptionCaptured, RequiresOptIn.Level.WARNING)
public annotation class DelicateRaiseApi

/**
 * [RaiseCancellationException] is a _delicate_ api, and should be used with care.
 * It drives the short-circuiting behavior of [Raise].
 */
@DelicateRaiseApi
public sealed class RaiseCancellationException(
  internal val raised: Any?,
  internal val raise: Raise<Any?>
) : CancellationException(RaiseCancellationExceptionCaptured)

@DelicateRaiseApi
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal expect class NoTrace(raised: Any?, raise: Raise<Any?>) : RaiseCancellationException

@DelicateRaiseApi
internal class Traced(raised: Any?, raise: Raise<Any?>, override val cause: Traced? = null) : RaiseCancellationException(raised, raise)

private class RaiseLeakedException : IllegalStateException(
  """
  'raise' or 'bind' was leaked outside of its context scope.
  Make sure all calls to 'raise' and 'bind' occur within the lifecycle of nullable { }, either { } or similar builders.
 
  See Arrow documentation on 'Typed errors' for further information.
  """.trimIndent()
)

internal const val RaiseCancellationExceptionCaptured: String =
  "kotlin.coroutines.cancellation.CancellationException should never get swallowed. Always re-throw it if captured." +
    "This swallows the exception of Arrow's Raise, and leads to unexpected behavior." +
    "When working with Arrow prefer Either.catch or arrow.core.raise.catch to automatically rethrow CancellationException."

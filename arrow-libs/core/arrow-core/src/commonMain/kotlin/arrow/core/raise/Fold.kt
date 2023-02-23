@file:JvmMultifileClass
@file:JvmName("RaiseKt")
@file:OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)

package arrow.core.raise

import arrow.atomic.AtomicBoolean
import arrow.core.nonFatalOrThrow
import arrow.core.Either
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
 *  - _raised_ [recover] from `raised` value of [R] to a value of [B].
 *  - _exception_ [error] from [Throwable] by transforming value into [B].
 *
 * This method should never be wrapped in `try`/`catch` as it will not throw any unexpected errors,
 * it will only result in [CancellationException], or fatal exceptions such as `OutOfMemoryError`.
 */
public suspend fun <R, A, B> Effect<R, A>.fold(
  error: suspend (error: Throwable) -> B,
  recover: suspend (raised: R) -> B,
  transform: suspend (value: A) -> B,
): B {
  contract {
    callsInPlace(error, AT_MOST_ONCE)
    callsInPlace(recover, AT_MOST_ONCE)
    callsInPlace(transform, AT_MOST_ONCE)
  }
  return fold({ invoke() }, { error(it) }, { recover(it) }, { transform(it) })
}

public suspend fun <R, A, B> Effect<R, A>.fold(
  recover: suspend (raised: R) -> B,
  transform: suspend (value: A) -> B,
): B {
  contract {
    callsInPlace(recover, AT_MOST_ONCE)
    callsInPlace(transform, AT_MOST_ONCE)
  }
  return fold({ throw it }, recover, transform)
}

public inline fun <R, A, B> EagerEffect<R, A>.fold(
  error: (error: Throwable) -> B,
  recover: (raised: R) -> B,
  transform: (value: A) -> B,
): B {
  contract {
    callsInPlace(error, AT_MOST_ONCE)
    callsInPlace(recover, AT_MOST_ONCE)
    callsInPlace(transform, AT_MOST_ONCE)
  }
  return fold({ invoke(this) }, error, recover, transform)
}

public inline fun <R, A, B> EagerEffect<R, A>.fold(recover: (R) -> B, transform: (A) -> B): B {
  contract {
    callsInPlace(recover, AT_MOST_ONCE)
    callsInPlace(transform, AT_MOST_ONCE)
  }
  return fold({ throw it }, recover, transform)
}

@JvmName("_foldOrThrow")
public inline fun <R, A, B> fold(
  @BuilderInference program: Raise<R>.() -> A,
  recover: (raised: R) -> B,
  transform: (value: A) -> B,
): B {
  contract {
    callsInPlace(program, EXACTLY_ONCE)
    callsInPlace(recover, AT_MOST_ONCE)
    callsInPlace(transform, AT_MOST_ONCE)
  }
  return fold(program, { throw it }, recover, transform)
}

@JvmName("_fold")
public inline fun <R, A, B> fold(
  @BuilderInference program: Raise<R>.() -> A,
  error: (error: Throwable) -> B,
  recover: (raised: R) -> B,
  transform: (value: A) -> B,
): B {
  contract {
    callsInPlace(program, EXACTLY_ONCE)
    callsInPlace(error, AT_MOST_ONCE)
    callsInPlace(recover, AT_MOST_ONCE)
    callsInPlace(transform, AT_MOST_ONCE)
  }
  val raise = DefaultRaise(false)
  return try {
    val res = program(raise)
    raise.complete()
    transform(res)
  } catch (e: CancellationException) {
    raise.complete()
    recover(e.raisedOrRethrow(raise))
  } catch (e: Throwable) {
    raise.complete()
    error(e.nonFatalOrThrow())
  }
}

public fun <R, A> Effect<R, A>.traced(recover: Raise<R>.(traces: Traced<R>) -> Unit): Effect<R, A> =
  effect { traced({ bind() }, recover) }

public fun <R, A> EagerEffect<R, A>.traced(recover: Raise<R>.(traces: Traced<R>) -> Unit): EagerEffect<R, A> =
  eagerEffect { traced({ bind() }, recover) }

/**
 * Inspect a [Traced] value of [R].
 *
 * Tracing [R] can be useful to know where certain errors, or failures are coming from.
 * Let's say you have a `DomainError`, but it might be raised from many places in the project.
 *
 * You would have to manually _trace_ where this error is coming from,
 * instead [Traced] offers you ways to inspect the actual stacktrace of where the raised value occurred.
 *
 * Beware that tracing can only track the [Raise.bind] or [Raise.raise] call that resulted in the [R] value,
 * and not any location of where the [R], or [Either.Left] value was created.
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
public inline fun <R, A> Raise<R>.traced(
  @BuilderInference program: Raise<R>.() -> A,
  recover: Raise<R>.(traces: Traced<R>) -> Unit,
): A {
  val nested = DefaultRaise(true)
  return try {
    program.invoke(nested)
  } catch (e: RaiseCancellationExceptionNoTrace) {
    val r: R = e.raisedOrRethrow(nested)
    recover(Traced(e, r))
    raise(r)
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
internal class DefaultRaise(private val isTraced: Boolean) : Raise<Any?> {
  private val isActive = AtomicBoolean(true)

  @PublishedApi
  internal fun complete(): Boolean = isActive.getAndSet(false)
  override fun raise(r: Any?): Nothing = when {
    isActive.value && !isTraced -> throw RaiseCancellationExceptionNoTrace(r, this)
    isActive.value && !isTraced -> throw RaiseCancellationException(r, this)
    else -> throw RaiseLeakedException()
  }
}

/** CancellationException is required to cancel coroutines when raising from within them. */
private class RaiseCancellationExceptionNoTrace(val raised: Any?, val raise: Raise<Any?>) :
  CancellationExceptionNoTrace()

private class RaiseCancellationException(val raised: Any?, val raise: Raise<Any?>) : CancellationExceptionNoTrace()

public expect open class CancellationExceptionNoTrace() : CancellationException

private class RaiseLeakedException : IllegalStateException(
  """
  raise or bind was called outside of its DSL scope, and the DSL Scoped operator was leaked
  This is kind of usage is incorrect, make sure all calls to raise or bind occur within the lifecycle of effect { }, either { } or similar builders.
 
  See: Effect documentation for additional information.
  """.trimIndent()
)

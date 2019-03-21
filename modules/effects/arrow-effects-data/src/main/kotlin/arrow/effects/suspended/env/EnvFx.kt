package arrow.effects.suspended.env

import arrow.core.*
import arrow.effects.KindConnection
import arrow.effects.internal.Platform
import arrow.effects.internal.UnsafePromise
import arrow.effects.internal.asyncContinuation
import arrow.effects.suspended.error.mapLeft
import arrow.effects.suspended.fx.*
import arrow.effects.typeclasses.*
import arrow.effects.typeclasses.suspended.concurrent.Fx
import arrow.extension
import arrow.typeclasses.*
import java.util.concurrent.CancellationException
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.*

class ForEnvFx private constructor() {
  companion object
}
typealias EnvFxOf<R, E, A> = arrow.Kind3<ForEnvFx, R, E, A>
typealias EnvFxPartialOf<R, E> = arrow.Kind2<ForEnvFx, R, E>
typealias EnvFxProcF<R, E, A> = ConnectedProcF<EnvFxPartialOf<R, E>, A>
typealias EnvFxConnectedProc<R, E, A> = (KindConnection<EnvFxPartialOf<R, E>>, ((Either<Throwable, Either<E, A>>) -> Unit)) -> Unit

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <R, E, A> EnvFxOf<R, E, A>.fix(): EnvFx<R, E, A> =
  this as EnvFx<R, E, A>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
fun <R, E, A> effect(f: suspend R.() -> Either<E, A>): EnvFx<R, E, A> =
  EnvFx(f)

fun <R, E, A> env(f: R.() -> EnvFx<R, E, A>): EnvFx<R, E, A> =
  EnvFx { f(this).fa(this) }

class EnvFx<R, out E, out A>(val fa: suspend R.() -> Either<E, A>) : EnvFxOf<R, E, A> {
  companion object {
    fun <R, E> unit(): EnvFx<R, E, Unit> =
      EnvFx { Unit.right() }

    fun <R, E, A> just(a: A): EnvFx<R, E, A> =
      EnvFx { a.right() }
  }
}

fun <R, E, A> EnvFxOf<R, E, A>.toFx(r: R): arrow.effects.suspended.fx.Fx<Either<E, A>> =
  arrow.effects.suspended.fx.Fx { fix().fa.invoke(r) }

suspend operator fun <R, E, A> EnvFxOf<R, E, A>.invoke(r: R): Either<E, A> =
  fix().fa.invoke(r)

fun <R, E, A> A.just(): EnvFx<R, E, A> =
  EnvFx { this@just.right() }

suspend fun <R, E, A, B> (suspend R.() -> Either<E, A>).map(f: (A) -> B): suspend R.() -> Either<E, B> =
  { this@map(this).map(f) }

suspend fun <R, E, A, B> (suspend R.() -> Either<E, A>).mapLeft(f: (E) -> B): suspend R.() -> Either<B, A> =
  { mapLeft(f)(this) }

suspend fun <R, E, A> (suspend R.() -> Either<E, A>).attempt(): suspend R.() -> Either<E, A> = {
  try {
    this@attempt(this)
  } catch (e: Throwable) {
    throw RaisedError(e.nonFatalOrThrow())
  }
}

@Suppress("UNCHECKED_CAST")
suspend fun <R, E, A, B> (suspend R.() -> Either<E, A>).flatMap(f: (A) -> suspend R.() -> Either<E, B>): suspend R.() -> Either<E, B> = {
  when (val x = this@flatMap.attempt()(this)) {
    is Either.Left -> x.a.left()
    is Either.Right -> f(x.b).attempt()(this)
  }
}

suspend fun <R, E, A, B> (suspend R.() -> Either<E, A>).ap(ff: suspend () -> (A) -> B): suspend R.() -> Either<E, B> =
  map(ff())

suspend fun <R, E, A> attempt(
  fa: suspend () -> A,
  onError: (Throwable) -> E,
  unit: Unit = Unit
): suspend R.() -> Either<E, A> =
  { arrow.effects.suspended.fx.attempt(fa).mapLeft(onError)() }

fun <R, E> E.raiseError(unit: Unit = Unit): suspend R.() -> Either<E, Nothing> =
  { this@raiseError.left() }

suspend fun <R, E, A> (suspend R.() -> Either<E, A>).handleErrorWith(f: (E) -> suspend R.() -> Either<E, A>): suspend R.() -> Either<E, A> = {
  when (val result = this@handleErrorWith.attempt()(this)) {
    is Either.Left -> f(result.a).attempt()(this)
    is Either.Right -> this@handleErrorWith.attempt()(this)
  }
}

suspend fun <R, E, A> (suspend R.() -> Either<E, A>).handleErrorWith(unit: Unit = Unit, f: (Throwable) -> suspend R.() -> Either<E, A>): suspend R.() -> Either<E, A> = {
  try {
    this@handleErrorWith(this)
  } catch (t: Throwable) {
    f(t.nonFatalOrThrow())(this)
  }
}

@Suppress("UNCHECKED_CAST")
suspend fun <R, E, A> (suspend R.() -> Either<E, A>).handleError(f: (E) -> A): suspend R.() -> Either<E, A> = {
  when (val result = this@handleError.attempt()(this)) {
    is Either.Left -> {
      f(result.a).right()
    }
    is Either.Right -> this@handleError(this)
  }
}

suspend fun <R, E, A> (suspend R.() -> Either<E, A>).ensure(
  error: () -> E,
  predicate: (A) -> Boolean
): suspend R.() -> Either<E, A> = {
  when (val result = this@ensure.attempt()(this)) {
    is Either.Left -> this@ensure(this)
    is Either.Right -> if (predicate(result.b)) this@ensure(this)
    else {
      error().left()
    }
  }
}

suspend fun <R, E, A, B> (suspend R.() -> Either<E, A>).bracketCase(
  release: (A, ExitCase<Throwable>) -> suspend R.() -> Either<E, Unit>,
  use: (A) -> suspend R.() -> Either<E, B>
): suspend R.() -> Either<E, B> = {
  when (val result = this@bracketCase(this)) {
    is Either.Left -> result.a.left()
    is Either.Right -> {
      val a = result.b
      val fxB = try {
        use(a)
      } catch (e: Throwable) {
        suspend { release(a, ExitCase.Error(e))(this) }.foldContinuation { e2 ->
          throw Platform.composeErrors(e, e2)
        }
        throw e
      }

      val b = suspend { fxB(this) }.foldContinuation { e ->
        when (e) {
          is CancellationException -> suspend { release(a, ExitCase.Canceled)(this) }.foldContinuation { e2 ->
            throw Platform.composeErrors(e, e2)
          }
          else -> suspend { release(a, ExitCase.Error(e))(this) }.foldContinuation { e2 ->
            throw Platform.composeErrors(e, e2)
          }
        }
        throw e
      }
      release(a, ExitCase.Completed)(this)
      b
    }
  }

}



package arrow.effects.suspended.error

import arrow.core.Either
import arrow.core.left
import arrow.core.nonFatalOrThrow
import arrow.core.right
import arrow.effects.KindConnection
import arrow.effects.internal.Platform
import arrow.effects.suspended.fx.RaisedError
import arrow.effects.suspended.fx.foldContinuation
import arrow.effects.typeclasses.ConnectedProcF
import arrow.effects.typeclasses.ExitCase
import java.util.concurrent.CancellationException

class ForCatchFx private constructor() {
  companion object
}
typealias CatchFxOf<E, A> = arrow.Kind2<ForCatchFx, E, A>
typealias CatchFxPartialOf<E> = arrow.Kind<ForCatchFx, E>
typealias CatchFxProcF<E, A> = ConnectedProcF<CatchFxPartialOf<E>, A>
typealias CatchFxConnectedProc<E, A> = (KindConnection<CatchFxPartialOf<E>>, ((Either<Throwable, Either<E, A>>) -> Unit)) -> Unit

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <E, A> CatchFxOf<E, A>.fix(): CatchFx<E, A> =
  this as CatchFx<E, A>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <E, A> (suspend () -> Either<E, A>).k(): CatchFx<E, A> =
  CatchFx(this)

class CatchFx<out E, out A>(val fa: suspend () -> Either<E, A>) : CatchFxOf<E, A> {
  companion object {
    val unit: CatchFx<Nothing, Unit> = CatchFx { Unit.right() }

    fun <A> just(a: A): CatchFx<Nothing, A> =
      CatchFx { a.right() }
  }
}

fun <E, A> CatchFxOf<E, A>.toFx(): arrow.effects.suspended.fx.Fx<Either<E, A>> =
  arrow.effects.suspended.fx.Fx(fix().fa)

suspend operator fun <E, A> CatchFxOf<E, A>.invoke(): Either<E, A> =
  fix().fa.invoke()

fun <A> A.just(): CatchFx<Nothing, A> =
  CatchFx { right() }

suspend fun <E, A, B> (suspend () -> Either<E, A>).map(f: (A) -> B): suspend () -> Either<E, B> =
  { this().map(f) }

suspend fun <E, A, B> (suspend () -> Either<E, A>).mapLeft(f: (E) -> B): suspend () -> Either<B, A> =
  { mapLeft(f)() }

suspend fun <E, A> (suspend () -> Either<E, A>).attempt(): Either<E, A> =
  try {
    this()
  } catch (e: Throwable) {
    throw RaisedError(e.nonFatalOrThrow())
  }

@Suppress("UNCHECKED_CAST")
suspend fun <E, A, B> (suspend () -> Either<E, A>).flatMap(f: (A) -> suspend () -> Either<E, B>): suspend () -> Either<E, B> = {
  when (val x = this.attempt()) {
    is Either.Left -> x.a.left()
    is Either.Right -> f(x.b).attempt()
  }
}

suspend fun <E, A, B> (suspend () -> Either<E, A>).ap(ff: suspend () -> (A) -> B): suspend () -> Either<E, B> =
  map(ff())

suspend fun <E, A> attempt(
  fa: suspend () -> A,
  onError: (Throwable) -> E
): suspend () -> Either<E, A> =
  { arrow.effects.suspended.fx.attempt(fa).mapLeft(onError)() }

fun <E> E.raiseError(): suspend () -> Either<E, Nothing> =
  { left() }

suspend fun <E, A> (suspend () -> Either<E, A>).handleErrorWith(f: (E) -> suspend () -> Either<E, A>): suspend () -> Either<E, A> = {
  when (val result = this.attempt()) {
    is Either.Left -> f(result.a).attempt()
    is Either.Right -> this@handleErrorWith.attempt()
  }
}

suspend fun <E, A> (suspend () -> Either<E, A>).handleErrorWith(unit: Unit = Unit, f: (Throwable) -> suspend () -> Either<E, A>): suspend () -> Either<E, A> = {
  try {
    this()
  } catch (t: Throwable) {
    f(t.nonFatalOrThrow())()
  }
}

@Suppress("UNCHECKED_CAST")
suspend fun <E, A> (suspend () -> Either<E, A>).handleError(f: (E) -> A): suspend () -> Either<E, A> = {
  when (val result = this.attempt()) {
    is Either.Left -> {
      f(result.a).right()
    }
    is Either.Right -> this@handleError()
  }
}

suspend fun <E, A> (suspend () -> Either<E, A>).ensure(
  error: () -> E,
  predicate: (A) -> Boolean
): suspend () -> Either<E, A> = {
  when (val result = this.attempt()) {
    is Either.Left -> this@ensure()
    is Either.Right -> if (predicate(result.b)) this@ensure()
    else {
      error().left()
    }
  }
}

suspend fun <E, A, B> (suspend () -> Either<E, A>).bracketCase(
  release: (A, ExitCase<Throwable>) -> suspend () -> Either<E, Unit>,
  use: (A) -> suspend () -> Either<E, B>
): suspend () -> Either<E, B> = {
  when (val result = this()) {
    is Either.Left -> result.a.left()
    is Either.Right -> {
      val a = result.b
      val fxB = try {
        use(a)
      } catch (e: Throwable) {
        release(a, ExitCase.Error(e)).foldContinuation { e2 ->
          throw Platform.composeErrors(e, e2)
        }
        throw e
      }

      val b = fxB.foldContinuation { e ->
        when (e) {
          is CancellationException -> release(a, ExitCase.Canceled).foldContinuation { e2 ->
            throw Platform.composeErrors(e, e2)
          }
          else -> release(a, ExitCase.Error(e)).foldContinuation { e2 ->
            throw Platform.composeErrors(e, e2)
          }
        }
        throw e
      }
      release(a, ExitCase.Completed)()
      b
    }
  }

}

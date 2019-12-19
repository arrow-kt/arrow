package arrow.fx

import arrow.core.Either
import arrow.fx.IO.Pure

/**
 * An [IOFrame] knows how to [recover] from a [Throwable] and how to map a value [A] to [B].
 *
 * Internal to `IO`'s implementations, used to specify
 * error handlers in their respective `Bind` internal states.
 *
 * To use an [IOFrame] you must use [IO.Bind] instead of `flatMap` or the [IOFrame]
 * is **not guaranteed** to execute.
 *
 * It's used to implement [attempt], [handleErrorWith] and [arrow.fx.internal.IOBracket]
 */
internal interface IOFrame<in E, in A, out B> : (A) -> B {
  override operator fun invoke(a: A): B
  fun recover(e: Throwable): B
  fun handleError(e: E): B

  companion object {

    internal class Redeem<E, A, B>(val ft: (Throwable) -> B, val fe: (E) -> B, val fb: (A) -> B) : IOFrame<E, A, IO<Nothing, B>> {
      override fun invoke(a: A): IO<Nothing, B> = Pure(fb(a))
      override fun recover(e: Throwable): IO<Nothing, B> = Pure(ft(e))
      override fun handleError(e: E): IO<Nothing, B> = Pure(fe(e))
    }

    internal class RedeemWith<E, A, E2, B>(val ft: (Throwable) -> IOOf<E2, B>, val fe: (E) -> IOOf<E2, B>, val fb: (A) -> IOOf<E2, B>) : IOFrame<E, A, IO<E2, B>> {
      override fun invoke(a: A): IO<E2, B> = fb(a).fix()
      override fun recover(e: Throwable): IO<E2, B> = ft(e).fix()
      override fun handleError(e: E): IO<E2, B> = fe(e).fix()
    }

    internal class ErrorHandler<E, A, E2>(val ft: (Throwable) -> IOOf<E2, A>, val fe: (E) -> IOOf<E2, A>) : IOFrame<E, A, IO<E2, A>> {
      override fun invoke(a: A): IO<E2, A> = Pure(a)
      override fun recover(e: Throwable): IO<E2, A> = ft(e).fix()
      override fun handleError(e: E): IO<E2, A> = fe(e).fix()
    }

    internal class MapError<E, A, E2>(val fe: (E) -> IOOf<E2, A>) : IOFrame<E, A, IO<E2, A>> {
      override fun invoke(a: A): IO<E2, A> = Pure(a)
      override fun recover(e: Throwable): IO<E2, A> = IO.RaiseException(e)
      override fun handleError(e: E): IO<E2, A> = fe(e).fix()
    }

    @Suppress("UNCHECKED_CAST")
    fun <E, A> attempt(): (A) -> IO<E, Either<Throwable, A>> = AttemptIO as (A) -> IO<E, Either<Throwable, A>>

    private object AttemptIO : IOFrame<Any?, Any?, IO<Any?, Either<Throwable, Any?>>> {
      override operator fun invoke(a: Any?): IO<Any?, Either<Throwable, Any?>> = Pure(Either.Right(a))
      override fun recover(e: Throwable): IO<Any?, Either<Throwable, Any?>> = Pure(Either.Left(e))
      override fun handleError(e: Any?): IO<Any?, Either<Throwable, Any?>> = IO.RaiseError(e)
    }
  }
}

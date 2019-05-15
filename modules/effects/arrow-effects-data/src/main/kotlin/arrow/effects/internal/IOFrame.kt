package arrow.effects.internal

import arrow.core.Either
import arrow.effects.IO
import arrow.effects.IOOf
import arrow.effects.fix

/**
 * An [IOFrame] knows how to [recover] from a [Throwable] and how to map a value [A] to [B].
 *
 * Internal to `IO`'s implementations, used to specify
 * error handlers in their respective `FlatMap` internal states.
 *
 * To use an [IOFrame] you must use [IO.FlatMap] instead of [IO.flatMap] or the [IO.FlatMap]
 * is **not guaranteed** to execute.
 *
 * It's used to implement [IO.attempt], [IO.handleErrorWith], [IO.redeemWith] and [IO.bracketCase]
 */
@PublishedApi
internal interface IOFrame<in A, out B> : (A) -> B {
  override operator fun invoke(a: A): B
  fun recover(e: Throwable): B

  fun fold(value: Either<Throwable, A>): B = when (value) {
    is Either.Right -> invoke(value.b)
    is Either.Left -> recover(value.a)
  }

  companion object {

    internal class ErrorHandler<A>(val fe: (Throwable) -> IOOf<A>) : IOFrame<A, IO<A>> {
      override fun invoke(a: A): IO<A> = IO.Pure(a)
      override fun recover(e: Throwable): IO<A> = fe(e).fix()
    }

    internal class Redeem<A, B>(val fe: (Throwable) -> B, val fs: (A) -> B) : IOFrame<A, IOOf<B>> {
      override fun invoke(a: A): IOOf<B> = IO.Pure(fs(a))
      override fun recover(e: Throwable): IO<B> = IO.Pure(fe(e))
    }

    internal class RedeemWith<A, B>(val fe: (Throwable) -> IOOf<B>, val fs: (A) -> IOOf<B>) : IOFrame<A, IOOf<B>> {
      override fun invoke(a: A): IOOf<B> = fs(a)
      override fun recover(e: Throwable): IOOf<B> = fe(e)
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <A> attempt(): (A) -> IO<Either<Throwable, A>> = AttemptIO as (A) -> IO<Either<Throwable, A>>

    @PublishedApi
    internal object AttemptIO : IOFrame<Any?, IO<Either<Throwable, Any?>>> {
      override fun invoke(a: Any?): IO<Either<Throwable, Any?>> = IO.Pure(Either.Right(a))
      override fun recover(e: Throwable): IO<Either<Throwable, Any?>> = IO.Pure(Either.Left(e))
    }
  }
}

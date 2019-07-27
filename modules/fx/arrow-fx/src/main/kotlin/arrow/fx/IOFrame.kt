package arrow.fx

import arrow.core.Either
import arrow.fx.IO.Pure

/**
 * An [IOFrame] knows how to [recover] from a [Throwable] and how to map a value [A] to [R].
 *
 * Internal to `IO`'s implementations, used to specify
 * error handlers in their respective `Bind` internal states.
 *
 * To use an [IOFrame] you must use [IO.Bind] instead of `flatMap` or the [IOFrame]
 * is **not guaranteed** to execute.
 *
 * It's used to implement [attempt], [handleErrorWith] and [arrow.fx.internal.IOBracket]
 */
internal interface IOFrame<in A, out R> : (A) -> R {
  override operator fun invoke(a: A): R

  fun recover(e: Throwable): R

  fun fold(value: Either<Throwable, A>): R =
    when (value) {
      is Either.Right -> invoke(value.b)
      is Either.Left -> recover(value.a)
    }

  companion object {

    internal class Redeem<A, B>(val fe: (Throwable) -> B, val fb: (A) -> B) : IOFrame<A, IO<Throwable, B>> {
      override fun invoke(a: A): IO<Throwable, B> = Pure(fb(a))
      override fun recover(e: Throwable): IO<Throwable, B> = Pure(fe(e))
    }

    internal class RedeemWith<A, B>(val fe: (Throwable) -> IOOf<Throwable, B>, val fb: (A) -> IOOf<Throwable, B>) : IOFrame<A, IO<Throwable, B>> {
      override fun invoke(a: A): IO<Throwable, B> = fb(a).fix()
      override fun recover(e: Throwable): IO<Throwable, B> = fe(e).fix()
    }

    internal class ErrorHandler<A>(val fe: (Throwable) -> IOOf<Throwable, A>) : IOFrame<A, IO<Throwable, A>> {
      override fun invoke(a: A): IO<Throwable, A> = Pure(a)
      override fun recover(e: Throwable): IO<Throwable, A> = fe(e).fix()
    }

    @Suppress("UNCHECKED_CAST")
    fun <A> attempt(): (A) -> IO<Throwable, Either<Throwable, A>> = AttemptIO as (A) -> IO<Throwable, Either<Throwable, A>>

    private object AttemptIO : IOFrame<Any?, IO<Throwable, Either<Throwable, Any?>>> {
      override operator fun invoke(a: Any?): IO<Throwable, Either<Nothing, Any?>> = Pure(Either.Right(a))
      override fun recover(e: Throwable): IO<Throwable, Either<Throwable, Nothing>> = Pure(Either.Left(e))
    }
  }
}

package arrow.fx

import arrow.core.Either


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

    internal class Redeem<A, B>(val fe: (Throwable) -> B, val fb: (A) -> B) : IOFrame<A, IO<Nothing, B>> {
      override fun invoke(a: A): IO<Nothing, B> = IO.Pure(fb(a))
      override fun recover(e: Throwable): IO<Nothing, B> = IO.Pure(fe(e))
    }

    internal class RedeemWith<A, B>(val fe: (Throwable) -> IOOf<Nothing, B>, val fb: (A) -> IOOf<Nothing, B>) : IOFrame<A, IO<Nothing, B>> {
      override fun invoke(a: A): IO<Nothing, B> = fb(a).fix()
      override fun recover(e: Throwable): IO<Nothing, B> = fe(e).fix()
    }

    internal class ErrorHandler<A>(val fe: (Throwable) -> IOOf<Nothing, A>) : IOFrame<A, IO<Nothing, A>> {
      override fun invoke(a: A): IO<Nothing, A> = IO.Pure(a)
      override fun recover(e: Throwable): IO<Nothing, A> = fe(e).fix()
    }

    @Suppress("UNCHECKED_CAST")
    fun <A> attempt(): (A) -> IO<Nothing, Either<Throwable, A>> = AttemptIO as (A) -> IO<Nothing, Either<Throwable, A>>

    private object AttemptIO : IOFrame<Any?, IO<Nothing, Either<Throwable, Any?>>> {
      override operator fun invoke(a: Any?): IO<Nothing, Either<Nothing, Any?>> = IO.Pure(Either.Right(a))
      override fun recover(e: Throwable): IO<Nothing, Either<Throwable, Nothing>> = IO.Pure(Either.Left(e))
    }
  }
}

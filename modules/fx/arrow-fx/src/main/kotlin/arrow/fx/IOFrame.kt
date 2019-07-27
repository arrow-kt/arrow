package arrow.fx

import arrow.core.Either
import arrow.fx.IO.Pure
import arrow.fx.IOFrame.Companion.attempt

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
internal interface IOFrame<E, in A, out R> : (A) -> R {
  override operator fun invoke(a: A): R

  fun recover(e: E): R

  fun fold(value: Either<E, A>): R =
    when (value) {
      is Either.Right -> invoke(value.b)
      is Either.Left -> recover(value.a)
    }

  companion object {
    internal class Redeem<E, A, B>(val fe: (E) -> B, val fb: (A) -> B) : IOFrame<E, A, IO<E, B>> {
      override fun invoke(a: A): IO<E, B> = Pure(fb(a))
      override fun recover(e: E): IO<E, B> = Pure(fe(e))
    }


    internal class RedeemWith<E, A, B>(val fe: (E) -> IOOf<E, B>, val fb: (A) -> IOOf<E, B>) : IOFrame<E, A, IO<E, B>> {
      override fun invoke(a: A): IO<E, B> = fb(a).fix()
      override fun recover(e: E): IO<E, B> = fe(e).fix()
    }

    internal class ErrorHandler<E, A>(val fe: (E) -> IOOf<E, A>) : IOFrame<E, A, IO<E, A>> {
      override fun invoke(a: A): IO<E, A> = Pure(a)
      override fun recover(e: E): IO<E, A> = fe(e).fix()
    }

    @Suppress("UNCHECKED_CAST")
    fun <E, A> attempt(): (A) -> IO<E, Either<E, A>> = AttemptIO as (A) -> IO<E, Either<E, A>>

    private object AttemptIO : IOFrame<Any?, Any?, IO<Any?, Either<Any?, Any?>>> {
      override operator fun invoke(a: Any?): IO<Any?, Either<Nothing, Any?>> = Pure(Either.Right(a))
      override fun recover(e: Any?): IO<Any?, Either<Any?, Nothing>> = Pure(Either.Left(e))
    }
  }
}

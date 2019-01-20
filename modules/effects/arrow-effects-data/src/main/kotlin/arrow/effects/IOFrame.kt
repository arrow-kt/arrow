package arrow.effects

import arrow.core.Either
import arrow.effects.BIO.Companion.just

/**
 * An [IOFrame] knows how to [recover] from a [Throwable] and how to map a value [A] to [R].
 *
 * Internal to `IO`'s implementations, used to specify
 * error handlers in their respective `Bind` internal states.
 *
 * To use an [IOFrame] you must use [IO.Bind] instead of `flatMap` or the [IOFrame]
 * is **not guaranteed** to execute.
 *
 * It's used to implement [attempt], [handleErrorWith] and [arrow.effects.internal.IOBracket]
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
    fun <E, A> errorHandler(fe: (E) -> BIOOf<E, A>): IOFrame<E, A, BIO<E, A>> =
      redeem(fe, ::just)

    @Suppress("UNCHECKED_CAST")
    fun <E, X, A, B> redeem(fe: (E) -> BIOOf<X, B>, fa: (A) -> BIOOf<X, B>): IOFrame<E, A, BIO<X, B>> = AttemptIO(fe, fa)

    class AttemptIO<E, X, A, B>(val fe: (E) -> BIOOf<X, B>, val fa: (A) -> BIOOf<X, B>) : IOFrame<E, A, BIO<X, B>> {
      override fun invoke(a: A): BIO<X, B> = fa(a).fix()

      override fun recover(e: E): BIO<X, B> = fe(e).fix()
    }
  }
}

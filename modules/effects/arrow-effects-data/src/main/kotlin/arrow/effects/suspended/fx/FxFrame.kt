package arrow.effects.suspended.fx

import arrow.core.Either
import arrow.effects.IO
import arrow.effects.IOFrame
import arrow.effects.handleErrorWith

/**
 * An [IOFrame] knows how to [recover] from a [Throwable] and how to map a value [A] to [B].
 *
 * Internal to `IO`'s implementations, used to specify
 * error handlers in their respective `Bind` internal states.
 *
 * To use an [IOFrame] you must use [IO.Bind] instead of `flatMap` or the [IOFrame]
 * is **not guaranteed** to execute.
 *
 * It's used to implement [attempt], [handleErrorWith] and [arrow.effects.internal.IOBracket]
 */
@PublishedApi
internal interface FxFrame<in A, out B> : (A) -> B {
  override operator fun invoke(a: A): B
  fun recover(e: Throwable): B

  fun fold(value: Either<Throwable, A>): B = when (value) {
    is Either.Right -> invoke(value.b)
    is Either.Left -> recover(value.a)
  }

  companion object {

    internal class ErrorHandler<A>(val fe: (Throwable) -> FxOf<A>) : FxFrame<A, Fx<A>> {
      override fun invoke(a: A): Fx<A> = Fx.Pure(a, 0)
      override fun recover(e: Throwable): Fx<A> = fe(e).fix()
    }

    internal class Redeem<A, B>(val fe: (Throwable) -> B, val fs: (A) -> B) : FxFrame<A, FxOf<B>> {
      override fun invoke(a: A): FxOf<B> = Fx.Pure(fs(a), 0)
      override fun recover(e: Throwable): Fx<B> = Fx.Pure(fe(e), 0)
    }

    internal class RedeemWith<A, B>(val fe: (Throwable) -> FxOf<B>, val fs: (A) -> FxOf<B>) : FxFrame<A, FxOf<B>> {
      override fun invoke(a: A): FxOf<B> = fs(a)
      override fun recover(e: Throwable): FxOf<B> = fe(e)
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <A> attempt(): (A) -> Fx<Either<Throwable, A>> = AttemptFx as (A) -> Fx<Either<Throwable, A>>

    @PublishedApi
    internal object AttemptFx : FxFrame<Any?, Fx<Either<Throwable, Any?>>> {
      override fun invoke(a: Any?): Fx<Either<Throwable, Any?>> = Fx.Pure(Either.Right(a), 0)
      override fun recover(e: Throwable): Fx<Either<Throwable, Any?>> = Fx.Pure(Either.Left(e), 0)
    }
  }
}

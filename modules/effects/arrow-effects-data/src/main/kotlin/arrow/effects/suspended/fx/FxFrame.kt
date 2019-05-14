package arrow.effects.suspended.fx

import arrow.core.Either
import arrow.effects.handleErrorWith

/**
 * An [FxFrame] knows how to [recover] from a [Throwable] and how to map a value [A] to [B].
 *
 * Internal to `Fx`'s implementations, used to specify
 * error handlers in their respective `FlatMap` internal states.
 *
 * To use an [FxFrame] you must use [Fx.FlatMap] instead of [Fx.flatMap] or the [Fx.FlatMap]
 * is **not guaranteed** to execute.
 *
 * It's used to implement [Fx.attempt], [Fx.handleErrorWith], [Fx.redeemWith] and [Fx.bracketCase]
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
      override fun invoke(a: A): Fx<A> = Fx.Pure(a)
      override fun recover(e: Throwable): Fx<A> = fe(e).fix()
    }

    internal class Redeem<A, B>(val fe: (Throwable) -> B, val fs: (A) -> B) : FxFrame<A, FxOf<B>> {
      override fun invoke(a: A): FxOf<B> = Fx.Pure(fs(a))
      override fun recover(e: Throwable): Fx<B> = Fx.Pure(fe(e))
    }

    internal class RedeemWith<A, B>(val fe: (Throwable) -> FxOf<B>, val fs: (A) -> FxOf<B>) : FxFrame<A, FxOf<B>> {
      override fun invoke(a: A): FxOf<B> = fs(a)
      override fun recover(e: Throwable): FxOf<B> = fe(e)
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <A> attempt(): (A) -> Fx<Either<Throwable, A>> = AttemptFx as (A) -> Fx<Either<Throwable, A>>

    @PublishedApi
    internal object AttemptFx : FxFrame<Any?, Fx<Either<Throwable, Any?>>> {
      override fun invoke(a: Any?): Fx<Either<Throwable, Any?>> = Fx.Pure(Either.Right(a))
      override fun recover(e: Throwable): Fx<Either<Throwable, Any?>> = Fx.Pure(Either.Left(e))
    }
  }
}

package arrow.effects.typeclasses

import arrow.Kind
import arrow.core.*
import kotlin.coroutines.CoroutineContext

interface Concurrent<F> : Async<F> {

  fun <A> Kind<F, A>.startF(ctx: CoroutineContext): Kind<F, Fiber<F, A>>

  fun <A, B> racePair(ctx: CoroutineContext, fa: Kind<F, A>, fb: Kind<F, B>): Kind<F, Either<Tuple2<A, Fiber<F, B>>, Tuple2<Fiber<F, A>, B>>>

  //TODO blocked by Async#asyncF (https://github.com/arrow-kt/arrow/issues/1124)
  //fun <A> cancelable(cb: ((Either<Throwable, A>) -> Unit) -> Kind<F, Unit>): Kind<F, A> =

  fun <A, B> raceN(ctx: CoroutineContext, fa: Kind<F, A>, fb: Kind<F, B>): Kind<F, Either<A, B>> =
    racePair(ctx, fa, fb).flatMap {
      it.fold({ (a, b) ->
        b.cancel().map { a.left() }
      }, { (a, b) ->
        a.cancel().map { b.right() }
      })
    }

  fun <A, B, C> raceN(ctx: CoroutineContext, fa: Kind<F, A>, fb: Kind<F, B>, fc: Kind<F, C>): Kind<F, Either<A, Either<B, C>>> =
    racePair(ctx, fa, racePair(ctx, fb, fc)).flatMap {
      it.fold({ (a, b) ->
        b.cancel().map { a.left() }
      }, { (a, b) ->
        a.cancel().flatMap {
          b.fold({ (b, c) ->
            c.cancel().map { b.left().right() }
          }, { (b, c) ->
            b.cancel().map { c.right().right() }
          })
        }
      })
    }

  fun <A, B, C, D> raceN(ctx: CoroutineContext, a: Kind<F, A>, b: Kind<F, B>, c: Kind<F, C>, d: Kind<F, D>): Kind<F, Either<Either<A, B>, Either<C, D>>> =
    raceN(ctx,
      raceN(ctx, a, b),
      raceN(ctx, c, d)
    )

  fun <A, B, C, D, E> raceN(ctx: CoroutineContext, a: Kind<F, A>, b: Kind<F, B>, c: Kind<F, C>, d: Kind<F, D>, e: Kind<F, E>): Kind<F, Either<Either<A, Either<B, C>>, Either<D, E>>> =
    raceN(ctx,
      raceN(ctx, a, b, c),
      raceN(ctx, d, e)
    )

  fun <A, B, C, D, E, G> raceN(ctx: CoroutineContext, a: Kind<F, A>, b: Kind<F, B>, c: Kind<F, C>, d: Kind<F, D>, e: Kind<F, E>, g: Kind<F, G>): Kind<F, Either<Either<A, B>, Either<Either<C, D>, Either<E, G>>>> =
    raceN(ctx,
      raceN(ctx, a, b),
      raceN(ctx, c, d),
      raceN(ctx, e, g)
    )

  fun <A, B, C, D, E, G, H> raceN(ctx: CoroutineContext, a: Kind<F, A>, b: Kind<F, B>, c: Kind<F, C>, d: Kind<F, D>, e: Kind<F, E>, g: Kind<F, G>, h: Kind<F, H>): Kind<F, Either<Either<A, Either<B, C>>, Either<Either<D, E>, Either<G, H>>>> =
    raceN(ctx,
      raceN(ctx, a, b, c),
      raceN(ctx, d, e),
      raceN(ctx, g, h)
    )

  fun <A, B, C, D, E, G, H, I> raceN(ctx: CoroutineContext, a: Kind<F, A>, b: Kind<F, B>, c: Kind<F, C>, d: Kind<F, D>, e: Kind<F, E>, g: Kind<F, G>, h: Kind<F, H>, i: Kind<F, I>): Kind<F, Either<Either<Either<A, B>, Either<C, D>>, Either<Either<E, G>, Either<H, I>>>> =
    raceN(ctx,
      raceN(ctx, a, b),
      raceN(ctx, c, d),
      raceN(ctx, e, g),
      raceN(ctx, h, i)
    )

  fun <A, B, C, D, E, G, H, I, J> raceN(ctx: CoroutineContext, a: Kind<F, A>, b: Kind<F, B>, c: Kind<F, C>, d: Kind<F, D>, e: Kind<F, E>, g: Kind<F, G>, h: Kind<F, H>, i: Kind<F, I>, j: Kind<F, J>): Kind<F, Either<Either<Either<A, Either<B, C>>, Either<D, E>>, Either<Either<G, H>, Either<I, J>>>> =
    raceN(ctx,
      raceN(ctx, a, b, c),
      raceN(ctx, d, e),
      raceN(ctx, g, h),
      raceN(ctx, i, j)
    )

}

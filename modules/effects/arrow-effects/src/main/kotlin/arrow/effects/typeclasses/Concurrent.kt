package arrow.effects.typeclasses

import arrow.Kind
import arrow.core.Either
import arrow.core.Tuple2
import arrow.core.left
import arrow.core.right
import kotlin.coroutines.CoroutineContext

interface Concurrent<F> : Async<F> {

  fun <A> Kind<F, A>.startF(ctx: CoroutineContext): Kind<F, Fiber<F, A>>

  fun <A, B> racePair(ctx: CoroutineContext, lh: Kind<F, A>, rh: Kind<F, B>): Kind<F, Either<Tuple2<A, Fiber<F, B>>, Tuple2<Fiber<F, A>, B>>>

  fun <A, B> race(ctx: CoroutineContext, lh: Kind<F, A>, rh: Kind<F, B>): Kind<F, Either<A, B>> =
    racePair(ctx, lh, rh).flatMap {
      it.fold({ (a, b) ->
        b.cancel.map { a.left() }
      }, { (a, b) ->
        a.cancel.map { b.right() }
      })
    }

  //TODO blocked by Async#asyncF (https://github.com/arrow-kt/arrow/issues/1124)
  //fun <A> cancelable(cb: ((Either<Throwable, A>) -> Unit) -> Kind<F, Unit>): Kind<F, A> =

//  fun <A, B, C> parallelMapN(ctx: CoroutineContext, fa: Kind<F, A>, fb: Kind<F, B>, f: (A, B) -> C): Kind<F, C> =
//    fa.startF(ctx).flatMap { (joinA, _) ->
//      fb.startF(ctx).flatMap { (joinB, _) ->
//        joinA.flatMap { a ->
//          joinB.map { b ->
//            f(a, b)
//          }
//        }
//      }
//    }
//
//  fun <A, B, C, D> parallelMapN(ctx: CoroutineContext, fa: Kind<F, A>, fb: Kind<F, B>, fc: Kind<F, C>, f: (A, B, C) -> D): Kind<F, D> =
//    fa.startF(ctx).flatMap { (joinA, _) ->
//      fb.startF(ctx).flatMap { (joinB, _) ->
//        fc.startF(ctx).flatMap { (joinC, _) ->
//          joinA.flatMap { a ->
//            joinB.flatMap { b ->
//              joinC.map { c ->
//                f(a, b, c)
//              }
//            }
//          }
//        }
//      }
//    }
//
//  fun <A, B, C, D, E> parallelMapN(ctx: CoroutineContext, fa: Kind<F, A>, fb: Kind<F, B>, fc: Kind<F, C>, fd: Kind<F, D>, f: (A, B, C, D) -> E): Kind<F, E> =
//    parallelMapN(ctx,
//      parallelMapN(ctx, fa, fb, ::Tuple2),
//      parallelMapN(ctx, fc, fd, ::Tuple2)
//    ) { (a, b), (c, d) -> f(a, b, c, d) }
//
//  fun <A, B, C, D, E, G> parallelMapN(ctx: CoroutineContext, fa: Kind<F, A>, fb: Kind<F, B>, fc: Kind<F, C>, fd: Kind<F, D>, fe: Kind<F, E>, f: (A, B, C, D, E) -> G): Kind<F, G> =
//    parallelMapN(ctx,
//      parallelMapN(ctx, fa, fb, fc, ::Tuple3),
//      parallelMapN(ctx, fd, fe, ::Tuple2)
//    ) { (a, b, c), (d, e) -> f(a, b, c, d, e) }
//
//    fun <A, B, C, D, E, G, H> parallelMapN(ctx: CoroutineContext, fa: Kind<F, A>, fb: Kind<F, B>, fc: Kind<F, C>, fd: Kind<F, D>, fe: Kind<F, E>, fg: Kind<F, G>, f: (A, B, C, D, E, G) -> H): Kind<F, H> =
//    parallelMapN(ctx,
//      parallelMapN(ctx, fa, fb, fc, ::Tuple3),
//      parallelMapN(ctx, fd, fe, fg, ::Tuple3)
//    ) { (a, b, c), (d, e, g) -> f(a, b, c, d, e, g) }
//
//  fun <A, B, C, D, E, G, H, I> parallelMapN(ctx: CoroutineContext, fa: Kind<F, A>, fb: Kind<F, B>, fc: Kind<F, C>, fd: Kind<F, D>, fe: Kind<F, E>, fg: Kind<F, G>, fh: Kind<F, H>, f: (A, B, C, D, E, G, H) -> I): Kind<F, I> =
//    parallelMapN(ctx,
//      parallelMapN(ctx, fa, fb, fc, ::Tuple3),
//      parallelMapN(ctx, fd, fe, ::Tuple2),
//      parallelMapN(ctx, fg, fh, ::Tuple2)
//    ) { (a, b, c), (d, e), (g, h) -> f(a, b, c, d, e, g, h) }
//
//  fun <A, B, C, D, E, G, H, I, J> parallelMapN(ctx: CoroutineContext, fa: Kind<F, A>, fb: Kind<F, B>, fc: Kind<F, C>, fd: Kind<F, D>, fe: Kind<F, E>, fg: Kind<F, G>, fh: Kind<F, H>, fi: Kind<F, I>, f: (A, B, C, D, E, G, H, I) -> J): Kind<F, J> =
//    parallelMapN(ctx,
//      parallelMapN(ctx, fa, fb, fc, ::Tuple3),
//      parallelMapN(ctx, fd, fe, fg, ::Tuple3),
//      parallelMapN(ctx, fh, fi, ::Tuple2)
//    ) { (a, b, c), (d, e, g), (h, i) -> f(a, b, c, d, e, g, h, i) }
//
//  fun <A, B, C, D, E, G, H, I, J, K> parallelMapN(ctx: CoroutineContext, fa: Kind<F, A>, fb: Kind<F, B>, fc: Kind<F, C>, fd: Kind<F, D>, fe: Kind<F, E>, fg: Kind<F, G>, fh: Kind<F, H>, fi: Kind<F, I>, fj: Kind<F, J>, f: (A, B, C, D, E, G, H, I, J) -> K): Kind<F, K> =
//    parallelMapN(ctx,
//      parallelMapN(ctx, fa, fb, fc, ::Tuple3),
//      parallelMapN(ctx, fd, fe, fg, ::Tuple3),
//      parallelMapN(ctx, fh, fi, fj, ::Tuple3)
//    ) { (a, b, c), (d, e, g), (h, i, j) -> f(a, b, c, d, e, g, h, i, j) }

}
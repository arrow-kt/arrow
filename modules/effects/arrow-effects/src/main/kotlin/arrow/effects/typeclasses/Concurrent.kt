package arrow.effects.typeclasses

import arrow.Kind
import arrow.core.*
import arrow.effects.Fiber
import kotlin.coroutines.CoroutineContext

/**
 * Type class for async data types that are cancelable and can be started concurrently.
 */
interface Concurrent<F> : Async<F> {

  /**
   * Create a new [F] that upon execution starts the receiver [F] within a [Fiber] on [ctx].
   *
   * ```kotlin:ank:playground
   * import arrow.effects.*
   * import arrow.effects.instances.io.async.async
   * import arrow.effects.instances.io.monad.binding
   * import kotlinx.coroutines.Dispatchers
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   binding {
   *     val promise = Promise.uncancelable<ForIO, Int>(IO.async()).bind()
   *     val fiber = promise.get.startF(Dispatchers.Default).bind()
   *     promise.complete(1).bind()
   *     fiber.join().bind()
   *   }.unsafeRunSync() == 1
   *   //sampleEnd
   * }
   * ```
   *
   * @receiver [F] to execute on [ctx] within a new suspended [F].
   * @param ctx [CoroutineContext] to execute the source [F] on.
   * @return [F] with suspended execution of source [F] on context [ctx].
   */
  fun <A> Kind<F, A>.startF(ctx: CoroutineContext): Kind<F, Fiber<F, A>>

  /**
   * Race two tasks concurrently within a new [F].
   * Race results in a winner and the other, yet to finish task running in a [Fiber].
   *
   * ```kotlin:ank:playground
   * import arrow.effects.*
   * import arrow.effects.instances.io.async.async
   * import arrow.effects.instances.io.concurrent.racePair
   * import arrow.effects.instances.io.monad.binding
   * import arrow.effects.typeclasses.Fiber
   * import kotlinx.coroutines.Dispatchers
   * import java.lang.RuntimeException
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   binding {
   *     val promise = Promise.uncancelable<ForIO, Int>(IO.async()).bind()
   *     val eitherGetOrUnit = racePair(Dispatchers.Default, promise.get, IO.unit).bind()
   *     eitherGetOrUnit.fold(
   *       { IO.raiseError<Int>(RuntimeException("Promise.get cannot win before complete")) },
   *       { (a: Fiber<ForIO, Int>, _) -> promise.complete(1).flatMap { a.join() } }
   *     ).bind()
   *   }.unsafeRunSync() == 1
   *   //sampleEnd
   * }
   * ```
   *
   * @param ctx [CoroutineContext] to execute the source [F] on.
   * @param fa task to participate in the race
   * @param fb task to participate in the race
   * @return [F] either [Left] with product of the winner's result [fa] and still running task [fb],
   *   or [Right] with product of running task [fa] and the winner's result [fb].
   *
   * @see raceN for a simpler version that cancels loser.
   */
  fun <A, B> racePair(ctx: CoroutineContext, fa: Kind<F, A>, fb: Kind<F, B>): Kind<F, Either<Tuple2<A, Fiber<F, B>>, Tuple2<Fiber<F, A>, B>>>

  /**
   * Map two tasks in parallel within a new [F] on [ctx].
   *
   * ```kotlin:ank:playground
   * import arrow.effects.instances.io.concurrent.parMapN
   * import arrow.effects.instances.io.monadDefer.delay
   * import kotlinx.coroutines.Dispatchers
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val result = parMapN(Dispatchers.Default,
   *     delay { "First one is on ${Thread.currentThread().name}" },
   *     delay { "Second one is on ${Thread.currentThread().name}" }
   *   ) { a, b ->
   *     "$a\n$b"
   *   }
   *   //sampleEnd
   *   println(result.unsafeRunSync())
   * }
   * ```
   *
   * @param ctx [CoroutineContext] to execute the source [F] on.
   * @param fa value to parallel map
   * @param fb value to parallel map
   * @param f function to map/combine value [A] and [B]
   * @return [F] with the result of function [f].
   *
   * @see racePair for a version that does not await all results to be finished.
   */
  fun <A, B, C> parMapN(ctx: CoroutineContext, fa: Kind<F, A>, fb: Kind<F, B>, f: (A, B) -> C): Kind<F, C> =
    racePair(ctx, fa, fb).flatMap {
      it.fold({ (a, fiberB) ->
        fiberB.join().map { b -> f(a, b) }
      }, { (fiberA, b) ->
        fiberA.join().map { a -> f(a, b) }
      })
    }

  /**
   * @see parMapN
   */
  fun <A, B, C, D> parMapN(ctx: CoroutineContext, fa: Kind<F, A>, fb: Kind<F, B>, fc: Kind<F, C>, f: (A, B, C) -> D): Kind<F, D> =
    racePair(ctx, fa, racePair(ctx, fb, fc)).flatMap {
      it.fold({ (a, bOrC) ->
        bOrC.join().flatMap { r ->
          r.fold({ (b, fiberC) ->
            fiberC.join().map { c -> f(a, b, c) }
          }, { (fiberB, c) ->
            fiberB.join().map { b -> f(a, b, c) }
          })
        }
      }, { (fiberA, bOrD) ->
        bOrD.fold({ (b, fiberC) ->
          fiberA.join().flatMap { a ->
            fiberC.join().map { c -> f(a, b, c) }
          }
        }, { (fiberB, c) ->
          fiberA.join().flatMap { a ->
            fiberB.join().map { b -> f(a, b, c) }
          }
        })
      })
    }

  /**
   * @see parMapN
   */
  fun <A, B, C, D, E> parMapN(
    ctx: CoroutineContext,
    fa: Kind<F, A>,
    fb: Kind<F, B>,
    fc: Kind<F, C>,
    fd: Kind<F, D>,
    f: (A, B, C, D) -> E): Kind<F, E> =
    parMapN(ctx, fa, fb, parMapN(ctx, fc, fd, ::Tuple2)) { a, b, (c, d) ->
      f(a, b, c, d)
    }

  /**
   * @see parMapN
   */
  fun <A, B, C, D, E, G> parMapN(
    ctx: CoroutineContext,
    fa: Kind<F, A>,
    fb: Kind<F, B>,
    fc: Kind<F, C>,
    fd: Kind<F, D>,
    fe: Kind<F, E>,
    f: (A, B, C, D, E) -> G): Kind<F, G> =
    parMapN(ctx, fa, fb, parMapN(ctx, fc, fd, fe, ::Tuple3)) { a, b, (c, d, e) ->
      f(a, b, c, d, e)
    }

  /**
   * @see parMapN
   */
  fun <A, B, C, D, E, G, H> parMapN(
    ctx: CoroutineContext,
    fa: Kind<F, A>,
    fb: Kind<F, B>,
    fc: Kind<F, C>,
    fd: Kind<F, D>,
    fe: Kind<F, E>,
    fg: Kind<F, G>,
    f: (A, B, C, D, E, G) -> H): Kind<F, H> =
    parMapN(ctx, parMapN(ctx, fa, fb, fc, ::Tuple3), parMapN(ctx, fd, fe, fg, ::Tuple3)) { (a, b, c), (d, e, g) ->
      f(a, b, c, d, e, g)
    }

  /**
   * @see parMapN
   */
  fun <A, B, C, D, E, G, H, I> parMapN(
    ctx: CoroutineContext,
    fa: Kind<F, A>,
    fb: Kind<F, B>,
    fc: Kind<F, C>,
    fd: Kind<F, D>,
    fe: Kind<F, E>,
    fg: Kind<F, G>,
    fh: Kind<F, H>,
    f: (A, B, C, D, E, G, H) -> I): Kind<F, I> =
    parMapN(ctx, parMapN(ctx, fa, fb, fc, ::Tuple3), parMapN(ctx, fd, fe, fg, ::Tuple3), fh) { (a, b, c), (d, e, g), h ->
      f(a, b, c, d, e, g, h)
    }

  /**
   * @see parMapN
   */
  fun <A, B, C, D, E, G, H, I, J> parMapN(
    ctx: CoroutineContext,
    fa: Kind<F, A>,
    fb: Kind<F, B>,
    fc: Kind<F, C>,
    fd: Kind<F, D>,
    fe: Kind<F, E>,
    fg: Kind<F, G>,
    fh: Kind<F, H>,
    fi: Kind<F, I>, f: (A, B, C, D, E, G, H, I) -> J): Kind<F, J> =
    parMapN(ctx, parMapN(ctx, fa, fb, fc, ::Tuple3), parMapN(ctx, fd, fe, fg, ::Tuple3), parMapN(ctx, fh, fi, ::Tuple2)) { (a, b, c), (d, e, g), (h, i) ->
      f(a, b, c, d, e, g, h, i)
    }

  /**
   * @see parMapN
   */
  fun <A, B, C, D, E, G, H, I, J, K> parMapN(
    ctx: CoroutineContext,
    fa: Kind<F, A>,
    fb: Kind<F, B>,
    fc: Kind<F, C>,
    fd: Kind<F, D>,
    fe: Kind<F, E>,
    fg: Kind<F, G>,
    fh: Kind<F, H>,
    fi: Kind<F, I>,
    fj: Kind<F, J>,
    f: (A, B, C, D, E, G, H, I, J) -> K): Kind<F, K> =
    parMapN(ctx, parMapN(ctx, fa, fb, fc, ::Tuple3), parMapN(ctx, fd, fe, fg, ::Tuple3), parMapN(ctx, fh, fi, fj, ::Tuple3)) { (a, b, c), (d, e, g), (h, i, j) ->
      f(a, b, c, d, e, g, h, i, j)
    }

  /**
   * Race two tasks concurrently within a new [F] on [ctx].
   * At the end of the race it automatically cancels the loser.
   *
   * ```kotlin:ank:playground
   * import arrow.effects.*
   * import arrow.effects.instances.io.concurrent.raceN
   * import arrow.effects.instances.io.monad.binding
   * import kotlinx.coroutines.Dispatchers
   * import java.lang.RuntimeException
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   binding {
   *     val eitherGetOrUnit = raceN(Dispatchers.Default, IO.never, IO.just(5)).bind()
   *     eitherGetOrUnit.fold(
   *       { IO.raiseError<Int>(RuntimeException("Never always loses race")) },
   *       IO.Companion::just
   *     ).bind()
   *   }.unsafeRunSync()
   *   //sampleEnd
   * }
   * ```
   *
   * @param ctx [CoroutineContext] to execute the source [F] on.
   * @param fa task to participate in the race
   * @param fb task to participate in the race
   * @return [F] either [Left] if [fa] won the race,
   *   or [Right] if [fb] won the race.
   *
   * @see racePair for a version that does not automatically cancel the loser.
   */
  fun <A, B> raceN(
    ctx: CoroutineContext,
    fa: Kind<F, A>,
    fb: Kind<F, B>): Kind<F, Either<A, B>> =
    racePair(ctx, fa, fb).flatMap {
      it.fold({ (a, b) ->
        b.cancel().map { a.left() }
      }, { (a, b) ->
        a.cancel().map { b.right() }
      })
    }

  /**
   * @see raceN
   */
  fun <A, B, C> raceN(
    ctx: CoroutineContext,
    fa: Kind<F, A>,
    fb: Kind<F, B>,
    fc: Kind<F, C>): Kind<F, Either<A, Either<B, C>>> =
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

  /**
   * @see raceN
   */
  fun <A, B, C, D> raceN(
    ctx: CoroutineContext,
    a: Kind<F, A>,
    b: Kind<F, B>,
    c: Kind<F, C>,
    d: Kind<F, D>): Kind<F, Either<Either<A, B>, Either<C, D>>> =
    raceN(ctx, raceN(ctx, a, b), raceN(ctx, c, d))

  /**
   * @see raceN
   */
  fun <A, B, C, D, E> raceN(
    ctx: CoroutineContext,
    a: Kind<F, A>,
    b: Kind<F, B>,
    c: Kind<F, C>,
    d: Kind<F, D>,
    e: Kind<F, E>): Kind<F, Either<Either<A, Either<B, C>>, Either<D, E>>> =
    raceN(ctx, raceN(ctx, a, b, c), raceN(ctx, d, e))

  /**
   * @see raceN
   */
  fun <A, B, C, D, E, G> raceN(
    ctx: CoroutineContext,
    a: Kind<F, A>,
    b: Kind<F, B>,
    c: Kind<F, C>,
    d: Kind<F, D>,
    e: Kind<F, E>,
    g: Kind<F, G>): Kind<F, Either<Either<A, B>, Either<Either<C, D>, Either<E, G>>>> =
    raceN(ctx, raceN(ctx, a, b), raceN(ctx, c, d), raceN(ctx, e, g))

  /**
   * @see raceN
   */
  fun <A, B, C, D, E, G, H> raceN(
    ctx: CoroutineContext,
    a: Kind<F, A>,
    b: Kind<F, B>,
    c: Kind<F, C>, d: Kind<F, D>, e: Kind<F, E>, g: Kind<F, G>, h: Kind<F, H>): Kind<F, Either<Either<A, Either<B, C>>, Either<Either<D, E>, Either<G, H>>>> =
    raceN(ctx, raceN(ctx, a, b, c), raceN(ctx, d, e), raceN(ctx, g, h))

  /**
   * @see raceN
   */
  fun <A, B, C, D, E, G, H, I> raceN(
    ctx: CoroutineContext,
    a: Kind<F, A>,
    b: Kind<F, B>,
    c: Kind<F, C>,
    d: Kind<F, D>,
    e: Kind<F, E>,
    g: Kind<F, G>,
    h: Kind<F, H>,
    i: Kind<F, I>): Kind<F, Either<Either<Either<A, B>, Either<C, D>>, Either<Either<E, G>, Either<H, I>>>> =
    raceN(ctx, raceN(ctx, a, b), raceN(ctx, c, d), raceN(ctx, e, g), raceN(ctx, h, i))

  /**
   * @see raceN
   */
  fun <A, B, C, D, E, G, H, I, J> raceN(
    ctx: CoroutineContext,
    a: Kind<F, A>,
    b: Kind<F, B>,
    c: Kind<F, C>,
    d: Kind<F, D>,
    e: Kind<F, E>,
    g: Kind<F, G>,
    h: Kind<F, H>,
    i: Kind<F, I>,
    j: Kind<F, J>): Kind<F, Either<Either<Either<A, Either<B, C>>, Either<D, E>>, Either<Either<G, H>, Either<I, J>>>> =
    raceN(ctx, raceN(ctx, a, b, c), raceN(ctx, d, e), raceN(ctx, g, h), raceN(ctx, i, j))

}

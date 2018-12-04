package arrow.effects.typeclasses

import arrow.Kind
import arrow.core.*
import arrow.effects.Fiber
import arrow.effects.IO
import kotlin.coroutines.CoroutineContext

/**
 * Type class for async data types that are cancelable and can be started concurrently.
 */
interface Concurrent<F> : Async<F> {

  /**
   * Create a new [F] that upon execution starts the receiver [F] within a [Fiber] on [ctx].
   *
   * {: data-executable='true'}
   *
   * ```kotlin:ank
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
       }.unsafeRunSync() == 1
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
   * {: data-executable='true'}
   *
   * ```kotlin:ank
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

  //TODO blocked by Async#asyncF (https://github.com/arrow-kt/arrow/issues/1124)
  //fun <A> cancelable(cb: ((Either<Throwable, A>) -> Unit) -> Kind<F, Unit>): Kind<F, A> =

  /**
   * Race two tasks concurrently within a new [F] on [ctx].
   * At the end of the race it automatically cancels the loser.
   *
   * {: data-executable='true'}
   *
   * ```kotlin:ank
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
  fun <A, B> raceN(ctx: CoroutineContext, fa: Kind<F, A>, fb: Kind<F, B>): Kind<F, Either<A, B>> =
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

  /**
   * @see raceN
   */
  fun <A, B, C, D> raceN(ctx: CoroutineContext, a: Kind<F, A>, b: Kind<F, B>, c: Kind<F, C>, d: Kind<F, D>): Kind<F, Either<Either<A, B>, Either<C, D>>> =
    raceN(ctx,
      raceN(ctx, a, b),
      raceN(ctx, c, d)
    )

  /**
   * @see raceN
   */
  fun <A, B, C, D, E> raceN(ctx: CoroutineContext, a: Kind<F, A>, b: Kind<F, B>, c: Kind<F, C>, d: Kind<F, D>, e: Kind<F, E>): Kind<F, Either<Either<A, Either<B, C>>, Either<D, E>>> =
    raceN(ctx,
      raceN(ctx, a, b, c),
      raceN(ctx, d, e)
    )

  /**
   * @see raceN
   */
  fun <A, B, C, D, E, G> raceN(ctx: CoroutineContext, a: Kind<F, A>, b: Kind<F, B>, c: Kind<F, C>, d: Kind<F, D>, e: Kind<F, E>, g: Kind<F, G>): Kind<F, Either<Either<A, B>, Either<Either<C, D>, Either<E, G>>>> =
    raceN(ctx,
      raceN(ctx, a, b),
      raceN(ctx, c, d),
      raceN(ctx, e, g)
    )

  /**
   * @see raceN
   */
  fun <A, B, C, D, E, G, H> raceN(ctx: CoroutineContext, a: Kind<F, A>, b: Kind<F, B>, c: Kind<F, C>, d: Kind<F, D>, e: Kind<F, E>, g: Kind<F, G>, h: Kind<F, H>): Kind<F, Either<Either<A, Either<B, C>>, Either<Either<D, E>, Either<G, H>>>> =
    raceN(ctx,
      raceN(ctx, a, b, c),
      raceN(ctx, d, e),
      raceN(ctx, g, h)
    )

  /**
   * @see raceN
   */
  fun <A, B, C, D, E, G, H, I> raceN(ctx: CoroutineContext, a: Kind<F, A>, b: Kind<F, B>, c: Kind<F, C>, d: Kind<F, D>, e: Kind<F, E>, g: Kind<F, G>, h: Kind<F, H>, i: Kind<F, I>): Kind<F, Either<Either<Either<A, B>, Either<C, D>>, Either<Either<E, G>, Either<H, I>>>> =
    raceN(ctx,
      raceN(ctx, a, b),
      raceN(ctx, c, d),
      raceN(ctx, e, g),
      raceN(ctx, h, i)
    )

  /**
   * @see raceN
   */
  fun <A, B, C, D, E, G, H, I, J> raceN(ctx: CoroutineContext, a: Kind<F, A>, b: Kind<F, B>, c: Kind<F, C>, d: Kind<F, D>, e: Kind<F, E>, g: Kind<F, G>, h: Kind<F, H>, i: Kind<F, I>, j: Kind<F, J>): Kind<F, Either<Either<Either<A, Either<B, C>>, Either<D, E>>, Either<Either<G, H>, Either<I, J>>>> =
    raceN(ctx,
      raceN(ctx, a, b, c),
      raceN(ctx, d, e),
      raceN(ctx, g, h),
      raceN(ctx, i, j)
    )

}

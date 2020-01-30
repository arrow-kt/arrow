package arrow.fx

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.core.internal.AtomicBooleanW
import arrow.fx.internal.IOFiber
import arrow.fx.internal.IOForkedStart
import arrow.fx.internal.Platform
import arrow.fx.internal.UnsafePromise
import arrow.fx.typeclasses.Fiber
import kotlin.coroutines.CoroutineContext

interface IORace {

  fun <A, B> raceN(ioA: IOOf<A>, ioB: IOOf<B>): IO<Race2<A, B>> =
    IO.raceN(IODispatchers.CommonPool, ioA, ioB)

  fun <A, B, C> raceN(ioA: IOOf<A>, ioB: IOOf<B>, ioC: IOOf<C>): IO<Race3<out A, out B, out C>> =
    IO.raceN(IODispatchers.CommonPool, ioA, ioB, ioC)

  fun <A, B, C, D> raceN(ioA: IOOf<A>, ioB: IOOf<B>, ioC: IOOf<C>, ioD: IOOf<D>): IO<Race4<out A, out B, out C, out D>> =
    IO.raceN(IODispatchers.CommonPool, ioA, ioB, ioC, ioD)

  fun <A, B, C, D, E> raceN(ioA: IOOf<A>, ioB: IOOf<B>, ioC: IOOf<C>, ioD: IOOf<D>, ioE: IOOf<E>): IO<Race5<out A, out B, out C, out D, out E>> =
    IO.raceN(IODispatchers.CommonPool, ioA, ioB, ioC, ioD, ioE)

  fun <A, B, C, D, E, F> raceN(ioA: IOOf<A>, ioB: IOOf<B>, ioC: IOOf<C>, ioD: IOOf<D>, ioE: IOOf<E>, ioF: IOOf<F>): IO<Race6<out A, out B, out C, out D, out E, out F>> =
    IO.raceN(IODispatchers.CommonPool, ioA, ioB, ioC, ioD, ioE, ioF)

  fun <A, B, C, D, E, F, G> raceN(ioA: IOOf<A>, ioB: IOOf<B>, ioC: IOOf<C>, ioD: IOOf<D>, ioE: IOOf<E>, ioF: IOOf<F>, ioG: IOOf<G>): IO<Race7<out A, out B, out C, out D, out E, out F, out G>> =
    IO.raceN(IODispatchers.CommonPool, ioA, ioB, ioC, ioD, ioE, ioF, ioG)

  fun <A, B, C, D, E, F, G, H> raceN(ioA: IOOf<A>, ioB: IOOf<B>, ioC: IOOf<C>, ioD: IOOf<D>, ioE: IOOf<E>, ioF: IOOf<F>, ioG: IOOf<G>, ioH: IOOf<H>): IO<Race8<out A, out B, out C, out D, out E, out F, out G, out H>> =
    IO.raceN(IODispatchers.CommonPool, ioA, ioB, ioC, ioD, ioE, ioF, ioG, ioH)

  fun <A, B, C, D, E, F, G, H, I> raceN(ioA: IOOf<A>, ioB: IOOf<B>, ioC: IOOf<C>, ioD: IOOf<D>, ioE: IOOf<E>, ioF: IOOf<F>, ioG: IOOf<G>, ioH: IOOf<H>, ioI: IOOf<I>): IO<Race9<out A, out B, out C, out D, out E, out F, out G, out H, out I>> =
    IO.raceN(IODispatchers.CommonPool, ioA, ioB, ioC, ioD, ioE, ioF, ioG, ioH, ioI)

  /**
   * Race two tasks concurrently within a new [IO] on [this@raceN].
   * At the end of the race it automatically cancels the loser.
   *
   * ```kotlin:ank:playground
   * import arrow.Kind
   * import arrow.fx.*
   * import arrow.fx.typeclasses.Concurrent
   * import kotlinx.coroutines.Dispatchers
   * import arrow.fx.extensions.io.concurrent.concurrent
   *
   * fun main(args: Array<String>) {
   *   fun <F> Concurrent<F>.example(): Kind<F, String> {
   *     val never: Kind<F, Int> = cancelable { effect { println("Never got canelled for losing.") } }
   *
   *     //sampleStart
   *     val result = fx.concurrent {
   *       val eitherGetOrUnit = !Dispatchers.Default.raceN(never, just(5))
   *       eitherGetOrUnit.fold(
   *         { "Never always loses race" },
   *         { i -> "Race was won with $i" }
   *       )
   *     }
   *     //sampleEnd
   *     return result
   *   }
   *
   *   IO.concurrent().example().fix().unsafeRunSync().let(::println)
   * }
   * ```
   *
   * @param ctx [CoroutineContext] to execute the source [IO] on.
   * @param ioA task to participate in the race
   * @param ioB task to participate in the race
   * @return [IO] either [Left] if [ioA] won the race,
   *   or [Right] if [ioB] won the race.
   *
   * @see racePair for a version that does not automatically cancel the loser.
   */
  fun <A, B> raceN(
    ctx: CoroutineContext,
    ioA: IOOf<A>,
    ioB: IOOf<B>
  ): IO<Race2<A, B>> =
    racePairCancellable(ctx, ioA, ioB)

  /**
   * @see raceN
   */
  fun <A, B, C> raceN(
    ctx: CoroutineContext,
    ioA: IOOf<A>,
    ioB: IOOf<B>,
    ioC: IOOf<C>
  ): IO<Race3<out A, out B, out C>> =
    raceN(ctx,
      raceN(ctx, ioA, ioB),
      ioC
    ).map {
      it.fold(
        { it.fold({ a -> Race3.First(a) }, { b -> Race3.Second(b) }) },
        { c -> Race3.Third(c) }
      )
    }

  /**
   * @see raceN
   */
  fun <A, B, C, D> raceN(
    ctx: CoroutineContext,
    ioA: IOOf<A>,
    ioB: IOOf<B>,
    ioC: IOOf<C>,
    ioD: IOOf<D>
  ): IO<Race4<out A, out B, out C, out D>> =
    raceN(ctx,
      raceN(ctx, ioA, ioB),
      raceN(ctx, ioC, ioD)
    ).map { res ->
      res.fold(
        { it.fold({ a -> Race4.First(a) }, { b -> Race4.Second(b) }) },
        { it.fold({ c -> Race4.Third(c) }, { d -> Race4.Fourth(d) }) }
      )
    }

  /**
   * @see raceN
   */
  fun <A, B, C, D, E> raceN(
    ctx: CoroutineContext,
    ioA: IOOf<A>,
    ioB: IOOf<B>,
    ioC: IOOf<C>,
    ioD: IOOf<D>,
    ioE: IOOf<E>
  ): IO<Race5<out A, out B, out C, out D, out E>> =
    raceN(ctx,
      raceN(ctx, ioA, ioB, ioC),
      raceN(ctx, ioD, ioE)
    ).map { res ->
      res.fold(
        { race3 -> race3.fold({ a -> Race5.First(a) }, { b -> Race5.Second(b) }, { c -> Race5.Third(c) }) },
        { race2 -> race2.fold({ d -> Race5.Fourth(d) }, { e -> Race5.Fifth(e) }) }
      )
    }

  /**
   * @see raceN
   */
  fun <A, B, C, D, E, F> raceN(
    ctx: CoroutineContext,
    ioA: IOOf<A>,
    ioB: IOOf<B>,
    ioC: IOOf<C>,
    ioD: IOOf<D>,
    ioE: IOOf<E>,
    ioF: IOOf<F>
  ): IO<Race6<out A, out B, out C, out D, out E, out F>> =
    raceN(ctx,
      raceN(ctx, ioA, ioB, ioC),
      raceN(ctx, ioD, ioE, ioF)
    ).map { res ->
      res.fold(
        { race3 -> race3.fold({ a -> Race6.First(a) }, { b -> Race6.Second(b) }, { c -> Race6.Third(c) }) },
        { race3 -> race3.fold({ d -> Race6.Fourth(d) }, { e -> Race6.Fifth(e) }, { f -> Race6.Sixth(f) }) }
      )
    }

  /**
   * @see raceN
   */
  fun <A, B, C, D, E, F, G> raceN(
    ctx: CoroutineContext,
    ioA: IOOf<A>,
    ioB: IOOf<B>,
    ioC: IOOf<C>,
    ioD: IOOf<D>,
    ioE: IOOf<E>,
    ioF: IOOf<F>,
    ioG: IOOf<G>
  ): IO<Race7<out A, out B, out C, out D, out E, out F, out G>> =
    raceN(ctx,
      raceN(ctx, ioA, ioB, ioC),
      raceN(ctx, ioD, ioE),
      raceN(ctx, ioF, ioG)
    ).map { res ->
      res.fold(
        { race3 -> race3.fold({ a -> Race7.First(a) }, { b -> Race7.Second(b) }, { c -> Race7.Third(c) }) },
        { race2 -> race2.fold({ d -> Race7.Fourth(d) }, { e -> Race7.Fifth(e) }) },
        { race2 -> race2.fold({ f -> Race7.Sixth(f) }, { g -> Race7.Seventh(g) }) }
      )
    }

  /**
   * @see raceN
   */
  fun <A, B, C, D, E, F, G, H> raceN(
    ctx: CoroutineContext,
    ioA: IOOf<A>,
    ioB: IOOf<B>,
    ioC: IOOf<C>,
    ioD: IOOf<D>,
    ioE: IOOf<E>,
    ioF: IOOf<F>,
    ioG: IOOf<G>,
    ioH: IOOf<H>
  ): IO<Race8<out A, out B, out C, out D, out E, out F, out G, out H>> =
    raceN(ctx,
      raceN(ctx, ioA, ioB, ioC),
      raceN(ctx, ioD, ioE, ioF),
      raceN(ctx, ioG, ioH)
    ).map { res ->
      res.fold(
        { race3 -> race3.fold({ a -> Race8.First(a) }, { b -> Race8.Second(b) }, { c -> Race8.Third(c) }) },
        { race3 -> race3.fold({ d -> Race8.Fourth(d) }, { e -> Race8.Fifth(e) }, { f -> Race8.Sixth(f) }) },
        { race2 -> race2.fold({ g -> Race8.Seventh(g) }, { h -> Race8.Eighth(h) }) }
      )
    }

  /**
   * @see raceN
   */
  fun <A, B, C, D, E, F, G, H, I> raceN(
    ctx: CoroutineContext,
    ioA: IOOf<A>,
    ioB: IOOf<B>,
    ioC: IOOf<C>,
    ioD: IOOf<D>,
    ioE: IOOf<E>,
    ioF: IOOf<F>,
    ioG: IOOf<G>,
    ioH: IOOf<H>,
    ioI: IOOf<I>
  ): IO<Race9<out A, out B, out C, out D, out E, out F, out G, out H, out I>> =
    raceN(ctx,
      raceN(ctx, ioA, ioB, ioC),
      raceN(ctx, ioD, ioE, ioF),
      raceN(ctx, ioG, ioH, ioI)
    ).map { res ->
      res.fold(
        { race3 -> race3.fold({ a -> Race9.First(a) }, { b -> Race9.Second(b) }, { c -> Race9.Third(c) }) },
        { race3 -> race3.fold({ d -> Race9.Fourth(d) }, { e -> Race9.Fifth(e) }, { f -> Race9.Sixth(f) }) },
        { race3 -> race3.fold({ g -> Race9.Seventh(g) }, { h -> Race9.Eighth(h) }, { i -> Race9.Ninth(i) }) }
      )
    }

  /**
   * Implementation for `IO.racePair`, but this way it is more efficient,
   * as we no longer have to keep internal promises.
   */
  private fun <A, B> racePairCancellable(ctx: CoroutineContext, ioA: IOOf<A>, ioB: IOOf<B>): IO<Either<A, B>> {
    fun <T, U> onSuccess(
      isActive: AtomicBooleanW,
      main: IOConnection,
      other: IOConnection,
      cb: (Either<Throwable, Either<T, U>>) -> Unit,
      r: Either<T, U>
    ): Unit =
      if (isActive.getAndSet(false)) {
        other.cancel().fix().unsafeRunAsync { r2 ->
          main.pop()
          cb(Right(r))
        }
      } else Unit

    fun <T> onError(
      active: AtomicBooleanW,
      cb: (Either<Throwable, T>) -> Unit,
      main: IOConnection,
      other: IOConnection,
      err: Throwable
    ): Unit =
      if (active.getAndSet(false)) {
        other.cancel().fix().unsafeRunAsync { r2 ->
          main.pop()
          cb(Left(r2.fold({ Platform.composeErrors(err, it) }, { err })))
        }
      } else Unit

    val start = { conn: IOConnection, cb: (Either<Throwable, Either<A, B>>) -> Unit ->
      val active = AtomicBooleanW(true)
      val connA = IOConnection()
      val connB = IOConnection()
      conn.pushPair(connA, connB)

      IORunLoop.startCancelable(IOForkedStart(ioA, ctx), connA) { result ->
        result.fold({
          onError(active, cb, conn, connB, it)
        }, {
          onSuccess(active, conn, connB, cb, Left(it))
        })
      }

      IORunLoop.startCancelable(IOForkedStart(ioB, ctx), connB) { result ->
        result.fold({
          onError(active, cb, conn, connA, it)
        }, {
          onSuccess(active, conn, connA, cb, Right(it))
        })
      }
    }

    return IO.Async(true, start)
  }

  /**
   * Race two tasks concurrently within a new [IO].
   * Race results in a winner and the other, yet to finish task running in a [Fiber].
   *
   * ```kotlin:ank:playground
   * import arrow.fx.*
   * import arrow.fx.extensions.fx
   * import kotlinx.coroutines.Dispatchers
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val result = IO.fx {
   *     val racePair = !IO.racePair(Dispatchers.Default, never<Int>(), just("Hello World!"))
   *     racePair.fold(
   *       { _, _ -> "never cannot win race" },
   *       { _, winner -> winner }
   *     )
   *   }
   *   //sampleEnd
   *
   *   val r = result.unsafeRunSync()
   *   println("Race winner result is: $r")
   * }
   * ```
   *
   * @param ctx [CoroutineContext] to execute the source [IO] on.
   * @param ioA task to participate in the race
   * @param ioB task to participate in the race
   * @return [IO] either [Left] with product of the winner's result [ioA] and still running task [ioB],
   *   or [Right] with product of running task [ioA] and the winner's result [ioB].
   *
   * @see [arrow.fx.typeclasses.Concurrent.raceN] for a simpler version that cancels loser.
   */
  fun <A, B> racePair(ctx: CoroutineContext, ioA: IOOf<A>, ioB: IOOf<B>): IO<RacePair<ForIO, A, B>> =
    IO.Async(true) { conn, cb ->
      val active = AtomicBooleanW(true)

      val connA = IOConnection()
      val promiseA = UnsafePromise<A>()

      val connB = IOConnection()
      val promiseB = UnsafePromise<B>()

      conn.pushPair(connA, connB)

      IORunLoop.startCancelable(IOForkedStart(ioA, ctx), connA) { either: Either<Throwable, A> ->
        either.fold({ error ->
          if (active.getAndSet(false)) { // if an error finishes first, stop the race.
            connB.cancel().fix().unsafeRunAsync { r2 ->
              conn.pop()
              cb(Left(r2.fold({ Platform.composeErrors(error, it) }, { error })))
            }
          } else {
            promiseA.complete(Left(error))
          }
        }, { a ->
          if (active.getAndSet(false)) {
            conn.pop()
            cb(Right(RacePair.First(a, IOFiber(promiseB, connB))))
          } else {
            promiseA.complete(Right(a))
          }
        })
      }

      IORunLoop.startCancelable(IOForkedStart(ioB, ctx), connB) { either: Either<Throwable, B> ->
        either.fold({ error ->
          if (active.getAndSet(false)) { // if an error finishes first, stop the race.
            connA.cancel().fix().unsafeRunAsync { r2 ->
              conn.pop()
              cb(Left(r2.fold({ Platform.composeErrors(error, it) }, { error })))
            }
          } else {
            promiseB.complete(Left(error))
          }
        }, { b ->
          if (active.getAndSet(false)) {
            conn.pop()
            cb(Right(RacePair.Second(IOFiber(promiseA, connA), b)))
          } else {
            promiseB.complete(Right(b))
          }
        })
      }
    }

  /**
   * Race three tasks concurrently within a new [IO].
   * Race results in a winner and the others, yet to finish task running in a [Fiber].
   *
   * ```kotlin:ank:playground
   * import arrow.fx.*
   * import arrow.fx.extensions.fx
   * import kotlinx.coroutines.Dispatchers
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val result = IO.fx {
   *     val raceResult = !IO.raceTriple(Dispatchers.Default, never<Int>(), just("Hello World!"), never<Double>())
   *     raceResult.fold(
   *       { _, _, _ -> "never cannot win before complete" },
   *       { _, winner, _ -> winner },
   *       { _, _, _ -> "never cannot win before complete" }
   *     )
   *   }
   *   //sampleEnd
   *
   *   val r = result.unsafeRunSync()
   *   println("Race winner result is: $r")
   * }
   * ```
   *
   * @param ctx [CoroutineContext] to execute the source [IO] on.
   * @param ioA task to participate in the race
   * @param ioB task to participate in the race
   * @param ioC task to participate in the race
   * @return [RaceTriple]
   *
   * @see [arrow.fx.typeclasses.Concurrent.raceN] for a simpler version that cancels losers.
   */
  fun <A, B, C> raceTriple(ctx: CoroutineContext, ioA: IOOf<A>, ioB: IOOf<B>, ioC: IOOf<C>): IO<RaceTriple<ForIO, A, B, C>> =
    IO.Async(true) { conn, cb ->
      val active = AtomicBooleanW(true)

      val connA = IOConnection()
      val promiseA = UnsafePromise<A>()

      val connB = IOConnection()
      val promiseB = UnsafePromise<B>()

      val connC = IOConnection()
      val promiseC = UnsafePromise<C>()

      conn.push(connA.cancel(), connB.cancel(), connC.cancel())

      IORunLoop.startCancelable(IOForkedStart(ioA, ctx), connA) { either: Either<Throwable, A> ->
        either.fold({ error ->
          if (active.getAndSet(false)) { // if an error finishes first, stop the race.
            connB.cancel().fix().unsafeRunAsync { r2 ->
              connC.cancel().fix().unsafeRunAsync { r3 ->
                conn.pop()
                val errorResult = r2.fold({ e2 ->
                  r3.fold({ e3 -> Platform.composeErrors(error, e2, e3) }, { Platform.composeErrors(error, e2) })
                }, {
                  r3.fold({ e3 -> Platform.composeErrors(error, e3) }, { error })
                })
                cb(Left(errorResult))
              }
            }
          } else {
            promiseA.complete(Left(error))
          }
        }, { a ->
          if (active.getAndSet(false)) {
            conn.pop()
            cb(Right(RaceTriple.First(a, IOFiber(promiseB, connB), IOFiber(promiseC, connC))))
          } else {
            promiseA.complete(Right(a))
          }
        })
      }

      IORunLoop.startCancelable(IOForkedStart(ioB, ctx), connB) { either: Either<Throwable, B> ->
        either.fold({ error ->
          if (active.getAndSet(false)) { // if an error finishes first, stop the race.
            connA.cancel().fix().unsafeRunAsync { r2 ->
              connC.cancel().fix().unsafeRunAsync { r3 ->
                conn.pop()
                val errorResult = r2.fold({ e2 ->
                  r3.fold({ e3 -> Platform.composeErrors(error, e2, e3) }, { Platform.composeErrors(error, e2) })
                }, {
                  r3.fold({ e3 -> Platform.composeErrors(error, e3) }, { error })
                })
                cb(Left(errorResult))
              }
            }
          } else {
            promiseB.complete(Left(error))
          }
        }, { b ->
          if (active.getAndSet(false)) {
            conn.pop()
            cb(Right(RaceTriple.Second(IOFiber(promiseA, connA), b, IOFiber(promiseC, connC))))
          } else {
            promiseB.complete(Right(b))
          }
        })
      }

      IORunLoop.startCancelable(IOForkedStart(ioC, ctx), connC) { either: Either<Throwable, C> ->
        either.fold({ error ->
          if (active.getAndSet(false)) { // if an error finishes first, stop the race.
            connA.cancel().fix().unsafeRunAsync { r2 ->
              connB.cancel().fix().unsafeRunAsync { r3 ->
                conn.pop()
                val errorResult = r2.fold({ e2 ->
                  r3.fold({ e3 -> Platform.composeErrors(error, e2, e3) }, { Platform.composeErrors(error, e2) })
                }, {
                  r3.fold({ e3 -> Platform.composeErrors(error, e3) }, { error })
                })
                cb(Left(errorResult))
              }
            }
          } else {
            promiseC.complete(Left(error))
          }
        }, { c ->
          if (active.getAndSet(false)) {
            conn.pop()
            cb(Right(RaceTriple.Third(IOFiber(promiseA, connA), IOFiber(promiseB, connB), c)))
          } else {
            promiseC.complete(Right(c))
          }
        })
      }
    }
}

package arrow.fx

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.fx.internal.AtomicBooleanW
import arrow.fx.internal.IOFiber
import arrow.fx.internal.IOForkedStart
import arrow.fx.internal.Platform
import arrow.fx.internal.UnsafePromise
import arrow.fx.typeclasses.Fiber
import kotlin.coroutines.CoroutineContext

interface IORace {

  fun <EE, A, B> raceN(ioA: IOOf<EE, A>, ioB: IOOf<EE, B>): IO<EE, Race2<A, B>> =
    IO.raceN(IODispatchers.CommonPool, ioA, ioB)

  fun <EE, A, B, C> raceN(ioA: IOOf<EE, A>, ioB: IOOf<EE, B>, ioC: IOOf<EE, C>): IO<EE, Race3<out A, out B, out C>> =
    IO.raceN(IODispatchers.CommonPool, ioA, ioB, ioC)

  fun <EE, A, B, C, D> raceN(ioA: IOOf<EE, A>, ioB: IOOf<EE, B>, ioC: IOOf<EE, C>, ioD: IOOf<EE, D>): IO<EE, Race4<out A, out B, out C, out D>> =
    IO.raceN(IODispatchers.CommonPool, ioA, ioB, ioC, ioD)

  fun <EE, A, B, C, D, E> raceN(ioA: IOOf<EE, A>, ioB: IOOf<EE, B>, ioC: IOOf<EE, C>, ioD: IOOf<EE, D>, ioE: IOOf<EE, E>): IO<EE, Race5<out A, out B, out C, out D, out E>> =
    IO.raceN(IODispatchers.CommonPool, ioA, ioB, ioC, ioD, ioE)

  fun <EE, A, B, C, D, E, F> raceN(ioA: IOOf<EE, A>, ioB: IOOf<EE, B>, ioC: IOOf<EE, C>, ioD: IOOf<EE, D>, ioE: IOOf<EE, E>, ioF: IOOf<EE, F>): IO<EE, Race6<out A, out B, out C, out D, out E, out F>> =
    IO.raceN(IODispatchers.CommonPool, ioA, ioB, ioC, ioD, ioE, ioF)

  fun <EE, A, B, C, D, E, F, G> raceN(ioA: IOOf<EE, A>, ioB: IOOf<EE, B>, ioC: IOOf<EE, C>, ioD: IOOf<EE, D>, ioE: IOOf<EE, E>, ioF: IOOf<EE, F>, ioG: IOOf<EE, G>): IO<EE, Race7<out A, out B, out C, out D, out E, out F, out G>> =
    IO.raceN(IODispatchers.CommonPool, ioA, ioB, ioC, ioD, ioE, ioF, ioG)

  fun <EE, A, B, C, D, E, F, G, H> raceN(ioA: IOOf<EE, A>, ioB: IOOf<EE, B>, ioC: IOOf<EE, C>, ioD: IOOf<EE, D>, ioE: IOOf<EE, E>, ioF: IOOf<EE, F>, ioG: IOOf<EE, G>, ioH: IOOf<EE, H>): IO<EE, Race8<out A, out B, out C, out D, out E, out F, out G, out H>> =
    IO.raceN(IODispatchers.CommonPool, ioA, ioB, ioC, ioD, ioE, ioF, ioG, ioH)

  fun <EE, A, B, C, D, E, F, G, H, I> raceN(ioA: IOOf<EE, A>, ioB: IOOf<EE, B>, ioC: IOOf<EE, C>, ioD: IOOf<EE, D>, ioE: IOOf<EE, E>, ioF: IOOf<EE, F>, ioG: IOOf<EE, G>, ioH: IOOf<EE, H>, ioI: IOOf<EE, I>): IO<EE, Race9<out A, out B, out C, out D, out E, out F, out G, out H, out I>> =
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
   *     val never: Kind<F, Int> = cancellable { effect { println("Never got cancelled for losing.") } }
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
   *   IO.concurrent<Nothing>().example().fix().unsafeRunSync().let(::println)
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
  fun <EE, A, B> raceN(
    ctx: CoroutineContext,
    ioA: IOOf<EE, A>,
    ioB: IOOf<EE, B>
  ): IO<EE, Race2<A, B>> =
    race2(ctx, ioA, ioB)

  /**
   * @see raceN
   */
  fun <EE, A, B, C> raceN(
    ctx: CoroutineContext,
    ioA: IOOf<EE, A>,
    ioB: IOOf<EE, B>,
    ioC: IOOf<EE, C>
  ): IO<EE, Race3<out A, out B, out C>> =
    race3(ctx, ioA, ioB, ioC)

  /**
   * @see raceN
   */
  fun <EE, A, B, C, D> raceN(
    ctx: CoroutineContext,
    ioA: IOOf<EE, A>,
    ioB: IOOf<EE, B>,
    ioC: IOOf<EE, C>,
    ioD: IOOf<EE, D>
  ): IO<EE, Race4<out A, out B, out C, out D>> =
    race2(ctx,
      race2(ctx, ioA, ioB),
      race2(ctx, ioC, ioD)
    ).map { res ->
      res.fold(
        { it.fold({ a -> Race4.First(a) }, { b -> Race4.Second(b) }) },
        { it.fold({ c -> Race4.Third(c) }, { d -> Race4.Fourth(d) }) }
      )
    }

  /**
   * @see raceN
   */
  fun <EE, A, B, C, D, E> raceN(
    ctx: CoroutineContext,
    ioA: IOOf<EE, A>,
    ioB: IOOf<EE, B>,
    ioC: IOOf<EE, C>,
    ioD: IOOf<EE, D>,
    ioE: IOOf<EE, E>
  ): IO<EE, Race5<out A, out B, out C, out D, out E>> =
    race2(ctx,
      race3(ctx, ioA, ioB, ioC),
      race2(ctx, ioD, ioE)
    ).map { res ->
      res.fold(
        { race3 -> race3.fold({ a -> Race5.First(a) }, { b -> Race5.Second(b) }, { c -> Race5.Third(c) }) },
        { race2 -> race2.fold({ d -> Race5.Fourth(d) }, { e -> Race5.Fifth(e) }) }
      )
    }

  /**
   * @see raceN
   */
  fun <EE, A, B, C, D, E, F> raceN(
    ctx: CoroutineContext,
    ioA: IOOf<EE, A>,
    ioB: IOOf<EE, B>,
    ioC: IOOf<EE, C>,
    ioD: IOOf<EE, D>,
    ioE: IOOf<EE, E>,
    ioF: IOOf<EE, F>
  ): IO<EE, Race6<out A, out B, out C, out D, out E, out F>> =
    race2(ctx,
      race3(ctx, ioA, ioB, ioC),
      race3(ctx, ioD, ioE, ioF)
    ).map { res ->
      res.fold(
        { race3 -> race3.fold({ a -> Race6.First(a) }, { b -> Race6.Second(b) }, { c -> Race6.Third(c) }) },
        { race3 -> race3.fold({ d -> Race6.Fourth(d) }, { e -> Race6.Fifth(e) }, { f -> Race6.Sixth(f) }) }
      )
    }

  /**
   * @see raceN
   */
  fun <EE, A, B, C, D, E, F, G> raceN(
    ctx: CoroutineContext,
    ioA: IOOf<EE, A>,
    ioB: IOOf<EE, B>,
    ioC: IOOf<EE, C>,
    ioD: IOOf<EE, D>,
    ioE: IOOf<EE, E>,
    ioF: IOOf<EE, F>,
    ioG: IOOf<EE, G>
  ): IO<EE, Race7<out A, out B, out C, out D, out E, out F, out G>> =
    race3(ctx,
      race3(ctx, ioA, ioB, ioC),
      race2(ctx, ioD, ioE),
      race2(ctx, ioF, ioG)
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
  fun <EE, A, B, C, D, E, F, G, H> raceN(
    ctx: CoroutineContext,
    ioA: IOOf<EE, A>,
    ioB: IOOf<EE, B>,
    ioC: IOOf<EE, C>,
    ioD: IOOf<EE, D>,
    ioE: IOOf<EE, E>,
    ioF: IOOf<EE, F>,
    ioG: IOOf<EE, G>,
    ioH: IOOf<EE, H>
  ): IO<EE, Race8<out A, out B, out C, out D, out E, out F, out G, out H>> =
    race3(ctx,
      race3(ctx, ioA, ioB, ioC),
      race3(ctx, ioD, ioE, ioF),
      race2(ctx, ioG, ioH)
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
  fun <EE, A, B, C, D, E, F, G, H, I> raceN(
    ctx: CoroutineContext,
    ioA: IOOf<EE, A>,
    ioB: IOOf<EE, B>,
    ioC: IOOf<EE, C>,
    ioD: IOOf<EE, D>,
    ioE: IOOf<EE, E>,
    ioF: IOOf<EE, F>,
    ioG: IOOf<EE, G>,
    ioH: IOOf<EE, H>,
    ioI: IOOf<EE, I>
  ): IO<EE, Race9<out A, out B, out C, out D, out E, out F, out G, out H, out I>> =
    race3(ctx,
      race3(ctx, ioA, ioB, ioC),
      race3(ctx, ioD, ioE, ioF),
      race3(ctx, ioG, ioH, ioI)
    ).map { res ->
      res.fold(
        { race3 -> race3.fold({ a -> Race9.First(a) }, { b -> Race9.Second(b) }, { c -> Race9.Third(c) }) },
        { race3 -> race3.fold({ d -> Race9.Fourth(d) }, { e -> Race9.Fifth(e) }, { f -> Race9.Sixth(f) }) },
        { race3 -> race3.fold({ g -> Race9.Seventh(g) }, { h -> Race9.Eighth(h) }, { i -> Race9.Ninth(i) }) }
      )
    }

  /** Implementation for `IO.raceN` arity 2, this way it is more efficient than racePair, as we no longer have to keep internal promises. */
  private fun <E, A, B> race2(ctx: CoroutineContext, ioA: IOOf<E, A>, ioB: IOOf<E, B>): IO<E, Either<A, B>> {
    fun <T, U> onSuccess(
      isActive: AtomicBooleanW,
      main: IOConnection,
      other: IOConnection,
      cb: (IOResult<E, Either<T, U>>) -> Unit,
      r: Either<T, U>
    ): Unit =
      if (isActive.getAndSet(false)) {
        other.cancel().fix().unsafeRunAsync { r2 ->
          main.pop()
          cb(IOResult.Success(r))
        }
      } else Unit

    fun onException(
      active: AtomicBooleanW,
      cb: (IOResult<E, Nothing>) -> Unit,
      main: IOConnection,
      other: IOConnection,
      err: Throwable
    ): Unit =
      if (active.getAndSet(false)) {
        other.cancel().fix().unsafeRunAsync { r2 ->
          main.pop()
          cb(IOResult.Exception(r2.fold({ Platform.composeErrors(err, it) }, { err })))
        }
      } else Unit

    fun onError(
      active: AtomicBooleanW,
      cb: (IOResult<E, Nothing>) -> Unit,
      main: IOConnection,
      other: IOConnection,
      err: E
    ): Unit =
      if (active.getAndSet(false)) {
        other.cancel().fix().unsafeRunAsync { r2 ->
          main.pop()
          cb(IOResult.Error(r2.fold({
            it.printStackTrace() // TODO send to undelivered cancellation error to async handler
            err
          }, { err })))
        }
      } else Unit

    val start = { conn: IOConnection, cb: (IOResult<E, Either<A, B>>) -> Unit ->
      val active = AtomicBooleanW(true)
      val connA = IOConnection()
      val connB = IOConnection()
      conn.pushPair(connA, connB)

      IORunLoop.startCancellable(IOForkedStart(ioA, ctx), connA) { result ->
        result.fold({
          onException(active, cb, conn, connB, it)
        }, {
          onError(active, cb, conn, connB, it)
        }, {
          onSuccess(active, conn, connB, cb, Left(it))
        })
      }

      IORunLoop.startCancellable(IOForkedStart(ioB, ctx), connB) { result ->
        result.fold({
          onException(active, cb, conn, connA, it)
        }, {
          onError(active, cb, conn, connA, it)
        }, {
          onSuccess(active, conn, connA, cb, Right(it))
        })
      }
    }

    return IO.Async(true, start)
  }

  /** Implementation for `IO.raceN` arity 3, this way it is more efficient than racePair, as we no longer have to keep internal promises. */
  private fun <E, A, B, C> race3(ctx: CoroutineContext, ioA: IOOf<E, A>, ioB: IOOf<E, B>, ioC: IOOf<E, C>): IO<E, Race3<A, B, C>> {
    fun onSuccess(
      isActive: AtomicBooleanW,
      main: IOConnection,
      other2: IOConnection,
      other3: IOConnection,
      cb: (IOResult<E, Race3<A, B, C>>) -> Unit,
      r: Race3<A, B, C>
    ): Unit = if (isActive.getAndSet(false)) {
      other2.cancel().fix().unsafeRunAsync { r2 ->
        other3.cancel().fix().unsafeRunAsync { r3 ->
          main.pop()
          cb(IOResult.Success(r))
        }
      }
    } else Unit

    fun onError(
      active: AtomicBooleanW,
      main: IOConnection,
      other2: IOConnection,
      other3: IOConnection,
      cb: (IOResult<E, Nothing>) -> Unit,
      err: E
    ): Unit =
      if (active.getAndSet(false)) {
        other2.cancel().fix().unsafeRunAsync { r2 ->
          other3.cancel().fix().unsafeRunAsync { r3 ->
            main.pop()
            r2.fold({ it.printStackTrace() }, {}) // TODO send to undelivered cancellation error to async handler
            r3.fold({ it.printStackTrace() }, {})
            cb(IOResult.Error(err))
          }
        }
      } else Unit

    fun onException(
      active: AtomicBooleanW,
      cb: (IOResult<Nothing, Nothing>) -> Unit,
      main: IOConnection,
      other2: IOConnection,
      other3: IOConnection,
      err: Throwable
    ): Unit = if (active.getAndSet(false)) {
      other2.cancel().fix().unsafeRunAsync { r2 ->
        other3.cancel().fix().unsafeRunAsync { r3 ->
          main.pop()
          cb(IOResult.Exception(
            r2.fold({ err2 ->
              r3.fold({ err3 ->
                Platform.composeErrors(err, err2, err3)
              }, {
                Platform.composeErrors(err, err2)
              })
            }, {
              r3.fold({ err3 ->
                Platform.composeErrors(err, err3)
              }, {
                err
              })
            })
          ))
        }
      }
    } else Unit

    val start = { conn: IOConnection, cb: (IOResult<E, Race3<A, B, C>>) -> Unit ->
      val active = AtomicBooleanW(true)
      val connA = IOConnection()
      val connB = IOConnection()
      val connC = IOConnection()
      conn.push(connA.cancel(), connB.cancel(), connC.cancel())

      IORunLoop.startCancellable(IOForkedStart(ioA, ctx), connA) { result ->
        result.fold({
          onException(active, cb, conn, connB, connC, it)
        }, {
          onError(active, conn, connB, connC, cb, it)
        }, {
          onSuccess(active, conn, connB, connC, cb, Race3.First(it))
        })
      }

      IORunLoop.startCancellable(IOForkedStart(ioB, ctx), connB) { result ->
        result.fold({
          onException(active, cb, conn, connA, connC, it)
        }, {
          onError(active, conn, connA, connC, cb, it)
        }, {
          onSuccess(active, conn, connA, connC, cb, Race3.Second(it))
        })
      }

      IORunLoop.startCancellable(IOForkedStart(ioC, ctx), connC) { result ->
        result.fold({
          onException(active, cb, conn, connA, connB, it)
        }, {
          onError(active, conn, connA, connB, cb, it)
        }, {
          onSuccess(active, conn, connA, connB, cb, Race3.Third(it))
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
   *     //sampleStart
   *     val result = IO.fx<String> {
   *       val racePair = !IO.racePair(Dispatchers.Default, never<Int>(), just("Hello World!"))
   *       racePair.fold(
   *         { _, _ -> "never cannot win race" },
   *         { _, winner -> winner }
   *       )
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
  fun <E, A, B> racePair(ctx: CoroutineContext, ioA: IOOf<E, A>, ioB: IOOf<E, B>): IO<E, RacePair<IOPartialOf<E>, A, B>> =
    IO.Async(true) { conn, cb ->
      val active = AtomicBooleanW(true)

      // Cancellable connection for the left value
      val connA = IOConnection()
      val promiseA = UnsafePromise<E, A>()

      // Cancellable connection for the right value
      val connB = IOConnection()
      val promiseB = UnsafePromise<E, B>()

      conn.pushPair(connA, connB)

      IORunLoop.startCancellable(IOForkedStart(ioA, ctx), connA) { either: IOResult<E, A> ->
        either.fold({ error ->
          if (active.getAndSet(false)) { // if an error finishes first, stop the race.
            connB.cancel().unsafeRunAsync { r2 ->
              conn.pop()
              cb(IOResult.Exception(r2.fold({ Platform.composeErrors(error, it) }, { error })))
            }
          } else {
            promiseA.complete(IOResult.Exception(error))
          }
        }, { e ->
          if (active.getAndSet(false)) { // if an error finishes first, stop the race.
            connB.cancel().unsafeRunAsync { r2 ->
              conn.pop()
              // TODO asyncErrorHandler r2
              cb(IOResult.Error(e))
            }
          } else {
            promiseA.complete(IOResult.Error(e))
          }
        }, { a ->
          if (active.getAndSet(false)) {
            conn.pop()
            cb(IOResult.Success(RacePair.First(a, IOFiber(promiseB, connB))))
          } else {
            promiseA.complete(IOResult.Success(a))
          }
        })
      }

      IORunLoop.startCancellable(IOForkedStart(ioB, ctx), connB) { either: IOResult<E, B> ->
        either.fold({ error ->
          if (active.getAndSet(false)) { // if an error finishes first, stop the race.
            connA.cancel().unsafeRunAsync { r2 ->
              conn.pop()
              cb(IOResult.Exception(r2.fold({ Platform.composeErrors(error, it) }, { error })))
            }
          } else {
            promiseB.complete(IOResult.Exception(error))
          }
        }, { e ->
          if (active.getAndSet(false)) { // if an error finishes first, stop the race.
            connB.cancel().unsafeRunAsync { r2 ->
              conn.pop()
              // TODO asyncErrorHandler r2
              cb(IOResult.Error(e))
            }
          } else {
            promiseB.complete(IOResult.Error(e))
          }
        }, { b ->
          if (active.getAndSet(false)) {
            conn.pop()
            cb(IOResult.Success(RacePair.Second(IOFiber(promiseA, connA), b)))
          } else {
            promiseB.complete(IOResult.Success(b))
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
   *   val result = IO.fx<String> {
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
  fun <E, A, B, C> raceTriple(ctx: CoroutineContext, ioA: IOOf<E, A>, ioB: IOOf<E, B>, ioC: IOOf<E, C>): IO<E, RaceTriple<IOPartialOf<E>, A, B, C>> =
    IO.Async(true) { conn, cb ->
      val active = AtomicBooleanW(true)

      val connA = IOConnection()
      val promiseA = UnsafePromise<E, A>()

      val connB = IOConnection()
      val promiseB = UnsafePromise<E, B>()

      val connC = IOConnection()
      val promiseC = UnsafePromise<E, C>()

      conn.push(connA.cancel(), connB.cancel(), connC.cancel())

      IORunLoop.startCancellable(IOForkedStart(ioA, ctx), connA) { either: IOResult<E, A> ->
        either.fold({ error ->
          if (active.getAndSet(false)) { // if an error finishes first, stop the race.
            connB.cancel().unsafeRunAsync { r2 ->
              connC.cancel().unsafeRunAsync { r3 ->
                conn.pop()
                val errorResult = r2.fold({ e2 ->
                  r3.fold({ e3 -> Platform.composeErrors(error, e2, e3) }, { Platform.composeErrors(error, e2) })
                }, {
                  r3.fold({ e3 -> Platform.composeErrors(error, e3) }, { error })
                })
                cb(IOResult.Exception(errorResult))
              }
            }
          } else {
            promiseA.complete(IOResult.Exception(error))
          }
        }, { e ->
          if (active.getAndSet(false)) { // if an error finishes first, stop the race.
            connB.cancel().unsafeRunAsync { r2 ->
              connC.cancel().unsafeRunAsync { r3 ->
                conn.pop()
                cb(IOResult.Error(e))
              }
            }
          } else {
            promiseA.complete(IOResult.Error(e))
          }
        }, { a ->
          if (active.getAndSet(false)) {
            conn.pop()
            cb(IOResult.Success(RaceTriple.First(a, IOFiber(promiseB, connB), IOFiber(promiseC, connC))))
          } else {
            promiseA.complete(IOResult.Success(a))
          }
        })
      }

      IORunLoop.startCancellable(IOForkedStart(ioB, ctx), connB) { either: IOResult<E, B> ->
        either.fold({ error ->
          if (active.getAndSet(false)) { // if an error finishes first, stop the race.
            connA.cancel().unsafeRunAsync { r2 ->
              connC.cancel().unsafeRunAsync { r3 ->
                conn.pop()
                val errorResult = r2.fold({ e2 ->
                  r3.fold({ e3 -> Platform.composeErrors(error, e2, e3) }, { Platform.composeErrors(error, e2) })
                }, {
                  r3.fold({ e3 -> Platform.composeErrors(error, e3) }, { error })
                })
                cb(IOResult.Exception(errorResult))
              }
            }
          } else {
            promiseB.complete(IOResult.Exception(error))
          }
        }, { e ->
          if (active.getAndSet(false)) { // if an error finishes first, stop the race.
            connA.cancel().unsafeRunAsync { r2 ->
              connC.cancel().unsafeRunAsync { r3 ->
                conn.pop()
                cb(IOResult.Error(e))
              }
            }
          } else {
            promiseB.complete(IOResult.Error(e))
          }
        }, { b ->
          if (active.getAndSet(false)) {
            conn.pop()
            cb(IOResult.Success(RaceTriple.Second(IOFiber(promiseA, connA), b, IOFiber(promiseC, connC))))
          } else {
            promiseB.complete(IOResult.Success(b))
          }
        })
      }

      IORunLoop.startCancellable(IOForkedStart(ioC, ctx), connC) { either: IOResult<E, C> ->
        either.fold({ error ->
          if (active.getAndSet(false)) { // if an error finishes first, stop the race.
            connA.cancel().unsafeRunAsync { r2 ->
              connB.cancel().unsafeRunAsync { r3 ->
                conn.pop()
                val errorResult = r2.fold({ e2 ->
                  r3.fold({ e3 -> Platform.composeErrors(error, e2, e3) }, { Platform.composeErrors(error, e2) })
                }, {
                  r3.fold({ e3 -> Platform.composeErrors(error, e3) }, { error })
                })
                cb(IOResult.Exception(errorResult))
              }
            }
          } else {
            promiseC.complete(IOResult.Exception(error))
          }
        }, { e ->
          if (active.getAndSet(false)) { // if an error finishes first, stop the race.
            connA.cancel().unsafeRunAsync { r2 ->
              connB.cancel().unsafeRunAsync { r3 ->
                conn.pop()
                //
                cb(IOResult.Error(e))
              }
            }
          } else {
            promiseC.complete(IOResult.Error(e))
          }
        }, { c ->
          if (active.getAndSet(false)) {
            conn.pop()
            cb(IOResult.Success(RaceTriple.Third(IOFiber(promiseA, connA), IOFiber(promiseB, connB), c)))
          } else {
            promiseC.complete(IOResult.Success(c))
          }
        })
      }
    }
}

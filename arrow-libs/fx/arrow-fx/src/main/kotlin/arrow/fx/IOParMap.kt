package arrow.fx

import arrow.core.Either
import arrow.core.Left
import arrow.core.None
import arrow.core.Option
import arrow.core.Right
import arrow.core.Tuple2
import arrow.core.Tuple3
import arrow.core.Tuple4
import arrow.core.Tuple5
import arrow.core.Tuple6
import arrow.core.Tuple7
import arrow.core.Tuple8
import arrow.core.Tuple9
import arrow.core.extensions.option.applicative.applicative
import arrow.core.extensions.option.applicativeError.handleError
import arrow.fx.internal.AtomicBooleanW
import arrow.fx.internal.AtomicRefW
import arrow.core.nonFatalOrThrow
import arrow.core.none
import arrow.core.some
import arrow.fx.internal.IOForkedStart
import arrow.fx.internal.Platform
import kotlin.coroutines.CoroutineContext

/** Mix-in to enable `parMapN` N-arity on IO's companion directly. */
interface IOParMap {

  @Deprecated("This API is not consistent with others within Arrow, see version with Tuple instead of function params")
  fun <EE, A, B, C> parMapN(fa: IOOf<EE, A>, fb: IOOf<EE, B>, f: (A, B) -> C): IO<EE, C> =
    parMapN(IODispatchers.CommonPool, fa, fb, f)

  /**
   * @see parMapN
   */
  fun <EE, A, B, C> parMapN(fa: IOOf<EE, A>, fb: IOOf<EE, B>, f: (Tuple2<A, B>) -> C): IO<EE, C> =
    parMapN(IODispatchers.CommonPool, fa, fb, f)

  /**
   * @see parTupledN
   */
  fun <EE, A, B> parTupledN(fa: IOOf<EE, A>, fb: IOOf<EE, B>): IO<EE, Tuple2<A, B>> =
    parTupledN(IODispatchers.CommonPool, fa, fb)

  @Deprecated("This API is not consistent with others within Arrow, see version with Tuple instead of function params")
  fun <EE, A, B, C, D> parMapN(fa: IOOf<EE, A>, fb: IOOf<EE, B>, fc: IOOf<EE, C>, f: (A, B, C) -> D): IO<EE, D> =
    parMapN(IODispatchers.CommonPool, fa, fb, fc, f)

  /**
   * @see parMapN
   */
  fun <EE, A, B, C, D> parMapN(fa: IOOf<EE, A>, fb: IOOf<EE, B>, fc: IOOf<EE, C>, f: (Tuple3<A, B, C>) -> D): IO<EE, D> =
    parMapN(IODispatchers.CommonPool, fa, fb, fc, f)

  /**
   * @see parTupledN
   */
  fun <EE, A, B, C> parTupledN(fa: IOOf<EE, A>, fb: IOOf<EE, B>, fc: IOOf<EE, C>): IO<EE, Tuple3<A, B, C>> =
    parTupledN(IODispatchers.CommonPool, fa, fb, fc)

  @Deprecated("This API is not consistent with others within Arrow, see version with Tuple instead of function params")
  fun <EE, A, B, C, D, E> parMapN(fa: IOOf<EE, A>, fb: IOOf<EE, B>, fc: IOOf<EE, C>, fd: IOOf<EE, D>, f: (A, B, C, D) -> E): IO<EE, E> =
    parMapN(IODispatchers.CommonPool, fa, fb, fc, fd, f)

  /**
   * @see parMapN
   */
  fun <EE, A, B, C, D, E> parMapN(
    fa: IOOf<EE, A>,
    fb: IOOf<EE, B>,
    fc: IOOf<EE, C>,
    fd: IOOf<EE, D>,
    f: (Tuple4<A, B, C, D>) -> E
  ): IO<EE, E> =
    parMapN(IODispatchers.CommonPool, fa, fb, fc, fd, f)

  /**
   * @see parTupledN
   */
  fun <EE, A, B, C, D> parTupledN(
    fa: IOOf<EE, A>,
    fb: IOOf<EE, B>,
    fc: IOOf<EE, C>,
    fd: IOOf<EE, D>
  ): IO<EE, Tuple4<A, B, C, D>> =
    parTupledN(IODispatchers.CommonPool, fa, fb, fc, fd)

  @Deprecated("This API is not consistent with others within Arrow, see version with Tuple instead of function params")
  fun <EE, A, B, C, D, E, G> parMapN(fa: IOOf<EE, A>, fb: IOOf<EE, B>, fc: IOOf<EE, C>, fd: IOOf<EE, D>, fe: IOOf<EE, E>, f: (A, B, C, D, E) -> G): IO<EE, G> =
    parMapN(IODispatchers.CommonPool, fa, fb, fc, fd, fe, f)

  /**
   * @see parMapN
   */
  fun <EE, A, B, C, D, E, G> parMapN(
    fa: IOOf<EE, A>,
    fb: IOOf<EE, B>,
    fc: IOOf<EE, C>,
    fd: IOOf<EE, D>,
    fe: IOOf<EE, E>,
    f: (Tuple5<A, B, C, D, E>) -> G
  ): IO<EE, G> =
    parMapN(IODispatchers.CommonPool, fa, fb, fc, fd, fe, f)

  /**
   * @see parTupledN
   */
  fun <EE, A, B, C, D, E> parTupledN(
    fa: IOOf<EE, A>,
    fb: IOOf<EE, B>,
    fc: IOOf<EE, C>,
    fd: IOOf<EE, D>,
    fe: IOOf<EE, E>
  ): IO<EE, Tuple5<A, B, C, D, E>> =
    parTupledN(IODispatchers.CommonPool, fa, fb, fc, fd, fe)

  @Deprecated("This API is not consistent with others within Arrow, see version with Tuple instead of function params")
  fun <EE, A, B, C, D, E, G, H> parMapN(fa: IOOf<EE, A>, fb: IOOf<EE, B>, fc: IOOf<EE, C>, fd: IOOf<EE, D>, fe: IOOf<EE, E>, fg: IOOf<EE, G>, f: (A, B, C, D, E, G) -> H): IO<EE, H> =
    parMapN(IODispatchers.CommonPool, fa, fb, fc, fd, fe, fg, f)

  /**
   * @see parMapN
   */
  fun <EE, A, B, C, D, E, G, H> parMapN(
    fa: IOOf<EE, A>,
    fb: IOOf<EE, B>,
    fc: IOOf<EE, C>,
    fd: IOOf<EE, D>,
    fe: IOOf<EE, E>,
    fg: IOOf<EE, G>,
    f: (Tuple6<A, B, C, D, E, G>) -> H
  ): IO<EE, H> =
    parMapN(IODispatchers.CommonPool, fa, fb, fc, fd, fe, fg, f)

  /**
   * @see parTupledN
   */
  fun <EE, A, B, C, D, E, G> parTupledN(
    fa: IOOf<EE, A>,
    fb: IOOf<EE, B>,
    fc: IOOf<EE, C>,
    fd: IOOf<EE, D>,
    fe: IOOf<EE, E>,
    fg: IOOf<EE, G>
  ): IO<EE, Tuple6<A, B, C, D, E, G>> =
    parTupledN(IODispatchers.CommonPool, fa, fb, fc, fd, fe, fg)

  @Deprecated("This API is not consistent with others within Arrow, see version with Tuple instead of function params")
  fun <EE, A, B, C, D, E, G, H, I> parMapN(fa: IOOf<EE, A>, fb: IOOf<EE, B>, fc: IOOf<EE, C>, fd: IOOf<EE, D>, fe: IOOf<EE, E>, fg: IOOf<EE, G>, fh: IOOf<EE, H>, f: (A, B, C, D, E, G, H) -> I): IO<EE, I> =
    parMapN(IODispatchers.CommonPool, fa, fb, fc, fd, fe, fg, fh, f)

  /**
   * @see parMapN
   */
  fun <EE, A, B, C, D, E, G, H, I> parMapN(
    fa: IOOf<EE, A>,
    fb: IOOf<EE, B>,
    fc: IOOf<EE, C>,
    fd: IOOf<EE, D>,
    fe: IOOf<EE, E>,
    fg: IOOf<EE, G>,
    fh: IOOf<EE, H>,
    f: (Tuple7<A, B, C, D, E, G, H>) -> I
  ): IO<EE, I> =
    parMapN(IODispatchers.CommonPool, fa, fb, fc, fd, fe, fg, fh, f)

  /**
   * @see parTupledN
   */
  fun <EE, A, B, C, D, E, G, H> parTupledN(
    fa: IOOf<EE, A>,
    fb: IOOf<EE, B>,
    fc: IOOf<EE, C>,
    fd: IOOf<EE, D>,
    fe: IOOf<EE, E>,
    fg: IOOf<EE, G>,
    fh: IOOf<EE, H>
  ): IO<EE, Tuple7<A, B, C, D, E, G, H>> =
    parTupledN(IODispatchers.CommonPool, fa, fb, fc, fd, fe, fg, fh)

  @Deprecated("This API is not consistent with others within Arrow, see version with Tuple instead of function params")
  fun <EE, A, B, C, D, E, G, H, I, J> parMapN(fa: IOOf<EE, A>, fb: IOOf<EE, B>, fc: IOOf<EE, C>, fd: IOOf<EE, D>, fe: IOOf<EE, E>, fg: IOOf<EE, G>, fh: IOOf<EE, H>, fi: IOOf<EE, I>, f: (A, B, C, D, E, G, H, I) -> J): IO<EE, J> =
    parMapN(IODispatchers.CommonPool, fa, fb, fc, fd, fe, fg, fh, fi, f)

  /**
   * @see parMapN
   */
  fun <EE, A, B, C, D, E, G, H, I, J> parMapN(
    fa: IOOf<EE, A>,
    fb: IOOf<EE, B>,
    fc: IOOf<EE, C>,
    fd: IOOf<EE, D>,
    fe: IOOf<EE, E>,
    fg: IOOf<EE, G>,
    fh: IOOf<EE, H>,
    fi: IOOf<EE, I>,
    f: (Tuple8<A, B, C, D, E, G, H, I>) -> J
  ): IO<EE, J> =
    parMapN(IODispatchers.CommonPool, fa, fb, fc, fd, fe, fg, fh, fi, f)

  /**
   * @see parTupledN
   */
  fun <EE, A, B, C, D, E, G, H, I> parTupledN(
    fa: IOOf<EE, A>,
    fb: IOOf<EE, B>,
    fc: IOOf<EE, C>,
    fd: IOOf<EE, D>,
    fe: IOOf<EE, E>,
    fg: IOOf<EE, G>,
    fh: IOOf<EE, H>,
    fi: IOOf<EE, I>
  ): IO<EE, Tuple8<A, B, C, D, E, G, H, I>> =
    parTupledN(IODispatchers.CommonPool, fa, fb, fc, fd, fe, fg, fh, fi)

  @Deprecated("This API is not consistent with others within Arrow, see version with Tuple instead of function params")
  fun <EE, A, B, C, D, E, G, H, I, J, K> parMapN(fa: IOOf<EE, A>, fb: IOOf<EE, B>, fc: IOOf<EE, C>, fd: IOOf<EE, D>, fe: IOOf<EE, E>, fg: IOOf<EE, G>, fh: IOOf<EE, H>, fi: IOOf<EE, I>, fj: IOOf<EE, J>, f: (A, B, C, D, E, G, H, I, J) -> K): IO<EE, K> =
    parMapN(IODispatchers.CommonPool, fa, fb, fc, fd, fe, fg, fh, fi, fj, f)

  /**
   * @see parMapN
   */
  fun <EE, A, B, C, D, E, G, H, I, J, K> parMapN(
    fa: IOOf<EE, A>,
    fb: IOOf<EE, B>,
    fc: IOOf<EE, C>,
    fd: IOOf<EE, D>,
    fe: IOOf<EE, E>,
    fg: IOOf<EE, G>,
    fh: IOOf<EE, H>,
    fi: IOOf<EE, I>,
    fj: IOOf<EE, J>,
    f: (Tuple9<A, B, C, D, E, G, H, I, J>) -> K
  ): IO<EE, K> =
    parMapN(IODispatchers.CommonPool, fa, fb, fc, fd, fe, fg, fh, fi, fj, f)

  /**
   * @see parTupledN
   */
  fun <EE, A, B, C, D, E, G, H, I, J> parTupledN(
    fa: IOOf<EE, A>,
    fb: IOOf<EE, B>,
    fc: IOOf<EE, C>,
    fd: IOOf<EE, D>,
    fe: IOOf<EE, E>,
    fg: IOOf<EE, G>,
    fh: IOOf<EE, H>,
    fi: IOOf<EE, I>,
    fj: IOOf<EE, J>
  ): IO<EE, Tuple9<A, B, C, D, E, G, H, I, J>> =
    parTupledN(IODispatchers.CommonPool, fa, fb, fc, fd, fe, fg, fh, fi, fj)

  @Deprecated("This API is not consistent with others within Arrow, see version with Tuple instead of function params")
  fun <EE, A, B, C> parMapN(ctx: CoroutineContext, fa: IOOf<EE, A>, fb: IOOf<EE, B>, f: (A, B) -> C): IO<EE, C> =
    parMapN(ctx, fa, fb) { (a, b) -> f(a, b) }

  fun <EE, A, B, C> parMapN(ctx: CoroutineContext, fa: IOOf<EE, A>, fb: IOOf<EE, B>, f: (Tuple2<A, B>) -> C): IO<EE, C> =
    parTupledN(ctx, fa, fb).map(f)

  fun <EE, A, B> parTupledN(ctx: CoroutineContext, fa: IOOf<EE, A>, fb: IOOf<EE, B>): IO<EE, Tuple2<A, B>> =
    IO.Async(true) { conn, cb ->
      // Used to store Throwable, Either<A, B> or empty (null). (No sealed class used for a slightly better performing ParMap2)
      val state = AtomicRefW<Any?>(null)

      val connA = IOConnection()
      val connB = IOConnection()

      conn.pushPair(connA, connB)

      fun complete(a: A, b: B) {
        conn.pop()
        cb(try {
          IOResult.Success(Tuple2(a, b))
        } catch (e: Throwable) {
          IOResult.Exception(e.nonFatalOrThrow())
        })
      }

      fun sendException(other: IOConnection, e: Throwable) = when (state.getAndSet(e)) {
        is Throwable -> Unit // Do nothing we already finished TODO replace with active field
        else -> other.cancel().unsafeRunAsync { r ->
          conn.pop()
          // TODO if `r` is an exception send it to the asyncErrorHandler
          cb(IOResult.Exception(r.fold({ e2 -> Platform.composeErrors(e, e2) }, { e })))
        }
      }

      fun sendError(other: IOConnection, e: EE) = when (state.getAndSet(e)) {
        is Throwable -> Unit
        else -> other.cancel().unsafeRunAsync { r ->
          conn.pop()
          cb(IOResult.Error(e))
          // TODO if `r` is an exception send it to the asyncErrorHandler
        }
      }

      IORunLoop.startCancellable(IOForkedStart(fa, ctx), connA) { resultA ->
        resultA.fold({ e ->
          sendException(connB, e)
        }, { e ->
          sendError(connB, e)
        }, { a ->
          when (val oldState = state.getAndSet(Left(a))) {
            null -> Unit // Wait for B
            is Throwable -> Unit // ParMapN already failed and A was cancelled.
            is Either.Left<*> -> Unit // Already state.getAndSet
            is Either.Right<*> -> complete(a, (oldState as Either.Right<B>).b)
          }
        })
      }

      IORunLoop.startCancellable(IOForkedStart(fb, ctx), connB) { resultB ->
        resultB.fold({ e ->
          sendException(connA, e)
        }, { e ->
          sendError(connB, e)
        }, { b ->
          when (val oldState = state.getAndSet(Right(b))) {
            null -> Unit // Wait for A
            is Throwable -> Unit // ParMapN already failed and B was cancelled.
            is Either.Right<*> -> Unit // IO cannot finish twice
            is Either.Left<*> -> complete((oldState as Either.Left<A>).a, b)
          }
        })
      }
    }

  @Deprecated("This API is not consistent with others within Arrow, see version with Tuple instead of function params")
  fun <EE, A, B, C, D> parMapN(ctx: CoroutineContext, fa: IOOf<EE, A>, fb: IOOf<EE, B>, fc: IOOf<EE, C>, f: (A, B, C) -> D): IO<EE, D> =
    parMapN(ctx, fa, fb, fc) { (a, b, c) -> f(a, b, c) }

  fun <EE, A, B, C, D> parMapN(ctx: CoroutineContext, fa: IOOf<EE, A>, fb: IOOf<EE, B>, fc: IOOf<EE, C>, f: (Tuple3<A, B, C>) -> D): IO<EE, D> =
    parTupledN(ctx, fa, fb, fc).map(f)

  fun <EE, A, B, C> parTupledN(ctx: CoroutineContext, fa: IOOf<EE, A>, fb: IOOf<EE, B>, fc: IOOf<EE, C>): IO<EE, Tuple3<A, B, C>> = IO.Async(true) { conn, cb ->
    val state: AtomicRefW<Option<Tuple3<Option<A>, Option<B>, Option<C>>>> = AtomicRefW(None)
    val active = AtomicBooleanW(true)

    val connA = IOConnection()
    val connB = IOConnection()
    val connC = IOConnection()

    // Composite cancellable that cancels all ops.
    // NOTE: conn.pop() called when cb gets called below in complete.
    conn.push(connA.cancel(), connB.cancel(), connC.cancel())

    fun complete(a: A, b: B, c: C) {
      conn.pop()
      val result: IOResult<EE, Tuple3<A, B, C>> = try {
        IOResult.Success(Tuple3(a, b, c))
      } catch (e: Throwable) {
        IOResult.Exception(e.nonFatalOrThrow())
      }
      cb(result)
    }

    fun tryComplete(result: Option<Tuple3<Option<A>, Option<B>, Option<C>>>): Unit =
      result.fold({ Unit }, { (a, b, c) -> Option.applicative().map(a, b, c) { (a, b, c) -> complete(a, b, c) } })

    fun sendException(other: IOConnection, other2: IOConnection, e: Throwable) =
      if (active.getAndSet(false)) { // We were already cancelled so don't do anything.
        other.cancel().unsafeRunAsync { r1 ->
          other2.cancel().unsafeRunAsync { r2 ->
            conn.pop()
            cb(IOResult.Exception(r1.fold({ e2 ->
              r2.fold({ e3 -> Platform.composeErrors(e, e2, e3) }, { Platform.composeErrors(e, e2) })
            }, {
              r2.fold({ e3 -> Platform.composeErrors(e, e3) }, { e })
            })))
          }
        }
      } else Unit

    fun sendError(other: IOConnection, other2: IOConnection, e: EE) =
      if (active.getAndSet(false)) { // We were already cancelled so don't do anything.
        other.cancel().unsafeRunAsync { r1 ->
          other2.cancel().unsafeRunAsync { r2 ->
            conn.pop()
            // Send r1 & r2 to asyncErrorHandler if cancellation failed
            cb(IOResult.Error(e))
          }
        }
      } else Unit

    IORunLoop.startCancellable(IOForkedStart(fa, ctx), connA) { resultA ->
      resultA.fold({ e ->
        sendException(connB, connC, e)
      }, { e ->
        sendError(connB, connC, e)
      }, { a ->
        tryComplete(state.updateAndGet { current ->
          current
            .map { it.copy(a = a.some()) }
            .handleError { Tuple3(a.some(), none(), none()) }
        })
      })
    }

    IORunLoop.startCancellable(IOForkedStart(fb, ctx), connB) { resultB ->
      resultB.fold({ e ->
        sendException(connA, connC, e)
      }, { e ->
        sendError(connB, connC, e)
      }, { b ->
        tryComplete(state.updateAndGet { current ->
          current
            .map { it.copy(b = b.some()) }
            .handleError { Tuple3(none(), b.some(), none()) }
        })
      })
    }

    IORunLoop.startCancellable(IOForkedStart(fc, ctx), connC) { resultC ->
      resultC.fold({ e ->
        sendException(connA, connB, e)
      }, { e ->
        sendError(connB, connC, e)
      }, { c ->
        tryComplete(state.updateAndGet { current ->
          current
            .map { it.copy(c = c.some()) }
            .handleError { Tuple3(none(), none(), c.some()) }
        })
      })
    }
  }

  /**
   * @see parMapN
   */
  @Deprecated("This API is not consistent with others within Arrow, see version with Tuple instead of function params")
  fun <EE, A, B, C, D, E> parMapN(
    ctx: CoroutineContext,
    fa: IOOf<EE, A>,
    fb: IOOf<EE, B>,
    fc: IOOf<EE, C>,
    fd: IOOf<EE, D>,
    f: (A, B, C, D) -> E
  ): IO<EE, E> =
    parMapN(ctx,
      parMapN(ctx, fa, fb, ::Tuple2),
      parMapN(ctx, fc, fd, ::Tuple2)
    ) { (a, b), (c, d) ->
      f(a, b, c, d)
    }

  fun <EE, A, B, C, D, E> parMapN(
    ctx: CoroutineContext,
    fa: IOOf<EE, A>,
    fb: IOOf<EE, B>,
    fc: IOOf<EE, C>,
    fd: IOOf<EE, D>,
    f: (Tuple4<A, B, C, D>) -> E
  ): IO<EE, E> = parTupledN(ctx, fa, fb, fc, fd).map(f)

  fun <EE, A, B, C, D> parTupledN(
    ctx: CoroutineContext,
    fa: IOOf<EE, A>,
    fb: IOOf<EE, B>,
    fc: IOOf<EE, C>,
    fd: IOOf<EE, D>
  ): IO<EE, Tuple4<A, B, C, D>> =
    parMapN(ctx,
      parTupledN(ctx, fa, fb),
      parTupledN(ctx, fc, fd)
    ) { (a, b), (c, d) ->
      Tuple4(a, b, c, d)
    }

  /**
   * @see parMapN
   */
  @Deprecated("This API is not consistent with others within Arrow, see version with Tuple instead of function params")
  fun <EE, A, B, C, D, E, G> parMapN(
    ctx: CoroutineContext,
    fa: IOOf<EE, A>,
    fb: IOOf<EE, B>,
    fc: IOOf<EE, C>,
    fd: IOOf<EE, D>,
    fe: IOOf<EE, E>,
    f: (A, B, C, D, E) -> G
  ): IO<EE, G> =
    parMapN(ctx,
      parMapN(ctx, fa, fb, fc, ::Tuple3),
      parMapN(ctx, fd, fe, ::Tuple2)
    ) { (a, b, c), (d, e) ->
      f(a, b, c, d, e)
    }

  fun <EE, A, B, C, D, E, G> parMapN(
    ctx: CoroutineContext,
    fa: IOOf<EE, A>,
    fb: IOOf<EE, B>,
    fc: IOOf<EE, C>,
    fd: IOOf<EE, D>,
    fe: IOOf<EE, E>,
    f: (Tuple5<A, B, C, D, E>) -> G
  ): IO<EE, G> = parTupledN(ctx, fa, fb, fc, fd, fe).map(f)

  fun <EE, A, B, C, D, E> parTupledN(
    ctx: CoroutineContext,
    fa: IOOf<EE, A>,
    fb: IOOf<EE, B>,
    fc: IOOf<EE, C>,
    fd: IOOf<EE, D>,
    fe: IOOf<EE, E>
  ): IO<EE, Tuple5<A, B, C, D, E>> =
    parMapN(ctx,
      parTupledN(ctx, fa, fb, fc),
      parTupledN(ctx, fd, fe)
    ) { (abc, de) ->
      val (a, b, c) = abc
      val (d, e) = de
      Tuple5(a, b, c, d, e)
    }

  /**
   * @see parMapN
   */
  @Deprecated("This API is not consistent with others within Arrow, see version with Tuple instead of function params")
  fun <EE, A, B, C, D, E, G, H> parMapN(
    ctx: CoroutineContext,
    fa: IOOf<EE, A>,
    fb: IOOf<EE, B>,
    fc: IOOf<EE, C>,
    fd: IOOf<EE, D>,
    fe: IOOf<EE, E>,
    fg: IOOf<EE, G>,
    f: (A, B, C, D, E, G) -> H
  ): IO<EE, H> =
    parMapN(ctx,
      parMapN(ctx, fa, fb, fc, ::Tuple3),
      parMapN(ctx, fd, fe, fg, ::Tuple3)
    ) { (a, b, c), (d, e, g) ->
      f(a, b, c, d, e, g)
    }

  fun <EE, A, B, C, D, E, G, H> parMapN(
    ctx: CoroutineContext,
    fa: IOOf<EE, A>,
    fb: IOOf<EE, B>,
    fc: IOOf<EE, C>,
    fd: IOOf<EE, D>,
    fe: IOOf<EE, E>,
    fg: IOOf<EE, G>,
    f: (Tuple6<A, B, C, D, E, G>) -> H
  ): IO<EE, H> = parTupledN(ctx, fa, fb, fc, fd, fe, fg).map(f)

  fun <EE, A, B, C, D, E, G> parTupledN(
    ctx: CoroutineContext,
    fa: IOOf<EE, A>,
    fb: IOOf<EE, B>,
    fc: IOOf<EE, C>,
    fd: IOOf<EE, D>,
    fe: IOOf<EE, E>,
    fg: IOOf<EE, G>
  ): IO<EE, Tuple6<A, B, C, D, E, G>> =
    parMapN(ctx,
      parTupledN(ctx, fa, fb, fc),
      parTupledN(ctx, fd, fe, fg)
    ) { (abc, deg) ->
      val (a, b, c) = abc
      val (d, e, g) = deg
      Tuple6(a, b, c, d, e, g)
    }

  /**
   * @see parMapN
   */
  @Deprecated("This API is not consistent with others within Arrow, see version with Tuple instead of function params")
  fun <EE, A, B, C, D, E, G, H, I> parMapN(
    ctx: CoroutineContext,
    fa: IOOf<EE, A>,
    fb: IOOf<EE, B>,
    fc: IOOf<EE, C>,
    fd: IOOf<EE, D>,
    fe: IOOf<EE, E>,
    fg: IOOf<EE, G>,
    fh: IOOf<EE, H>,
    f: (A, B, C, D, E, G, H) -> I
  ): IO<EE, I> =
    parMapN(ctx,
      parMapN(ctx, fa, fb, fc, ::Tuple3),
      parMapN(ctx, fd, fe, ::Tuple2),
      parMapN(ctx, fg, fh, ::Tuple2)
    ) { (a, b, c), (d, e), (g, h) ->
      f(a, b, c, d, e, g, h)
    }

  fun <EE, A, B, C, D, E, G, H, I> parMapN(
    ctx: CoroutineContext,
    fa: IOOf<EE, A>,
    fb: IOOf<EE, B>,
    fc: IOOf<EE, C>,
    fd: IOOf<EE, D>,
    fe: IOOf<EE, E>,
    fg: IOOf<EE, G>,
    fh: IOOf<EE, H>,
    f: (Tuple7<A, B, C, D, E, G, H>) -> I
  ): IO<EE, I> = parTupledN(ctx, fa, fb, fc, fd, fe, fg, fh).map(f)

  fun <EE, A, B, C, D, E, G, H> parTupledN(
    ctx: CoroutineContext,
    fa: IOOf<EE, A>,
    fb: IOOf<EE, B>,
    fc: IOOf<EE, C>,
    fd: IOOf<EE, D>,
    fe: IOOf<EE, E>,
    fg: IOOf<EE, G>,
    fh: IOOf<EE, H>
  ): IO<EE, Tuple7<A, B, C, D, E, G, H>> =
    parMapN(ctx,
      parTupledN(ctx, fa, fb, fc),
      parTupledN(ctx, fd, fe),
      parTupledN(ctx, fg, fh)
    ) { (abc, de, gh) ->
      val (a, b, c) = abc
      val (d, e) = de
      val (g, h) = gh
      Tuple7(a, b, c, d, e, g, h)
    }

  /**
   * @see parMapN
   */
  @Deprecated("This API is not consistent with others within Arrow, see version with Tuple instead of function params")
  fun <EE, A, B, C, D, E, G, H, I, J> parMapN(
    ctx: CoroutineContext,
    fa: IOOf<EE, A>,
    fb: IOOf<EE, B>,
    fc: IOOf<EE, C>,
    fd: IOOf<EE, D>,
    fe: IOOf<EE, E>,
    fg: IOOf<EE, G>,
    fh: IOOf<EE, H>,
    fi: IOOf<EE, I>,
    f: (A, B, C, D, E, G, H, I) -> J
  ): IO<EE, J> =
    parMapN(ctx,
      parMapN(ctx, fa, fb, fc, ::Tuple3),
      parMapN(ctx, fd, fe, fg, ::Tuple3),
      parMapN(ctx, fh, fi, ::Tuple2)
    ) { (a, b, c), (d, e, g), (h, i) ->
      f(a, b, c, d, e, g, h, i)
    }

  fun <EE, A, B, C, D, E, G, H, I, J> parMapN(
    ctx: CoroutineContext,
    fa: IOOf<EE, A>,
    fb: IOOf<EE, B>,
    fc: IOOf<EE, C>,
    fd: IOOf<EE, D>,
    fe: IOOf<EE, E>,
    fg: IOOf<EE, G>,
    fh: IOOf<EE, H>,
    fi: IOOf<EE, I>,
    f: (Tuple8<A, B, C, D, E, G, H, I>) -> J
  ): IO<EE, J> = parTupledN(ctx, fa, fb, fc, fd, fe, fg, fh, fi).map(f)

  fun <EE, A, B, C, D, E, G, H, I> parTupledN(
    ctx: CoroutineContext,
    fa: IOOf<EE, A>,
    fb: IOOf<EE, B>,
    fc: IOOf<EE, C>,
    fd: IOOf<EE, D>,
    fe: IOOf<EE, E>,
    fg: IOOf<EE, G>,
    fh: IOOf<EE, H>,
    fi: IOOf<EE, I>
  ): IO<EE, Tuple8<A, B, C, D, E, G, H, I>> =
    parMapN(ctx,
      parTupledN(ctx, fa, fb, fc),
      parTupledN(ctx, fd, fe, fg),
      parTupledN(ctx, fh, fi)
    ) { (abc, deg, hi) ->
      val (a, b, c) = abc
      val (d, e, g) = deg
      val (h, i) = hi
      Tuple8(a, b, c, d, e, g, h, i)
    }

  /**
   * @see parMapN
   */
  @Deprecated("This API is not consistent with others within Arrow, see version with Tuple instead of function params")
  fun <EE, A, B, C, D, E, G, H, I, J, K> parMapN(
    ctx: CoroutineContext,
    fa: IOOf<EE, A>,
    fb: IOOf<EE, B>,
    fc: IOOf<EE, C>,
    fd: IOOf<EE, D>,
    fe: IOOf<EE, E>,
    fg: IOOf<EE, G>,
    fh: IOOf<EE, H>,
    fi: IOOf<EE, I>,
    fj: IOOf<EE, J>,
    f: (A, B, C, D, E, G, H, I, J) -> K
  ): IO<EE, K> =
    parMapN(ctx,
      parMapN(ctx, fa, fb, fc, ::Tuple3),
      parMapN(ctx, fd, fe, fg, ::Tuple3),
      parMapN(ctx, fh, fi, fj, ::Tuple3)
    ) { (a, b, c), (d, e, g), (h, i, j) ->
      f(a, b, c, d, e, g, h, i, j)
    }

  fun <EE, A, B, C, D, E, G, H, I, J, K> parMapN(
    ctx: CoroutineContext,
    fa: IOOf<EE, A>,
    fb: IOOf<EE, B>,
    fc: IOOf<EE, C>,
    fd: IOOf<EE, D>,
    fe: IOOf<EE, E>,
    fg: IOOf<EE, G>,
    fh: IOOf<EE, H>,
    fi: IOOf<EE, I>,
    fj: IOOf<EE, J>,
    f: (Tuple9<A, B, C, D, E, G, H, I, J>) -> K
  ): IO<EE, K> = parTupledN(ctx, fa, fb, fc, fd, fe, fg, fh, fi, fj).map(f)

  fun <EE, A, B, C, D, E, G, H, I, J> parTupledN(
    ctx: CoroutineContext,
    fa: IOOf<EE, A>,
    fb: IOOf<EE, B>,
    fc: IOOf<EE, C>,
    fd: IOOf<EE, D>,
    fe: IOOf<EE, E>,
    fg: IOOf<EE, G>,
    fh: IOOf<EE, H>,
    fi: IOOf<EE, I>,
    fj: IOOf<EE, J>
  ): IO<EE, Tuple9<A, B, C, D, E, G, H, I, J>> =
    parMapN(ctx,
      parTupledN(ctx, fa, fb, fc),
      parTupledN(ctx, fd, fe, fg),
      parTupledN(ctx, fh, fi, fj)
    ) { (abc, deg, hij) ->
      val (a, b, c) = abc
      val (d, e, g) = deg
      val (h, i, j) = hij
      Tuple9(a, b, c, d, e, g, h, i, j)
    }
}

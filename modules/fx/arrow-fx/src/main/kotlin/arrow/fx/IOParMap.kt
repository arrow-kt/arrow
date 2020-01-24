package arrow.fx

import arrow.core.Either
import arrow.core.Left
import arrow.core.Option
import arrow.core.Right
import arrow.core.Tuple2
import arrow.core.Tuple3
import arrow.core.extensions.option.applicative.applicative
import arrow.core.internal.AtomicRefW
import arrow.core.nonFatalOrThrow
import arrow.core.none
import arrow.core.some
import arrow.fx.internal.IOForkedStart
import arrow.fx.internal.Platform
import kotlin.coroutines.CoroutineContext
import arrow.core.extensions.option.applicativeError.handleError
import arrow.core.internal.AtomicBooleanW

/** Mix-in to enable `parMapN` 2-arity on IO's companion directly. */
interface IOParMap {

  fun <EE, A, B, C> parMapN(fa: IOOf<EE, A>, fb: IOOf<EE, B>, f: (A, B) -> C): IO<EE, C> =
    IO.parMapN(IODispatchers.CommonPool, fa, fb, f)

  fun <EE, A, B, C, D> parMapN(fa: IOOf<EE, A>, fb: IOOf<EE, B>, fc: IOOf<EE, C>, f: (A, B, C) -> D): IO<EE, D> =
    IO.parMapN(IODispatchers.CommonPool, fa, fb, fc, f)

  fun <EE, A, B, C, D, E> parMapN(fa: IOOf<EE, A>, fb: IOOf<EE, B>, fc: IOOf<EE, C>, fd: IOOf<EE, D>, f: (A, B, C, D) -> E): IOOf<EE, E> =
    IO.parMapN(IODispatchers.CommonPool, fa, fb, fc, fd, f)

  /**
   * @see parMapN
   */
  fun <EE, A, B, C, D, E, G> parMapN(fa: IOOf<EE, A>, fb: IOOf<EE, B>, fc: IOOf<EE, C>, fd: IOOf<EE, D>, fe: IOOf<EE, E>, f: (A, B, C, D, E) -> G): IO<EE, G> =
    IO.parMapN(IODispatchers.CommonPool, fa, fb, fc, fd, fe, f)

  /**
   * @see parMapN
   */
  fun <EE, A, B, C, D, E, G, H> parMapN(fa: IOOf<EE, A>, fb: IOOf<EE, B>, fc: IOOf<EE, C>, fd: IOOf<EE, D>, fe: IOOf<EE, E>, fg: IOOf<EE, G>, f: (A, B, C, D, E, G) -> H): IO<EE, H> =
    IO.parMapN(IODispatchers.CommonPool, fa, fb, fc, fd, fe, fg, f)

  /**
   * @see parMapN
   */
  fun <EE, A, B, C, D, E, G, H, I> parMapN(fa: IOOf<EE, A>, fb: IOOf<EE, B>, fc: IOOf<EE, C>, fd: IOOf<EE, D>, fe: IOOf<EE, E>, fg: IOOf<EE, G>, fh: IOOf<EE, H>, f: (A, B, C, D, E, G, H) -> I): IO<EE, I> =
    IO.parMapN(IODispatchers.CommonPool, fa, fb, fc, fd, fe, fg, fh, f)

  /**
   * @see parMapN
   */
  fun <EE, A, B, C, D, E, G, H, I, J> parMapN(fa: IOOf<EE, A>, fb: IOOf<EE, B>, fc: IOOf<EE, C>, fd: IOOf<EE, D>, fe: IOOf<EE, E>, fg: IOOf<EE, G>, fh: IOOf<EE, H>, fi: IOOf<EE, I>, f: (A, B, C, D, E, G, H, I) -> J): IO<EE, J> =
    IO.parMapN(IODispatchers.CommonPool, fa, fb, fc, fd, fe, fg, fh, fi, f)

  /**
   * @see parMapN
   */
  fun <EE, A, B, C, D, E, G, H, I, J, K> parMapN(fa: IOOf<EE, A>, fb: IOOf<EE, B>, fc: IOOf<EE, C>, fd: IOOf<EE, D>, fe: IOOf<EE, E>, fg: IOOf<EE, G>, fh: IOOf<EE, H>, fi: IOOf<EE, I>, fj: IOOf<EE, J>, f: (A, B, C, D, E, G, H, I, J) -> K): IO<EE, K> =
    IO.parMapN(IODispatchers.CommonPool, fa, fb, fc, fd, fe, fg, fh, fi, fj, f)

  fun <E, A, B, C> parMapN(ctx: CoroutineContext, fa: IOOf<E, A>, fb: IOOf<E, B>, f: (A, B) -> C): IO<E, C> =
    IO.Async(true) { conn, cb ->
      // Used to store Throwable, Either<A, B> or empty (null). (No sealed class used for a slightly better performing ParMap2)
      val state = AtomicRefW<Any?>(null)

      val connA = IOConnection()
      val connB = IOConnection()

      conn.pushPair(connA, connB)

      fun complete(a: A, b: B) {
        conn.pop()
        cb(try {
          IOResult.Success(f(a, b))
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

      fun sendError(other: IOConnection, e: E) = when (state.getAndSet(e)) {
        is Throwable -> Unit
        else -> other.cancel().fix().unsafeRunAsync { r ->
          conn.pop()
          cb(IOResult.Error(e))
          // TODO if `r` is an exception send it to the asyncErrorHandler
        }
      }

      IORunLoop.startCancelable(IOForkedStart(fa, ctx), connA) { resultA ->
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

      IORunLoop.startCancelable(IOForkedStart(fb, ctx), connB) { resultB ->
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

  fun <E, A, B, C, D> parMapN(ctx: CoroutineContext, fa: IOOf<E, A>, fb: IOOf<E, B>, fc: IOOf<E, C>, f: (A, B, C) -> D): IO<E, D> =
    IO.Async(true) { conn, cb ->

      val state: AtomicRefW<Option<Tuple3<Option<A>, Option<B>, Option<C>>>> = AtomicRefW(none())
      val active = AtomicBooleanW(true)

      val connA = IOConnection()
      val connB = IOConnection()
      val connC = IOConnection()

      // Composite cancelable that cancels all ops.
      // NOTE: conn.pop() called when cb gets called below in complete.
      conn.push(connA.cancel(), connB.cancel(), connC.cancel())

      fun complete(a: A, b: B, c: C) {
        conn.pop()
        val result: IOResult<E, D> = try {
          IOResult.Success(f(a, b, c))
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

      fun sendError(other: IOConnection, other2: IOConnection, e: E) =
        if (active.getAndSet(false)) { // We were already cancelled so don't do anything.
          other.cancel().fix().unsafeRunAsync { r1 ->
            other2.cancel().fix().unsafeRunAsync { r2 ->
              conn.pop()
              // Send r1 & r2 to asyncErrorHandler if cancelation failed
              cb(IOResult.Error(e))
            }
          }
        } else Unit

      IORunLoop.startCancelable(IOForkedStart(fa, ctx), connA) { resultA ->
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

      IORunLoop.startCancelable(IOForkedStart(fb, ctx), connB) { resultB ->
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

      IORunLoop.startCancelable(IOForkedStart(fc, ctx), connC) { resultC ->
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
  fun <EE, A, B, C, D, E> parMapN(
    ctx: CoroutineContext,
    fa: IOOf<EE, A>,
    fb: IOOf<EE, B>,
    fc: IOOf<EE, C>,
    fd: IOOf<EE, D>,
    f: (A, B, C, D) -> E
  ): IOOf<EE, E> = IO.parMapN(ctx,
    IO.parMapN(ctx, fa, fb, ::Tuple2),
    IO.parMapN(ctx, fc, fd, ::Tuple2)
  ) { (a, b), (c, d) ->
    f(a, b, c, d)
  }

  /**
   * @see parMapN
   */
  fun <EE, A, B, C, D, E, G> parMapN(
    ctx: CoroutineContext,
    fa: IOOf<EE, A>,
    fb: IOOf<EE, B>,
    fc: IOOf<EE, C>,
    fd: IOOf<EE, D>,
    fe: IOOf<EE, E>,
    f: (A, B, C, D, E) -> G
  ): IO<EE, G> = IO.parMapN(ctx,
    IO.parMapN(ctx, fa, fb, fc, ::Tuple3),
    IO.parMapN(ctx, fd, fe, ::Tuple2)
  ) { (a, b, c), (d, e) ->
    f(a, b, c, d, e)
  }

  /**
   * @see parMapN
   */
  fun <EE, A, B, C, D, E, G, H> parMapN(
    ctx: CoroutineContext,
    fa: IOOf<EE, A>,
    fb: IOOf<EE, B>,
    fc: IOOf<EE, C>,
    fd: IOOf<EE, D>,
    fe: IOOf<EE, E>,
    fg: IOOf<EE, G>,
    f: (A, B, C, D, E, G) -> H
  ): IO<EE, H> = IO.parMapN(ctx,
    IO.parMapN(ctx, fa, fb, fc, ::Tuple3),
    IO.parMapN(ctx, fd, fe, fg, ::Tuple3)
  ) { (a, b, c), (d, e, g) ->
    f(a, b, c, d, e, g)
  }

  /**
   * @see parMapN
   */
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
  ): IO<EE, I> = IO.parMapN(ctx,
    IO.parMapN(ctx, fa, fb, fc, ::Tuple3),
    IO.parMapN(ctx, fd, fe, ::Tuple2),
    IO.parMapN(ctx, fg, fh, ::Tuple2)) { (a, b, c), (d, e), (g, h) ->
    f(a, b, c, d, e, g, h)
  }

  /**
   * @see parMapN
   */
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
  ): IO<EE, J> = IO.parMapN(ctx,
    IO.parMapN(ctx, fa, fb, fc, ::Tuple3),
    IO.parMapN(ctx, fd, fe, fg, ::Tuple3),
    IO.parMapN(ctx, fh, fi, ::Tuple2)) { (a, b, c), (d, e, g), (h, i) ->
    f(a, b, c, d, e, g, h, i)
  }

  /**
   * @see parMapN
   */
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
  ): IO<EE, K> = IO.parMapN(ctx,
    IO.parMapN(ctx, fa, fb, fc, ::Tuple3),
    IO.parMapN(ctx, fd, fe, fg, ::Tuple3),
    IO.parMapN(ctx, fh, fi, fj, ::Tuple3)) { (a, b, c), (d, e, g), (h, i, j) ->
    f(a, b, c, d, e, g, h, i, j)
  }
}

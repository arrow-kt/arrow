package arrow.fx

import arrow.core.Either
import arrow.core.Left
import arrow.core.None
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

  fun <A, B, C> parMapN(fa: IOOf<A>, fb: IOOf<B>, f: (A, B) -> C): IO<C> =
    IO.parMapN(IODispatchers.CommonPool, fa, fb, f)

  fun <A, B, C, D> parMapN(fa: IOOf<A>, fb: IOOf<B>, fc: IOOf<C>, f: (A, B, C) -> D): IO<D> =
    IO.parMapN(IODispatchers.CommonPool, fa, fb, fc, f)

  fun <A, B, C, D, E> parMapN(fa: IOOf<A>, fb: IOOf<B>, fc: IOOf<C>, fd: IOOf<D>, f: (A, B, C, D) -> E): IOOf<E> =
    IO.parMapN(IODispatchers.CommonPool, fa, fb, fc, fd, f)

  /**
   * @see parMapN
   */
  fun <A, B, C, D, E, G> parMapN(fa: IOOf<A>, fb: IOOf<B>, fc: IOOf<C>, fd: IOOf<D>, fe: IOOf<E>, f: (A, B, C, D, E) -> G): IO<G> =
    IO.parMapN(IODispatchers.CommonPool, fa, fb, fc, fd, fe, f)

  /**
   * @see parMapN
   */
  fun <A, B, C, D, E, G, H> parMapN(fa: IOOf<A>, fb: IOOf<B>, fc: IOOf<C>, fd: IOOf<D>, fe: IOOf<E>, fg: IOOf<G>, f: (A, B, C, D, E, G) -> H): IO<H> =
    IO.parMapN(IODispatchers.CommonPool, fa, fb, fc, fd, fe, fg, f)

  /**
   * @see parMapN
   */
  fun <A, B, C, D, E, G, H, I> parMapN(fa: IOOf<A>, fb: IOOf<B>, fc: IOOf<C>, fd: IOOf<D>, fe: IOOf<E>, fg: IOOf<G>, fh: IOOf<H>, f: (A, B, C, D, E, G, H) -> I): IO<I> =
    IO.parMapN(IODispatchers.CommonPool, fa, fb, fc, fd, fe, fg, fh, f)

  /**
   * @see parMapN
   */
  fun <A, B, C, D, E, G, H, I, J> parMapN(fa: IOOf<A>, fb: IOOf<B>, fc: IOOf<C>, fd: IOOf<D>, fe: IOOf<E>, fg: IOOf<G>, fh: IOOf<H>, fi: IOOf<I>, f: (A, B, C, D, E, G, H, I) -> J): IO<J> =
    IO.parMapN(IODispatchers.CommonPool, fa, fb, fc, fd, fe, fg, fh, fi, f)

  /**
   * @see parMapN
   */
  fun <A, B, C, D, E, G, H, I, J, K> parMapN(fa: IOOf<A>, fb: IOOf<B>, fc: IOOf<C>, fd: IOOf<D>, fe: IOOf<E>, fg: IOOf<G>, fh: IOOf<H>, fi: IOOf<I>, fj: IOOf<J>, f: (A, B, C, D, E, G, H, I, J) -> K): IO<K> =
    IO.parMapN(IODispatchers.CommonPool, fa, fb, fc, fd, fe, fg, fh, fi, fj, f)

  fun <A, B, C> parMapN(ctx: CoroutineContext, fa: IOOf<A>, fb: IOOf<B>, f: (A, B) -> C): IO<C> = IO.Async(true) { conn, cb ->
    // Used to store Throwable, Either<A, B> or empty (null). (No sealed class used for a slightly better performing ParMap2)
    val state = AtomicRefW<Any?>(null)

    val connA = IOConnection()
    val connB = IOConnection()

    conn.pushPair(connA, connB)

    fun complete(a: A, b: B) {
      conn.pop()
      cb(try {
        Either.Right(f(a, b))
      } catch (e: Throwable) {
        Either.Left(e.nonFatalOrThrow())
      })
    }

    fun sendError(other: IOConnection, e: Throwable) = when (state.getAndSet(e)) {
      is Throwable -> Unit // Do nothing we already finished
      else -> other.cancel().fix().unsafeRunAsync { r ->
        conn.pop()
        cb(Left(r.fold({ e2 -> Platform.composeErrors(e, e2) }, { e })))
      }
    }

    IORunLoop.startCancelable(IOForkedStart(fa, ctx), connA) { resultA ->
      resultA.fold({ e ->
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
        sendError(connA, e)
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

  fun <A, B, C, D> parMapN(ctx: CoroutineContext, fa: IOOf<A>, fb: IOOf<B>, fc: IOOf<C>, f: (A, B, C) -> D): IO<D> = IO.Async(true) { conn, cb ->

    val state: AtomicRefW<Option<Tuple3<Option<A>, Option<B>, Option<C>>>> = AtomicRefW(None)
    val active = AtomicBooleanW(true)

    val connA = IOConnection()
    val connB = IOConnection()
    val connC = IOConnection()

    // Composite cancelable that cancels all ops.
    // NOTE: conn.pop() called when cb gets called below in complete.
    conn.push(connA.cancel(), connB.cancel(), connC.cancel())

    fun complete(a: A, b: B, c: C) {
      conn.pop()
      val result: Either<Throwable, D> = try {
        Either.Right(f(a, b, c))
      } catch (e: Throwable) {
        Either.Left(e.nonFatalOrThrow())
      }
      cb(result)
    }

    fun tryComplete(result: Option<Tuple3<Option<A>, Option<B>, Option<C>>>): Unit =
      result.fold({ Unit }, { (a, b, c) -> Option.applicative().map(a, b, c) { (a, b, c) -> complete(a, b, c) } })

    fun sendError(other: IOConnection, other2: IOConnection, e: Throwable) =
      if (active.getAndSet(false)) { // We were already cancelled so don't do anything.
        other.cancel().fix().unsafeRunAsync { r1 ->
          other2.cancel().fix().unsafeRunAsync { r2 ->
            conn.pop()
            cb(Left(r1.fold({ e2 ->
              r2.fold({ e3 -> Platform.composeErrors(e, e2, e3) }, { Platform.composeErrors(e, e2) })
            }, {
              r2.fold({ e3 -> Platform.composeErrors(e, e3) }, { e })
            })))
          }
        }
      } else Unit

    IORunLoop.startCancelable(IOForkedStart(fa, ctx), connA) { resultA ->
      resultA.fold({ e ->
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
        sendError(connA, connC, e)
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
        sendError(connA, connB, e)
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
  fun <A, B, C, D, E> parMapN(
    ctx: CoroutineContext,
    fa: IOOf<A>,
    fb: IOOf<B>,
    fc: IOOf<C>,
    fd: IOOf<D>,
    f: (A, B, C, D) -> E
  ): IOOf<E> = IO.parMapN(ctx,
    IO.parMapN(ctx, fa, fb, ::Tuple2),
    IO.parMapN(ctx, fc, fd, ::Tuple2)
  ) { (a, b), (c, d) ->
    f(a, b, c, d)
  }

  /**
   * @see parMapN
   */
  fun <A, B, C, D, E, G> parMapN(
    ctx: CoroutineContext,
    fa: IOOf<A>,
    fb: IOOf<B>,
    fc: IOOf<C>,
    fd: IOOf<D>,
    fe: IOOf<E>,
    f: (A, B, C, D, E) -> G
  ): IO<G> = IO.parMapN(ctx,
    IO.parMapN(ctx, fa, fb, fc, ::Tuple3),
    IO.parMapN(ctx, fd, fe, ::Tuple2)
  ) { (a, b, c), (d, e) ->
    f(a, b, c, d, e)
  }

  /**
   * @see parMapN
   */
  fun <A, B, C, D, E, G, H> parMapN(
    ctx: CoroutineContext,
    fa: IOOf<A>,
    fb: IOOf<B>,
    fc: IOOf<C>,
    fd: IOOf<D>,
    fe: IOOf<E>,
    fg: IOOf<G>,
    f: (A, B, C, D, E, G) -> H
  ): IO<H> = IO.parMapN(ctx,
    IO.parMapN(ctx, fa, fb, fc, ::Tuple3),
    IO.parMapN(ctx, fd, fe, fg, ::Tuple3)
  ) { (a, b, c), (d, e, g) ->
    f(a, b, c, d, e, g)
  }

  /**
   * @see parMapN
   */
  fun <A, B, C, D, E, G, H, I> parMapN(
    ctx: CoroutineContext,
    fa: IOOf<A>,
    fb: IOOf<B>,
    fc: IOOf<C>,
    fd: IOOf<D>,
    fe: IOOf<E>,
    fg: IOOf<G>,
    fh: IOOf<H>,
    f: (A, B, C, D, E, G, H) -> I
  ): IO<I> = IO.parMapN(ctx,
    IO.parMapN(ctx, fa, fb, fc, ::Tuple3),
    IO.parMapN(ctx, fd, fe, ::Tuple2),
    IO.parMapN(ctx, fg, fh, ::Tuple2)) { (a, b, c), (d, e), (g, h) ->
    f(a, b, c, d, e, g, h)
  }

  /**
   * @see parMapN
   */
  fun <A, B, C, D, E, G, H, I, J> parMapN(
    ctx: CoroutineContext,
    fa: IOOf<A>,
    fb: IOOf<B>,
    fc: IOOf<C>,
    fd: IOOf<D>,
    fe: IOOf<E>,
    fg: IOOf<G>,
    fh: IOOf<H>,
    fi: IOOf<I>,
    f: (A, B, C, D, E, G, H, I) -> J
  ): IO<J> = IO.parMapN(ctx,
    IO.parMapN(ctx, fa, fb, fc, ::Tuple3),
    IO.parMapN(ctx, fd, fe, fg, ::Tuple3),
    IO.parMapN(ctx, fh, fi, ::Tuple2)) { (a, b, c), (d, e, g), (h, i) ->
    f(a, b, c, d, e, g, h, i)
  }

  /**
   * @see parMapN
   */
  fun <A, B, C, D, E, G, H, I, J, K> parMapN(
    ctx: CoroutineContext,
    fa: IOOf<A>,
    fb: IOOf<B>,
    fc: IOOf<C>,
    fd: IOOf<D>,
    fe: IOOf<E>,
    fg: IOOf<G>,
    fh: IOOf<H>,
    fi: IOOf<I>,
    fj: IOOf<J>,
    f: (A, B, C, D, E, G, H, I, J) -> K
  ): IO<K> = IO.parMapN(ctx,
    IO.parMapN(ctx, fa, fb, fc, ::Tuple3),
    IO.parMapN(ctx, fd, fe, fg, ::Tuple3),
    IO.parMapN(ctx, fh, fi, fj, ::Tuple3)) { (a, b, c), (d, e, g), (h, i, j) ->
    f(a, b, c, d, e, g, h, i, j)
  }
}

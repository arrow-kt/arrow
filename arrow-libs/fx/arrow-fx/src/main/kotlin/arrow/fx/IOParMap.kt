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
import arrow.core.toT
import arrow.fx.coroutines.SuspendConnection
import arrow.fx.internal.IOForkedStart
import arrow.fx.internal.Platform
import kotlin.coroutines.CoroutineContext

/** Mix-in to enable `parMapN` N-arity on IO's companion directly. */
interface IOParMap {

  @Deprecated("This API is not consistent with others within Arrow, see version with Tuple instead of function params")
  fun <A, B, C> parMapN(fa: IOOf<A>, fb: IOOf<B>, f: (A, B) -> C): IO<C> =
    parMapN(IODispatchers.CommonPool, fa, fb, f)

  /**
   * @see parMapN
   */
  fun <A, B, C> parMapN(fa: IOOf<A>, fb: IOOf<B>, f: (Tuple2<A, B>) -> C): IO<C> =
    parMapN(IODispatchers.CommonPool, fa, fb, f)

  /**
   * @see parTupledN
   */
  fun <A, B> parTupledN(fa: IOOf<A>, fb: IOOf<B>): IO<Tuple2<A, B>> =
    parTupledN(IODispatchers.CommonPool, fa, fb)

  @Deprecated("This API is not consistent with others within Arrow, see version with Tuple instead of function params")
  fun <A, B, C, D> parMapN(fa: IOOf<A>, fb: IOOf<B>, fc: IOOf<C>, f: (A, B, C) -> D): IO<D> =
    parMapN(IODispatchers.CommonPool, fa, fb, fc, f)

  /**
   * @see parMapN
   */
  fun <A, B, C, D> parMapN(fa: IOOf<A>, fb: IOOf<B>, fc: IOOf<C>, f: (Tuple3<A, B, C>) -> D): IO<D> =
    parMapN(IODispatchers.CommonPool, fa, fb, fc, f)

  /**
   * @see parTupledN
   */
  fun <A, B, C> parTupledN(fa: IOOf<A>, fb: IOOf<B>, fc: IOOf<C>): IO<Tuple3<A, B, C>> =
    parTupledN(IODispatchers.CommonPool, fa, fb, fc)

  @Deprecated("This API is not consistent with others within Arrow, see version with Tuple instead of function params")
  fun <A, B, C, D, E> parMapN(fa: IOOf<A>, fb: IOOf<B>, fc: IOOf<C>, fd: IOOf<D>, f: (A, B, C, D) -> E): IO<E> =
    parMapN(IODispatchers.CommonPool, fa, fb, fc, fd, f)

  /**
   * @see parMapN
   */
  fun <A, B, C, D, E> parMapN(
    fa: IOOf<A>,
    fb: IOOf<B>,
    fc: IOOf<C>,
    fd: IOOf<D>,
    f: (Tuple4<A, B, C, D>) -> E
  ): IO<E> =
    parMapN(IODispatchers.CommonPool, fa, fb, fc, fd, f)

  /**
   * @see parTupledN
   */
  fun <A, B, C, D> parTupledN(
    fa: IOOf<A>,
    fb: IOOf<B>,
    fc: IOOf<C>,
    fd: IOOf<D>
  ): IO<Tuple4<A, B, C, D>> =
    parTupledN(IODispatchers.CommonPool, fa, fb, fc, fd)

  @Deprecated("This API is not consistent with others within Arrow, see version with Tuple instead of function params")
  fun <A, B, C, D, E, G> parMapN(fa: IOOf<A>, fb: IOOf<B>, fc: IOOf<C>, fd: IOOf<D>, fe: IOOf<E>, f: (A, B, C, D, E) -> G): IO<G> =
    parMapN(IODispatchers.CommonPool, fa, fb, fc, fd, fe, f)

  /**
   * @see parMapN
   */
  fun <A, B, C, D, E, G> parMapN(
    fa: IOOf<A>,
    fb: IOOf<B>,
    fc: IOOf<C>,
    fd: IOOf<D>,
    fe: IOOf<E>,
    f: (Tuple5<A, B, C, D, E>) -> G
  ): IO<G> =
    parMapN(IODispatchers.CommonPool, fa, fb, fc, fd, fe, f)

  /**
   * @see parTupledN
   */
  fun <A, B, C, D, E> parTupledN(
    fa: IOOf<A>,
    fb: IOOf<B>,
    fc: IOOf<C>,
    fd: IOOf<D>,
    fe: IOOf<E>
  ): IO<Tuple5<A, B, C, D, E>> =
    parTupledN(IODispatchers.CommonPool, fa, fb, fc, fd, fe)

  @Deprecated("This API is not consistent with others within Arrow, see version with Tuple instead of function params")
  fun <A, B, C, D, E, G, H> parMapN(fa: IOOf<A>, fb: IOOf<B>, fc: IOOf<C>, fd: IOOf<D>, fe: IOOf<E>, fg: IOOf<G>, f: (A, B, C, D, E, G) -> H): IO<H> =
    parMapN(IODispatchers.CommonPool, fa, fb, fc, fd, fe, fg, f)

  /**
   * @see parMapN
   */
  fun <A, B, C, D, E, G, H> parMapN(
    fa: IOOf<A>,
    fb: IOOf<B>,
    fc: IOOf<C>,
    fd: IOOf<D>,
    fe: IOOf<E>,
    fg: IOOf<G>,
    f: (Tuple6<A, B, C, D, E, G>) -> H
  ): IO<H> =
    parMapN(IODispatchers.CommonPool, fa, fb, fc, fd, fe, fg, f)

  /**
   * @see parTupledN
   */
  fun <A, B, C, D, E, G> parTupledN(
    fa: IOOf<A>,
    fb: IOOf<B>,
    fc: IOOf<C>,
    fd: IOOf<D>,
    fe: IOOf<E>,
    fg: IOOf<G>
  ): IO<Tuple6<A, B, C, D, E, G>> =
    parTupledN(IODispatchers.CommonPool, fa, fb, fc, fd, fe, fg)

  @Deprecated("This API is not consistent with others within Arrow, see version with Tuple instead of function params")
  fun <A, B, C, D, E, G, H, I> parMapN(fa: IOOf<A>, fb: IOOf<B>, fc: IOOf<C>, fd: IOOf<D>, fe: IOOf<E>, fg: IOOf<G>, fh: IOOf<H>, f: (A, B, C, D, E, G, H) -> I): IO<I> =
    parMapN(IODispatchers.CommonPool, fa, fb, fc, fd, fe, fg, fh, f)

  /**
   * @see parMapN
   */
  fun <A, B, C, D, E, G, H, I> parMapN(
    fa: IOOf<A>,
    fb: IOOf<B>,
    fc: IOOf<C>,
    fd: IOOf<D>,
    fe: IOOf<E>,
    fg: IOOf<G>,
    fh: IOOf<H>,
    f: (Tuple7<A, B, C, D, E, G, H>) -> I
  ): IO<I> =
    parMapN(IODispatchers.CommonPool, fa, fb, fc, fd, fe, fg, fh, f)

  /**
   * @see parTupledN
   */
  fun <A, B, C, D, E, G, H> parTupledN(
    fa: IOOf<A>,
    fb: IOOf<B>,
    fc: IOOf<C>,
    fd: IOOf<D>,
    fe: IOOf<E>,
    fg: IOOf<G>,
    fh: IOOf<H>
  ): IO<Tuple7<A, B, C, D, E, G, H>> =
    parTupledN(IODispatchers.CommonPool, fa, fb, fc, fd, fe, fg, fh)

  @Deprecated("This API is not consistent with others within Arrow, see version with Tuple instead of function params")
  fun <A, B, C, D, E, G, H, I, J> parMapN(fa: IOOf<A>, fb: IOOf<B>, fc: IOOf<C>, fd: IOOf<D>, fe: IOOf<E>, fg: IOOf<G>, fh: IOOf<H>, fi: IOOf<I>, f: (A, B, C, D, E, G, H, I) -> J): IO<J> =
    parMapN(IODispatchers.CommonPool, fa, fb, fc, fd, fe, fg, fh, fi, f)

  /**
   * @see parMapN
   */
  fun <A, B, C, D, E, G, H, I, J> parMapN(
    fa: IOOf<A>,
    fb: IOOf<B>,
    fc: IOOf<C>,
    fd: IOOf<D>,
    fe: IOOf<E>,
    fg: IOOf<G>,
    fh: IOOf<H>,
    fi: IOOf<I>,
    f: (Tuple8<A, B, C, D, E, G, H, I>) -> J
  ): IO<J> =
    parMapN(IODispatchers.CommonPool, fa, fb, fc, fd, fe, fg, fh, fi, f)

  /**
   * @see parTupledN
   */
  fun <A, B, C, D, E, G, H, I> parTupledN(
    fa: IOOf<A>,
    fb: IOOf<B>,
    fc: IOOf<C>,
    fd: IOOf<D>,
    fe: IOOf<E>,
    fg: IOOf<G>,
    fh: IOOf<H>,
    fi: IOOf<I>
  ): IO<Tuple8<A, B, C, D, E, G, H, I>> =
    parTupledN(IODispatchers.CommonPool, fa, fb, fc, fd, fe, fg, fh, fi)

  @Deprecated("This API is not consistent with others within Arrow, see version with Tuple instead of function params")
  fun <A, B, C, D, E, G, H, I, J, K> parMapN(fa: IOOf<A>, fb: IOOf<B>, fc: IOOf<C>, fd: IOOf<D>, fe: IOOf<E>, fg: IOOf<G>, fh: IOOf<H>, fi: IOOf<I>, fj: IOOf<J>, f: (A, B, C, D, E, G, H, I, J) -> K): IO<K> =
    parMapN(IODispatchers.CommonPool, fa, fb, fc, fd, fe, fg, fh, fi, fj, f)

  /**
   * @see parMapN
   */
  fun <A, B, C, D, E, G, H, I, J, K> parMapN(
    fa: IOOf<A>,
    fb: IOOf<B>,
    fc: IOOf<C>,
    fd: IOOf<D>,
    fe: IOOf<E>,
    fg: IOOf<G>,
    fh: IOOf<H>,
    fi: IOOf<I>,
    fj: IOOf<J>,
    f: (Tuple9<A, B, C, D, E, G, H, I, J>) -> K
  ): IO<K> =
    parMapN(IODispatchers.CommonPool, fa, fb, fc, fd, fe, fg, fh, fi, fj, f)

  /**
   * @see parTupledN
   */
  fun <A, B, C, D, E, G, H, I, J> parTupledN(
    fa: IOOf<A>,
    fb: IOOf<B>,
    fc: IOOf<C>,
    fd: IOOf<D>,
    fe: IOOf<E>,
    fg: IOOf<G>,
    fh: IOOf<H>,
    fi: IOOf<I>,
    fj: IOOf<J>
  ): IO<Tuple9<A, B, C, D, E, G, H, I, J>> =
    parTupledN(IODispatchers.CommonPool, fa, fb, fc, fd, fe, fg, fh, fi, fj)

  @Deprecated("This API is not consistent with others within Arrow, see version with Tuple instead of function params")
  fun <A, B, C> parMapN(ctx: CoroutineContext, fa: IOOf<A>, fb: IOOf<B>, f: (A, B) -> C): IO<C> =
    parMapN(ctx, fa, fb) { (a, b) -> f(a, b) }

  fun <A, B, C> parMapN(ctx: CoroutineContext, fa: IOOf<A>, fb: IOOf<B>, f: (Tuple2<A, B>) -> C): IO<C> =
    parTupledN(ctx, fa, fb).map(f)

  fun <A, B> parTupledN(ctx: CoroutineContext, fa: IOOf<A>, fb: IOOf<B>): IO<Tuple2<A, B>> = IO.Async(true) { conn, cb ->
    // Used to store Throwable, Either<A, B> or empty (null). (No sealed class used for a slightly better performing ParMap2)
    val state = AtomicRefW<Any?>(null)

    val connA = SuspendConnection()
    val connB = SuspendConnection()

    conn.pushPair(connA, connB)

    fun complete(a: A, b: B) {
      conn.pop()
      cb(try {
        Either.Right(a toT b)
      } catch (e: Throwable) {
        Either.Left(e.nonFatalOrThrow())
      })
    }

    fun sendError(other: SuspendConnection, e: Throwable) = when (state.getAndSet(e)) {
      is Throwable -> Unit // Do nothing we already finished
      else -> IO.effect { other.cancel() }.unsafeRunAsync { r ->
        conn.pop()
        cb(Left(r.fold({ e2 -> Platform.composeErrors(e, e2) }, { e })))
      }
    }

    IORunLoop.startCancellable(IOForkedStart(fa, ctx), connA) { resultA ->
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

    IORunLoop.startCancellable(IOForkedStart(fb, ctx), connB) { resultB ->
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

  @Deprecated("This API is not consistent with others within Arrow, see version with Tuple instead of function params")
  fun <A, B, C, D> parMapN(ctx: CoroutineContext, fa: IOOf<A>, fb: IOOf<B>, fc: IOOf<C>, f: (A, B, C) -> D): IO<D> =
    parMapN(ctx, fa, fb, fc) { (a, b, c) -> f(a, b, c) }

  fun <A, B, C, D> parMapN(ctx: CoroutineContext, fa: IOOf<A>, fb: IOOf<B>, fc: IOOf<C>, f: (Tuple3<A, B, C>) -> D): IO<D> =
    parTupledN(ctx, fa, fb, fc).map(f)

  fun <A, B, C> parTupledN(ctx: CoroutineContext, fa: IOOf<A>, fb: IOOf<B>, fc: IOOf<C>): IO<Tuple3<A, B, C>> = IO.Async(true) { conn, cb ->
    val state: AtomicRefW<Option<Tuple3<Option<A>, Option<B>, Option<C>>>> = AtomicRefW(None)
    val active = AtomicBooleanW(true)

    val connA = SuspendConnection()
    val connB = SuspendConnection()
    val connC = SuspendConnection()

    // Composite cancellable that cancels all ops.
    // NOTE: conn.pop() called when cb gets called below in complete.
    conn.push(listOf(connA::cancel, connB::cancel, connC::cancel))

    fun complete(a: A, b: B, c: C) {
      conn.pop()
      cb(try {
        Either.Right(Tuple3(a, b, c))
      } catch (e: Throwable) {
        Either.Left(e.nonFatalOrThrow())
      })
    }

    fun tryComplete(result: Option<Tuple3<Option<A>, Option<B>, Option<C>>>): Unit =
      result.fold({ Unit }, { (a, b, c) -> Option.applicative().map(a, b, c) { (a, b, c) -> complete(a, b, c) } })

    fun sendError(other: SuspendConnection, other2: SuspendConnection, e: Throwable) =
      if (active.getAndSet(false)) { // We were already cancelled so don't do anything.
        IO.effect { other.cancel() }.unsafeRunAsync { r1 ->
          IO.effect { other2.cancel() }.unsafeRunAsync { r2 ->
            conn.pop()
            cb(Left(r1.fold({ e2 ->
              r2.fold({ e3 -> Platform.composeErrors(e, e2, e3) }, { Platform.composeErrors(e, e2) })
            }, {
              r2.fold({ e3 -> Platform.composeErrors(e, e3) }, { e })
            })))
          }
        }
      } else Unit

    IORunLoop.startCancellable(IOForkedStart(fa, ctx), connA) { resultA ->
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

    IORunLoop.startCancellable(IOForkedStart(fb, ctx), connB) { resultB ->
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

    IORunLoop.startCancellable(IOForkedStart(fc, ctx), connC) { resultC ->
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
  @Deprecated("This API is not consistent with others within Arrow, see version with Tuple instead of function params")
  fun <A, B, C, D, E> parMapN(
    ctx: CoroutineContext,
    fa: IOOf<A>,
    fb: IOOf<B>,
    fc: IOOf<C>,
    fd: IOOf<D>,
    f: (A, B, C, D) -> E
  ): IO<E> =
    parMapN(ctx,
      parMapN(ctx, fa, fb, ::Tuple2),
      parMapN(ctx, fc, fd, ::Tuple2)
    ) { (a, b), (c, d) ->
      f(a, b, c, d)
    }

  fun <A, B, C, D, E> parMapN(
    ctx: CoroutineContext,
    fa: IOOf<A>,
    fb: IOOf<B>,
    fc: IOOf<C>,
    fd: IOOf<D>,
    f: (Tuple4<A, B, C, D>) -> E
  ): IO<E> = parTupledN(ctx, fa, fb, fc, fd).map(f)

  fun <A, B, C, D> parTupledN(
    ctx: CoroutineContext,
    fa: IOOf<A>,
    fb: IOOf<B>,
    fc: IOOf<C>,
    fd: IOOf<D>
  ): IO<Tuple4<A, B, C, D>> =
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
  fun <A, B, C, D, E, G> parMapN(
    ctx: CoroutineContext,
    fa: IOOf<A>,
    fb: IOOf<B>,
    fc: IOOf<C>,
    fd: IOOf<D>,
    fe: IOOf<E>,
    f: (A, B, C, D, E) -> G
  ): IO<G> =
    parMapN(ctx,
      parMapN(ctx, fa, fb, fc, ::Tuple3),
      parMapN(ctx, fd, fe, ::Tuple2)
    ) { (a, b, c), (d, e) ->
      f(a, b, c, d, e)
    }

  fun <A, B, C, D, E, G> parMapN(
    ctx: CoroutineContext,
    fa: IOOf<A>,
    fb: IOOf<B>,
    fc: IOOf<C>,
    fd: IOOf<D>,
    fe: IOOf<E>,
    f: (Tuple5<A, B, C, D, E>) -> G
  ): IO<G> = parTupledN(ctx, fa, fb, fc, fd, fe).map(f)

  fun <A, B, C, D, E> parTupledN(
    ctx: CoroutineContext,
    fa: IOOf<A>,
    fb: IOOf<B>,
    fc: IOOf<C>,
    fd: IOOf<D>,
    fe: IOOf<E>
  ): IO<Tuple5<A, B, C, D, E>> =
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
  fun <A, B, C, D, E, G, H> parMapN(
    ctx: CoroutineContext,
    fa: IOOf<A>,
    fb: IOOf<B>,
    fc: IOOf<C>,
    fd: IOOf<D>,
    fe: IOOf<E>,
    fg: IOOf<G>,
    f: (A, B, C, D, E, G) -> H
  ): IO<H> =
    parMapN(ctx,
      parMapN(ctx, fa, fb, fc, ::Tuple3),
      parMapN(ctx, fd, fe, fg, ::Tuple3)
    ) { (a, b, c), (d, e, g) ->
      f(a, b, c, d, e, g)
    }

  fun <A, B, C, D, E, G, H> parMapN(
    ctx: CoroutineContext,
    fa: IOOf<A>,
    fb: IOOf<B>,
    fc: IOOf<C>,
    fd: IOOf<D>,
    fe: IOOf<E>,
    fg: IOOf<G>,
    f: (Tuple6<A, B, C, D, E, G>) -> H
  ): IO<H> = parTupledN(ctx, fa, fb, fc, fd, fe, fg).map(f)

  fun <A, B, C, D, E, G> parTupledN(
    ctx: CoroutineContext,
    fa: IOOf<A>,
    fb: IOOf<B>,
    fc: IOOf<C>,
    fd: IOOf<D>,
    fe: IOOf<E>,
    fg: IOOf<G>
  ): IO<Tuple6<A, B, C, D, E, G>> =
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
  ): IO<I> =
    parMapN(ctx,
      parMapN(ctx, fa, fb, fc, ::Tuple3),
      parMapN(ctx, fd, fe, ::Tuple2),
      parMapN(ctx, fg, fh, ::Tuple2)
    ) { (a, b, c), (d, e), (g, h) ->
      f(a, b, c, d, e, g, h)
    }

  fun <A, B, C, D, E, G, H, I> parMapN(
    ctx: CoroutineContext,
    fa: IOOf<A>,
    fb: IOOf<B>,
    fc: IOOf<C>,
    fd: IOOf<D>,
    fe: IOOf<E>,
    fg: IOOf<G>,
    fh: IOOf<H>,
    f: (Tuple7<A, B, C, D, E, G, H>) -> I
  ): IO<I> = parTupledN(ctx, fa, fb, fc, fd, fe, fg, fh).map(f)

  fun <A, B, C, D, E, G, H> parTupledN(
    ctx: CoroutineContext,
    fa: IOOf<A>,
    fb: IOOf<B>,
    fc: IOOf<C>,
    fd: IOOf<D>,
    fe: IOOf<E>,
    fg: IOOf<G>,
    fh: IOOf<H>
  ): IO<Tuple7<A, B, C, D, E, G, H>> =
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
  ): IO<J> =
    parMapN(ctx,
      parMapN(ctx, fa, fb, fc, ::Tuple3),
      parMapN(ctx, fd, fe, fg, ::Tuple3),
      parMapN(ctx, fh, fi, ::Tuple2)
    ) { (a, b, c), (d, e, g), (h, i) ->
      f(a, b, c, d, e, g, h, i)
    }

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
    f: (Tuple8<A, B, C, D, E, G, H, I>) -> J
  ): IO<J> = parTupledN(ctx, fa, fb, fc, fd, fe, fg, fh, fi).map(f)

  fun <A, B, C, D, E, G, H, I> parTupledN(
    ctx: CoroutineContext,
    fa: IOOf<A>,
    fb: IOOf<B>,
    fc: IOOf<C>,
    fd: IOOf<D>,
    fe: IOOf<E>,
    fg: IOOf<G>,
    fh: IOOf<H>,
    fi: IOOf<I>
  ): IO<Tuple8<A, B, C, D, E, G, H, I>> =
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
  ): IO<K> =
    parMapN(ctx,
      parMapN(ctx, fa, fb, fc, ::Tuple3),
      parMapN(ctx, fd, fe, fg, ::Tuple3),
      parMapN(ctx, fh, fi, fj, ::Tuple3)
    ) { (a, b, c), (d, e, g), (h, i, j) ->
      f(a, b, c, d, e, g, h, i, j)
    }

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
    f: (Tuple9<A, B, C, D, E, G, H, I, J>) -> K
  ): IO<K> = parTupledN(ctx, fa, fb, fc, fd, fe, fg, fh, fi, fj).map(f)

  fun <A, B, C, D, E, G, H, I, J> parTupledN(
    ctx: CoroutineContext,
    fa: IOOf<A>,
    fb: IOOf<B>,
    fc: IOOf<C>,
    fd: IOOf<D>,
    fe: IOOf<E>,
    fg: IOOf<G>,
    fh: IOOf<H>,
    fi: IOOf<I>,
    fj: IOOf<J>
  ): IO<Tuple9<A, B, C, D, E, G, H, I, J>> =
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

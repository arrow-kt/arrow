package arrow.effects.internal

import arrow.Kind
import arrow.core.*
import arrow.effects.typeclasses.ConcurrentEffect
import arrow.effects.typeclasses.Disposable
import arrow.effects.typeclasses.Effect
import arrow.effects.typeclasses.Proc
import kotlin.coroutines.*
import kotlin.coroutines.Continuation
import arrow.core.Continuation as AContinuation

/* See parMap3 */
internal fun <F, A, B, C> Effect<F>.parMap2(ctx: CoroutineContext, ioA: Kind<F, A>, ioB: Kind<F, B>, f: (A, B) -> C,
  /* start is used because this has to start inside the coroutine. Using Future won't work */
                                            start: (Kind<F, Unit>) -> Unit): Proc<C> = { cc ->
  val a: suspend () -> Either<A, B> = {
    suspendCoroutine { ca: Continuation<Either<A, B>> ->
      start(ioA.map { it.left() }.runAsync {
        it.fold({ delay { ca.resumeWith(Result.failure(it)) } }, { delay { ca.resumeWith(Result.success(it)) } })
      })
    }
  }
  val b: suspend () -> Either<A, B> = {
    suspendCoroutine { ca: Continuation<Either<A, B>> ->
      start(ioB.map { it.right() }.runAsync {
        it.fold({ delay { ca.resumeWith(Result.failure(it)) } }, { delay { ca.resumeWith(Result.success(it)) } })
      })
    }
  }
  val parCont = parContinuation(ctx, f, asyncIOContinuation(ctx, cc))
  a.startCoroutine(parCont)
  b.startCoroutine(parCont)
}

/* Parallelization is only provided in pairs and triples.
 * Every time you start 4+ elements, each pair or triple has to be combined with another one at the same depth.
 * Elements at higher depths that are synchronous can prevent elements at a lower depth from starting.
 * Thus, we need to provide solutions for even and uneven amounts of IOs for all to be started at the same depth. */
internal fun <F, A, B, C, D> Effect<F>.parMap3(ctx: CoroutineContext, ioA: Kind<F, A>, ioB: Kind<F, B>, ioC: Kind<F, C>, f: (A, B, C) -> D,
  /* start is used because this has to start inside the coroutine. Using Future won't work */
                                               start: (Kind<F, Unit>) -> Unit): Proc<D> = { cc ->
  val a: suspend () -> Treither<A, B, C> = {
    suspendCoroutine { ca: Continuation<Treither<A, B, C>> ->
      start(ioA.map { Treither.Left<A, B, C>(it) }.runAsync {
        it.fold({ delay { ca.resumeWith(Result.failure(it)) } }, { delay { ca.resumeWith(Result.success(it)) } })
      })
    }
  }
  val b: suspend () -> Treither<A, B, C> = {
    suspendCoroutine { ca: Continuation<Treither<A, B, C>> ->
      start(ioB.map { Treither.Middle<A, B, C>(it) }.runAsync {
        it.fold({ delay { ca.resumeWith(Result.failure(it)) } }, { delay { ca.resumeWith(Result.success(it)) } })
      })
    }
  }
  val c: suspend () -> Treither<A, B, C> = {
    suspendCoroutine { ca: Continuation<Treither<A, B, C>> ->
      start(ioC.map { Treither.Right<A, B, C>(it) }.runAsync {
        it.fold({ delay { ca.resumeWith(Result.failure(it)) } }, { delay { ca.resumeWith(Result.success(it)) } })
      })
    }
  }
  val triCont = triContinuation(ctx, f, asyncIOContinuation(ctx, cc))
  a.startCoroutine(triCont)
  b.startCoroutine(triCont)
  c.startCoroutine(triCont)
}

/* See parMap3 */
fun <F, A, B, C> ConcurrentEffect<F>.parMapCancellable2(
  ctx: CoroutineContext, ioA: Kind<F, A>, ioB: Kind<F, B>, f: (A, B) -> C,
  /* start is used because this has to start inside the coroutine. Using Future won't work */
  start: (Kind<F, Disposable>) -> Unit): Proc<C> = { cc ->
  val a: suspend () -> Either<A, B> = {
    suspendCoroutine { ca: Continuation<Either<A, B>> ->
      start(ioA.map { it.left() }.runAsyncCancellable {
        it.fold({ delay { ca.resumeWithException(it) } }, { delay { ca.resume(it) } })
      })
    }
  }
  val b: suspend () -> Either<A, B> = {
    suspendCoroutine { ca: Continuation<Either<A, B>> ->
      start(ioB.map { it.right() }.runAsyncCancellable {
        it.fold({ delay { ca.resumeWithException(it) } }, { delay { ca.resume(it) } })
      })
    }
  }
  val parCont = parContinuation(ctx, f, asyncIOContinuation(ctx, cc))
  a.startCoroutine(parCont)
  b.startCoroutine(parCont)
}

/* See parMap3 */
fun <F, A, B, C, D> ConcurrentEffect<F>.parMapCancellable3(ctx: CoroutineContext, ioA: Kind<F, A>, ioB: Kind<F, B>, ioC: Kind<F, C>, f: (A, B, C) -> D,
  /* start is used because this has to start inside the coroutine. Using Future won't work */
                                                           start: (Kind<F, Disposable>) -> Unit): Proc<D> = { cc ->
  val a: suspend () -> Treither<A, B, C> = {
    suspendCoroutine { ca: Continuation<Treither<A, B, C>> ->
      start(ioA.map { Treither.Left<A, B, C>(it) }.runAsyncCancellable {
        it.fold({ delay { ca.resumeWithException(it) } }, { delay { ca.resume(it) } })
      })
    }
  }
  val b: suspend () -> Treither<A, B, C> = {
    suspendCoroutine { ca: Continuation<Treither<A, B, C>> ->
      start(ioB.map { Treither.Middle<A, B, C>(it) }.runAsyncCancellable {
        it.fold({ delay { ca.resumeWithException(it) } }, { delay { ca.resume(it) } })
      })
    }
  }
  val c: suspend () -> Treither<A, B, C> = {
    suspendCoroutine { ca: Continuation<Treither<A, B, C>> ->
      start(ioC.map { Treither.Right<A, B, C>(it) }.runAsyncCancellable {
        it.fold({ delay { ca.resumeWithException(it) } }, { delay { ca.resume(it) } })
      })
    }
  }
  val triCont = triContinuation(ctx, f, asyncIOContinuation(ctx, cc))
  a.startCoroutine(triCont)
  b.startCoroutine(triCont)
  c.startCoroutine(triCont)
}

private fun <A, B, C> parContinuation(ctx: CoroutineContext, f: (A, B) -> C, cc: AContinuation<C>): AContinuation<Either<A, B>> =
  object : AContinuation<Either<A, B>> {
    override val context: CoroutineContext = ctx

    var intermediate: Tuple2<A?, B?> = null toT null

    override fun resume(value: Either<A, B>) {
      synchronized(this) {
        val resA = intermediate.a
        val resB = intermediate.b
        value.fold({ a ->
          intermediate = a toT resB
          if (resB != null) {
            cc.resume(f(a, resB))
          }
        }, { b ->
          intermediate = resA toT b
          if (resA != null) {
            cc.resume(f(resA, b))
          }
        })
      }
    }

    override fun resumeWithException(exception: Throwable) {
      cc.resumeWithException(exception)
    }

  }

private fun <A, B, C, D> triContinuation(ctx: CoroutineContext, f: (A, B, C) -> D, cc: AContinuation<D>): AContinuation<Treither<A, B, C>> =
  object : AContinuation<Treither<A, B, C>> {
    override val context: CoroutineContext = ctx

    var intermediate: Tuple3<A?, B?, C?> = Tuple3(null, null, null)

    override fun resume(value: Treither<A, B, C>) {
      synchronized(this) {
        val resA = intermediate.a
        val resB = intermediate.b
        val resC = intermediate.c
        value.fold({ a ->
          intermediate = Tuple3(a, resB, resC)
          if (resB != null && resC != null) {
            cc.resume(f(a, resB, resC))
          }
        }, { b ->
          intermediate = Tuple3(resA, b, resC)
          if (resA != null && resC != null) {
            cc.resume(f(resA, b, resC))
          }
        }, { c ->
          intermediate = Tuple3(resA, resB, c)
          if (resA != null && resB != null) {
            cc.resume(f(resA, resB, c))
          }
        })
      }
    }

    override fun resumeWithException(exception: Throwable) {
      cc.resumeWithException(exception)
    }

  }

private sealed class Treither<out A, out B, out C> {
  data class Left<out A, out B, out C>(val a: A) : Treither<A, B, C>() {
    override fun <D> fold(fa: (A) -> D, fb: (B) -> D, fc: (C) -> D) =
      fa(a)
  }

  data class Middle<out A, out B, out C>(val b: B) : Treither<A, B, C>() {
    override fun <D> fold(fa: (A) -> D, fb: (B) -> D, fc: (C) -> D) =
      fb(b)
  }

  data class Right<out A, out B, out C>(val c: C) : Treither<A, B, C>() {
    override fun <D> fold(fa: (A) -> D, fb: (B) -> D, fc: (C) -> D) =
      fc(c)
  }

  abstract fun <D> fold(fa: (A) -> D, fb: (B) -> D, fc: (C) -> D): D
}

private fun <A> asyncIOContinuation(ctx: CoroutineContext, cc: (Either<Throwable, A>) -> Unit): AContinuation<A> =
  object : AContinuation<A> {
    override val context: CoroutineContext = ctx

    override fun resume(value: A) {
      cc(value.right())
    }

    override fun resumeWithException(exception: Throwable) {
      cc(exception.left())
    }

  }


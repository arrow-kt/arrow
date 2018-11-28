package arrow.effects

import arrow.core.*
import arrow.effects.instances.io.concurrentEffect.concurrentEffect
import arrow.effects.internal.Platform.onceOnly
import arrow.effects.internal.parMapCancellable2
import arrow.effects.internal.parMapCancellable3
import arrow.effects.typeclasses.Disposable
import arrow.effects.typeclasses.Duration
import java.util.concurrent.CountDownLatch
import kotlin.coroutines.CoroutineContext

internal class FutureN<A>(count: Int = 1) {
  private val latch = CountDownLatch(count)
  private var ref: MutableList<A> = mutableListOf()

  fun unsafeGet(): List<A> {
    latch.await()
    return ref.toList()
  }

  fun get(limit: Duration): List<A> {
    latch.await(limit.amount, limit.timeUnit)
    return ref.toList()
  }

  fun set(value: A) = synchronized(this) {
    ref.add(value)
    latch.countDown()
  }
}

fun <A, B, C> IO.Companion.parallelMapN(ctx: CoroutineContext, ioA: IOOf<A>, ioB: IOOf<B>, f: (A, B) -> C): IO<C> {
  val cancel: FutureN<Disposable> = FutureN(2)

  val cbf: FutureN<(Either<Throwable, C>) -> Unit> = FutureN()

  val complete = onceOnly { result: Either<Throwable, C> ->
    cancel.unsafeGet().forEach { it() }
    cbf.unsafeGet().forEach { it(result) }
  }

  return IO.async { TODO, cb: (Either<Throwable, C>) -> Unit ->
    cbf.set(cb)
    IO.async(IO.concurrentEffect().parMapCancellable2(ctx, ioA, ioB, f)
    { it.fix().unsafeRunAsync { it.fold({ cb(it.left()) }, { cancel.set(it) }) } }.toIOProc()
    ).unsafeRunAsync(complete)
  }.handleErrorWith {
    if (it == OnCancel.CancellationException) {
      complete(it.left())
    }
    IO.raiseError(it)
  }
}

fun <A, B, C, D> IO.Companion.parallelMapN(ctx: CoroutineContext, ioA: IOOf<A>, ioB: IOOf<B>, ioC: IOOf<C>, f: (A, B, C) -> D): IO<D> {
  val cancel: FutureN<Disposable> = FutureN(3)

  val cbf: FutureN<(Either<Throwable, D>) -> Unit> = FutureN()

  val complete = onceOnly { result: Either<Throwable, D> ->
    cancel.unsafeGet().forEach { it() }
    cbf.unsafeGet().forEach { it(result) }
  }

  return IO.async { TODO, cb: (Either<Throwable, D>) -> Unit ->
    cbf.set(cb)
    IO.async(IO.concurrentEffect().parMapCancellable3(ctx, ioA, ioB, ioC, f)
    { it.fix().unsafeRunAsync { it.fold({ cb(it.left()) }, { cancel.set(it) }) } }.toIOProc()
    ).unsafeRunAsync(complete)
  }.handleErrorWith {
    if (it == OnCancel.CancellationException) {
      complete(it.left())
    }
    IO.raiseError(it)
  }
}

fun <A, B, C, D, E> IO.Companion.parallelMapN(ctx: CoroutineContext, ioA: IOOf<A>, ioB: IOOf<B>, ioC: IOOf<C>, ioD: IOOf<D>, f: (A, B, C, D) -> E): IO<E> =
  parallelMapN(ctx,
    parallelMapN(ctx, ioA, ioB, ::Tuple2),
    parallelMapN(ctx, ioC, ioD, ::Tuple2)
  ) { ab, cd -> f(ab.a, ab.b, cd.a, cd.b) }

fun <A, B, C, D, E, F> IO.Companion.parallelMapN(ctx: CoroutineContext, ioA: IOOf<A>, ioB: IOOf<B>, ioC: IOOf<C>, ioD: IOOf<D>, ioE: IOOf<E>, f: (A, B, C, D, E) -> F): IO<F> =
  parallelMapN(ctx,
    parallelMapN(ctx, ioA, ioB, ioC, ::Tuple3),
    parallelMapN(ctx, ioD, ioE, ::Tuple2)
  ) { abc, de -> f(abc.a, abc.b, abc.c, de.a, de.b) }

fun <A, B, C, D, E, F, G> IO.Companion.parallelMapN(ctx: CoroutineContext, ioA: IOOf<A>, ioB: IOOf<B>, ioC: IOOf<C>, ioD: IOOf<D>, ioE: IOOf<E>, ioF: IOOf<F>, f: (A, B, C, D, E, F) -> G): IO<G> =
  parallelMapN(ctx,
    parallelMapN(ctx, ioA, ioB, ioC, ::Tuple3),
    parallelMapN(ctx, ioD, ioE, ioF, ::Tuple3)
  ) { abc, def -> f(abc.a, abc.b, abc.c, def.a, def.b, def.c) }

fun <A, B, C, D, E, F, G, H> IO.Companion.parallelMapN(ctx: CoroutineContext, ioA: IOOf<A>, ioB: IOOf<B>, ioC: IOOf<C>, ioD: IOOf<D>, ioE: IOOf<E>, ioF: IOOf<F>, ioG: IOOf<G>, f: (A, B, C, D, E, F, G) -> H): IO<H> =
  parallelMapN(ctx,
    parallelMapN(ctx, ioA, ioB, ioC, ::Tuple3),
    parallelMapN(ctx, ioD, ioE, ::Tuple2),
    parallelMapN(ctx, ioF, ioG, ::Tuple2)
  ) { abc, de, fg -> f(abc.a, abc.b, abc.c, de.a, de.b, fg.a, fg.b) }

fun <A, B, C, D, E, F, G, H, I> IO.Companion.parallelMapN(ctx: CoroutineContext, ioA: IOOf<A>, ioB: IOOf<B>, ioC: IOOf<C>, ioD: IOOf<D>, ioE: IOOf<E>, ioF: IOOf<F>, ioG: IOOf<G>, ioH: IOOf<H>, f: (A, B, C, D, E, F, G, H) -> I): IO<I> =
  parallelMapN(ctx,
    parallelMapN(ctx, ioA, ioB, ioC, ::Tuple3),
    parallelMapN(ctx, ioD, ioE, ioF, ::Tuple3),
    parallelMapN(ctx, ioG, ioH, ::Tuple2)
  ) { abc, def, gh -> f(abc.a, abc.b, abc.c, def.a, def.b, def.c, gh.a, gh.b) }

fun <A, B, C, D, E, F, G, H, I, J> IO.Companion.parallelMapN(ctx: CoroutineContext, ioA: IOOf<A>, ioB: IOOf<B>, ioC: IOOf<C>, ioD: IOOf<D>, ioE: IOOf<E>, ioF: IOOf<F>, ioG: IOOf<G>, ioH: IOOf<H>, ioI: IOOf<I>, f: (A, B, C, D, E, F, G, H, I) -> J): IO<J> =
  parallelMapN(ctx,
    parallelMapN(ctx, ioA, ioB, ioC, ::Tuple3),
    parallelMapN(ctx, ioD, ioE, ioF, ::Tuple3),
    parallelMapN(ctx, ioG, ioH, ioI, ::Tuple3)
  ) { abc, def, ghi -> f(abc.a, abc.b, abc.c, def.a, def.b, def.c, ghi.a, ghi.b, ghi.c) }

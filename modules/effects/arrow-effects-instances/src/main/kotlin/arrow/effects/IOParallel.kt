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

fun <A, B, C> IO.Companion.parallelMapN(ctx: CoroutineContext, ioA: IO<A>, ioB: IO<B>, f: (A, B) -> C): IO<C> {
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

fun <A, B, C, D> IO.Companion.parallelMapN(ctx: CoroutineContext, ioA: IO<A>, ioB: IO<B>, ioC: IO<C>, f: (A, B, C) -> D): IO<D> {
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

fun <A, B, C, D, E> IO.Companion.parallelMapN(ctx: CoroutineContext, ioA: IO<A>, ioB: IO<B>, ioC: IO<C>, ioD: IO<D>, f: (A, B, C, D) -> E): IO<E> =
  parallelMapN(ctx,
    parallelMapN(ctx, ioA, ioB, ::Tuple2),
    parallelMapN(ctx, ioC, ioD, ::Tuple2)
  ) { ab, cd -> f(ab.a, ab.b, cd.a, cd.b) }

fun <A, B, C, D, E, F> IO.Companion.parallelMapN(ctx: CoroutineContext, ioA: IO<A>, ioB: IO<B>, ioC: IO<C>, ioD: IO<D>, ioE: IO<E>, f: (A, B, C, D, E) -> F): IO<F> =
  parallelMapN(ctx,
    parallelMapN(ctx, ioA, ioB, ioC, ::Tuple3),
    parallelMapN(ctx, ioD, ioE, ::Tuple2)
  ) { abc, de -> f(abc.a, abc.b, abc.c, de.a, de.b) }

fun <A, B, C, D, E, F, G> IO.Companion.parallelMapN(ctx: CoroutineContext, ioA: IO<A>, ioB: IO<B>, ioC: IO<C>, ioD: IO<D>, ioE: IO<E>, ioF: IO<F>, f: (A, B, C, D, E, F) -> G): IO<G> =
  parallelMapN(ctx,
    parallelMapN(ctx, ioA, ioB, ioC, ::Tuple3),
    parallelMapN(ctx, ioD, ioE, ioF, ::Tuple3)
  ) { abc, def -> f(abc.a, abc.b, abc.c, def.a, def.b, def.c) }

fun <A, B, C, D, E, F, G, H> IO.Companion.parallelMapN(ctx: CoroutineContext, ioA: IO<A>, ioB: IO<B>, ioC: IO<C>, ioD: IO<D>, ioE: IO<E>, ioF: IO<F>, ioG: IO<G>, f: (A, B, C, D, E, F, G) -> H): IO<H> =
  parallelMapN(ctx,
    parallelMapN(ctx, ioA, ioB, ioC, ::Tuple3),
    parallelMapN(ctx, ioD, ioE, ::Tuple2),
    parallelMapN(ctx, ioF, ioG, ::Tuple2)
  ) { abc, de, fg -> f(abc.a, abc.b, abc.c, de.a, de.b, fg.a, fg.b) }

fun <A, B, C, D, E, F, G, H, I> IO.Companion.parallelMapN(ctx: CoroutineContext, ioA: IO<A>, ioB: IO<B>, ioC: IO<C>, ioD: IO<D>, ioE: IO<E>, ioF: IO<F>, ioG: IO<G>, ioH: IO<H>, f: (A, B, C, D, E, F, G, H) -> I): IO<I> =
  parallelMapN(ctx,
    parallelMapN(ctx, ioA, ioB, ioC, ::Tuple3),
    parallelMapN(ctx, ioD, ioE, ioF, ::Tuple3),
    parallelMapN(ctx, ioG, ioH, ::Tuple2)
  ) { abc, def, gh -> f(abc.a, abc.b, abc.c, def.a, def.b, def.c, gh.a, gh.b) }

fun <A, B, C, D, E, F, G, H, I, J> IO.Companion.parallelMapN(ctx: CoroutineContext, ioA: IO<A>, ioB: IO<B>, ioC: IO<C>, ioD: IO<D>, ioE: IO<E>, ioF: IO<F>, ioG: IO<G>, ioH: IO<H>, ioI: IO<I>, f: (A, B, C, D, E, F, G, H, I) -> J): IO<J> =
  parallelMapN(ctx,
    parallelMapN(ctx, ioA, ioB, ioC, ::Tuple3),
    parallelMapN(ctx, ioD, ioE, ioF, ::Tuple3),
    parallelMapN(ctx, ioG, ioH, ioI, ::Tuple3)
  ) { abc, def, ghi -> f(abc.a, abc.b, abc.c, def.a, def.b, def.c, ghi.a, ghi.b, ghi.c) }

fun <A, B> IO.Companion.raceN(ctx: CoroutineContext, a: IO<A>, b: IO<B>): IO<Either<A, B>> {
  val cancel: FutureN<Disposable> = FutureN(2)

  val cbf: FutureN<(Either<Throwable, Either<A, B>>) -> Unit> = FutureN()

  val complete = onceOnly { result: Either<Throwable, Either<A, B>> ->
    cancel.unsafeGet().forEach { it() }
    cbf.unsafeGet().forEach { it(result) }
  }

  return IO.async { TODO, cb: (Either<Throwable, Either<A, B>>) -> Unit ->
    cbf.set(cb)
    IO.async(IO.concurrentEffect().parMapCancellable2(ctx,
      a.flatMap { IO { complete(it.left().right()) } },
      b.flatMap { IO { complete(it.right().right()) } },
      ::Tuple2)
    { it.fix().unsafeRunAsync { it.fold({ cb(it.left()) }, { cancel.set(it) }) } }.toIOProc()
    ).unsafeRunAsync { it.fold({ complete(it.left()) }, { /* should never happen */ }) }
  }.handleErrorWith {
    if (it == OnCancel.CancellationException) {
      complete(it.left())
    }
    raiseError(it)
  }
}

fun <A, B, C> IO.Companion.raceN(ctx: CoroutineContext, a: IO<A>, b: IO<B>, c: IO<C>): IO<Either<A, Either<B, C>>> {
  val cancel: FutureN<Disposable> = FutureN(3)

  val cbf: FutureN<(Either<Throwable, Either<A, Either<B, C>>>) -> Unit> = FutureN()

  val complete = onceOnly { result: Either<Throwable, Either<A, Either<B, C>>> ->
    cancel.unsafeGet().forEach { it() }
    cbf.unsafeGet().forEach { it(result) }
  }

  return IO.async { TODO, cb: (Either<Throwable, Either<A, Either<B, C>>>) -> Unit ->
    cbf.set(cb)
    IO.async(IO.concurrentEffect().parMapCancellable3(ctx,
      a.flatMap { IO { complete(it.left().right()) } },
      b.flatMap { IO { complete(it.left().right().right()) } },
      c.flatMap { IO { complete(it.right().right().right()) } },
      ::Tuple3)
    { it.fix().unsafeRunAsync { it.fold({ cb(it.left()) }, { cancel.set(it) }) } }.toIOProc()
    ).unsafeRunAsync { it.fold({ complete(it.left()) }, { /* should never happen */ }) }
  }.handleErrorWith {
    if (it == OnCancel.CancellationException) {
      complete(it.left())
    }
    raiseError(it)
  }
}

fun <A, B, C, D> IO.Companion.raceN(ctx: CoroutineContext, a: IO<A>, b: IO<B>, c: IO<C>, d: IO<D>): IO<Either<Either<A, B>, Either<C, D>>> =
  raceN(ctx,
    raceN(ctx, a, b),
    raceN(ctx, c, d)
  )

fun <A, B, C, D, E> IO.Companion.raceN(ctx: CoroutineContext, a: IO<A>, b: IO<B>, c: IO<C>, d: IO<D>, e: IO<E>): IO<Either<Either<A, Either<B, C>>, Either<D, E>>> =
  raceN(ctx,
    raceN(ctx, a, b, c),
    raceN(ctx, d, e)
  )

fun <A, B, C, D, E, F> IO.Companion.raceN(ctx: CoroutineContext, a: IO<A>, b: IO<B>, c: IO<C>, d: IO<D>, e: IO<E>, f: IO<F>): IO<Either<Either<A, B>, Either<Either<C, D>, Either<E, F>>>> =
  raceN(ctx,
    raceN(ctx, a, b),
    raceN(ctx, c, d),
    raceN(ctx, e, f)
  )

fun <A, B, C, D, E, F, G> IO.Companion.raceN(ctx: CoroutineContext, a: IO<A>, b: IO<B>, c: IO<C>, d: IO<D>, e: IO<E>, f: IO<F>, g: IO<G>): IO<Either<Either<A, Either<B, C>>, Either<Either<D, E>, Either<F, G>>>> =
  raceN(ctx,
    raceN(ctx, a, b, c),
    raceN(ctx, d, e),
    raceN(ctx, f, g)
  )

fun <A, B, C, D, E, F, G, H> IO.Companion.raceN(ctx: CoroutineContext, a: IO<A>, b: IO<B>, c: IO<C>, d: IO<D>, e: IO<E>, f: IO<F>, g: IO<G>, h: IO<H>): IO<Either<Either<Either<A, B>, Either<C, D>>, Either<Either<E, F>, Either<G, H>>>> =
  raceN(ctx,
    raceN(ctx, a, b),
    raceN(ctx, c, d),
    raceN(ctx, e, f),
    raceN(ctx, g, h)
  )

fun <A, B, C, D, E, F, G, H, I> IO.Companion.raceN(ctx: CoroutineContext, a: IO<A>, b: IO<B>, c: IO<C>, d: IO<D>, e: IO<E>, f: IO<F>, g: IO<G>, h: IO<H>, i: IO<I>): IO<Either<Either<Either<A, Either<B, C>>, Either<D, E>>, Either<Either<F, G>, Either<H, I>>>> =
  raceN(ctx,
    raceN(ctx, a, b, c),
    raceN(ctx, d, e),
    raceN(ctx, f, g),
    raceN(ctx, h, i)
  )

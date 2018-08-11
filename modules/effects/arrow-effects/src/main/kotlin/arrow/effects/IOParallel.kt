package arrow.effects

import arrow.core.*
import arrow.effects.internal.Future
import arrow.effects.internal.Platform.onceOnly
import arrow.effects.internal.parMap2
import arrow.effects.internal.parMap3
import arrow.effects.typeclasses.Disposable
import kotlin.coroutines.experimental.CoroutineContext

fun <A, B, C> IO.Companion.parallelMapN(ctx: CoroutineContext, ioA: IO<A>, ioB: IO<B>, f: (A, B) -> C): IO<C> =
  IO.async(IO.effect().parMap2(ctx, ioA, ioB, f) /* see parMap2 notes on this parameter */ { it.fix().unsafeRunSync() })

fun <A, B, C, D> IO.Companion.parallelMapN(ctx: CoroutineContext, ioA: IO<A>, ioB: IO<B>, ioC: IO<C>, f: (A, B, C) -> D): IO<D> =
  IO.async(IO.effect().parMap3(ctx, ioA, ioB, ioC, f) /* see parMap2 notes on this parameter */ { it.fix().unsafeRunSync() })

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

fun <A, B> IO.Companion.race(a: IO<A>, b: IO<B>): IO<Either<A, B>> =
  IO.async { asyncCb ->
    val cancellation: Future<Tuple2<Disposable, Disposable>> = Future()

    val cb = onceOnly { result: Either<Throwable, Either<A, B>> ->
      cancellation.unsafeGet().let { (da, db) -> da(); db(); }
      asyncCb(result)
    }

    cancellation.set(
      IO.applicative().tupled(
        a.runAsyncCancellable { IO { it.fold({ cb(it.left()) }, { cb(it.left().right()) }) } },
        b.runAsyncCancellable { IO { it.fold({ cb(it.left()) }, { cb(it.right().right()) }) } }
      ).fix().unsafeRunSync()
    )
  }

fun <A> IO.Companion.race(l: List<IO<A>>): IO<A> =
  IO.async { asyncCb ->
    val cancellation: Future<List<Disposable>> = Future()

    val cb = onceOnly { result: Either<Throwable, A> ->
      cancellation.unsafeGet().let { it.forEach { it() } }
      asyncCb(result)
    }

    cancellation.set(l.map { it.unsafeRunAsyncCancellable(cb = cb) })
  }

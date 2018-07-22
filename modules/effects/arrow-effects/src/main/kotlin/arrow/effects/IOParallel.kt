package arrow.effects

import arrow.core.Tuple2
import arrow.core.Tuple3
import arrow.effects.internal.par2
import arrow.effects.internal.par3
import kotlin.coroutines.experimental.CoroutineContext

fun <A, B, C> IO.Companion.parMapN(ctx: CoroutineContext, ioA: IO<A>, ioB: IO<B>, f: (A, B) -> C): IO<C> =
  IO.async(par2(ctx, ioA, ioB, f))

fun <A, B, C, D> IO.Companion.parMapN(ctx: CoroutineContext, ioA: IO<A>, ioB: IO<B>, ioC: IO<C>, f: (A, B, C) -> D): IO<D> =
  IO.async(par3(ctx, ioA, ioB, ioC, f))

fun <A, B, C, D, E> IO.Companion.parMapN(ctx: CoroutineContext, ioA: IO<A>, ioB: IO<B>, ioC: IO<C>, ioD: IO<D>, f: (A, B, C, D) -> E): IO<E> =
  parMapN(ctx,
    parMapN(ctx, ioA, ioB, ::Tuple2),
    parMapN(ctx, ioC, ioD, ::Tuple2),
    { ab, cd -> f(ab.a, ab.b, cd.a, cd.b) })

fun <A, B, C, D, E, F> IO.Companion.parMapN(ctx: CoroutineContext, ioA: IO<A>, ioB: IO<B>, ioC: IO<C>, ioD: IO<D>, ioE: IO<E>, f: (A, B, C, D, E) -> F): IO<F> =
  parMapN(ctx,
    parMapN(ctx, ioA, ioB, ::Tuple2),
    parMapN(ctx, ioC, ioD, ioE, ::Tuple3),
    { ab, cde -> f(ab.a, ab.b, cde.a, cde.b, cde.c) })

fun <A, B, C, D, E, F, G> IO.Companion.parMapN(ctx: CoroutineContext, ioA: IO<A>, ioB: IO<B>, ioC: IO<C>, ioD: IO<D>, ioE: IO<E>, ioF: IO<F>, f: (A, B, C, D, E, F) -> G): IO<G> =
  parMapN(ctx,
    parMapN(ctx, ioA, ioB, ioC, ::Tuple3),
    parMapN(ctx, ioD, ioE, ioF, ::Tuple3),
    { abc, def -> f(abc.a, abc.b, abc.c, def.a, def.b, def.c) })

fun <A, B, C, D, E, F, G, H> IO.Companion.parMapN(ctx: CoroutineContext, ioA: IO<A>, ioB: IO<B>, ioC: IO<C>, ioD: IO<D>, ioE: IO<E>, ioF: IO<F>, ioG: IO<G>, f: (A, B, C, D, E, F, G) -> H): IO<H> =
  parMapN(ctx,
    parMapN(ctx, ioA, ioB, ioC, ::Tuple3),
    parMapN(ctx, ioD, ioE, ::Tuple2),
    parMapN(ctx, ioF, ioG, ::Tuple2),
    { abc, de, fg -> f(abc.a, abc.b, abc.c, de.a, de.b, fg.a, fg.b) })

fun <A, B, C, D, E, F, G, H, I> IO.Companion.parMapN(ctx: CoroutineContext, ioA: IO<A>, ioB: IO<B>, ioC: IO<C>, ioD: IO<D>, ioE: IO<E>, ioF: IO<F>, ioG: IO<G>, ioH: IO<H>, f: (A, B, C, D, E, F, G, H) -> I): IO<I> =
  parMapN(ctx,
    parMapN(ctx, ioA, ioB, ioC, ::Tuple3),
    parMapN(ctx, ioD, ioE, ioF, ::Tuple3),
    parMapN(ctx, ioG, ioH, ::Tuple2),
    { abc, def, gh -> f(abc.a, abc.b, abc.c, def.a, def.b, def.c, gh.a, gh.b) })

fun <A, B, C, D, E, F, G, H, I, J> IO.Companion.parMapN(ctx: CoroutineContext, ioA: IO<A>, ioB: IO<B>, ioC: IO<C>, ioD: IO<D>, ioE: IO<E>, ioF: IO<F>, ioG: IO<G>, ioH: IO<H>, ioI: IO<I>, f: (A, B, C, D, E, F, G, H, I) -> J): IO<J> =
  parMapN(ctx,
    parMapN(ctx, ioA, ioB, ioC, ::Tuple3),
    parMapN(ctx, ioD, ioE, ioF, ::Tuple3),
    parMapN(ctx, ioG, ioH, ioI, ::Tuple3),
    { abc, def, ghi -> f(abc.a, abc.b, abc.c, def.a, def.b, def.c, ghi.a, ghi.b, ghi.c) })
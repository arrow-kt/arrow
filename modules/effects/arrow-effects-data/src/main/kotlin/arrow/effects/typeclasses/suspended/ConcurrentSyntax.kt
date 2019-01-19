package arrow.effects.typeclasses.suspended

import arrow.Kind
import arrow.core.Tuple2
import arrow.core.Tuple3
import arrow.core.Tuple4
import arrow.core.Tuple5
import arrow.effects.typeclasses.Concurrent
import arrow.effects.typeclasses.Fiber
import arrow.effects.typeclasses.RacePair
import arrow.effects.typeclasses.RaceTriple
import kotlin.coroutines.CoroutineContext

interface ConcurrentSyntax<F> : AsyncSyntax<F>, Concurrent<F> {

  private suspend fun <A> concurrently(fb: Concurrent<F>.() -> Kind<F, A>): A =
    run<Concurrent<F>, Kind<F, A>> { fb(this) }.bind()

  suspend fun <A> asyncCallback(k: SProc<A>): A =
    concurrently { asyncF(k.kr()) }

  suspend fun <A> (suspend () -> A).startFiber(ctx: CoroutineContext): Fiber<F, A> =
    concurrently { this@startFiber.k().startF(ctx) }

  suspend fun <A, B> CoroutineContext.racePair(
    fa: suspend () -> A,
    fb: suspend () -> B
  ): RacePair<F, A, B> =
    concurrently { racePair(this@racePair, fa.k(), fb.k()) }

  suspend fun <A, B, C> CoroutineContext.raceTriple(
    fa: suspend () -> A,
    fb: suspend () -> B,
    fc: suspend () -> C
  ): RaceTriple<F, A, B, C> =
    concurrently { raceTriple(this@raceTriple, fa.k(), fb.k(), fc.k()) }

  suspend operator fun <A, B, C> STuple2<A, B>.times(fc: suspend () -> C): STuple3<A, B, C> =
    Tuple3(a, b, fc)

  suspend operator fun <A, B, C, D> STuple3<A, B, C>.times(fd: suspend () -> D): STuple4<A, B, C, D> =
    Tuple4(a, b, c, fd)

  suspend operator fun <A, B, C, D, E> STuple4<A, B, C, D>.times(fe: suspend () -> E): STuple5<A, B, C, D, E> =
    Tuple5(a, b, c, d, fe)

  suspend fun <A, B> CoroutineContext.parallel(f: () -> STuple2<A, B>): Tuple2<A, B> {
    val t = f()
    return parTupled(this, t.a, t.b)
  }

  suspend fun <A, B, C> CoroutineContext.parallel(unit: Unit = Unit, f: () -> STuple3<A, B, C>): Tuple3<A, B, C> {
    val t = f()
    return parTupled(this, t.a, t.b, t.c)
  }

  suspend fun <A, B, C, D> CoroutineContext.parallel(unit: Unit = Unit, unit2: Unit = Unit, f: () -> STuple4<A, B, C, D>): Tuple4<A, B, C, D> {
    val t = f()
    return parTupled(this, t.a, t.b, t.c, t.d)
  }

  suspend fun <A, B, C, D, E> CoroutineContext.parallel(unit: Unit = Unit, unit2: Unit = Unit, unit3: Unit = Unit, f: () -> STuple5<A, B, C, D, E>): Tuple5<A, B, C, D, E> {
    val t = f()
    return parTupled(this, t.a, t.b, t.c, t.d, t.e)
  }

  suspend fun <A, B, C> parMap(
    ctx: CoroutineContext,
    fa: suspend () -> A,
    fb: suspend () -> B,
    f: (A, B) -> C
  ): C =
    concurrently { parMapN(ctx, fa.k(), fb.k(), f) }

  suspend fun <A, B, C, D> parMap(
    ctx: CoroutineContext,
    fa: suspend () -> A,
    fb: suspend () -> B,
    fc: suspend () -> C,
    f: (A, B, C) -> D
  ): D =
    concurrently { parMapN(ctx, fa.k(), fb.k(), fc.k(), f) }

  suspend fun <A, B, C, D, E> parMap(
    ctx: CoroutineContext,
    fa: suspend () -> A,
    fb: suspend () -> B,
    fc: suspend () -> C,
    fd: suspend () -> D,
    f: (A, B, C, D) -> E
  ): E =
    concurrently { parMapN(ctx, fa.k(), fb.k(), fc.k(), fd.k(), f) }

  suspend fun <A, B, C, D, E, G> parMap(
    ctx: CoroutineContext,
    fa: suspend () -> A,
    fb: suspend () -> B,
    fc: suspend () -> C,
    fd: suspend () -> D,
    fe: suspend () -> E,
    f: (A, B, C, D, E) -> G
  ): G =
    concurrently { parMapN(ctx, fa.k(), fb.k(), fc.k(), fd.k(), fe.k(), f) }


  suspend fun <A, B> parTupled(
    ctx: CoroutineContext,
    fa: suspend () -> A,
    fb: suspend () -> B
  ): Tuple2<A, B> =
    parMap(ctx, fa, fb, ::Tuple2)

  suspend fun <A, B, C> parTupled(
    ctx: CoroutineContext,
    fa: suspend () -> A,
    fb: suspend () -> B,
    fc: suspend () -> C
  ): Tuple3<A, B, C> =
    parMap(ctx, fa, fb, fc, ::Tuple3)

  suspend fun <A, B, C, D> parTupled(
    ctx: CoroutineContext,
    fa: suspend () -> A,
    fb: suspend () -> B,
    fc: suspend () -> C,
    fd: suspend () -> D
  ): Tuple4<A, B, C, D> =
    parMap(ctx, fa, fb, fc, fd, ::Tuple4)

  suspend fun <A, B, C, D, E> parTupled(
    ctx: CoroutineContext,
    fa: suspend () -> A,
    fb: suspend () -> B,
    fc: suspend () -> C,
    fd: suspend () -> D,
    fe: suspend () -> E
  ): Tuple5<A, B, C, D, E> =
    parMap(ctx, fa, fb, fc, fd, fe, ::Tuple5)

}
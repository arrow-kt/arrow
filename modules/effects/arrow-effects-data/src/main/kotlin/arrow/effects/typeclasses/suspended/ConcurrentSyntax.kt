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

interface ConcurrentSyntax<F> : AsyncSyntax<F>, Concurrent<F>, ListParTraverseSyntax<F> {

  private suspend fun <A> concurrently(fb: Concurrent<F>.() -> Kind<F, A>): A =
    run<Concurrent<F>, Kind<F, A>> { fb(this) }.bind()

  suspend fun <A> asyncCallback(k: SProc<A>): A =
    concurrently { asyncF(k.flatLiftM()) }

  override suspend fun <A> CoroutineContext.startFiber(f: suspend () -> A): Fiber<F, A> =
    concurrently { f.liftM().startF(this@startFiber) }

  suspend fun <A, B> CoroutineContext.racePair(
    fa: suspend () -> A,
    fb: suspend () -> B
  ): RacePair<F, A, B> =
    concurrently { racePair(this@racePair, fa.liftM(), fb.liftM()) }

  suspend fun <A, B, C> CoroutineContext.raceTriple(
    fa: suspend () -> A,
    fb: suspend () -> B,
    fc: suspend () -> C
  ): RaceTriple<F, A, B, C> =
    concurrently { raceTriple(this@raceTriple, fa.liftM(), fb.liftM(), fc.liftM()) }

  suspend fun <A, B, C> CoroutineContext.parMap(
    fa: suspend () -> A,
    fb: suspend () -> B,
    f: (A, B) -> C
  ): C =
    concurrently { parMapN(this@parMap, fa.liftM(), fb.liftM(), f) }

  suspend fun <A, B, C, D> CoroutineContext.parMap(
    fa: suspend () -> A,
    fb: suspend () -> B,
    fc: suspend () -> C,
    f: (A, B, C) -> D
  ): D =
    concurrently { parMapN(this@parMap, fa.liftM(), fb.liftM(), fc.liftM(), f) }

  suspend fun <A, B, C, D, E> CoroutineContext.parMap(
    fa: suspend () -> A,
    fb: suspend () -> B,
    fc: suspend () -> C,
    fd: suspend () -> D,
    f: (A, B, C, D) -> E
  ): E =
    concurrently { parMapN(this@parMap, fa.liftM(), fb.liftM(), fc.liftM(), fd.liftM(), f) }

  suspend fun <A, B, C, D, E, G> CoroutineContext.parMap(
    fa: suspend () -> A,
    fb: suspend () -> B,
    fc: suspend () -> C,
    fd: suspend () -> D,
    fe: suspend () -> E,
    f: (A, B, C, D, E) -> G
  ): G =
    concurrently { parMapN(this@parMap, fa.liftM(), fb.liftM(), fc.liftM(), fd.liftM(), fe.liftM(), f) }


  suspend fun <A, B> CoroutineContext.parallel(
    fa: suspend () -> A,
    fb: suspend () -> B
  ): Tuple2<A, B> =
    this.parMap(fa, fb, ::Tuple2)

  suspend fun <A, B, C> CoroutineContext.parallel(
    fa: suspend () -> A,
    fb: suspend () -> B,
    fc: suspend () -> C
  ): Tuple3<A, B, C> =
    this.parMap(fa, fb, fc, ::Tuple3)

  suspend fun <A, B, C, D> CoroutineContext.parallel(
    fa: suspend () -> A,
    fb: suspend () -> B,
    fc: suspend () -> C,
    fd: suspend () -> D
  ): Tuple4<A, B, C, D> =
    this.parMap(fa, fb, fc, fd, ::Tuple4)

  suspend fun <A, B, C, D, E> CoroutineContext.parallel(
    fa: suspend () -> A,
    fb: suspend () -> B,
    fc: suspend () -> C,
    fd: suspend () -> D,
    fe: suspend () -> E
  ): Tuple5<A, B, C, D, E> =
    this.parMap(fa, fb, fc, fd, fe, ::Tuple5)

}
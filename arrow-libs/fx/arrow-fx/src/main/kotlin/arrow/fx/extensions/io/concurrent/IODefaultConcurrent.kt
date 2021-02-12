package arrow.fx.extensions.io.concurrent

import arrow.Kind
import arrow.core.Either
import arrow.core.Tuple2
import arrow.core.Tuple3
import arrow.core.Tuple4
import arrow.core.Tuple5
import arrow.core.Tuple6
import arrow.core.Tuple7
import arrow.core.Tuple8
import arrow.core.Tuple9
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.IO.Companion
import arrow.fx.IODeprecation
import arrow.fx.MVar
import arrow.fx.Promise
import arrow.fx.Race3
import arrow.fx.Race4
import arrow.fx.Race5
import arrow.fx.Race6
import arrow.fx.Race7
import arrow.fx.Race8
import arrow.fx.Race9
import arrow.fx.RacePair
import arrow.fx.RaceTriple
import arrow.fx.Semaphore
import arrow.fx.Timer
import arrow.fx.extensions.IODefaultConcurrent
import arrow.fx.typeclasses.Concurrent
import arrow.fx.typeclasses.ConcurrentContinuation
import arrow.fx.typeclasses.Dispatchers
import arrow.fx.typeclasses.Duration
import arrow.fx.typeclasses.Fiber
import arrow.typeclasses.Applicative
import arrow.typeclasses.Traverse
import kotlin.Deprecated
import kotlin.Function1
import kotlin.Function2
import kotlin.Function3
import kotlin.Function4
import kotlin.Function5
import kotlin.Function6
import kotlin.Function7
import kotlin.Function8
import kotlin.Function9
import kotlin.Long
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.Throwable
import kotlin.Unit
import kotlin.collections.Iterable
import kotlin.collections.List
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val concurrent_singleton: IODefaultConcurrent = object :
    arrow.fx.extensions.IODefaultConcurrent {}

@JvmName("timer")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun timer(): Timer<ForIO> = arrow.fx.IO
   .concurrent()
   .timer() as arrow.fx.Timer<arrow.fx.ForIO>

@JvmName("parApplicative")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun parApplicative(): Applicative<ForIO> = arrow.fx.IO
   .concurrent()
   .parApplicative() as arrow.typeclasses.Applicative<arrow.fx.ForIO>

@JvmName("parApplicative")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun parApplicative(ctx: CoroutineContext): Applicative<ForIO> = arrow.fx.IO
   .concurrent()
   .parApplicative(ctx) as arrow.typeclasses.Applicative<arrow.fx.ForIO>

@JvmName("fork")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A> Kind<ForIO, A>.fork(ctx: CoroutineContext): IO<Fiber<ForIO, A>> =
    arrow.fx.IO.concurrent().run {
  this@fork.fork<A>(ctx) as arrow.fx.IO<arrow.fx.typeclasses.Fiber<arrow.fx.ForIO, A>>
}

@JvmName("fork")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A> Kind<ForIO, A>.fork(): IO<Fiber<ForIO, A>> = arrow.fx.IO.concurrent().run {
  this@fork.fork<A>() as arrow.fx.IO<arrow.fx.typeclasses.Fiber<arrow.fx.ForIO, A>>
}

@JvmName("racePair")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B> CoroutineContext.racePair(fa: Kind<ForIO, A>, fb: Kind<ForIO, B>): IO<RacePair<ForIO, A,
    B>> = arrow.fx.IO.concurrent().run {
  this@racePair.racePair<A, B>(fa, fb) as arrow.fx.IO<arrow.fx.RacePair<arrow.fx.ForIO, A, B>>
}

@JvmName("raceTriple")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C> CoroutineContext.raceTriple(
  fa: Kind<ForIO, A>,
  fb: Kind<ForIO, B>,
  fc: Kind<ForIO, C>
): IO<RaceTriple<ForIO, A, B, C>> = arrow.fx.IO.concurrent().run {
  this@raceTriple.raceTriple<A, B, C>(fa, fb, fc) as arrow.fx.IO<arrow.fx.RaceTriple<arrow.fx.ForIO,
    A, B, C>>
}

@JvmName("cancellable")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A> cancellable(k: Function1<Function1<Either<Throwable, A>, Unit>, Kind<ForIO, Unit>>): IO<A> =
    arrow.fx.IO
   .concurrent()
   .cancellable<A>(k) as arrow.fx.IO<A>

@JvmName("cancelable")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A> cancelable(k: Function1<Function1<Either<Throwable, A>, Unit>, Kind<ForIO, Unit>>): IO<A> =
    arrow.fx.IO
   .concurrent()
   .cancelable<A>(k) as arrow.fx.IO<A>

@JvmName("cancellableF")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A> cancellableF(
  k: Function1<Function1<Either<Throwable, A>, Unit>, Kind<ForIO, Kind<ForIO,
Unit>>>
): IO<A> = arrow.fx.IO
   .concurrent()
   .cancellableF<A>(k) as arrow.fx.IO<A>

@JvmName("cancelableF")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A> cancelableF(
  k: Function1<Function1<Either<Throwable, A>, Unit>, Kind<ForIO, Kind<ForIO,
Unit>>>
): IO<A> = arrow.fx.IO
   .concurrent()
   .cancelableF<A>(k) as arrow.fx.IO<A>

@JvmName("parTraverse")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <G, A, B> Kind<G, A>.parTraverse(
  ctx: CoroutineContext,
  TG: Traverse<G>,
  f: Function1<A, Kind<ForIO, B>>
): IO<Kind<G, B>> = arrow.fx.IO.concurrent().run {
  this@parTraverse.parTraverse<G, A, B>(ctx, TG, f) as arrow.fx.IO<arrow.Kind<G, B>>
}

@JvmName("parTraverse")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <G, A, B> Kind<G, A>.parTraverse(TG: Traverse<G>, f: Function1<A, Kind<ForIO, B>>): IO<Kind<G,
    B>> = arrow.fx.IO.concurrent().run {
  this@parTraverse.parTraverse<G, A, B>(TG, f) as arrow.fx.IO<arrow.Kind<G, B>>
}

@JvmName("parTraverse")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B> Iterable<A>.parTraverse(ctx: CoroutineContext, f: Function1<A, Kind<ForIO, B>>):
    IO<List<B>> = arrow.fx.IO.concurrent().run {
  this@parTraverse.parTraverse<A, B>(ctx, f) as arrow.fx.IO<kotlin.collections.List<B>>
}

@JvmName("parTraverse")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B> Iterable<A>.parTraverse(f: Function1<A, Kind<ForIO, B>>): IO<List<B>> =
    arrow.fx.IO.concurrent().run {
  this@parTraverse.parTraverse<A, B>(f) as arrow.fx.IO<kotlin.collections.List<B>>
}

@JvmName("parSequence")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <G, A> Kind<G, Kind<ForIO, A>>.parSequence(TG: Traverse<G>, ctx: CoroutineContext): IO<Kind<G,
    A>> = arrow.fx.IO.concurrent().run {
  this@parSequence.parSequence<G, A>(TG, ctx) as arrow.fx.IO<arrow.Kind<G, A>>
}

@JvmName("parSequence")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <G, A> Kind<G, Kind<ForIO, A>>.parSequence(TG: Traverse<G>): IO<Kind<G, A>> =
    arrow.fx.IO.concurrent().run {
  this@parSequence.parSequence<G, A>(TG) as arrow.fx.IO<arrow.Kind<G, A>>
}

@JvmName("parSequence")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A> Iterable<Kind<ForIO, A>>.parSequence(ctx: CoroutineContext): IO<List<A>> =
    arrow.fx.IO.concurrent().run {
  this@parSequence.parSequence<A>(ctx) as arrow.fx.IO<kotlin.collections.List<A>>
}

@JvmName("parSequence")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A> Iterable<Kind<ForIO, A>>.parSequence(): IO<List<A>> = arrow.fx.IO.concurrent().run {
  this@parSequence.parSequence<A>() as arrow.fx.IO<kotlin.collections.List<A>>
}

@JvmName("parMapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C> parMapN(
  ctx: CoroutineContext,
  fa: Kind<ForIO, A>,
  fb: Kind<ForIO, B>,
  f: Function1<Tuple2<A, B>, C>
): IO<C> = arrow.fx.IO
   .concurrent()
   .parMapN<A, B, C>(ctx, fa, fb, f) as arrow.fx.IO<C>

@JvmName("parTupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B> parTupledN(
  ctx: CoroutineContext,
  fa: Kind<ForIO, A>,
  fb: Kind<ForIO, B>
): IO<Tuple2<A, B>> = arrow.fx.IO
   .concurrent()
   .parTupledN<A, B>(ctx, fa, fb) as arrow.fx.IO<arrow.core.Tuple2<A, B>>

@JvmName("parMapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C> CoroutineContext.parMapN(
  fa: Kind<ForIO, A>,
  fb: Kind<ForIO, B>,
  f: Function2<A, B, C>
): IO<C> = arrow.fx.IO.concurrent().run {
  this@parMapN.parMapN<A, B, C>(fa, fb, f) as arrow.fx.IO<C>
}

@JvmName("parMapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, D> parMapN(
  ctx: CoroutineContext,
  fa: Kind<ForIO, A>,
  fb: Kind<ForIO, B>,
  fc: Kind<ForIO, C>,
  f: Function1<Tuple3<A, B, C>, D>
): IO<D> = arrow.fx.IO
   .concurrent()
   .parMapN<A, B, C, D>(ctx, fa, fb, fc, f) as arrow.fx.IO<D>

@JvmName("parTupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C> parTupledN(
  ctx: CoroutineContext,
  fa: Kind<ForIO, A>,
  fb: Kind<ForIO, B>,
  fc: Kind<ForIO, C>
): IO<Tuple3<A, B, C>> = arrow.fx.IO
   .concurrent()
   .parTupledN<A, B, C>(ctx, fa, fb, fc) as arrow.fx.IO<arrow.core.Tuple3<A, B, C>>

@JvmName("parMapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, D> CoroutineContext.parMapN(
  fa: Kind<ForIO, A>,
  fb: Kind<ForIO, B>,
  fc: Kind<ForIO, C>,
  f: Function3<A, B, C, D>
): IO<D> = arrow.fx.IO.concurrent().run {
  this@parMapN.parMapN<A, B, C, D>(fa, fb, fc, f) as arrow.fx.IO<D>
}

@JvmName("parMapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, D, E> parMapN(
  ctx: CoroutineContext,
  fa: Kind<ForIO, A>,
  fb: Kind<ForIO, B>,
  fc: Kind<ForIO, C>,
  fd: Kind<ForIO, D>,
  f: Function1<Tuple4<A, B, C, D>, E>
): IO<E> = arrow.fx.IO
   .concurrent()
   .parMapN<A, B, C, D, E>(ctx, fa, fb, fc, fd, f) as arrow.fx.IO<E>

@JvmName("parTupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, D> parTupledN(
  ctx: CoroutineContext,
  fa: Kind<ForIO, A>,
  fb: Kind<ForIO, B>,
  fc: Kind<ForIO, C>,
  fd: Kind<ForIO, D>
): IO<Tuple4<A, B, C, D>> = arrow.fx.IO
   .concurrent()
   .parTupledN<A, B, C, D>(ctx, fa, fb, fc, fd) as arrow.fx.IO<arrow.core.Tuple4<A, B, C, D>>

@JvmName("parMapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, D, E> CoroutineContext.parMapN(
  fa: Kind<ForIO, A>,
  fb: Kind<ForIO, B>,
  fc: Kind<ForIO, C>,
  fd: Kind<ForIO, D>,
  f: Function4<A, B, C, D, E>
): IO<E> = arrow.fx.IO.concurrent().run {
  this@parMapN.parMapN<A, B, C, D, E>(fa, fb, fc, fd, f) as arrow.fx.IO<E>
}

@JvmName("parMapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, D, E, G> parMapN(
  ctx: CoroutineContext,
  fa: Kind<ForIO, A>,
  fb: Kind<ForIO, B>,
  fc: Kind<ForIO, C>,
  fd: Kind<ForIO, D>,
  fe: Kind<ForIO, E>,
  f: Function1<Tuple5<A, B, C, D, E>, G>
): IO<G> = arrow.fx.IO
   .concurrent()
   .parMapN<A, B, C, D, E, G>(ctx, fa, fb, fc, fd, fe, f) as arrow.fx.IO<G>

@JvmName("parTupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, D, E> Concurrent<ForIO>.parTupledN(
  ctx: CoroutineContext,
  fa: Kind<ForIO, A>,
  fb: Kind<ForIO, B>,
  fc: Kind<ForIO, C>,
  fd: Kind<ForIO, D>,
  fe: Kind<ForIO, E>
): IO<Tuple5<A, B, C, D, E>> = arrow.fx.IO.concurrent().run {
  this@parTupledN.parTupledN<A, B, C, D, E>(ctx, fa, fb, fc, fd, fe) as
    arrow.fx.IO<arrow.core.Tuple5<A, B, C, D, E>>
}

@JvmName("parMapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, D, E, G> CoroutineContext.parMapN(
  fa: Kind<ForIO, A>,
  fb: Kind<ForIO, B>,
  fc: Kind<ForIO, C>,
  fd: Kind<ForIO, D>,
  fe: Kind<ForIO, E>,
  f: Function5<A, B, C, D, E, G>
): IO<G> = arrow.fx.IO.concurrent().run {
  this@parMapN.parMapN<A, B, C, D, E, G>(fa, fb, fc, fd, fe, f) as arrow.fx.IO<G>
}

@JvmName("parMapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, D, E, G, H> Concurrent<ForIO>.parMapN(
  ctx: CoroutineContext,
  fa: Kind<ForIO, A>,
  fb: Kind<ForIO, B>,
  fc: Kind<ForIO, C>,
  fd: Kind<ForIO, D>,
  fe: Kind<ForIO, E>,
  fg: Kind<ForIO, G>,
  f: Function1<Tuple6<A, B, C, D, E, G>, H>
): IO<H> = arrow.fx.IO.concurrent().run {
  this@parMapN.parMapN<A, B, C, D, E, G, H>(ctx, fa, fb, fc, fd, fe, fg, f) as arrow.fx.IO<H>
}

@JvmName("parTupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, D, E, G> Concurrent<ForIO>.parTupledN(
  ctx: CoroutineContext,
  fa: Kind<ForIO, A>,
  fb: Kind<ForIO, B>,
  fc: Kind<ForIO, C>,
  fd: Kind<ForIO, D>,
  fe: Kind<ForIO, E>,
  fg: Kind<ForIO, G>
): IO<Tuple6<A, B, C, D, E, G>> = arrow.fx.IO.concurrent().run {
  this@parTupledN.parTupledN<A, B, C, D, E, G>(ctx, fa, fb, fc, fd, fe, fg) as
    arrow.fx.IO<arrow.core.Tuple6<A, B, C, D, E, G>>
}

@JvmName("parMapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, D, E, G, H> CoroutineContext.parMapN(
  fa: Kind<ForIO, A>,
  fb: Kind<ForIO, B>,
  fc: Kind<ForIO, C>,
  fd: Kind<ForIO, D>,
  fe: Kind<ForIO, E>,
  fg: Kind<ForIO, G>,
  f: Function6<A, B, C, D, E, G, H>
): IO<H> = arrow.fx.IO.concurrent().run {
  this@parMapN.parMapN<A, B, C, D, E, G, H>(fa, fb, fc, fd, fe, fg, f) as arrow.fx.IO<H>
}

@JvmName("parMapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, A, B, C, D, E, G, H, I> Concurrent<F>.parMapN(
  ctx: CoroutineContext,
  fa: Kind<F, A>,
  fb: Kind<F, B>,
  fc: Kind<F, C>,
  fd: Kind<F, D>,
  fe: Kind<F, E>,
  fg: Kind<F, G>,
  fh: Kind<F, H>,
  f: Function1<Tuple7<A, B, C, D, E, G, H>, I>
): Kind<F, I> = arrow.fx.IO.concurrent().run {
  this@parMapN.parMapN<F, A, B, C, D, E, G, H, I>(ctx, fa, fb, fc, fd, fe, fg, fh, f) as
    arrow.Kind<F, I>
}

@JvmName("parTupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, D, E, G, H> parTupledN(
  ctx: CoroutineContext,
  fa: Kind<ForIO, A>,
  fb: Kind<ForIO, B>,
  fc: Kind<ForIO, C>,
  fd: Kind<ForIO, D>,
  fe: Kind<ForIO, E>,
  fg: Kind<ForIO, G>,
  fh: Kind<ForIO, H>
): IO<Tuple7<A, B, C, D, E, G, H>> = arrow.fx.IO
   .concurrent()
   .parTupledN<A, B, C, D, E, G, H>(ctx, fa, fb, fc, fd, fe, fg, fh) as
    arrow.fx.IO<arrow.core.Tuple7<A, B, C, D, E, G, H>>

@JvmName("parMapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, D, E, G, H, I> CoroutineContext.parMapN(
  fa: Kind<ForIO, A>,
  fb: Kind<ForIO, B>,
  fc: Kind<ForIO, C>,
  fd: Kind<ForIO, D>,
  fe: Kind<ForIO, E>,
  fg: Kind<ForIO, G>,
  fh: Kind<ForIO, H>,
  f: Function7<A, B, C, D, E, G, H, I>
): IO<I> = arrow.fx.IO.concurrent().run {
  this@parMapN.parMapN<A, B, C, D, E, G, H, I>(fa, fb, fc, fd, fe, fg, fh, f) as arrow.fx.IO<I>
}

@JvmName("parMapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, D, E, G, H, I, J> parMapN(
  ctx: CoroutineContext,
  fa: Kind<ForIO, A>,
  fb: Kind<ForIO, B>,
  fc: Kind<ForIO, C>,
  fd: Kind<ForIO, D>,
  fe: Kind<ForIO, E>,
  fg: Kind<ForIO, G>,
  fh: Kind<ForIO, H>,
  fi: Kind<ForIO, I>,
  f: Function1<Tuple8<A, B, C, D, E, G, H, I>, J>
): IO<J> = arrow.fx.IO
   .concurrent()
   .parMapN<A, B, C, D, E, G, H, I, J>(ctx, fa, fb, fc, fd, fe, fg, fh, fi, f) as arrow.fx.IO<J>

@JvmName("parTupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, D, E, G, H, I> parTupledN(
  ctx: CoroutineContext,
  fa: Kind<ForIO, A>,
  fb: Kind<ForIO, B>,
  fc: Kind<ForIO, C>,
  fd: Kind<ForIO, D>,
  fe: Kind<ForIO, E>,
  fg: Kind<ForIO, G>,
  fh: Kind<ForIO, H>,
  fi: Kind<ForIO, I>
): IO<Tuple8<A, B, C, D, E, G, H, I>> = arrow.fx.IO
   .concurrent()
   .parTupledN<A, B, C, D, E, G, H, I>(ctx, fa, fb, fc, fd, fe, fg, fh, fi) as
    arrow.fx.IO<arrow.core.Tuple8<A, B, C, D, E, G, H, I>>

@JvmName("parMapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, D, E, G, H, I, J> CoroutineContext.parMapN(
  fa: Kind<ForIO, A>,
  fb: Kind<ForIO, B>,
  fc: Kind<ForIO, C>,
  fd: Kind<ForIO, D>,
  fe: Kind<ForIO, E>,
  fg: Kind<ForIO, G>,
  fh: Kind<ForIO, H>,
  fi: Kind<ForIO, I>,
  f: Function8<A, B, C, D, E, G, H, I, J>
): IO<J> = arrow.fx.IO.concurrent().run {
  this@parMapN.parMapN<A, B, C, D, E, G, H, I, J>(fa, fb, fc, fd, fe, fg, fh, fi, f) as
    arrow.fx.IO<J>
}

@JvmName("parMapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, D, E, G, H, I, J, K> parMapN(
  ctx: CoroutineContext,
  fa: Kind<ForIO, A>,
  fb: Kind<ForIO, B>,
  fc: Kind<ForIO, C>,
  fd: Kind<ForIO, D>,
  fe: Kind<ForIO, E>,
  fg: Kind<ForIO, G>,
  fh: Kind<ForIO, H>,
  fi: Kind<ForIO, I>,
  fj: Kind<ForIO, J>,
  f: Function1<Tuple9<A, B, C, D, E, G, H, I, J>, K>
): IO<K> = arrow.fx.IO
   .concurrent()
   .parMapN<A, B, C, D, E, G, H, I, J, K>(ctx, fa, fb, fc, fd, fe, fg, fh, fi, fj, f) as
    arrow.fx.IO<K>

@JvmName("parTupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, D, E, G, H, I, J> parTupledN(
  ctx: CoroutineContext,
  fa: Kind<ForIO, A>,
  fb: Kind<ForIO, B>,
  fc: Kind<ForIO, C>,
  fd: Kind<ForIO, D>,
  fe: Kind<ForIO, E>,
  fg: Kind<ForIO, G>,
  fh: Kind<ForIO, H>,
  fi: Kind<ForIO, I>,
  fj: Kind<ForIO, J>
): IO<Tuple9<A, B, C, D, E, G, H, I, J>> = arrow.fx.IO
   .concurrent()
   .parTupledN<A, B, C, D, E, G, H, I, J>(ctx, fa, fb, fc, fd, fe, fg, fh, fi, fj) as
    arrow.fx.IO<arrow.core.Tuple9<A, B, C, D, E, G, H, I, J>>

@JvmName("parMapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, D, E, G, H, I, J, K> CoroutineContext.parMapN(
  fa: Kind<ForIO, A>,
  fb: Kind<ForIO, B>,
  fc: Kind<ForIO, C>,
  fd: Kind<ForIO, D>,
  fe: Kind<ForIO, E>,
  fg: Kind<ForIO, G>,
  fh: Kind<ForIO, H>,
  fi: Kind<ForIO, I>,
  fj: Kind<ForIO, J>,
  f: Function9<A, B, C, D, E, G, H, I, J, K>
): IO<K> = arrow.fx.IO.concurrent().run {
  this@parMapN.parMapN<A, B, C, D, E, G, H, I, J, K>(fa, fb, fc, fd, fe, fg, fh, fi, fj, f) as
    arrow.fx.IO<K>
}

@JvmName("raceN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B> CoroutineContext.raceN(fa: Kind<ForIO, A>, fb: Kind<ForIO, B>): IO<Either<A, B>> =
    arrow.fx.IO.concurrent().run {
  this@raceN.raceN<A, B>(fa, fb) as arrow.fx.IO<arrow.core.Either<A, B>>
}

@JvmName("raceN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C> CoroutineContext.raceN(
  fa: Kind<ForIO, A>,
  fb: Kind<ForIO, B>,
  fc: Kind<ForIO, C>
): IO<Race3<A, B, C>> = arrow.fx.IO.concurrent().run {
  this@raceN.raceN<A, B, C>(fa, fb, fc) as arrow.fx.IO<arrow.fx.Race3<A, B, C>>
}

@JvmName("raceN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, D> CoroutineContext.raceN(
  a: Kind<ForIO, A>,
  b: Kind<ForIO, B>,
  c: Kind<ForIO, C>,
  d: Kind<ForIO, D>
): IO<Race4<A, B, C, D>> = arrow.fx.IO.concurrent().run {
  this@raceN.raceN<A, B, C, D>(a, b, c, d) as arrow.fx.IO<arrow.fx.Race4<A, B, C, D>>
}

@JvmName("raceN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, D, E> CoroutineContext.raceN(
  a: Kind<ForIO, A>,
  b: Kind<ForIO, B>,
  c: Kind<ForIO, C>,
  d: Kind<ForIO, D>,
  e: Kind<ForIO, E>
): IO<Race5<A, B, C, D, E>> = arrow.fx.IO.concurrent().run {
  this@raceN.raceN<A, B, C, D, E>(a, b, c, d, e) as arrow.fx.IO<arrow.fx.Race5<A, B, C, D, E>>
}

@JvmName("raceN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, D, E, G> CoroutineContext.raceN(
  a: Kind<ForIO, A>,
  b: Kind<ForIO, B>,
  c: Kind<ForIO, C>,
  d: Kind<ForIO, D>,
  e: Kind<ForIO, E>,
  g: Kind<ForIO, G>
): IO<Race6<A, B, C, D, E, G>> = arrow.fx.IO.concurrent().run {
  this@raceN.raceN<A, B, C, D, E, G>(a, b, c, d, e, g) as arrow.fx.IO<arrow.fx.Race6<A, B, C, D, E,
    G>>
}

@JvmName("raceN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, D, E, G, H> CoroutineContext.raceN(
  a: Kind<ForIO, A>,
  b: Kind<ForIO, B>,
  c: Kind<ForIO, C>,
  d: Kind<ForIO, D>,
  e: Kind<ForIO, E>,
  g: Kind<ForIO, G>,
  h: Kind<ForIO, H>
): IO<Race7<A, B, C, D, E, G, H>> = arrow.fx.IO.concurrent().run {
  this@raceN.raceN<A, B, C, D, E, G, H>(a, b, c, d, e, g, h) as arrow.fx.IO<arrow.fx.Race7<A, B, C,
    D, E, G, H>>
}

@JvmName("raceN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, D, E, G, H, I> CoroutineContext.raceN(
  a: Kind<ForIO, A>,
  b: Kind<ForIO, B>,
  c: Kind<ForIO, C>,
  d: Kind<ForIO, D>,
  e: Kind<ForIO, E>,
  g: Kind<ForIO, G>,
  h: Kind<ForIO, H>,
  i: Kind<ForIO, I>
): IO<Race8<A, B, C, D, E, G, H, I>> = arrow.fx.IO.concurrent().run {
  this@raceN.raceN<A, B, C, D, E, G, H, I>(a, b, c, d, e, g, h, i) as arrow.fx.IO<arrow.fx.Race8<A,
    B, C, D, E, G, H, I>>
}

@JvmName("raceN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, D, E, G, H, I, J> CoroutineContext.raceN(
  a: Kind<ForIO, A>,
  b: Kind<ForIO, B>,
  c: Kind<ForIO, C>,
  d: Kind<ForIO, D>,
  e: Kind<ForIO, E>,
  g: Kind<ForIO, G>,
  h: Kind<ForIO, H>,
  i: Kind<ForIO, I>,
  j: Kind<ForIO, J>
): IO<Race9<A, B, C, D, E, G, H, I, J>> = arrow.fx.IO.concurrent().run {
  this@raceN.raceN<A, B, C, D, E, G, H, I, J>(a, b, c, d, e, g, h, i, j) as
    arrow.fx.IO<arrow.fx.Race9<A, B, C, D, E, G, H, I, J>>
}

@JvmName("Promise")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A> Promise(): IO<Promise<ForIO, A>> = arrow.fx.IO
   .concurrent()
   .Promise<A>() as arrow.fx.IO<arrow.fx.Promise<arrow.fx.ForIO, A>>

@JvmName("Semaphore")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun Semaphore(n: Long): IO<Semaphore<ForIO>> = arrow.fx.IO
   .concurrent()
   .Semaphore(n) as arrow.fx.IO<arrow.fx.Semaphore<arrow.fx.ForIO>>

@JvmName("MVar")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A> MVar(a: A): IO<MVar<ForIO, A>> = arrow.fx.IO
   .concurrent()
   .MVar<A>(a) as arrow.fx.IO<arrow.fx.MVar<arrow.fx.ForIO, A>>

@JvmName("bindingConcurrent")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <B> bindingConcurrent(c: suspend ConcurrentContinuation<ForIO, *>.() -> B): IO<B> = arrow.fx.IO
   .concurrent()
   .bindingConcurrent<B>(c) as arrow.fx.IO<B>

@JvmName("sleep")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun sleep(duration: Duration): IO<Unit> = arrow.fx.IO
   .concurrent()
   .sleep(duration) as arrow.fx.IO<kotlin.Unit>

@JvmName("waitFor")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A> Kind<ForIO, A>.waitFor(duration: Duration, p2_772401952: Kind<ForIO, A>): IO<A> =
    arrow.fx.IO.concurrent().run {
  this@waitFor.waitFor<A>(duration, p2_772401952) as arrow.fx.IO<A>
}

@JvmName("waitFor")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A> Kind<ForIO, A>.waitFor(duration: Duration): IO<A> = arrow.fx.IO.concurrent().run {
  this@waitFor.waitFor<A>(duration) as arrow.fx.IO<A>
}

@JvmName("dispatchers")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun dispatchers(): Dispatchers<ForIO> = arrow.fx.IO
   .concurrent()
   .dispatchers() as arrow.fx.typeclasses.Dispatchers<arrow.fx.ForIO>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(IODeprecation)
inline fun Companion.concurrent(): IODefaultConcurrent = concurrent_singleton

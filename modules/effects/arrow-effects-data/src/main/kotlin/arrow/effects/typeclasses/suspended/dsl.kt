package arrow.effects.typeclasses.suspended

import arrow.Kind
import arrow.core.*
import arrow.data.*
import arrow.data.extensions.list.foldable.foldM
import arrow.data.extensions.list.foldable.sequence_
import arrow.data.extensions.list.traverse.flatTraverse
import arrow.data.extensions.listk.foldable.traverse_
import arrow.data.extensions.listk.monad.monad
import arrow.data.extensions.nonemptylist.semigroup.semigroup
import arrow.effects.CancelToken
import arrow.effects.typeclasses.*
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.Continuation
import arrow.typeclasses.suspended.MonadErrorSyntax
import arrow.typeclasses.suspended.MonadSyntax
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine
import arrow.core.extensions.either.applicative.tupled as tuppledEither
import arrow.data.extensions.validated.applicative.tupled as tuppledVal

interface ListFoldableSyntax<F> : MonadSyntax<F> {

  suspend fun <A, B> List<Kind<F, A>>.traverse_(f: (Kind<F, A>) -> Kind<F, B>): Unit =
    k().traverse_(this@ListFoldableSyntax, f).bind()

  suspend fun <A> List<Kind<F, A>>.sequence_(): Unit =
    k().sequence_(this@ListFoldableSyntax).bind()

  suspend fun <A, B> List<A>.foldM(z: B, f: (B, A) -> Kind<F, B>): B =
    foldM(this@ListFoldableSyntax, z, f).bind()

}

interface ListTraverseSyntax<F> : ListFoldableSyntax<F>, MonadSyntax<F> {

  suspend fun <A, B> List<Kind<F, A>>.traverse(f: (Kind<F, A>) -> Kind<F, B>): List<B> =
    k().traverse(this@ListTraverseSyntax, f).bind()

  suspend fun <A> List<Kind<F, A>>.sequence(): List<A> =
    traverse(::identity)

  suspend fun <A, B> List<A>.flatTraverse(f: (A) -> Kind<F, List<B>>): List<B> =
    flatTraverse(
      ListK.monad(),
      this@ListTraverseSyntax,
      f.andThen { kind -> kind.map { list -> list.k() } }
    ).bind().fix()

  suspend fun <A> List<List<Kind<F, A>>>.flatSequence(): List<A> =
    flatten().sequence()
}

interface ValidatedSyntax<F, E> : MonadSyntax<F>, ApplicativeError<F, E> {

  suspend fun <A, B> validate(
    a: ValidatedNel<E, A>,
    b: ValidatedNel<E, B>
  ): ValidatedNel<E, Tuple2<A, B>> =
    tuppledVal(Nel.semigroup(), a, b)

  suspend fun <A, B, C> validate(
    a: Validated<Nel<E>, A>,
    b: Validated<Nel<E>, B>,
    c: Validated<Nel<E>, C>
  ): ValidatedNel<E, Tuple3<A, B, C>> =
    tuppledVal(Nel.semigroup(), a, b, c)

  suspend fun <A, B, C, D> validate(
    a: Validated<Nel<E>, A>,
    b: Validated<Nel<E>, B>,
    c: Validated<Nel<E>, C>,
    d: Validated<Nel<E>, D>
  ): ValidatedNel<E, Tuple4<A, B, C, D>> =
    tuppledVal(Nel.semigroup(), a, b, c, d)

  suspend operator fun <A> ValidatedNel<E, A>.component1(): A =
    fold<Kind<F, A>>({ raiseError(it.head) }, ::just).bind()

  fun <A> ValidatedNel<E, A>.errors(): List<E> =
    fold({ it.all }, { emptyList() })

}

interface EitherSyntax<F, E> : MonadSyntax<F>, ApplicativeError<F, E> {

  suspend fun <A, B> validate(
    a: Either<E, A>,
    b: Either<E, B>
  ): Either<E, Tuple2<A, B>> =
    tuppledEither(a, b)

  suspend fun <A, B, C> validate(
    a: Either<E, A>,
    b: Either<E, B>,
    c: Either<E, C>
  ): Either<E, Tuple3<A, B, C>> =
    tuppledEither(a, b, c)

  suspend fun <A, B, C> validate(
    a: Either<E, A>,
    b: Either<E, B>,
    c: Either<E, C>,
    d: Either<E, C>
  ): Either<E, Tuple3<A, B, C>> =
    tuppledEither(a, b, c)

  suspend operator fun <A> Either<E, A>.component1(): A =
    fold<Kind<F, A>>(::raiseError, ::just).bind()

}

interface BracketSyntax<F, E> :
  MonadErrorSyntax<F, E>,
  Bracket<F, E>,
  ListTraverseSyntax<F>,
  ValidatedSyntax<F, E>,
  EitherSyntax<F, E> {

  private suspend fun <A> bracketing(fb: Bracket<F, E>.() -> Kind<F, A>): A =
    run<Bracket<F, E>, Kind<F, A>> { fb(this) }.bind()

  suspend fun <A, B> Kind<F, A>.bracketCase(
    unit: Unit = Unit,
    release: (A, ExitCase<E>) -> Kind<F, Unit>,
    use: (A) -> Kind<F, B>
  ): B =
    bracketing { bracketCase(release, use) }

  suspend fun <A, B> Kind<F, A>.bracket(
    unit: Unit = Unit,
    release: (A) -> Kind<F, Unit>,
    use: (A) -> Kind<F, B>
  ): B =
    bracketing { bracket(release, use) }

  suspend fun <A> Kind<F, A>.uncancelable(unit: Unit = Unit): A =
    bracketing { uncancelable() }

  suspend fun <A> Kind<F, A>.guarantee(
    unit: Unit = Unit,
    finalizer: Kind<F, Unit>
  ): A =
    bracketing { guarantee(finalizer) }

  suspend fun <A> Kind<F, A>.guaranteeCase(
    unit: Unit = Unit,
    finalizer: (ExitCase<E>) -> Kind<F, Unit>
  ): A =
    bracketing { guaranteeCase(finalizer) }

}

interface MonadDeferSyntax<F> : BracketSyntax<F, Throwable>, MonadDefer<F> {

  suspend fun <A> effect(
    f: suspend () -> A
  ): A =
    delay {
      lateinit var result: Kind<F, A>
      val continuation = object : Continuation<A> {
        override fun resume(value: A) {
          result = value.just()
        }
        override fun resumeWithException(exception: Throwable) {
          result = raiseError(exception)
        }
        override val context: CoroutineContext = EmptyCoroutineContext
      }
      f.startCoroutine(continuation)
      result
    }.bind().bind()

  fun <A> (suspend MonadDeferCancellableContinuation<F, *>.() -> A).k(): Kind<F, A> =
    bindingCancellable { this.this@k() }.a

  private suspend fun <A> deferring(fb: MonadDefer<F>.() -> Kind<F, A>): A =
    run<MonadDefer<F>, Kind<F, A>> { fb(this) }.bind()

  suspend fun <A> defer(unit: Unit = Unit, fb: () -> Kind<F, A>): A =
    deferring { defer(fb) }

  suspend fun lazy(unit: Unit = Unit): Unit =
    deferring { lazy() }

}

interface AsyncSyntax<F> : MonadDeferSyntax<F>, Async<F> {

  private suspend fun <A> asyncOp(fb: Async<F>.() -> Kind<F, A>): A =
    run<Async<F>, Kind<F, A>> { fb(this) }.bind()

  suspend fun <A> async(
    unit: Unit = Unit,
    fa: Proc<A>
  ): A =
    asyncOp { async(fa) }

  suspend fun <A> asyncF(
    unit: Unit = Unit,
    k: ProcF<F, A>
  ): A =
    asyncOp { asyncF(k) }

  suspend fun <A> delay(
    unit: Unit = Unit,
    ctx: CoroutineContext,
    f: () -> A
  ): A =
    asyncOp { delay(ctx, f) }

  suspend fun <A> defer(
    unit: Unit = Unit,
    ctx: CoroutineContext,
    f: () -> Kind<F, A>
  ): A =
    asyncOp { defer(ctx, f) }

  suspend fun CoroutineContext.shift(unit: Unit = Unit): Unit =
    asyncOp { shift() }

  suspend fun <A> never(unit: Unit = Unit): A =
    asyncOp { never<A>() }

  suspend fun <A> cancelable(
    unit: Unit = Unit,
    k: ((Either<Throwable, A>) -> Unit) -> CancelToken<F>
  ): A =
    asyncOp { cancelable(k) }

  suspend fun <A> cancelableF(
    unit: Unit = Unit,
    k: ((Either<Throwable, A>) -> Unit) -> Kind<F, CancelToken<F>>
  ): A =
    asyncOp { cancelableF(k) }
}

interface EffectSyntax<F> : Effect<F>, AsyncSyntax<F> {

  private suspend fun <A> effects(fb: Effect<F>.() -> Kind<F, A>): A =
    run<Effect<F>, Kind<F, A>> { fb(this) }.bind()

  suspend fun <A> Kind<F, A>.runAsync(
    unit: Unit = Unit,
    cb: (Either<Throwable, A>) -> Kind<F, Unit>
  ): Unit =
    effects { runAsync(cb) }

}

interface ConcurrentSyntax<F> : AsyncSyntax<F>, Concurrent<F> {

  private suspend fun <A> concurrently(fb: Concurrent<F>.() -> Kind<F, A>): A =
    run<Concurrent<F>, Kind<F, A>> { fb(this) }.bind()

  suspend fun <A> async(unit: Unit = Unit, fa: ConnectedProc<F, A>): A =
    concurrently { async(fa) }

  suspend fun <A> asyncF(unit: Unit = Unit, k: ConnectedProcF<F, A>): A =
    concurrently { asyncF(k) }

  suspend fun <A> asyncF(unit1: Unit = Unit, unit2: Unit = Unit, k: ProcF<F, A>): A =
    concurrently { asyncF(k) }

  suspend fun <A> async(unit1: Unit = Unit, unit2: Unit = Unit, fa: Proc<A>): A =
    concurrently { async(fa) }

  suspend fun <A> Kind<F, A>.startF(unit1: Unit = Unit, ctx: CoroutineContext): Fiber<F, A> =
    concurrently { startF(ctx) }

  suspend fun <A, B> racePair(
    unit1: Unit = Unit,
    ctx: CoroutineContext,
    fa: Kind<F, A>,
    fb: Kind<F, B>
  ): RacePair<F, A, B> =
    concurrently { racePair(ctx, fa, fb) }

  suspend fun <A, B, C> raceTriple(
    unit1: Unit = Unit,
    ctx: CoroutineContext,
    fa: Kind<F, A>,
    fb: Kind<F, B>,
    fc: Kind<F, C>
  ): RaceTriple<F, A, B, C> =
    concurrently { raceTriple(ctx, fa, fb, fc) }

  suspend fun <A, B, C> parMap(
    ctx: CoroutineContext = EmptyCoroutineContext,
    fa: Kind<F, A>,
    fb: Kind<F, B>,
    f: (A, B) -> C
  ): C =
    concurrently { parMapN(ctx, fa, fb, f) }

  suspend fun <A, B, C, D> parMapN(
    unit1: Unit = Unit,
    ctx: CoroutineContext,
    fa: Kind<F, A>,
    fb: Kind<F, B>,
    fc: Kind<F, C>,
    f: (A, B, C) -> D
  ): D =
    concurrently { parMapN(ctx, fa, fb, fc, f) }

  suspend fun <A, B, C, D, E> parMapN(
    unit1: Unit = Unit,
    ctx: CoroutineContext,
    fa: Kind<F, A>,
    fb: Kind<F, B>,
    fc: Kind<F, C>,
    fd: Kind<F, D>,
    f: (A, B, C, D) -> E
  ): E =
    concurrently { parMapN(ctx, fa, fb, fc, fd, f) }

  suspend fun <A, B, C, D, E, G> parMapN(
    unit1: Unit = Unit,
    ctx: CoroutineContext,
    fa: Kind<F, A>,
    fb: Kind<F, B>,
    fc: Kind<F, C>,
    fd: Kind<F, D>,
    fe: Kind<F, E>,
    f: (A, B, C, D, E) -> G
  ): G =
    concurrently { parMapN(ctx, fa, fb, fc, fd, fe, f) }

  suspend fun <A, B, C, D, E, G, H> parMapN(
    unit1: Unit = Unit,
    ctx: CoroutineContext,
    fa: Kind<F, A>,
    fb: Kind<F, B>,
    fc: Kind<F, C>,
    fd: Kind<F, D>,
    fe: Kind<F, E>,
    fg: Kind<F, G>,
    f: (A, B, C, D, E, G) -> H
  ): H =
    concurrently { parMapN(ctx, fa, fb, fc, fd, fe, fg, f) }

  suspend fun <A, B, C, D, E, G, H, I> parMapN(
    unit1: Unit = Unit,
    ctx: CoroutineContext,
    fa: Kind<F, A>,
    fb: Kind<F, B>,
    fc: Kind<F, C>,
    fd: Kind<F, D>,
    fe: Kind<F, E>,
    fg: Kind<F, G>,
    fh: Kind<F, H>,
    f: (A, B, C, D, E, G, H) -> I
  ): I =
    concurrently { parMapN(ctx, fa, fb, fc, fd, fe, fg, fh, f) }

  suspend fun <A, B, C, D, E, G, H, I, J> parMapN(
    unit1: Unit = Unit,
    ctx: CoroutineContext,
    fa: Kind<F, A>,
    fb: Kind<F, B>,
    fc: Kind<F, C>,
    fd: Kind<F, D>,
    fe: Kind<F, E>,
    fg: Kind<F, G>,
    fh: Kind<F, H>,
    fi: Kind<F, I>,
    f: (A, B, C, D, E, G, H, I) -> J
  ): J =
    concurrently { parMapN(ctx, fa, fb, fc, fd, fe, fg, fh, fi, f) }

  suspend fun <A, B, C, D, E, G, H, I, J, K> parMapN(
    unit1: Unit = Unit,
    ctx: CoroutineContext,
    fa: Kind<F, A>,
    fb: Kind<F, B>,
    fc: Kind<F, C>,
    fd: Kind<F, D>,
    fe: Kind<F, E>,
    fg: Kind<F, G>,
    fh: Kind<F, H>,
    fi: Kind<F, I>,
    fj: Kind<F, J>,
    f: (A, B, C, D, E, G, H, I, J) -> K
  ): K =
    concurrently { parMapN(ctx, fa, fb, fc, fd, fe, fg, fh, fi, fj, f) }

  suspend fun <A, B> raceN(
    unit1: Unit = Unit,
    ctx: CoroutineContext,
    fa: Kind<F, A>,
    fb: Kind<F, B>
  ): Race2<A, B> =
    concurrently { raceN(ctx, fa, fb) }

  suspend fun <A, B, C> raceN(
    unit1: Unit = Unit,
    ctx: CoroutineContext,
    fa: Kind<F, A>,
    fb: Kind<F, B>,
    fc: Kind<F, C>
  ): Race3<A, B, C> =
    concurrently { raceN(ctx, fa, fb, fc) }

  suspend fun <A, B, C, D> raceN(
    unit1: Unit = Unit,
    ctx: CoroutineContext,
    a: Kind<F, A>,
    b: Kind<F, B>,
    c: Kind<F, C>,
    d: Kind<F, D>
  ): Race4<A, B, C, D> =
    concurrently { raceN(ctx, a, b, c, d) }

  suspend fun <A, B, C, D, E> raceN(
    unit1: Unit = Unit,
    ctx: CoroutineContext,
    a: Kind<F, A>,
    b: Kind<F, B>,
    c: Kind<F, C>,
    d: Kind<F, D>,
    e: Kind<F, E>
  ): Race5<A, B, C, D, E> =
    concurrently { raceN(ctx, a, b, c, d, e) }

  suspend fun <A, B, C, D, E, G> raceN(
    unit1: Unit = Unit,
    ctx: CoroutineContext,
    a: Kind<F, A>,
    b: Kind<F, B>,
    c: Kind<F, C>,
    d: Kind<F, D>,
    e: Kind<F, E>,
    g: Kind<F, G>
  ): Race6<A, B, C, D, E, G> =
    concurrently { raceN(ctx, a, b, c, d, e, g) }

  suspend fun <A, B, C, D, E, G, H> raceN(
    unit1: Unit = Unit,
    ctx: CoroutineContext,
    a: Kind<F, A>,
    b: Kind<F, B>,
    c: Kind<F, C>,
    d: Kind<F, D>,
    e: Kind<F, E>,
    g: Kind<F, G>,
    h: Kind<F, H>
  ): Race7<A, B, C, D, E, G, H> =
    concurrently { raceN(ctx, a, b, c, d, e, g, h) }

  suspend fun <A, B, C, D, E, G, H, I> raceN(
    unit1: Unit = Unit,
    ctx: CoroutineContext,
    a: Kind<F, A>,
    b: Kind<F, B>,
    c: Kind<F, C>,
    d: Kind<F, D>,
    e: Kind<F, E>,
    g: Kind<F, G>,
    h: Kind<F, H>,
    i: Kind<F, I>
  ): Race8<A, B, C, D, E, G, H, I> =
    concurrently { raceN(ctx, a, b, c, d, e, g, h, i) }

  suspend fun <A, B, C, D, E, G, H, I, J> raceN(
    unit1: Unit = Unit,
    ctx: CoroutineContext,
    a: Kind<F, A>,
    b: Kind<F, B>,
    c: Kind<F, C>,
    d: Kind<F, D>,
    e: Kind<F, E>,
    g: Kind<F, G>,
    h: Kind<F, H>,
    i: Kind<F, I>,
    j: Kind<F, J>
  ): Race9<A, B, C, D, E, G, H, I, J> =
    concurrently { raceN(ctx, a, b, c, d, e, g, h, i, j) }

}

interface ConcurrentEffectSyntax<F> : ConcurrentEffect<F>, EffectSyntax<F> {

  private suspend fun <A> concurrentEffect(fb: ConcurrentEffect<F>.() -> Kind<F, A>): A =
    run<ConcurrentEffect<F>, Kind<F, A>> { fb(this) }.bind()

  suspend fun <A> Kind<F, A>.runAsyncCancellable(
    unit: Unit = Unit,
    cb: (Either<Throwable, A>) -> Kind<F, Unit>
  ): Disposable =
    concurrentEffect { runAsyncCancellable(cb) }

}

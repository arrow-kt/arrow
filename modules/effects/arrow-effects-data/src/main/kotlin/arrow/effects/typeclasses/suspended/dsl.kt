package arrow.effects.typeclasses.suspended


import arrow.Kind
import arrow.core.*
import arrow.data.*
import arrow.data.extensions.list.foldable.sequence_
import arrow.data.extensions.list.traverse.flatTraverse
import arrow.data.extensions.listk.foldable.traverse_
import arrow.data.extensions.listk.monad.monad
import arrow.data.extensions.nonemptylist.semigroup.semigroup
import arrow.effects.CancelToken
import arrow.effects.typeclasses.*
import arrow.typeclasses.*
import arrow.typeclasses.Continuation
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine
import arrow.core.extensions.either.applicative.tupled as tuppledEither
import arrow.data.extensions.validated.applicative.tupled as tuppledVal

interface BindSyntax<F> {
  suspend fun <A> Kind<F, A>.bind(): A
  suspend operator fun <A> Kind<F, A>.component1(): A =
    bind()
  fun <A> (suspend () -> A).k(): Kind<F, A>
  fun <A, B> (suspend (A) -> B).k(): (Kind<F, A>) -> Kind<F, B> =
    { suspend { this(it.bind()) }.k() }
  fun <A, B> (suspend (A) -> B).k(unit: Unit = Unit): (A) -> Kind<F, B> =
    { suspend { this(it) }.k() }
}

interface FunctorSyntax<F> : Functor<F>, BindSyntax<F> {
  suspend fun <A, B> map(fa: suspend () -> A, f: (A) -> B): B =
    fa.k().map(f).bind()
}

interface ApplicativeSyntax<F> : FunctorSyntax<F>, Applicative<F> {

  private suspend fun <A> applicative(fb: Applicative<F>.() -> Kind<F, A>): A =
    run<Applicative<F>, Kind<F, A>> { fb(this) }.bind()

  suspend operator fun <A, B> Kind<F, A>.rangeTo(fb: Kind<F, B>): Tuple2<A, B> =
    tupled(fb)

  suspend operator fun <A, B, C> Tuple2<A, B>.rangeTo(fc: Kind<F, C>): Tuple3<A, B, C> =
    Tuple3(a, b, fc.bind())

  suspend operator fun <A, B, C, D> Tuple3<A, B, C>.rangeTo(fd: Kind<F, D>): Tuple4<A, B, C, D> =
    Tuple4(a, b, c, fd.bind())

  suspend operator fun <A, B, C, D, E> Tuple4<A, B, C, D>.rangeTo(fe: Kind<F, E>): Tuple5<A, B, C, D, E> =
    Tuple5(a, b, c, d, fe.bind())

  suspend operator fun <A, B, C, D, E, FF> Tuple5<A, B, C, D, E>.rangeTo(ff: Kind<F, FF>): Tuple6<A, B, C, D, E, FF> =
    Tuple6(a, b, c, d, e, ff.bind())

  suspend operator fun <A, B, C, D, E, FF, G> Tuple6<A, B, C, D, E, FF>.rangeTo(fg: Kind<F, G>): Tuple7<A, B, C, D, E, FF, G> =
    Tuple7(a, b, c, d, e, f, fg.bind())

  suspend fun <A, B, Z> Kind<F, A>.map(
    fb: Kind<F, B>,
    f: (Tuple2<A, B>) -> Z
  ): Z =
    applicative { map(this@map, fb, f) }

  suspend fun <A, B, C, Z> Kind<F, A>.map(
    fb: Kind<F, B>,
    fc: Kind<F, C>,
    f: (Tuple3<A, B, C>) -> Z
  ): Z =
    applicative { map(this@map, fb, fc, f) }

  suspend fun <A, B, C, D, Z> Kind<F, A>.map(
    fb: Kind<F, B>,
    fc: Kind<F, C>,
    fd: Kind<F, D>,
    f: (Tuple4<A, B, C, D>) -> Z
  ): Z =
    applicative { map(this@map, fb, fc, fd, f) }

  suspend fun <A, B> Kind<F, A>.tupled(fb: Kind<F, B>): Tuple2<A, B> =
    super.tupled(this, fb).bind()

  suspend fun <A, B, C> Kind<F, A>.tupled(
    fb: Kind<F, B>,
    fc: Kind<F, C>
  ): Tuple3<A, B, C> =
    super.tupled(this, fb, fc).bind()

  suspend fun <A, B, C, D> Kind<F, A>.tupled(
    fb: Kind<F, B>,
    fc: Kind<F, C>,
    fd: Kind<F, D>
  ): Tuple4<A, B, C, D> =
    super.tupled(this, fb, fc, fd).bind()

  suspend fun <A, B, C, D, E> Kind<F, A>.tupled(
    fb: Kind<F, B>,
    fc: Kind<F, C>,
    fd: Kind<F, D>,
    fe: Kind<F, E>
  ): Tuple5<A, B, C, D, E> =
    super.tupled(this, fb, fc, fd, fe).bind()

}

interface ApplicativeErrorSyntax<F, E> : ApplicativeError<F, E>, ApplicativeSyntax<F> {
  suspend fun <A> E.raiseError(): A =
    run<ApplicativeError<F, E>, Kind<F, A>> { raiseError(this@raiseError) }.bind()

  suspend fun <A> Kind<F, A>.handleErrorWith(unit: Unit = Unit, f: (E) -> Kind<F, A>): A =
    run<ApplicativeError<F, E>, Kind<F, A>> { handleErrorWith(f) }.bind()

  suspend fun <A> OptionOf<A>.getOrRaiseError(f: () -> E): A =
    run<ApplicativeError<F, E>, Kind<F, A>> { this@getOrRaiseError.fromOption(f) }.bind()

  suspend fun <A, B> Either<B, A>.getOrRaiseError(f: (B) -> E): A =
    run<ApplicativeError<F, E>, Kind<F, A>> { this@getOrRaiseError.fromEither(f) }.bind()

  suspend fun <A> TryOf<A>.getOrRaiseError(f: (Throwable) -> E): A =
    run<ApplicativeError<F, E>, Kind<F, A>> { this@getOrRaiseError.fromTry(f) }.bind()

  suspend fun <A> handleError(fa: suspend () -> A, f: (E) -> A): A =
    run<ApplicativeError<F, E>, Kind<F, A>> { fa.k().handleError(f) }.bind()

  suspend fun <A> attempt(fa: suspend () -> A): Either<E, A> =
    run<ApplicativeError<F, E>, Kind<F, Either<E, A>>> { fa.k().attempt() }.bind()

}

interface MonadSyntax<F> : ApplicativeSyntax<F>, Monad<F> {

  suspend infix fun <A, B> (suspend () -> A).followedBy(fb: suspend () -> B): B =
    run<Monad<F>, Kind<F, B>> { this@followedBy.k().followedBy(fb.k()) }.bind()

  suspend fun <A, B> (suspend () -> A).forEffect(fb: suspend () -> B): A =
    run<Monad<F>, Kind<F, A>> { this@forEffect.k().forEffect(fb.k()) }.bind()

  suspend infix fun <A> Kind<F, A>.effectM(f: (A) -> Kind<F, A>): A =
    run<Monad<F>, Kind<F, A>> { effectM(f) }.bind()

  suspend infix fun <A> Kind<F, A>.mproduct(f: (A) -> Kind<F, A>): Tuple2<A, A> =
    run<Monad<F>, Kind<F, Tuple2<A, A>>> { mproduct(f) }.bind()

}

interface MonadErrorSyntax<F, E> : MonadSyntax<F>, MonadError<F, E> {
  suspend fun <A> ensure(fa: suspend () -> A, error: () -> E, predicate: (A) -> Boolean): A =
    run<Monad<F>, Kind<F, A>> { fa.k().ensure(error, predicate) }.bind()
}

interface ListFoldableSyntax<F> : MonadSyntax<F> {

  suspend fun <A, B> List<suspend () -> A>.traverse_(f: suspend (A) -> B): Unit =
    k().map { it.k() }.traverse_(this@ListFoldableSyntax, f.k()).bind()

  suspend fun <A> List<suspend () -> A>.sequence_(): Unit =
    k().map { it.k() }.sequence_(this@ListFoldableSyntax).bind()

}

interface ListTraverseSyntax<F> : ListFoldableSyntax<F>, MonadSyntax<F> {

  suspend fun <A, B> List<suspend () -> A>.traverse(f: suspend (A) -> B): List<B> =
    k().map { it.k() }.traverse(this@ListTraverseSyntax, f.k()).bind()

  suspend fun <A> List<suspend () -> A>.sequence(): List<A> =
    traverse { it }

  suspend fun <A, B> List<A>.flatTraverse(f: (A) -> Kind<F, List<B>>): List<B> =
    flatTraverse(
      ListK.monad(),
      this@ListTraverseSyntax,
      f.andThen { kind -> kind.map { list -> list.k() } }
    ).bind().fix()

  suspend fun <A> List<List<suspend () -> A>>.flatSequence(): List<A> =
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

  override fun <A> (suspend () -> A).k(): Kind<F, A> =
    delay {
      val result: AtomicReference<A> = AtomicReference()
      val continuation = object : Continuation<A> {
        override fun resume(value: A) {
          result.set(value)
        }
        override fun resumeWithException(exception: Throwable) {
          throw exception
        }
        override val context: CoroutineContext = EmptyCoroutineContext
      }
      this@k.startCoroutine(continuation)
      result.get()
    }

  suspend fun <A> effect(f: suspend () -> A): A = f.k().bind()

  suspend operator fun <A> (suspend () -> A).not(): A = k().bind()

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
    fa: suspend () -> A,
    fb: suspend () -> B,
    f: (A, B) -> C
  ): C =
    concurrently { parMapN(ctx, fa.k(), fb.k(), f) }

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

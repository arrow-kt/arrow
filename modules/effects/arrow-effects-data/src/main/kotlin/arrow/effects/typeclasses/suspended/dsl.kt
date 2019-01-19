package arrow.effects.typeclasses.suspended


import arrow.Kind
import arrow.core.*
import arrow.data.Nel
import arrow.data.Validated
import arrow.data.ValidatedNel
import arrow.data.extensions.nonemptylist.semigroup.semigroup
import arrow.effects.typeclasses.*
import arrow.typeclasses.*
import arrow.typeclasses.Continuation
import arrow.typeclasses.suspended.BindSyntax
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine
import arrow.core.extensions.either.applicative.tupled as tuppledEither
import arrow.data.extensions.validated.applicative.tupled as tuppledVal

interface SuspendToKindSyntax<F> : BindSyntax<F> {
  fun <A> (suspend () -> A).k(): Kind<F, A>
  fun <A, B> (suspend (A) -> B).k(): (Kind<F, A>) -> Kind<F, B> =
    { suspend { this(it.bind()) }.k() }
  fun <A, B, C> (suspend (A, B) -> C).k(): (Kind<F, A>, Kind<F, B>) -> Kind<F, C> =
    { ka, kb -> suspend { this(ka.bind(), kb.bind()) }.k() }
  fun <A, B, C, D> (suspend (A, B, C) -> D).k(): (Kind<F, A>, Kind<F, B>, Kind<F, C>) -> Kind<F, D> =
    { ka, kb, kc -> suspend { this(ka.bind(), kb.bind(), kc.bind()) }.k() }
  
  fun <A, B> (suspend (A) -> B).kr(unit: Unit = Unit): (A) -> Kind<F, B> =
    { suspend { this(it) }.k() }
  fun <A, B, C> (suspend (A, B) -> C).kr(): (A, B) -> Kind<F, C> =
    { a, b -> suspend { this(a, b) }.k() }
  fun <A, B, C, D> (suspend (A, B, C) -> D).kr(): (A, B, C) -> Kind<F, D> =
    { a, b, c -> suspend { this(a, b, c) }.k() }
}

interface FunctorSyntax<F> : Functor<F>, SuspendToKindSyntax<F>

interface ApplicativeSyntax<F> : SuspendToKindSyntax<F>, Applicative<F>

interface ApplicativeErrorSyntax<F, E> : ApplicativeError<F, E>, ApplicativeSyntax<F> {

  suspend fun <A> E.raiseError(): A =
    run<ApplicativeError<F, E>, Kind<F, A>> { raiseError(this@raiseError) }.bind()

  suspend fun <A> raiseError(e: E, unit: Unit = Unit): A =
    run<ApplicativeError<F, E>, Kind<F, A>> { raiseError(e) }.bind()

  suspend fun <A> handleError(fa: suspend () -> A, recover: suspend (E) -> A): A =
    run<ApplicativeError<F, E>, Kind<F, A>> { fa.k().handleErrorWith(recover.kr()) }.bind()

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

interface MonadSyntax<F> : ApplicativeSyntax<F>, Monad<F>

interface MonadErrorSyntax<F, E> : MonadSyntax<F>, MonadError<F, E> {
  suspend fun <A> ensure(fa: suspend () -> A, error: () -> E, predicate: (A) -> Boolean): A =
    run<Monad<F>, Kind<F, A>> { fa.k().ensure(error, predicate) }.bind()
}

interface ListFoldableSyntax<F> : MonadSyntax<F> {

  suspend fun <A, B> List<suspend () -> A>.traverse_(f: suspend (A) -> B): Unit =
    forEach { f(it()) }

  suspend fun <A> List<suspend () -> A>.sequence_(): Unit =
    traverse_(::effectIdentity)

}

interface ListTraverseSyntax<F> : ListFoldableSyntax<F>, MonadSyntax<F> {

  suspend fun <A, B> List<suspend () -> A>.traverse(f: suspend (A) -> B): List<B> =
    map { fa : suspend () -> A -> f(fa()) }

  suspend fun <A> List<suspend () -> A>.sequence(): List<A> =
    traverse(::effectIdentity)

  suspend fun <A, B> List<A>.flatTraverse(f: suspend (A) -> List<B>): List<B> =
    flatMap { f(it) }

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

  private suspend fun <A> bracketing(fb: suspend Bracket<F, E>.() -> Kind<F, A>): A =
    run<Bracket<F, E>, Kind<F, A>> { fb(this) }.bind()

  suspend fun <A, B> bracketCase(
    f: suspend () -> A,
    release: suspend (A, ExitCase<E>) -> Unit,
    use: suspend (A) -> B
  ): B =
    bracketing { f.k().bracketCase(release.kr(), use.kr()) }

  suspend fun <A, B> bracket(
    f: suspend () -> A,
    release: suspend (A) -> Unit,
    use: suspend (A) -> B
  ): B =
    bracketing { f.k().bracket(release.kr(), use.kr()) }

  suspend fun <A> uncancelable(f: suspend () -> A): A =
    bracketing { f.k().uncancelable() }

  suspend fun <A> guarantee(
    f: suspend () -> A,
    finalizer: suspend () -> Unit
  ): A =
    bracketing { f.k().guarantee(finalizer.k()) }

  suspend fun <A> Kind<F, A>.guaranteeCase(
    unit: Unit = Unit,
    finalizer: suspend (ExitCase<E>) -> Unit
  ): A =
    bracketing { guaranteeCase(finalizer.kr()) }

}

interface MonadDeferSyntax<F> : BracketSyntax<F, Throwable>, MonadDefer<F> {

  suspend fun <A> effect(f: suspend () -> A): A = f()

  suspend operator fun <A> (suspend () -> A).not(): A = this()

  private suspend fun <A> deferring(fb: MonadDefer<F>.() -> Kind<F, A>): A =
    run<MonadDefer<F>, Kind<F, A>> { fb(this) }.bind()

  suspend fun <A> defer(unit: Unit = Unit, fb: () -> Kind<F, A>): A =
    deferring { defer(fb) }

}

interface AsyncSyntax<F> : MonadDeferSyntax<F>, Async<F> {

  override fun <A> (suspend () -> A).k(): Kind<F, A> =
    async { cb ->
      startCoroutine(object : Continuation<A> {
        override fun resume(value: A) {
          cb(value.right())
        }

        override fun resumeWithException(exception: Throwable) {
          cb(exception.left())
        }

        override val context: CoroutineContext = EmptyCoroutineContext
      })
    }


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
  
}

interface EffectSyntax<F> : Effect<F>, AsyncSyntax<F> {

  private suspend fun <A> effects(fb: Effect<F>.() -> Kind<F, A>): A =
    run<Effect<F>, Kind<F, A>> { fb(this) }.bind()

  suspend fun <A> (suspend () -> A).runAsync(cb: suspend (Either<Throwable, A>) -> Unit): Unit =
    effects { this@runAsync.k().runAsync(cb.kr()) }

}

typealias STuple2<A, B> = Tuple2<suspend () -> A, suspend () -> B>
typealias STuple3<A, B, C> = Tuple3<suspend () -> A, suspend () -> B, suspend () -> C>
typealias STuple4<A, B, C, D> = Tuple4<suspend () -> A, suspend () -> B, suspend () -> C, suspend () -> D>
typealias STuple5<A, B, C, D, E> = Tuple5<suspend () -> A, suspend () -> B, suspend () -> C, suspend () -> D, suspend () -> E>

typealias SProc<A> = suspend ((Either<Throwable, A>) -> Unit) -> Unit

interface ConcurrentSyntax<F> : AsyncSyntax<F>, Concurrent<F> {

  private suspend fun <A> concurrently(fb: Concurrent<F>.() -> Kind<F, A>): A =
    run<Concurrent<F>, Kind<F, A>> { fb(this) }.bind()

  suspend fun <A> async(k: SProc<A>): A =
    concurrently { asyncF(k.kr()) }

  suspend fun <A> (suspend () -> A).startFiber(ctx: CoroutineContext): Fiber<F, A> =
    concurrently { this@startFiber.k().startF(ctx) }

  suspend fun <A, B> racePair(
    unit1: Unit = Unit,
    ctx: CoroutineContext,
    fa: suspend () -> A,
    fb: suspend () -> B
  ): RacePair<F, A, B> =
    concurrently { racePair(ctx, fa.k(), fb.k()) }

  suspend fun <A, B, C> raceTriple(
    unit1: Unit = Unit,
    ctx: CoroutineContext,
    fa: suspend () -> A,
    fb: suspend () -> B,
    fc: suspend () -> C
  ): RaceTriple<F, A, B, C> =
    concurrently { raceTriple(ctx, fa.k(), fb.k(), fc.k()) }

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

interface ConcurrentEffectSyntax<F> : ConcurrentEffect<F>, EffectSyntax<F> {

  private suspend fun <A> concurrentEffect(fb: ConcurrentEffect<F>.() -> Kind<F, A>): A =
    run<ConcurrentEffect<F>, Kind<F, A>> { fb(this) }.bind()

  suspend fun <A> (suspend () -> A).runAsyncCancellable(cb: suspend (Either<Throwable, A>) -> Unit): Disposable =
    concurrentEffect { this@runAsyncCancellable.k().runAsyncCancellable(cb.kr()) }

}

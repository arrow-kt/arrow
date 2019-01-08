package arrow.effects.typeclasses.suspended

import arrow.Kind
import arrow.core.*
import arrow.data.Nel
import arrow.data.Validated
import arrow.data.ValidatedNel
import arrow.data.extensions.list.foldable.sequence_
import arrow.data.extensions.nonemptylist.semigroup.semigroup
import arrow.data.k
import arrow.effects.CancelToken
import arrow.effects.typeclasses.*
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.suspended.MonadErrorSyntax
import arrow.typeclasses.suspended.MonadSyntax
import kotlin.coroutines.CoroutineContext
import arrow.core.extensions.either.applicative.tupled as tuppledEither
import arrow.data.extensions.validated.applicative.tupled as tuppledVal

interface ListTraverseSyntax<F> : MonadSyntax<F> {
  suspend fun <A, B> List<Kind<F, A>>.traverse(f: (Kind<F, A>) -> Kind<F, B>): List<B> =
    k().traverse(this@ListTraverseSyntax, f).bind()

  suspend fun <A> List<Kind<F, A>>.sequence(): List<A> =
    traverse(::identity)

  suspend fun <A> List<Kind<F, A>>.sequence_(): Unit =
    k().sequence_(this@ListTraverseSyntax).bind()

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

  private suspend fun <A> deferring(fb: MonadDefer<F>.() -> Kind<F, A>): A =
    run<MonadDefer<F>, Kind<F, A>> { fb(this) }.bind()

  suspend fun <A> defer(unit: Unit = Unit, fb: () -> Kind<F, A>): A =
    deferring { defer(fb) }

  suspend fun <A> delay(unit: Unit = Unit, f: () -> A): A =
    deferring { delay(f) }

  suspend fun <A> delay(unit: Unit = Unit, fb: Kind<F, A>): A =
    deferring { delay(fb) }

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

  private suspend fun <A> effect(fb: Effect<F>.() -> Kind<F, A>): A =
    run<Effect<F>, Kind<F, A>> { fb(this) }.bind()

  suspend fun <A> Kind<F, A>.runAsync(
    unit: Unit = Unit,
    cb: (Either<Throwable, A>) -> Kind<F, Unit>
  ): Unit =
    effect { runAsync(cb) }

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


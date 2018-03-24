package arrow.effects

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.effects.typeclasses.Async
import arrow.effects.typeclasses.Effect
import arrow.effects.typeclasses.MonadSuspend
import arrow.effects.typeclasses.Proc
import arrow.instance
import arrow.typeclasses.*

@instance(ObservableK::class)
interface ObservableKFunctorInstance : Functor<ForObservableK> {
    override fun <A, B> map(fa: ObservableKOf<A>, f: (A) -> B): ObservableK<B> =
            fa.fix().map(f)
}

@instance(ObservableK::class)
interface ObservableKApplicativeInstance : Applicative<ForObservableK> {
    override fun <A, B> ObservableKOf<A>.ap(ff: ObservableKOf<(A) -> B>): ObservableK<B> =
            fix().ap(ff)

    override fun <A, B> map(fa: ObservableKOf<A>, f: (A) -> B): ObservableK<B> =
            fa.fix().map(f)

    override fun <A> pure(a: A): ObservableK<A> =
            ObservableK.pure(a)
}

@instance(ObservableK::class)
interface ObservableKMonadInstance : Monad<ForObservableK> {
    override fun <A, B> ObservableKOf<A>.ap(ff: ObservableKOf<(A) -> B>): ObservableK<B> =
            fix().ap(ff)

    override fun <A, B> Kind<ForObservableK, A>.flatMap(f: (A) -> Kind<ForObservableK, B>): ObservableK<B> =
            fix().flatMap(f)

    override fun <A, B> map(fa: ObservableKOf<A>, f: (A) -> B): ObservableK<B> =
            fa.fix().map(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, ObservableKOf<arrow.core.Either<A, B>>>): ObservableK<B> =
            ObservableK.tailRecM(a, f)

    override fun <A> pure(a: A): ObservableK<A> =
            ObservableK.pure(a)
}

@instance(ObservableK::class)
interface ObservableKFoldableInstance : arrow.typeclasses.Foldable<ForObservableK> {
    override fun <A, B> foldLeft(fa: ObservableKOf<A>, b: B, f: kotlin.Function2<B, A, B>): B =
            fa.fix().foldLeft(b, f)

    override fun <A, B> foldRight(fa: ObservableKOf<A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): arrow.core.Eval<B> =
            fa.fix().foldRight(lb, f)
}

@instance(ObservableK::class)
interface ObservableKTraverseInstance : arrow.typeclasses.Traverse<ForObservableK> {
    override fun <A, B> map(fa: ObservableKOf<A>, f: (A) -> B): ObservableK<B> =
            fa.fix().map(f)

    override fun <G, A, B> Applicative<G>.traverse(fa: ObservableKOf<A>, f: (A) -> Kind<G, B>): arrow.Kind<G, ObservableK<B>> =
            fa.fix().traverse(this, f)

    override fun <A, B> foldLeft(fa: ObservableKOf<A>, b: B, f: kotlin.Function2<B, A, B>): B =
            fa.fix().foldLeft(b, f)

    override fun <A, B> foldRight(fa: ObservableKOf<A>, lb: arrow.core.Eval<B>, f: kotlin.Function2<A, arrow.core.Eval<B>, arrow.core.Eval<B>>): arrow.core.Eval<B> =
            fa.fix().foldRight(lb, f)
}

@instance(ObservableK::class)
interface ObservableKApplicativeErrorInstance :
        ObservableKApplicativeInstance,
        ApplicativeError<ForObservableK, Throwable> {
    override fun <A> raiseError(e: Throwable): ObservableK<A> =
            ObservableK.raiseError(e)

    override fun <A> ObservableKOf<A>.handleErrorWith(f: (Throwable) -> ObservableKOf<A>): ObservableK<A> =
            fix().handleErrorWith(null) { f(it).fix() }
}

@instance(ObservableK::class)
interface ObservableKMonadErrorInstance :
        ObservableKMonadInstance,
        MonadError<ForObservableK, Throwable> {
    override fun <A> raiseError(e: Throwable): ObservableK<A> =
            ObservableK.raiseError(e)

    override fun <A> ObservableKOf<A>.handleErrorWith(f: (Throwable) -> ObservableKOf<A>): ObservableK<A> =
            fix().handleErrorWith(null) { f(it).fix() }
}

@instance(ObservableK::class)
interface ObservableKMonadSuspendInstance :
        ObservableKMonadErrorInstance,
        MonadSuspend<ForObservableK> {
    override fun <A> suspend(fa: () -> ObservableKOf<A>): ObservableK<A> =
            ObservableK.suspend(fa)
}

@instance(ObservableK::class)
interface ObservableKAsyncInstance :
        ObservableKMonadSuspendInstance,
        Async<ForObservableK> {
    override fun <A> async(fa: Proc<A>): ObservableK<A> =
            ObservableK.runAsync(fa)
}

@instance(ObservableK::class)
interface ObservableKEffectInstance :
        ObservableKAsyncInstance,
        Effect<ForObservableK> {
    override fun <A> ObservableKOf<A>.runAsync(cb: (Either<Throwable, A>) -> ObservableKOf<Unit>): ObservableK<Unit> =
            fix().runAsync(cb)
}

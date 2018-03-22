package arrow.effects

import arrow.core.Either
import arrow.effects.typeclasses.Async
import arrow.effects.typeclasses.Effect
import arrow.effects.typeclasses.MonadSuspend
import arrow.effects.typeclasses.Proc
import arrow.instance
import arrow.typeclasses.Applicative
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.MonadError

@instance(ObservableK::class)
interface ObservableKFunctorInstance : arrow.typeclasses.Functor<ForObservableK> {
    override fun <A, B> map(fa: arrow.effects.ObservableKOf<A>, f: kotlin.Function1<A, B>): arrow.effects.ObservableK<B> =
            fa.fix().map(f)
}

@instance(ObservableK::class)
interface ObservableKApplicativeInstance : arrow.typeclasses.Applicative<ForObservableK> {
    override fun <A, B> ap(fa: arrow.effects.ObservableKOf<A>, ff: arrow.effects.ObservableKOf<kotlin.Function1<A, B>>): arrow.effects.ObservableK<B> =
            fa.fix().ap(ff)

    override fun <A, B> map(fa: arrow.effects.ObservableKOf<A>, f: kotlin.Function1<A, B>): arrow.effects.ObservableK<B> =
            fa.fix().map(f)

    override fun <A> pure(a: A): arrow.effects.ObservableK<A> =
            arrow.effects.ObservableK.pure(a)
}

@instance(ObservableK::class)
interface ObservableKMonadInstance : arrow.typeclasses.Monad<ForObservableK> {
    override fun <A, B> ap(fa: arrow.effects.ObservableKOf<A>, ff: arrow.effects.ObservableKOf<kotlin.Function1<A, B>>): arrow.effects.ObservableK<B> =
            fa.fix().ap(ff)

    override fun <A, B> flatMap(fa: arrow.effects.ObservableKOf<A>, f: kotlin.Function1<A, arrow.effects.ObservableKOf<B>>): arrow.effects.ObservableK<B> =
            fa.fix().flatMap(f)

    override fun <A, B> map(fa: arrow.effects.ObservableKOf<A>, f: kotlin.Function1<A, B>): arrow.effects.ObservableK<B> =
            fa.fix().map(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, arrow.effects.ObservableKOf<arrow.core.Either<A, B>>>): arrow.effects.ObservableK<B> =
            arrow.effects.ObservableK.tailRecM(a, f)

    override fun <A> pure(a: A): arrow.effects.ObservableK<A> =
            arrow.effects.ObservableK.pure(a)
}

@instance(ObservableK::class)
interface ObservableKFoldableInstance : arrow.typeclasses.Foldable<ForObservableK> {
    override fun <A, B> foldLeft(fa: arrow.effects.ObservableKOf<A>, b: B, f: kotlin.Function2<B, A, B>): B =
            fa.fix().foldLeft(b, f)

    override fun <A, B> foldRight(fa: arrow.effects.ObservableKOf<A>, lb: arrow.core.Eval<B>, f: kotlin.Function2<A, arrow.core.Eval<B>, arrow.core.Eval<B>>): arrow.core.Eval<B> =
            fa.fix().foldRight(lb, f)
}

@instance(ObservableK::class)
interface ObservableKTraverseInstance : arrow.typeclasses.Traverse<ForObservableK> {
    override fun <A, B> map(fa: arrow.effects.ObservableKOf<A>, f: kotlin.Function1<A, B>): arrow.effects.ObservableK<B> =
            fa.fix().map(f)

    override fun <G, A, B> Applicative<G>.traverse(fa: arrow.effects.ObservableKOf<A>, f: kotlin.Function1<A, arrow.Kind<G, B>>): arrow.Kind<G, arrow.effects.ObservableK<B>> =
            fa.fix().traverse(f, this)

    override fun <A, B> foldLeft(fa: arrow.effects.ObservableKOf<A>, b: B, f: kotlin.Function2<B, A, B>): B =
            fa.fix().foldLeft(b, f)

    override fun <A, B> foldRight(fa: arrow.effects.ObservableKOf<A>, lb: arrow.core.Eval<B>, f: kotlin.Function2<A, arrow.core.Eval<B>, arrow.core.Eval<B>>): arrow.core.Eval<B> =
            fa.fix().foldRight(lb, f)
}

@instance(ObservableK::class)
interface ObservableKApplicativeErrorInstance :
        ObservableKApplicativeInstance,
        ApplicativeError<ForObservableK, Throwable> {
    override fun <A> raiseError(e: Throwable): ObservableK<A> =
            ObservableK.raiseError(e)

    override fun <A> handleErrorWith(fa: ObservableKOf<A>, f: (Throwable) -> ObservableKOf<A>): ObservableK<A> =
            fa.handleErrorWith { f(it).fix() }
}

@instance(ObservableK::class)
interface ObservableKMonadErrorInstance :
        ObservableKApplicativeErrorInstance,
        ObservableKMonadInstance,
        MonadError<ForObservableK, Throwable> {
    override fun <A, B> ap(fa: ObservableKOf<A>, ff: ObservableKOf<(A) -> B>): ObservableK<B> =
            super<ObservableKMonadInstance>.ap(fa, ff)

    override fun <A, B> map(fa: ObservableKOf<A>, f: (A) -> B): ObservableK<B> =
            super<ObservableKMonadInstance>.map(fa, f)

    override fun <A> pure(a: A): ObservableK<A> =
            super<ObservableKMonadInstance>.pure(a)
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
    override fun <A> runAsync(fa: ObservableKOf<A>, cb: (Either<Throwable, A>) -> ObservableKOf<Unit>): ObservableK<Unit> =
            fa.fix().runAsync(cb)
}

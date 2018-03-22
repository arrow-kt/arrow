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
import io.reactivex.BackpressureStrategy

@instance(FlowableK::class)
interface FlowableKFunctorInstance : arrow.typeclasses.Functor<ForFlowableK> {
    override fun <A, B> map(fa: arrow.effects.FlowableKOf<A>, f: kotlin.Function1<A, B>): arrow.effects.FlowableK<B> =
            fa.fix().map(f)
}

@instance(FlowableK::class)
interface FlowableKApplicativeInstance : arrow.typeclasses.Applicative<ForFlowableK> {
    override fun <A, B> ap(fa: arrow.effects.FlowableKOf<A>, ff: arrow.effects.FlowableKOf<kotlin.Function1<A, B>>): arrow.effects.FlowableK<B> =
            fa.fix().ap(ff)

    override fun <A, B> map(fa: arrow.effects.FlowableKOf<A>, f: kotlin.Function1<A, B>): arrow.effects.FlowableK<B> =
            fa.fix().map(f)

    override fun <A> pure(a: A): arrow.effects.FlowableK<A> =
            arrow.effects.FlowableK.pure(a)
}

@instance(FlowableK::class)
interface FlowableKMonadInstance : arrow.typeclasses.Monad<ForFlowableK> {
    override fun <A, B> ap(fa: arrow.effects.FlowableKOf<A>, ff: arrow.effects.FlowableKOf<kotlin.Function1<A, B>>): arrow.effects.FlowableK<B> =
            fa.fix().ap(ff)

    override fun <A, B> flatMap(fa: arrow.effects.FlowableKOf<A>, f: kotlin.Function1<A, arrow.effects.FlowableKOf<B>>): arrow.effects.FlowableK<B> =
            fa.fix().flatMap(f)

    override fun <A, B> map(fa: arrow.effects.FlowableKOf<A>, f: kotlin.Function1<A, B>): arrow.effects.FlowableK<B> =
            fa.fix().map(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, arrow.effects.FlowableKOf<arrow.core.Either<A, B>>>): arrow.effects.FlowableK<B> =
            arrow.effects.FlowableK.tailRecM(a, f)

    override fun <A> pure(a: A): arrow.effects.FlowableK<A> =
            arrow.effects.FlowableK.pure(a)
}

@instance(FlowableK::class)
interface FlowableKFoldableInstance : arrow.typeclasses.Foldable<ForFlowableK> {
    override fun <A, B> foldLeft(fa: arrow.effects.FlowableKOf<A>, b: B, f: kotlin.Function2<B, A, B>): B =
            fa.fix().foldLeft(b, f)

    override fun <A, B> foldRight(fa: arrow.effects.FlowableKOf<A>, lb: arrow.core.Eval<B>, f: kotlin.Function2<A, arrow.core.Eval<B>, arrow.core.Eval<B>>): arrow.core.Eval<B> =
            fa.fix().foldRight(lb, f)
}

@instance(FlowableK::class)
interface FlowableKTraverseInstance : arrow.typeclasses.Traverse<ForFlowableK> {
    override fun <A, B> map(fa: arrow.effects.FlowableKOf<A>, f: kotlin.Function1<A, B>): arrow.effects.FlowableK<B> =
            fa.fix().map(f)

    override fun <G, A, B> Applicative<G>.traverse(fa: arrow.effects.FlowableKOf<A>, f: kotlin.Function1<A, arrow.Kind<G, B>>): arrow.Kind<G, arrow.effects.FlowableK<B>> =
            fa.fix().traverse(f, this)

    override fun <A, B> foldLeft(fa: arrow.effects.FlowableKOf<A>, b: B, f: kotlin.Function2<B, A, B>): B =
            fa.fix().foldLeft(b, f)

    override fun <A, B> foldRight(fa: arrow.effects.FlowableKOf<A>, lb: arrow.core.Eval<B>, f: kotlin.Function2<A, arrow.core.Eval<B>, arrow.core.Eval<B>>): arrow.core.Eval<B> =
            fa.fix().foldRight(lb, f)
}

@instance(FlowableK::class)
interface FlowableKApplicativeErrorInstance :
        FlowableKApplicativeInstance,
        ApplicativeError<ForFlowableK, Throwable> {
    override fun <A> raiseError(e: Throwable): FlowableK<A> =
            FlowableK.raiseError(e)

    override fun <A> handleErrorWith(fa: FlowableKOf<A>, f: (Throwable) -> FlowableKOf<A>): FlowableK<A> =
            fa.handleErrorWith { f(it).fix() }
}

@instance(FlowableK::class)
interface FlowableKMonadErrorInstance :
        FlowableKApplicativeErrorInstance,
        FlowableKMonadInstance,
        MonadError<ForFlowableK, Throwable> {
    override fun <A, B> ap(fa: FlowableKOf<A>, ff: FlowableKOf<(A) -> B>): FlowableK<B> =
            super<FlowableKMonadInstance>.ap(fa, ff)

    override fun <A, B> map(fa: FlowableKOf<A>, f: (A) -> B): FlowableK<B> =
            super<FlowableKMonadInstance>.map(fa, f)

    override fun <A> pure(a: A): FlowableK<A> =
            super<FlowableKMonadInstance>.pure(a)
}

@instance(FlowableK::class)
interface FlowableKMonadSuspendInstance :
        FlowableKMonadErrorInstance,
        MonadSuspend<ForFlowableK> {
    override fun <A> suspend(fa: () -> FlowableKOf<A>): FlowableK<A> =
            FlowableK.suspend(fa)

    fun BS(): BackpressureStrategy = BackpressureStrategy.BUFFER
}

@instance(FlowableK::class)
interface FlowableKAsyncInstance :
        FlowableKMonadSuspendInstance,
        Async<ForFlowableK> {
    override fun <A> async(fa: Proc<A>): FlowableK<A> =
            FlowableK.async(fa, BS())
}

@instance(FlowableK::class)
interface FlowableKEffectInstance :
        FlowableKAsyncInstance,
        Effect<ForFlowableK> {
    override fun <A> runAsync(fa: FlowableKOf<A>, cb: (Either<Throwable, A>) -> FlowableKOf<Unit>): FlowableK<Unit> =
            fa.fix().runAsync(cb)
}
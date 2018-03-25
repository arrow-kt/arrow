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
import io.reactivex.BackpressureStrategy

@instance(FlowableK::class)
interface FlowableKFunctorInstance : Functor<ForFlowableK> {
    override fun <A, B> Kind<ForFlowableK, A>.map(f: (A) -> B): FlowableK<B> =
            fix().map(f)
}

@instance(FlowableK::class)
interface FlowableKApplicativeInstance : Applicative<ForFlowableK> {
    override fun <A, B> FlowableKOf<A>.ap(ff: FlowableKOf<(A) -> B>): FlowableK<B> =
            fix().ap(ff)

    override fun <A, B> Kind<ForFlowableK, A>.map(f: (A) -> B): FlowableK<B> =
            fix().map(f)

    override fun <A> pure(a: A): FlowableK<A> =
            FlowableK.pure(a)
}

@instance(FlowableK::class)
interface FlowableKMonadInstance : Monad<ForFlowableK> {
    override fun <A, B> FlowableKOf<A>.ap(ff: FlowableKOf<(A) -> B>): FlowableK<B> =
            fix().ap(ff)

    override fun <A, B> FlowableKOf<A>.flatMap(f: (A) -> Kind<ForFlowableK, B>): FlowableK<B> =
            fix().flatMap(f)

    override fun <A, B> FlowableKOf<A>.map(f: (A) -> B): FlowableK<B> =
            fix().map(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, FlowableKOf<arrow.core.Either<A, B>>>): FlowableK<B> =
            FlowableK.tailRecM(a, f)

    override fun <A> pure(a: A): FlowableK<A> =
            FlowableK.pure(a)
}

@instance(FlowableK::class)
interface FlowableKFoldableInstance : Foldable<ForFlowableK> {
    override fun <A, B> Kind<ForFlowableK, A>.foldLeft(b: B, f: (B, A) -> B): B =
            fix().foldLeft(b, f)

    override fun <A, B> foldRight(fa: FlowableKOf<A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): arrow.core.Eval<B> =
            fa.fix().foldRight(lb, f)
}

@instance(FlowableK::class)
interface FlowableKTraverseInstance : Traverse<ForFlowableK> {
    override fun <A, B> Kind<ForFlowableK, A>.map(f: (A) -> B): FlowableK<B> =
            fix().map(f)

    override fun <G, A, B> FlowableKOf<A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, FlowableK<B>> =
            fix().traverse(AP, f)

    override fun <A, B> Kind<ForFlowableK, A>.foldLeft(b: B, f: (B, A) -> B): B =
            fix().foldLeft(b, f)

    override fun <A, B> foldRight(fa: FlowableKOf<A>, lb: arrow.core.Eval<B>, f: kotlin.Function2<A, arrow.core.Eval<B>, arrow.core.Eval<B>>): arrow.core.Eval<B> =
            fa.fix().foldRight(lb, f)
}

@instance(FlowableK::class)
interface FlowableKApplicativeErrorInstance :
        FlowableKApplicativeInstance,
        ApplicativeError<ForFlowableK, Throwable> {
    override fun <A> raiseError(e: Throwable): FlowableK<A> =
            FlowableK.raiseError(e)

    override fun <A> FlowableKOf<A>.handleErrorWith(f: (Throwable) -> FlowableKOf<A>): FlowableK<A> =
            fix().handleErrorWith { f(it).fix() }
}

@instance(FlowableK::class)
interface FlowableKMonadErrorInstance :
        FlowableKMonadInstance,
        MonadError<ForFlowableK, Throwable> {
    override fun <A> raiseError(e: Throwable): FlowableK<A> =
            FlowableK.raiseError(e)

    override fun <A> FlowableKOf<A>.handleErrorWith(f: (Throwable) -> FlowableKOf<A>): FlowableK<A> =
            fix().handleErrorWith { f(it).fix() }
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
    override fun <A> FlowableKOf<A>.runAsync(cb: (Either<Throwable, A>) -> FlowableKOf<Unit>): FlowableK<Unit> =
            fix().runAsync(cb)
}

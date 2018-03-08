package arrow.effects

import arrow.Kind
import arrow.core.Either
import arrow.effects.continuations.EffectContinuation
import arrow.instance
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.Monad
import arrow.typeclasses.MonadError
import arrow.typeclasses.continuations.BindingCatchContinuation
import arrow.typeclasses.continuations.BindingContinuation
import kotlin.coroutines.experimental.CoroutineContext

@instance(DeferredK::class)
interface DeferredKApplicativeErrorInstance :
        DeferredKApplicativeInstance,
        ApplicativeError<ForDeferredK, Throwable> {
    override fun <A> raiseError(e: Throwable): DeferredK<A> =
            DeferredK.raiseError(e)

    override fun <A> handleErrorWith(fa: DeferredKOf<A>, f: (Throwable) -> DeferredKOf<A>): DeferredK<A> =
            fa.handleErrorWith { f(it).fix() }
}

@instance(DeferredK::class)
interface DeferredKMonadInstance : Monad<ForDeferredK> {
    override fun <A, B> ap(fa: DeferredKOf<A>, ff: DeferredKOf<kotlin.Function1<A, B>>): DeferredK<B> =
            fa.fix().ap(ff)

    override fun <A, B> flatMap(fa: DeferredKOf<A>, f: kotlin.Function1<A, DeferredKOf<B>>): DeferredK<B> =
            fa.fix().flatMap(f)

    override fun <A, B> map(fa: DeferredKOf<A>, f: kotlin.Function1<A, B>): DeferredK<B> =
            fa.fix().map(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, DeferredKOf<arrow.core.Either<A, B>>>): DeferredK<B> =
            DeferredK.tailRecM(a, f)

    override fun <A> pure(a: A): DeferredK<A> =
            DeferredK.pure(a)

    override fun <B> binding(cc: CoroutineContext, c: suspend BindingContinuation<ForDeferredK, *>.() -> B): DeferredK<B> =
            EffectContinuation.bindingIn(DeferredK.effect(), cc, c).fix()
}

@instance(DeferredK::class)
interface DeferredKMonadErrorInstance :
        DeferredKApplicativeErrorInstance,
        DeferredKMonadInstance,
        MonadError<ForDeferredK, Throwable> {
    override fun <A, B> ap(fa: DeferredKOf<A>, ff: DeferredKOf<(A) -> B>): DeferredK<B> =
            super<DeferredKMonadInstance>.ap(fa, ff)

    override fun <A, B> map(fa: DeferredKOf<A>, f: (A) -> B): DeferredK<B> =
            super<DeferredKMonadInstance>.map(fa, f)

    override fun <A> pure(a: A): DeferredK<A> =
            super<DeferredKMonadInstance>.pure(a)

    override fun <B> bindingCatch(cc: CoroutineContext, catch: (Throwable) -> Throwable, c: suspend BindingCatchContinuation<ForDeferredK, Throwable, *>.() -> B): DeferredK<B> =
            EffectContinuation.bindingCatchIn(DeferredK.effect(), catch, cc, c).fix()
}

@instance(DeferredK::class)
interface DeferredKMonadSuspendInstance : DeferredKMonadErrorInstance, MonadSuspend<ForDeferredK> {
    override fun <A> suspend(fa: () -> DeferredKOf<A>): DeferredK<A> =
            DeferredK.suspend(fa = fa)
}

@instance(DeferredK::class)
interface DeferredKAsyncInstance : DeferredKMonadSuspendInstance, Async<ForDeferredK> {
    override fun <A> async(fa: Proc<A>): DeferredK<A> =
            DeferredK.async(fa = fa)

    override fun <A> invoke(fa: () -> A): DeferredK<A> =
            DeferredK.invoke(f = fa)
}

@instance(DeferredK::class)
interface DeferredKEffectInstance : DeferredKAsyncInstance, Effect<ForDeferredK> {
    override fun <A> runAsync(fa: Kind<ForDeferredK, A>, cb: (Either<Throwable, A>) -> DeferredKOf<Unit>): DeferredK<Unit> =
            fa.fix().runAsync(cb)
}

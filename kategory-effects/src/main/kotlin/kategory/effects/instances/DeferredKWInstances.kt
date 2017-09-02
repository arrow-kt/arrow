package kategory.effects.instances

import kategory.*
import kotlin.coroutines.experimental.CoroutineContext

interface DeferredKWInstances :
        Functor<DeferredKWHK>,
        Applicative<DeferredKWHK>,
        Monad<DeferredKWHK>,
        MonadError<DeferredKWHK, Throwable>,
        AsyncContext<DeferredKWHK> {

    fun CC(): CoroutineContext

    override fun <A> pure(a: A): DeferredKW<A> =
            DeferredKW.pure(CC(), a)

    override fun <A, B> map(fa: HK<DeferredKWHK, A>, f: (A) -> B): DeferredKW<B> =
            fa.ev().map(CC(), f)

    override fun <A, B> flatMap(fa: HK<DeferredKWHK, A>, f: (A) -> HK<DeferredKWHK, B>): DeferredKW<B> =
            fa.ev().flatMap(CC()) { a: A -> f(a).ev() }

    override fun <A, B> tailRecM(a: A, f: (A) -> HK<DeferredKWHK, Either<A, B>>): DeferredKW<B> =
            DeferredKW.tailRecM(CC(), a) { aa: A -> f(aa).ev() }

    override fun <A> raiseError(e: Throwable): DeferredKW<A> =
            DeferredKW.raiseError(CC(), e)

    override fun <A> handleErrorWith(fa: HK<DeferredKWHK, A>, f: (Throwable) -> HK<DeferredKWHK, A>): DeferredKW<A> =
            fa.ev().handleErrorWith(CC()) { err: Throwable -> f(err).ev() }

    override fun <A> runAsync(fa: Proc<A>): HK<DeferredKWHK, A> =
            DeferredKW.async(CC(), fa)

}
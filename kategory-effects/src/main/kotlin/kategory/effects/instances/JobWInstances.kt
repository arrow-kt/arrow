package kategory

import kotlin.coroutines.experimental.CoroutineContext

interface JobWInstances :
        Functor<JobWHK>,
        Applicative<JobWHK>,
        Monad<JobWHK>,
        MonadError<JobWHK, Throwable>,
        AsyncContext<JobWHK> {

    fun CC(): CoroutineContext

    override fun <A> pure(a: A): JobW<A> =
            JobW.pure(CC(), a)

    override fun <A, B> map(fa: HK<JobWHK, A>, f: (A) -> B): JobW<B> =
            fa.ev().map(f)

    override fun <A, B> flatMap(fa: HK<JobWHK, A>, f: (A) -> HK<JobWHK, B>): JobW<B> =
            fa.ev().flatMap { a: A -> f(a).ev() }

    override fun <A, B> tailRecM(a: A, f: (A) -> HK<JobWHK, Either<A, B>>): JobW<B> =
            JobW.tailRecM(CC(), a) { aa: A -> f(aa).ev() }

    override fun <A> raiseError(e: Throwable): JobW<A> =
            JobW.raiseError(CC(), e)

    override fun <A> handleErrorWith(fa: HK<JobWHK, A>, f: (Throwable) -> HK<JobWHK, A>): JobW<A> =
            fa.ev().handleErrorWith { err: Throwable -> f(err).ev() }

    override fun <A> runAsync(fa: Proc<A>): HK<JobWHK, A> =
            JobW.async(CC(), fa)

}
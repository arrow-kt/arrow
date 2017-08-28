package kategory

import kotlin.coroutines.experimental.CoroutineContext

interface JobWInstances :
        Functor<JobKWHK>,
        Applicative<JobKWHK>,
        Monad<JobKWHK>,
        MonadError<JobKWHK, Throwable>,
        AsyncContext<JobKWHK> {

    fun CC(): CoroutineContext

    override fun <A> pure(a: A): JobKW<A> =
            JobKW.pure(CC(), a)

    override fun <A, B> map(fa: HK<JobKWHK, A>, f: (A) -> B): JobKW<B> =
            fa.ev().map(f)

    override fun <A, B> flatMap(fa: HK<JobKWHK, A>, f: (A) -> HK<JobKWHK, B>): JobKW<B> =
            fa.ev().flatMap(CC()) { a: A -> f(a).ev() }

    override fun <A, B> tailRecM(a: A, f: (A) -> HK<JobKWHK, Either<A, B>>): JobKW<B> =
            JobKW.tailRecM(CC(), a) { aa: A -> f(aa).ev() }

    override fun <A> raiseError(e: Throwable): JobKW<A> =
            JobKW.raiseError(CC(), e)

    override fun <A> handleErrorWith(fa: HK<JobKWHK, A>, f: (Throwable) -> HK<JobKWHK, A>): JobKW<A> =
            fa.ev().handleErrorWith { err: Throwable -> f(err).ev() }

    override fun <A> runAsync(fa: Proc<A>): HK<JobKWHK, A> =
            JobKW.async(CC(), fa)

}
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
            JobW.pure(a, CC())

    override fun <A, B> map(fa: HK<JobWHK, A>, f: (A) -> B): JobW<B> =
            fa.ev().map(f)

    override fun <A, B> flatMap(fa: HK<JobWHK, A>, f: (A) -> HK<JobWHK, B>): JobW<B> =
            fa.ev().flatMap { a: A -> f(a).ev() }

    override fun <A, B> tailRecM(a: A, f: (A) -> HK<JobWHK, Either<A, B>>): JobW<B> =
            f(a).ev().flatMap {
                when (it) {
                    is Either.Left -> tailRecM(it.a, f)
                    is Either.Right -> pure(it.b)
                }
            }

    override fun <A> raiseError(e: Throwable): JobW<A> =
            JobW.raiseError(e, CC())

    override fun <A> handleErrorWith(fa: HK<JobWHK, A>, f: (Throwable) -> HK<JobWHK, A>): JobW<A> =
            fa.ev().handleErrorWith { err: Throwable -> f(err).ev() }

    override fun <A> runAsync(fa: Proc<A>): HK<JobWHK, A> =
            JobW.runAsync(fa, CC())

}
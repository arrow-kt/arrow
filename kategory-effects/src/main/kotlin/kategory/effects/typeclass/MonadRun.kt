package kategory.effects

import kategory.*

interface MonadRun<F, E> : AsyncContext<F>, MonadError<F, E>, Typeclass {
    fun <A> unsafeRunSync(fa: HK<F, A>): A

    fun <A> runAsync(fa: HK<F, A>, cb: (Either<E, A>) -> HK<F, Unit>): HK<F, Unit>

    fun <A> unsafeRunAsync(fa: HK<F, A>, cb: (Either<E, A>) -> Unit): Unit
}

inline fun <reified F, reified E> monadRun(): MonadRun<F, E> =
        instance(InstanceParametrizedType(MonadRun::class.java, listOf(typeLiteral<F>(), typeLiteral<E>())))
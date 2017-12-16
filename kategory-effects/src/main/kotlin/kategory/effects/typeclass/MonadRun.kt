package kategory.effects

import kategory.*

interface MonadRun<F, E> : MonadError<F, E>, Typeclass {
    fun <A> unsafeRunSync(fa: HK<F, A>): A

    fun <A> runAsync(fa: HK<F, A>, cb: (Either<E, A>) -> HK<F, Unit>): HK<F, Unit>

    fun <A> unsafeRunAsync(fa: HK<F, A>, cb: (Either<E, A>) -> Unit): Unit

    fun <A> suspend(f: () -> HK<F, A>): HK<F, A>

    operator fun <A> invoke(f: () -> A): HK<F, A> =
            suspend { pure(f()) }

    fun lazy(): HK<F, Unit> = invoke { }
}

inline fun <reified F, reified E> monadRun(): MonadRun<F, E> =
        instance(InstanceParametrizedType(MonadRun::class.java, listOf(typeLiteral<F>(), typeLiteral<E>())))
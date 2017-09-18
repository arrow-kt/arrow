package kategory.effects

import kategory.*

typealias Disposable = () -> Unit

interface MonadDisposable<F, E> : MonadError<F, E>, Typeclass {
    fun <A> dispose(fa: HK<F, A>): Unit
}

inline fun <reified F, reified E> monadDisposable(): MonadDisposable<F, E> =
        instance(InstanceParametrizedType(MonadDisposable::class.java, listOf(typeLiteral<F>())))

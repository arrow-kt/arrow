package arrow.effects

import arrow.HK
import arrow.TC
import arrow.core.Either
import arrow.typeclass
import arrow.typeclasses.MonadError

/** The context required to defer evaluating a safe computation. **/
@typeclass
interface Sync<F> : MonadError<F, Throwable>, TC {
    fun <A> suspend(fa: () -> HK<F, A>): HK<F, A>

    operator fun <A> invoke(fa: () -> A): HK<F, A> =
            suspend {
                try {
                    pure(fa())
                } catch (e: Exception) {
                    raiseError<A>(e)
                }
            }

    fun lazy(): HK<F, Unit> = invoke { }

    fun <A> deferUnsafe(f: () -> Either<Throwable, A>): HK<F, A> =
            suspend { f().fold({ raiseError<A>(it) }, { pure(it) }) }
}

inline fun <reified F, A> (() -> A).defer(SC: Sync<F> = sync()): HK<F, A> = SC(this)

inline fun <reified F, A> (() -> Either<Throwable, A>).deferUnsafe(SC: Sync<F> = sync()): HK<F, A> =
        SC.deferUnsafe(this)
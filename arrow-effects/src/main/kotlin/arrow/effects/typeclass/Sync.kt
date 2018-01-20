package arrow.effects

import arrow.HK
import arrow.TC
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
}

inline fun <reified F, A> (() -> A).defer(SC: Sync<F> = sync()): HK<F, A> = SC(this)
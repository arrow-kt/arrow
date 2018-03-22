package arrow.mtl.typeclasses

import arrow.Kind
import arrow.typeclasses.Monad

interface MonadReader<F, D> : Monad<F> {
    /** Get the environment */
    fun ask(): Kind<F, D>

    /** Modify the environment */
    fun <A> local(f: (D) -> D, fa: Kind<F, A>): Kind<F, A>

    /** Retrieves a function of the environment */
    fun <A> reader(f: (D) -> A): Kind<F, A> = map(ask(), f)
}

package arrow.mtl

import arrow.*
import arrow.typeclasses.Monad

@typeclass
interface MonadReader<F, D> : Monad<F> {
    /** Get the environment */
    fun ask(): Kind<F, D>

    /** Modify the environment */
    fun <A> local(f: (D) -> D, fa: Kind<F, A>): Kind<F, A>

    /** Retrieves a function of the environment */
    fun <A> reader(f: (D) -> A): Kind<F, A> = map(ask(), f)
}

inline fun <reified F, A, reified D> Kind<F, A>.local(FT: MonadReader<F, D> = monadReader(), noinline f: (D) -> D): Kind<F, A> = FT.local(f, this)

inline fun <reified F, A, reified D> ((D) -> A).reader(FT: MonadReader<F, D> = monadReader()): Kind<F, A> = FT.reader(this)
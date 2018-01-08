package arrow.mtl

import arrow.*
import arrow.typeclasses.Monad

@typeclass
interface MonadReader<F, D> : Monad<F> {
    /** Get the environment */
    fun ask(): HK<F, D>

    /** Modify the environment */
    fun <A> local(f: (D) -> D, fa: HK<F, A>): HK<F, A>

    /** Retrieves a function of the environment */
    fun <A> reader(f: (D) -> A): HK<F, A> = map(ask(), f)
}

inline fun <reified F, A, reified D> HK<F, A>.local(FT: MonadReader<F, D> = monadReader(), noinline f: (D) -> D): HK<F, A> = FT.local(f, this)

inline fun <reified F, A, reified D> ((D) -> A).reader(FT: MonadReader<F, D> = monadReader()): HK<F, A> = FT.reader(this)
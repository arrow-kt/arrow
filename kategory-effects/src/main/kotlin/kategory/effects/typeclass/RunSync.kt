package kategory.effects

import kategory.HK
import kategory.MonadError
import kategory.Typeclass

interface RunSync<F, E> : MonadError<F, E>, Typeclass {
    fun <A> unsafeRunSync(fa: HK<F, A>): A
}
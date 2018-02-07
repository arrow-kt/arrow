package arrow.free

import arrow.*
import arrow.typeclasses.Functor
import arrow.typeclasses.functor

@higherkind abstract class Yoneda<F, A> : YonedaOf<F, A>, YonedaKindedJ<F, A> {

    abstract operator fun <B> invoke(f: (A) -> B): Kind<F, B>

    fun lower(): Kind<F, A> = invoke { a -> a }

    fun <B> map(ff: (A) -> B): Yoneda<F, B> =
            object : Yoneda<F, B>() {
                override fun <C> invoke(f: (B) -> C): Kind<F, C> = this@Yoneda { f(ff(it)) }
            }

    fun toCoyoneda(): Coyoneda<F, A, A> = Coyoneda(lower(), listOf({ a: Any? -> a }))

    companion object {
        inline operator fun <reified U, A> invoke(fa: Kind<U, A>, FF: Functor<U> = arrow.typeclasses.functor()): Yoneda<U, A> =
                object : Yoneda<U, A>() {
                    override fun <B> invoke(f: (A) -> B): Kind<U, B> = FF.map(fa, f)
                }
    }
}
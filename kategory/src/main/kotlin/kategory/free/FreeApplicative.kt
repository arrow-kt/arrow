package kategory

typealias FreeApplicativeKind<S, A> = HK2<FreeApplicative.F, S, A>
typealias FreeApplicativeF<S> = HK<FreeApplicative.F, S>

fun <S, A> FreeApplicativeKind<S, A>.ev(): FreeApplicative<S, A> =
        this as FreeApplicative<S, A>

sealed class FreeApplicative<S, out A> : FreeKind<S, A> {

    class F private constructor()

    companion object {
        fun <S, A> pure(a: A): FreeApplicative<S, A> =
                Pure(a)
    }

    inline fun <C> fold(crossinline ifPure: (A) -> C, crossinline ifAp: (B) -> C): C =
            when (this) {
                is Pure -> ifPure(value)
                is Ap<S, *, *> -> trans.map()
            }

    inline fun <B> ap(ap: FreeApplicative<S, (A)  -> B>): FreeApplicative<S, B> =
            when (ap) {
                is Pure -> map(ap.value)
                is Ap<S, *, *> -> Ap(ap.value, ap(ap.trans.map { }))
            }

    abstract fun <C> map(f: (A) -> C): FreeApplicative<S, C>

    data class Pure<S, out A>(val value: A) : FreeApplicative<S, A>() {
        override fun <C> map(f: (A) -> C): FreeApplicative<S, C> =
                Pure(f(value))
    }

    data class Ap<S, out A, B>(val value: HK<S, B>, val trans: FreeApplicative<S, (B) -> A>) : FreeApplicative<S, A>() {
        override fun <C> map(f: (A) -> C): FreeApplicative<S, C> =
                Ap(value, trans.map { it andThen f })
    }
}

fun <S, A> A.freeAp(): FreeApplicative<S, A> =
        FreeApplicative.pure(this)
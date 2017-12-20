package arrow

typealias CokleisliFun<F, A, B> = (HK<F, A>) -> B

typealias CoreaderT<F, A, B> = Cokleisli<F, A, B>

@higherkind data class Cokleisli<F, A, B>(val MM: Comonad<F>, val run: CokleisliFun<F, A, B>) : CokleisliKind<F, A, B>, CokleisliKindedJ<F, A, B> {

    inline fun <C, D> bimap(noinline g: (D) -> A, crossinline f: (B) -> C): Cokleisli<F, D, C> = Cokleisli(MM, { f(run(MM.map(it, g))) })

    fun <D> lmap(g: (D) -> A): Cokleisli<F, D, B> = Cokleisli(MM, { run(MM.map(it, g)) })

    inline fun <C> map(crossinline f: (B) -> C): Cokleisli<F, A, C> = Cokleisli(MM, { f(run(it)) })

    inline fun <C> contramapValue(crossinline f: (HK<F, C>) -> HK<F, A>): Cokleisli<F, C, B> = Cokleisli(MM, { run(f(it)) })

    fun <D> compose(a: Cokleisli<F, D, A>): Cokleisli<F, D, B> = Cokleisli(MM, { run(MM.coflatMap(it, a.run)) })

    @JvmName("andThenK")
    fun <C> andThen(a: HK<F, C>): Cokleisli<F, A, C> = Cokleisli(MM, { MM.extract(a) })

    fun <C> andThen(a: Cokleisli<F, B, C>): Cokleisli<F, A, C> = a.compose(this)

    inline fun <C> flatMap(crossinline f: (B) -> Cokleisli<F, A, C>): Cokleisli<F, A, C> = Cokleisli(MM, { f(run(it)).run(it) })

    companion object {
        inline operator fun <reified F, A, B> invoke(noinline run: (HK<F, A>) -> B, MF: Comonad<F> = comonad<F>()): Cokleisli<F, A, B> = Cokleisli(MF, run)

        inline fun <reified F, A, B> pure(b: B, MF: Comonad<F> = comonad<F>()): Cokleisli<F, A, B> = Cokleisli(MF, { b })

        inline fun <reified F, B> ask(MF: Comonad<F> = comonad<F>()): Cokleisli<F, B, B> = Cokleisli(MF, { MF.extract(it) })
    }
}

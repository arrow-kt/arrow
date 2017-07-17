package kategory

typealias OptionTKind<F, A> = HK2<OptionT.F, F, A>
typealias OptionTF<F> = HK<OptionT.F, F>

fun <F, A> OptionTKind<F, A>.ev(): OptionT<F, A> =
        this as OptionT<F, A>

/**
 * [OptionT]`<F, A>` is a light wrapper on an `F<`[Option]`<A>>` with some
 * convenient methods for working with this nested structure.
 *
 * It may also be said that [OptionT] is a monad transformer for [Option].
 */
data class OptionT<F, A>(val MF: Monad<F>, val value: HK<F, Option<A>>) : OptionTKind<F, A> {

    class F private constructor()

    companion object {

        inline operator fun <reified F, A> invoke(value: HK<F, Option<A>>, MF: Monad<F> = monad<F>()): OptionT<F, A> =
                OptionT(MF, value)

        @JvmStatic inline fun <reified F, A> pure(a: A, MF: Monad<F> = monad<F>()): OptionT<F, A> =
                OptionT(MF, MF.pure(Option.Some(a)))

        @JvmStatic inline fun <reified F> none(MF: Monad<F> = monad<F>()): OptionT<F, Nothing> =
                OptionT(MF, MF.pure(Option.None))

        @JvmStatic inline fun <reified F, A> fromOption(value: Option<A>, MF: Monad<F> = monad<F>()): OptionT<F, A> =
                OptionT(MF, MF.pure(value))
    }

    inline fun <B> fold(crossinline default: () -> B, crossinline f: (A) -> B): HK<F, B> =
            MF.map(value, { option -> option.fold(default, f) })

    inline fun <B> cata(crossinline default: () -> B, crossinline f: (A) -> B): HK<F, B> =
            fold(default, f)

    inline fun <B> flatMap(crossinline f: (A) -> OptionT<F, B>): OptionT<F, B> = flatMapF({ it -> f(it).value })

    inline fun <B> flatMapF(crossinline f: (A) -> HK<F, Option<B>>): OptionT<F, B> =
            OptionT(MF, MF.flatMap(value, { option -> option.fold({ MF.pure(Option.None) }, f) }))

    fun <B> liftF(fa: HK<F, B>): OptionT<F, B> = OptionT(MF, MF.map(fa, { Option.Some(it) }))

    inline fun <B> semiflatMap(crossinline f: (A) -> HK<F, B>): OptionT<F, B> =
            flatMap({ option -> liftF(f(option)) })

    inline fun <B> map(crossinline f: (A) -> B): OptionT<F, B> =
            OptionT(MF, MF.map(value, { it.map(f) }))

    fun getOrElse(default: () -> A): HK<F, A> =
            MF.map(value, { it.getOrElse(default) })

    inline fun getOrElseF(crossinline default: () -> HK<F, A>): HK<F, A> =
            MF.flatMap(value, { it.fold(default, { MF.pure(it) }) })

    inline fun filter(crossinline p: (A) -> Boolean): OptionT<F, A> =
            OptionT(MF, MF.map(value, { it.filter(p) }))

    inline fun forall(crossinline p: (A) -> Boolean): HK<F, Boolean> =
            MF.map(value, { it.forall(p) })

    fun isDefined(): HK<F, Boolean> =
            MF.map(value, { it.isDefined })

    fun isEmpty(): HK<F, Boolean> =
            MF.map(value, { it.isEmpty })

    inline fun orElse(crossinline default: () -> OptionT<F, A>): OptionT<F, A> =
            orElseF({ default().value })

    inline fun orElseF(crossinline default: () -> HK<F, Option<A>>): OptionT<F, A> =
            OptionT(MF, MF.flatMap(value) {
                when (it) {
                    is Option.Some<A> -> MF.pure(it)
                    is Option.None -> default()
                }
            })

    inline fun <B> transform(crossinline f: (Option<A>) -> Option<B>): OptionT<F, B> =
            OptionT(MF, MF.map(value, { f(it) }))

    inline fun <B> subflatMap(crossinline f: (A) -> Option<B>): OptionT<F, B> =
            transform({ it.flatMap(f) })

    fun <B> foldL(b: B, f: (B, A) -> B, FF: Foldable<F>): B =
            FF.compose(Option).foldLC(value, b, f)

    fun <B> foldR(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>, FF: Foldable<F>): Eval<B> =
            FF.compose(Option).foldRC(value, lb, f)

    fun <G, B> traverse(f: (A) -> HK<G, B>, GA: Applicative<G>, FF: Traverse<F>, MF: Monad<F>): HK<G, HK<OptionTF<F>, B>> {
        val fa = ComposedTraverse(FF, Option, Option).traverseC(value, f, GA)
        return GA.map(fa, { OptionT(MF, MF.map(it.lower(), { it.ev() })) })
    }

    //TODO: add toRight() and toLeft() once EitherT it's available
}

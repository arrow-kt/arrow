package arrow

/**
 * [OptionT]`<F, A>` is a light wrapper on an `F<`[Option]`<A>>` with some
 * convenient methods for working with this nested structure.
 *
 * It may also be said that [OptionT] is a monad transformer for [Option].
 */
@higherkind data class OptionT<F, A>(val value: HK<F, Option<A>>) : OptionTKind<F, A>, OptionTKindedJ<F, A> {

    companion object {

        operator fun <F, A> invoke(value: HK<F, Option<A>>): OptionT<F, A> = OptionT(value)

        inline fun <reified F, A> pure(a: A, AF: Applicative<F> = arrow.applicative<F>()): OptionT<F, A> = OptionT(AF.pure(Some(a)))

        inline fun <reified F> none(AF: Applicative<F> = arrow.applicative<F>()): OptionT<F, Nothing> = OptionT(AF.pure(None))

        inline fun <reified F, A> fromOption(value: Option<A>, AF: Applicative<F> = arrow.applicative<F>()): OptionT<F, A> =
                OptionT(AF.pure(value))

        fun <F, A, B> tailRecM(a: A, f: (A) -> OptionTKind<F, Either<A, B>>, MF: Monad<F>): OptionT<F, B> =
                OptionT(MF.tailRecM(a, {
                    MF.map(f(it).ev().value, {
                        it.fold({
                            Right<Option<B>>(None)
                        }, {
                            it.map { Some(it) }
                        })
                    })
                }))

    }

    inline fun <B> fold(crossinline default: () -> B, crossinline f: (A) -> B, FF: Functor<F>): HK<F, B> = FF.map(value, { option -> option.fold(default, f) })

    inline fun <B> cata(crossinline default: () -> B, crossinline f: (A) -> B, FF: Functor<F>): HK<F, B> = fold(default, f, FF)

    fun <B> ap(ff: OptionTKind<F, (A) -> B>, MF: Monad<F>): OptionT<F, B> = ff.ev().flatMap({ f -> map(f, MF) }, MF)

    inline fun <B> flatMap(crossinline f: (A) -> OptionT<F, B>, MF: Monad<F>): OptionT<F, B> = flatMapF({ it -> f(it).value }, MF)

    inline fun <B> flatMapF(crossinline f: (A) -> HK<F, Option<B>>, MF: Monad<F>): OptionT<F, B> =
            OptionT(MF.flatMap(value, { option -> option.fold({ MF.pure(None) }, f) }))

    fun <B> liftF(fa: HK<F, B>, FF: Functor<F>): OptionT<F, B> = OptionT(FF.map(fa, { Some(it) }))

    inline fun <B> semiflatMap(crossinline f: (A) -> HK<F, B>, MF: Monad<F>): OptionT<F, B> = flatMap({ option -> liftF(f(option), MF) }, MF)

    inline fun <B> map(crossinline f: (A) -> B, FF: Functor<F>): OptionT<F, B> = OptionT(FF.map(value, { it.map(f) }))

    fun getOrElse(default: () -> A, FF: Functor<F>): HK<F, A> = FF.map(value, { it.getOrElse(default) })

    inline fun getOrElseF(crossinline default: () -> HK<F, A>, MF: Monad<F>): HK<F, A> = MF.flatMap(value, { it.fold(default, { MF.pure(it) }) })

    inline fun filter(crossinline p: (A) -> Boolean, FF: Functor<F>): OptionT<F, A> = OptionT(FF.map(value, { it.filter(p) }))

    inline fun forall(crossinline p: (A) -> Boolean, FF: Functor<F>): HK<F, Boolean> = FF.map(value, { it.forall(p) })

    fun isDefined(FF: Functor<F>): HK<F, Boolean> = FF.map(value, { it.isDefined() })

    fun isEmpty(FF: Functor<F>): HK<F, Boolean> = FF.map(value, { it.isEmpty() })

    inline fun orElse(crossinline default: () -> OptionT<F, A>, MF: Monad<F>): OptionT<F, A> = orElseF({ default().value }, MF)

    inline fun orElseF(crossinline default: () -> HK<F, Option<A>>, MF: Monad<F>): OptionT<F, A> =
            OptionT(MF.flatMap(value) {
                when (it) {
                    is Some<A> -> MF.pure(it)
                    is None -> default()
                }
            })

    inline fun <B> transform(crossinline f: (Option<A>) -> Option<B>, FF: Functor<F>): OptionT<F, B> = OptionT(FF.map(value, { f(it) }))

    inline fun <B> subflatMap(crossinline f: (A) -> Option<B>, FF: Functor<F>): OptionT<F, B> = transform({ it.flatMap(f) }, FF)

    fun <B> foldLeft(b: B, f: (B, A) -> B, FF: Foldable<F>): B = FF.compose(Option.foldable()).foldLC(value, b, f)

    fun <B> foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>, FF: Foldable<F>): Eval<B> = FF.compose(Option.foldable()).foldRC(value, lb, f)

    fun <G, B> traverseFilter(f: (A) -> HK<G, Option<B>>, GA: Applicative<G>, FF: Traverse<F>): HK<G, OptionT<F, B>> {
        val fa = ComposedTraverseFilter(FF, Option.traverseFilter(), Option.applicative()).traverseFilterC(value, f, GA)
        return GA.map(fa, { OptionT(FF.map(it.unnest(), { it.ev() })) })
    }

    fun <G, B> traverse(f: (A) -> HK<G, B>, GA: Applicative<G>, FF: Traverse<F>): HK<G, OptionT<F, B>> {
        val fa = ComposedTraverse(FF, Option.traverse(), Option.applicative()).traverseC(value, f, GA)
        return GA.map(fa, { OptionT(FF.map(it.unnest(), { it.ev() })) })
    }

    fun <R> toLeft(default: () -> R, FF: Functor<F>): EitherT<F, A, R> =
            EitherT(cata({ Right(default()) }, { Left(it) }, FF))

    fun <L> toRight(default: () -> L, FF: Functor<F>): EitherT<F, L, A> =
            EitherT(cata({ Left(default()) }, { Right(it) }, FF))
}

inline fun <F, A, B> OptionT<F, A>.mapFilter(crossinline f: (A) -> Option<B>, FF: Functor<F>): OptionT<F, B> =
        OptionT(FF.map(value, { it.flatMap(f) }))

fun <F, A> OptionTKind<F, A>.value(): HK<F, Option<A>> = this.ev().value
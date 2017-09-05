package kategory

@Suppress("UNCHECKED_CAST") inline fun <F, A> OptionTKind<F, A>.value(): HK<F, Option<A>> = this.ev().value

/**
 * [OptionT]`<F, A>` is a light wrapper on an `F<`[Option]`<A>>` with some
 * convenient methods for working with this nested structure.
 *
 * It may also be said that [OptionT] is a monad transformer for [Option].
 */
@higherkind data class OptionT<F, A>(val MF: Monad<F>, val value: HK<F, Option<A>>) : OptionTKind<F, A> {

    companion object {

        inline operator fun <reified F, A> invoke(value: HK<F, Option<A>>, MF: Monad<F> = kategory.monad<F>()): OptionT<F, A> = OptionT(MF, value)

        @JvmStatic inline fun <reified F, A> pure(a: A, MF: Monad<F> = kategory.monad<F>()): OptionT<F, A> = OptionT(MF, MF.pure(Option.Some(a)))

        @JvmStatic inline fun <reified F> none(MF: Monad<F> = kategory.monad<F>()): OptionT<F, Nothing> = OptionT(MF, MF.pure(Option.None))

        @JvmStatic inline fun <reified F, A> fromOption(value: Option<A>, MF: Monad<F> = kategory.monad<F>()): OptionT<F, A> = OptionT(MF, MF.pure(value))

        inline fun <reified F> instances(MF: Monad<F> = kategory.monad<F>()): OptionTInstances<F> = object : OptionTInstances<F> {
            override fun MF(): Monad<F> = MF
        }

        inline fun <reified F> functor(MF: Monad<F> = kategory.monad<F>()): Functor<OptionTKindPartial<F>> = instances(MF)

        inline fun <reified F> applicative(MF: Monad<F> = kategory.monad<F>()): Applicative<OptionTKindPartial<F>> = instances(MF)

        inline fun <reified F> monad(MF: Monad<F> = kategory.monad<F>()): Monad<OptionTKindPartial<F>> = instances(MF)

        inline fun <reified F> traverseFilter(FF: TraverseFilter<F> = kategory.traverseFilter<F>(), MF: Monad<F> = kategory.monad<F>()):
                TraverseFilter<OptionTKindPartial<F>> = object : OptionTTraverseFilter<F> {
                    override fun FF(): TraverseFilter<F> = FF

                    override fun MF(): Monad<F> = MF
                }

        inline fun <reified F> traverse(FF: Traverse<F> = kategory.traverse<F>(), MF: Monad<F> = kategory.monad<F>()): Traverse<OptionTKindPartial<F>> =
                object : OptionTTraverse<F> {
                    override fun FF(): Traverse<F> = FF

                    override fun MF(): Monad<F> = MF
                }

        inline fun <reified F> foldable(FF: Traverse<F> = kategory.traverse<F>(), MF: Monad<F> = kategory.monad<F>()): Foldable<OptionTKindPartial<F>> =
                traverse(FF, MF)

        inline fun <reified F> semigroupK(MF: Monad<F> = kategory.monad<F>()): SemigroupK<OptionTKindPartial<F>> = object : OptionTSemigroupK<F> {
            override fun F(): Monad<F> = MF
        }

        inline fun <reified F> monoidK(MF: Monad<F> = kategory.monad<F>()): MonoidK<OptionTKindPartial<F>> = object : OptionTMonoidK<F> {
            override fun F(): Monad<F> = MF
        }

        inline fun <reified F> functorFilter(MF: Monad<F> = kategory.monad<F>()): FunctorFilter<OptionTKindPartial<F>> =
                object : OptionTFunctor<F> {
                    override fun MF(): Monad<F> = MF
                }
    }

    inline fun <B> fold(crossinline default: () -> B, crossinline f: (A) -> B): HK<F, B> = MF.map(value, { option -> option.fold(default, f) })

    inline fun <B> cata(crossinline default: () -> B, crossinline f: (A) -> B): HK<F, B> = fold(default, f)

    inline fun <B> flatMap(crossinline f: (A) -> OptionT<F, B>): OptionT<F, B> = flatMapF({ it -> f(it).value })

    inline fun <B> flatMapF(crossinline f: (A) -> HK<F, Option<B>>): OptionT<F, B> =
            OptionT(MF, MF.flatMap(value, { option -> option.fold({ MF.pure(Option.None) }, f) }))

    fun <B> liftF(fa: HK<F, B>): OptionT<F, B> = OptionT(MF, MF.map(fa, { Option.Some(it) }))

    inline fun <B> semiflatMap(crossinline f: (A) -> HK<F, B>): OptionT<F, B> = flatMap({ option -> liftF(f(option)) })

    inline fun <B> map(crossinline f: (A) -> B): OptionT<F, B> = OptionT(MF, MF.map(value, { it.map(f) }))

    fun getOrElse(default: () -> A): HK<F, A> = MF.map(value, { it.getOrElse(default) })

    inline fun getOrElseF(crossinline default: () -> HK<F, A>): HK<F, A> = MF.flatMap(value, { it.fold(default, { MF.pure(it) }) })

    inline fun filter(crossinline p: (A) -> Boolean): OptionT<F, A> = OptionT(MF, MF.map(value, { it.filter(p) }))

    inline fun forall(crossinline p: (A) -> Boolean): HK<F, Boolean> = MF.map(value, { it.forall(p) })

    fun isDefined(): HK<F, Boolean> = MF.map(value, { it.isDefined })

    fun isEmpty(): HK<F, Boolean> = MF.map(value, { it.isEmpty })

    inline fun orElse(crossinline default: () -> OptionT<F, A>): OptionT<F, A> = orElseF({ default().value })

    inline fun orElseF(crossinline default: () -> HK<F, Option<A>>): OptionT<F, A> =
            OptionT(MF, MF.flatMap(value) {
                when (it) {
                    is Option.Some<A> -> MF.pure(it)
                    is Option.None -> default()
                }
            })

    inline fun <B> transform(crossinline f: (Option<A>) -> Option<B>): OptionT<F, B> = OptionT(MF, MF.map(value, { f(it) }))

    inline fun <B> subflatMap(crossinline f: (A) -> Option<B>): OptionT<F, B> = transform({ it.flatMap(f) })

    fun <B> foldL(b: B, f: (B, A) -> B, FF: Foldable<F>): B = FF.compose(Option.foldable()).foldLC(value, b, f)

    fun <B> foldR(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>, FF: Foldable<F>): Eval<B> = FF.compose(Option.foldable()).foldRC(value, lb, f)

    fun <G, B> traverseFilter(f: (A) -> HK<G, Option<B>>, GA: Applicative<G>, FF: Traverse<F>, MF: Monad<F>):
            HK<G, HK<OptionTKindPartial<F>, B>> {
        val fa = ComposedTraverseFilter(FF, Option.traverseFilter(), Option.applicative()).traverseFilterC(value, f, GA)
        return GA.map(fa, { OptionT(MF, MF.map(it.lower(), { it.ev() })) })
    }

    fun <G, B> traverse(f: (A) -> HK<G, B>, GA: Applicative<G>, FF: Traverse<F>, MF: Monad<F>): HK<G, HK<OptionTKindPartial<F>, B>> {
        val fa = ComposedTraverse(FF, Option.traverse(), Option.applicative()).traverseC(value, f, GA)
        return GA.map(fa, { OptionT(MF, MF.map(it.lower(), { it.ev() })) })
    }

    fun <R> toLeft(default: () -> R): EitherT<F, A, R> =
            EitherT(MF, cata({ default().right() }, { it.left() }))

    fun <L> toRight(default: () -> L): EitherT<F, L, A> =
            EitherT(MF, cata({ default().left() }, { it.right() }))
}

inline fun <F, A, B> OptionT<F, A>.mapFilter(crossinline f: (A) -> Option<B>, MF: Monad<F>): OptionT<F, B> =
        OptionT(MF, MF.map(value, { it.flatMap(f) }))

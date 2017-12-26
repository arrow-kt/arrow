package arrow

/**
 * [EitherT]`<F, A, B>` is a light wrapper on an `F<`[Either]`<A, B>>` with some
 * convenient methods for working with this nested structure.
 *
 * It may also be said that [EitherT] is a monad transformer for [Either].
 */
@higherkind data class EitherT<F, A, B>(val value: HK<F, Either<A, B>>) : EitherTKind<F, A, B>, EitherTKindedJ<F, A, B> {

    companion object {

        inline operator fun <reified F, A, B> invoke(value: HK<F, Either<A, B>>): EitherT<F, A, B> = EitherT(value)

        fun <F, A, B> pure(b: B, MF: Applicative<F>): EitherT<F, A, B> = right(b, MF)

        fun <F, L, A, B> tailRecM(a: A, f: (A) -> EitherTKind<F, L, Either<A, B>>, MF: Monad<F>): EitherT<F, L, B> =
                EitherT(MF.tailRecM(a, {
                    MF.map(f(it).ev().value) { recursionControl ->
                        when (recursionControl) {
                            is Left<L, Either<A, B>> -> Right(Left(recursionControl.a))
                            is Right<L, Either<A, B>> -> {
                                val b: Either<A, B> = recursionControl.b
                                when (b) {
                                    is Left<A, B> -> Left(b.a)
                                    is Right<A, B> -> Right(Right(b.b))
                                }
                            }
                        }
                    }
                }))

        fun <F, A, B> right(b: B, MF: Applicative<F>): EitherT<F, A, B> = EitherT(MF.pure(Right(b)))

        fun <F, A, B> left(a: A, MF: Applicative<F>): EitherT<F, A, B> = EitherT(MF.pure(Left(a)))

        inline fun <reified F, A, B> fromEither(value: Either<A, B>, MF: Applicative<F> = monad<F>()): EitherT<F, A, B> =
                EitherT(MF.pure(value))

        inline fun <reified F, L> functor(FF: Functor<F> = functor<F>()): Functor<EitherTKindPartial<F, L>> =
                EitherTFunctorInstanceImplicits.instance(FF)

        inline fun <reified F, L> applicative(MF: Monad<F> = monad<F>()): Applicative<EitherTKindPartial<F, L>> =
                EitherTApplicativeInstanceImplicits.instance(MF)

        inline fun <reified F, L> monad(MF: Monad<F> = monad<F>()): Monad<EitherTKindPartial<F, L>> =
                EitherTMonadInstanceImplicits.instance(MF)

        inline fun <reified F, L> monadError(MF: Monad<F> = monad<F>()): MonadError<EitherTKindPartial<F, L>, L> =
                EitherTMonadErrorInstanceImplicits.instance(MF)

        inline fun <reified F, A> traverse(FF: Traverse<F> = traverse<F>()): Traverse<EitherTKindPartial<F, A>> =
                EitherTTraverseInstanceImplicits.instance(FF)

        inline fun <reified F, A> foldable(FF: Traverse<F> = traverse<F>()): Foldable<EitherTKindPartial<F, A>> =
                EitherTFoldableInstanceImplicits.instance(FF)

        inline fun <reified F, L> semigroupK(MF: Monad<F> = monad<F>()): SemigroupK<EitherTKindPartial<F, L>> =
                EitherTSemigroupKInstanceImplicits.instance(MF)
    }

    inline fun <C> fold(crossinline l: (A) -> C, crossinline r: (B) -> C, FF: Functor<F>): HK<F, C> = FF.map(value, { either -> either.fold(l, r) })

    inline fun <C> flatMap(crossinline f: (B) -> EitherT<F, A, C>, MF: Monad<F>): EitherT<F, A, C> = flatMapF({ it -> f(it).value }, MF)

    inline fun <C> flatMapF(crossinline f: (B) -> HK<F, Either<A, C>>, MF: Monad<F>): EitherT<F, A, C> =
            EitherT(MF.flatMap(value, { either -> either.fold({ MF.pure(Left(it)) }, { f(it) }) }))

    inline fun <C> cata(crossinline l: (A) -> C, crossinline r: (B) -> C, FF: Functor<F>): HK<F, C> = fold(l, r, FF)

    fun <C> liftF(fa: HK<F, C>, FF: Functor<F>): EitherT<F, A, C> = EitherT(FF.map(fa, { Right(it) }))

    inline fun <C> semiflatMap(crossinline f: (B) -> HK<F, C>, MF: Monad<F>): EitherT<F, A, C> = flatMap({ liftF(f(it), MF) }, MF)

    inline fun <C> map(crossinline f: (B) -> C, FF: Functor<F>): EitherT<F, A, C> = EitherT(FF.map(value, { it.map(f) }))

    inline fun exists(crossinline p: (B) -> Boolean, FF: Functor<F>): HK<F, Boolean> = FF.map(value, { it.exists(p) })

    inline fun <C, D> transform(crossinline f: (Either<A, B>) -> Either<C, D>, FF: Functor<F>): EitherT<F, C, D> = EitherT(FF.map(value, { f(it) }))

    inline fun <C> subflatMap(crossinline f: (B) -> Either<A, C>, FF: Functor<F>): EitherT<F, A, C> = transform({ it.flatMap(f) }, FF)

    fun toOptionT(FF: Functor<F>): OptionT<F, B> = OptionT(FF.map(value, { it.toOption() }))

    fun <C> foldLeft(b: C, f: (C, B) -> C, FF: Foldable<F>): C = FF.compose(Either.foldable<A>()).foldLC(value, b, f)

    fun <C> foldRight(lb: Eval<C>, f: (B, Eval<C>) -> Eval<C>, FF: Foldable<F>): Eval<C> = FF.compose(Either.foldable<A>()).foldRC(value, lb, f)

    fun <G, C> traverse(f: (B) -> HK<G, C>, GA: Applicative<G>, FF: Traverse<F>): HK<G, EitherT<F, A, C>> {
        val fa: HK<G, HK<Nested<F, EitherKindPartial<A>>, C>> = ComposedTraverse(FF, Either.traverse(), Either.monad<A>()).traverseC(value, f, GA)
        return GA.map(fa, { EitherT(FF.map(it.unnest(), { it.ev() })) })
    }

    fun combineK(y: EitherTKind<F, A, B>, MF: Monad<F>): EitherT<F, A, B> =
            EitherT(MF.flatMap(this.ev().value) {
                when (it) {
                    is Left -> y.ev().value
                    is Right -> MF.pure(it)
                }
            })

    fun <C> ap(ff: EitherTKind<F, A, (B) -> C>, MF: Monad<F>): EitherT<F, A, C> = ff.ev().flatMap ({ f -> map(f, MF) }, MF)
}

fun <F, A, B> EitherTKind<F, A, B>.value(): HK<F, Either<A, B>> = this.ev().value

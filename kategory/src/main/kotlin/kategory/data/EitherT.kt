package kategory

typealias EitherTKind<F, A, B> = HK3<EitherT.F, F, A, B>
typealias EitherTF<F, L> = HK2<EitherT.F, F, L>

fun <F, A, B> EitherTKind<F, A, B>.ev(): EitherT<F, A, B> =
        this as EitherT<F, A, B>

/**
 * [EitherT]`<F, A, B>` is a light wrapper on an `F<`[Either]`<A, B>>` with some
 * convenient methods for working with this nested structure.
 *
 * It may also be said that [EitherT] is a monad transformer for [Either].
 */
data class EitherT<F, A, B>(val MF: Monad<F>, val value: HK<F, Either<A, B>>) : EitherTKind<F, A, B> {

    class F private constructor()

    companion object {

        inline operator fun <reified F, A, B> invoke(value: HK<F, Either<A, B>>, MF: Monad<F> = monad<F>()): EitherT<F, A, B> =
                EitherT(MF, value)

        @JvmStatic inline fun <reified F, A, B> pure(b: B, MF: Monad<F> = monad<F>()): EitherT<F, A, B> =
                right(b, MF)

        @JvmStatic inline fun <reified F, A, B> right(b: B, MF: Monad<F> = monad<F>()): EitherT<F, A, B> =
                EitherT(MF, MF.pure(Either.Right(b)))

        @JvmStatic inline fun <reified F, A, B> left(a: A, MF: Monad<F> = monad<F>()): EitherT<F, A, B> =
                EitherT(MF, MF.pure(Either.Left(a)))

        @JvmStatic inline fun <reified F, A, B> fromEither(value: Either<A, B>, MF: Monad<F> = monad<F>()): EitherT<F, A, B> =
                EitherT(MF, MF.pure(value))
    }

    inline fun <C> fold(crossinline l: (A) -> C, crossinline r: (B) -> C): HK<F, C> =
            MF.map(value, { either -> either.fold(l, r) })

    inline fun <C> flatMap(crossinline f: (B) -> EitherT<F, A, C>): EitherT<F, A, C> =
            flatMapF({ it -> f(it).value })

    inline fun <C> flatMapF(crossinline f: (B) -> HK<F, Either<A, C>>): EitherT<F, A, C> =
            EitherT(MF, MF.flatMap(value, { either -> either.fold({ MF.pure(Either.Left(it)) }, { f(it) }) }))

    inline fun <C> cata(crossinline l: (A) -> C, crossinline r: (B) -> C): HK<F, C> =
            fold(l, r)

    fun <C> liftF(fa: HK<F, C>): EitherT<F, A, C> =
            EitherT(MF, MF.map(fa, { Either.Right(it) }))

    inline fun <C> semiflatMap(crossinline f: (B) -> HK<F, C>): EitherT<F, A, C> =
            flatMap({ liftF(f(it)) })

    inline fun <C> map(crossinline f: (B) -> C): EitherT<F, A, C> =
            EitherT(MF, MF.map(value, { it.map(f) }))

    inline fun exists(crossinline p: (B) -> Boolean): HK<F, Boolean> =
            MF.map(value, { it.exists(p) })

    inline fun <C, D> transform(crossinline f: (Either<A, B>) -> Either<C, D>): EitherT<F, C, D> =
            EitherT(MF, MF.map(value, { f(it) }))

    inline fun <C> subflatMap(crossinline f: (B) -> Either<A, C>): EitherT<F, A, C> =
            transform({ it.flatMap(f) })

    fun toOptionT(): OptionT<F, B> =
            OptionT(MF, MF.map(value, { it.toOption() }))

    fun <C> foldL(b: C, f: (C, B) -> C, FF: Foldable<F>): C =
            FF.compose(EitherTraverse<A>()).foldLC(value, b, f)

    fun <C> foldR(lb: Eval<C>, f: (B, Eval<C>) -> Eval<C>, FF: Foldable<F>): Eval<C> =
            FF.compose(EitherTraverse<A>()).foldRC(value, lb, f)

    fun <G, C> traverse(f: (B) -> HK<G, C>, GA: Applicative<G>, FF: Traverse<F>, MF: Monad<F>): HK<G, HK<EitherTF<F, A>, C>> {
        val fa = ComposedTraverse(FF, EitherTraverse<A>(), Either.monad<A>()).traverseC(value, f, GA)
        return GA.map(fa, { EitherT(MF, MF.map(it.lower(), { it.ev() })) })
    }
}

class EitherTInstances<F, L>(val MF : Monad<F>) : EitherTMonadError<F, L> {
    override fun MF(): Monad<F> = MF
}

interface EitherTMonad<F, L> : Monad<EitherTF<F, L>> {

    fun MF() : Monad<F>

    override fun <A> pure(a: A): EitherT<F, L, A> =
            EitherT(MF(), MF().pure(Either.Right(a)))

    override fun <A, B> map(fa: EitherTKind<F, L, A>, f: (A) -> B): EitherT<F, L, B> =
            fa.ev().map { f(it) }

    override fun <A, B> flatMap(fa: EitherTKind<F, L, A>, f: (A) -> EitherTKind<F, L, B>): EitherT<F, L, B> =
            fa.ev().flatMap { f(it).ev() }

    override fun <A, B> tailRecM(a: A, f: (A) -> HK<EitherTF<F, L>, Either<A, B>>): EitherT<F, L, B> =
            EitherT(MF(), MF().tailRecM(a, {
                MF().map(f(it).ev().value) { recursionControl ->
                    when (recursionControl) {
                        is Either.Left<L> -> Either.Right(Either.Left(recursionControl.a))
                        is Either.Right<Either<A, B>> ->
                            when (recursionControl.b) {
                                is Either.Left<A> -> Either.Left(recursionControl.b.a)
                                is Either.Right<B> -> Either.Right(Either.Right(recursionControl.b.b))
                            }
                    }
                }
            }))

}

interface EitherTMonadError<F, E> : EitherTMonad<F, E>, MonadError<EitherTF<F, E>, E> {

    override fun <A> handleErrorWith(fa: EitherTKind<F, E, A>, f: (E) -> EitherTKind<F, E, A>): EitherT<F, E, A> =
            EitherT(MF(), MF().flatMap(fa.ev().value, {
                when (it) {
                    is Either.Left -> f(it.a).ev().value
                    is Either.Right -> MF().pure(it)
                }
            }))

    override fun <A> raiseError(e: E): EitherT<F, E, A> =
            EitherT(MF(), MF().pure(Either.Left(e)))

}

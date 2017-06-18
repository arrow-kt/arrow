package kategory

data class EitherTMonad<F, L>(val MF : Monad<F>, val dummy: Unit = Unit) : Monad<EitherTF<F, L>> {
    override fun <A> pure(a: A): EitherT<F, L, A> =
            EitherT(MF, MF.pure(Either.Right(a)))

    override fun <A, B> map(fa: EitherTKind<F, L, A>, f: (A) -> B): EitherT<F, L, B> =
            fa.ev().map { f(it) }

    override fun <A, B> flatMap(fa: EitherTKind<F, L, A>, f: (A) -> EitherTKind<F, L, B>): EitherT<F, L, B> =
            fa.ev().flatMap { f(it).ev() }

    override fun <A, B> tailRecM(a: A, f: (A) -> HK<EitherTF<F, L>, Either<A, B>>): EitherT<F, L, B> =
            EitherT(MF, MF.tailRecM(a, {
                MF.map(f(it).ev().value) { recursionControl ->
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

    companion object {
        inline operator fun <reified F, L> invoke(MF: Monad<F> = monad<F>()): EitherTMonad<F, L> =
                EitherTMonad(MF, Unit)
    }
}
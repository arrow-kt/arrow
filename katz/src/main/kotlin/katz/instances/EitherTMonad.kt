package katz

class EitherTMonad<F, L>(val MF : Monad<F>) : Monad<EitherTF<F, L>> {
    override fun <A> pure(a: A): EitherT<F, L, A> =
            EitherT(MF, MF.pure(Either.Right(a)))

    override fun <A, B> map(fa: EitherTKind<F, L, A>, f: (A) -> B): EitherT<F, L, B> =
            fa.ev().map { f(it) }

    override fun <A, B> flatMap(fa: EitherTKind<F, L, A>, f: (A) -> EitherTKind<F, L, B>): EitherT<F, L, B> =
            fa.ev().flatMap { f(it).ev() }

    override fun <A, B> tailRecM(a: A, f: (A) -> HK<EitherTF<F, L>, Either<A, B>>): EitherT<F, L, B> =
            EitherT(MF, MF.tailRecM(a, {
                MF.map(f(it).ev().value) {
                    it.fold({
                        Either.Right(Either.Left(it))
                    }, {
                        it.fold({
                            Either.Left(it)
                        }, {
                            Either.Right(Either.Right(it))
                        })
                    })
                }
            }))
}

fun <F, A, B> EitherTKind<F, A, B>.ev(): EitherT<F, A, B> = this as EitherT<F, A, B>

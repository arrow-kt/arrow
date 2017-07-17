package kategory

interface EitherInstances<L> :
        Functor<EitherF<L>>,
        Applicative<EitherF<L>>,
        Monad<EitherF<L>>,
        MonadError<EitherF<L>, L>{

    override fun <A> pure(a: A): Either<L, A> = Either.Right(a)

    override fun <A, B> flatMap(fa: EitherKind<L, A>, f: (A) -> EitherKind<L, B>): Either<L, B> =
            fa.ev().flatMap { f(it).ev() }

    override fun <A, B> map(fa: HK<EitherF<L>, A>, f: (A) -> B): Either<L, B> =
            fa.ev().map(f)

    tailrec override fun <A, B> tailRecM(a: A, f: (A) -> HK<EitherF<L>, Either<A, B>>): Either<L, B> {
        val e = f(a).ev().ev()
        return when (e) {
            is Either.Left -> e
            is Either.Right -> when (e.b) {
                is Either.Left -> tailRecM(e.b.a, f)
                is Either.Right -> e.b
            }
        }
    }

    override fun <A> raiseError(e: L): Either<L, A> = Either.Left(e)

    override fun <A> handleErrorWith(fa: HK<EitherF<L>, A>, f: (L) -> HK<EitherF<L>, A>): Either<L, A> {
        val fea = fa.ev()
        return when (fea) {
            is Either.Left -> f(fea.a).ev()
            is Either.Right -> fea
        }
    }

}

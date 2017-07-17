package kategory

interface EitherInstances<L> :
        Functor<EitherF<L>>,
        Applicative<EitherF<L>>,
        Monad<EitherF<L>>,
        MonadError<EitherF<L>, L>,
        Foldable<EitherF<L>>,
        Traverse<EitherF<L>> {

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

    override fun <G, A, B> traverse(fa: HK<EitherF<L>, A>, f: (A) -> HK<G, B>, GA: Applicative<G>): HK<G, HK<EitherF<L>, B>> =
            fa.ev().let { either ->
                when (either) {
                    is Either.Right -> GA.map(f(either.b), { Either.Right(it) })
                    is Either.Left -> GA.pure(either)
                }
            }

    override fun <A, B> foldL(fa: HK<EitherF<L>, A>, b: B, f: (B, A) -> B): B =
            fa.ev().let { either ->
                when (either) {
                    is Either.Right -> f(b, either.b)
                    is Either.Left -> b
                }
            }

    override fun <A, B> foldR(fa: HK<EitherF<L>, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
            fa.ev().let { either ->
                when (either) {
                    is Either.Right -> f(either.b, lb)
                    is Either.Left -> lb
                }
            }

}

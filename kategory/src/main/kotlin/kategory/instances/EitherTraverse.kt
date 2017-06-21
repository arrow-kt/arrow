package kategory

class EitherTraverse<L> : Traverse<EitherF<L>> {
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

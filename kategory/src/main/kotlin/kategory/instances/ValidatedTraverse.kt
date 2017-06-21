package kategory

class ValidatedTraverse<L> : Traverse<ValidatedF<L>> {
    override fun <G, A, B> traverse(fa: HK<ValidatedF<L>, A>, f: (A) -> HK<G, B>, GA: Applicative<G>): HK<G, HK<ValidatedF<L>, B>> =
            fa.ev().let { validated ->
                when (validated) {
                    is Validated.Valid -> GA.map(f(validated.a), { Validated.Valid(it) })
                    is Validated.Invalid -> GA.pure(validated)
                }
            }

    override fun <A, B> foldL(fa: HK<ValidatedF<L>, A>, b: B, f: (B, A) -> B): B =
            fa.ev().let { validated ->
                when (validated) {
                    is Validated.Valid -> f(b, validated.a)
                    is Validated.Invalid -> b
                }
            }

    override fun <A, B> foldR(fa: HK<ValidatedF<L>, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
            fa.ev().let { validated ->
                when (validated) {
                    is Validated.Valid -> f(validated.a, lb)
                    is Validated.Invalid -> lb
                }
            }
}

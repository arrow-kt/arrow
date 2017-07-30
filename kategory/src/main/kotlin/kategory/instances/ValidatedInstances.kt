package kategory

import kategory.Validated.Valid
import kategory.Validated.Invalid

interface ValidatedInstances<E> :
        Functor<ValidatedF<E>>,
        Applicative<ValidatedF<E>>,
        ApplicativeError<ValidatedF<E>, E>,
        Foldable<ValidatedF<E>>,
        Traverse<ValidatedF<E>> {

    fun SE(): Semigroup<E>

    override fun <A> pure(a: A): Validated<E, A> = Validated.Valid(a)

    override fun <A, B> map(fa: HK<ValidatedF<E>, A>, f: (A) -> B): Validated<E, B> =
        fa.ev().map(f)

    override fun <A> raiseError(e: E): Validated<E, A> = Validated.Invalid(e)

    override fun <A> handleErrorWith(fa: ValidatedKind<E, A>, f: (E) -> ValidatedKind<E, A>): Validated<E, A> =
            fa.ev().fold({ f(it).ev() }, { Validated.Valid(it) })

    override fun <A, B> ap(fa: ValidatedKind<E, A>, ff: HK<ValidatedF<E>, (A) -> B>): Validated<E, B> =
            fa.ev().ap(ff.ev(), SE())

    override fun <G, A, B> traverse(fa: HK<ValidatedF<E>, A>, f: (A) -> HK<G, B>, GA: Applicative<G>): HK<G, Validated<E, B>> =
            fa.ev().let { validated ->
                when (validated) {
                    is Validated.Valid -> GA.map(f(validated.a), { Validated.Valid(it) })
                    is Validated.Invalid -> GA.pure(validated)
                }
            }

    override fun <A, B> foldL(fa: HK<ValidatedF<E>, A>, b: B, f: (B, A) -> B): B =
            fa.ev().let { validated ->
                when (validated) {
                    is Validated.Valid -> f(b, validated.a)
                    is Validated.Invalid -> b
                }
            }

    override fun <A, B> foldR(fa: HK<ValidatedF<E>, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
            fa.ev().let { validated ->
                when (validated) {
                    is Validated.Valid -> f(validated.a, lb)
                    is Validated.Invalid -> lb
                }
            }
}

interface ValidatedSemigroupK<A> : SemigroupK<ValidatedF<A>> {

    fun F(): SemigroupK<A>

    override fun <B> combineK(x: HK<ValidatedF<A>, B>, y: HK<ValidatedF<A>, B>): Validated<A, B> =
            when (x) {
                is Valid -> x
                else -> when (y) {
                    is Invalid -> Invalid(F().combine((x as Invalid).e, y.e))
                    else -> y.ev()
                }
            }
}

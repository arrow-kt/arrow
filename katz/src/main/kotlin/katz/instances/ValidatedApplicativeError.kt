package katz

class ValidatedApplicativeError<E>(val SE: Semigroup<E>) : ApplicativeError<ValidatedF<E>, E> {

    override fun <A> pure(a: A): Validated<E, A> = Validated.Valid(a)

    override fun <A> raiseError(e: E): Validated<E, A> = Validated.Invalid(e)

    override fun <A> handleErrorWith(fa: ValidatedKind<E, A>, f: (E) -> ValidatedKind<E, A>): Validated<E, A> =
            fa.ev().fold({ f(it).ev() }, { Validated.Valid(it) })

    override fun <A, B> ap(fa: ValidatedKind<E, A>, ff: HK<ValidatedF<E>, (A) -> B>): Validated<E, B> =
            fa.ev().ap(ff.ev(), SE)
}

package kategory

interface ValidatedFunctorInstance<E> : Functor<ValidatedKindPartial<E>> {
    override fun <A, B> map(fa: ValidatedKind<E, A>, f: (A) -> B): Validated<E, B> = fa.ev().map(f)
}

object ValidatedFunctorInstanceImplicits {
    @JvmStatic
    fun <E> instance(): ValidatedFunctorInstance<E> = object : ValidatedFunctorInstance<E> {}
}

interface ValidatedApplicativeInstance<E> : ValidatedFunctorInstance<E>, Applicative<ValidatedKindPartial<E>> {

    fun SE(): Semigroup<E>

    override fun <A> pure(a: A): Validated<E, A> = Validated.Valid(a)

    override fun <A, B> map(fa: ValidatedKind<E, A>, f: (A) -> B): Validated<E, B> = fa.ev().map(f)

    override fun <A, B> ap(fa: ValidatedKind<E, A>, ff: HK<ValidatedKindPartial<E>, (A) -> B>): Validated<E, B> = fa.ev().ap(ff.ev(), SE())

}

object ValidatedApplicativeInstanceImplicits {
    @JvmStatic
    fun <E> instance(SE: Semigroup<E>): ValidatedApplicativeInstance<E> = object : ValidatedApplicativeInstance<E> {
        override fun SE(): Semigroup<E> = SE
    }
}

interface ValidatedApplicativeErrorInstance<E> : ValidatedApplicativeInstance<E>, ApplicativeError<ValidatedKindPartial<E>, E> {

    override fun <A> raiseError(e: E): Validated<E, A> = Validated.Invalid(e)

    override fun <A> handleErrorWith(fa: ValidatedKind<E, A>, f: (E) -> ValidatedKind<E, A>): Validated<E, A> =
            fa.ev().handleLeftWith(f)

}

object ValidatedApplicativeErrorInstanceImplicits {
    @JvmStatic
    fun <E> instance(SE: Semigroup<E>): ValidatedApplicativeErrorInstance<E> = object : ValidatedApplicativeErrorInstance<E> {
        override fun SE(): Semigroup<E> = SE
    }
}

interface ValidatedFoldableInstance<E> : Foldable<ValidatedKindPartial<E>> {

    override fun <A, B> foldL(fa: ValidatedKind<E, A>, b: B, f: (B, A) -> B): B =
            fa.ev().foldL(b, f)

    override fun <A, B> foldR(fa: ValidatedKind<E, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
            fa.ev().foldR(lb, f)

}

object ValidatedFoldableInstanceImplicits {
    @JvmStatic
    fun <E> instance(): ValidatedFoldableInstance<E> = object : ValidatedFoldableInstance<E> {}
}

interface ValidatedTraverseInstance<E> : ValidatedFoldableInstance<E>, Traverse<ValidatedKindPartial<E>> {

    override fun <G, A, B> traverse(fa: HK<ValidatedKindPartial<E>, A>, f: (A) -> HK<G, B>, GA: Applicative<G>): HK<G, Validated<E, B>> =
            fa.ev().traverse(f, GA)

}

object ValidatedTraverseInstanceImplicits {
    @JvmStatic
    fun <E> instance(): ValidatedTraverseInstance<E> = object : ValidatedTraverseInstance<E> {}
}

interface ValidatedSemigroupKInstance<E> : SemigroupK<ValidatedKindPartial<E>> {

    fun SE(): Semigroup<E>

    override fun <B> combineK(x: ValidatedKind<E, B>, y: ValidatedKind<E, B>): Validated<E, B> =
            x.ev().combineK(y, SE())

}

object ValidatedSemigroupKInstanceImplicits {
    @JvmStatic
    fun <E> instance(SE: Semigroup<E>): ValidatedSemigroupKInstance<E> = object : ValidatedSemigroupKInstance<E> {
        override fun SE(): Semigroup<E> = SE
    }
}

package kategory

@instance(Validated::class)
interface ValidatedFunctorInstance<E> : Functor<ValidatedKindPartial<E>> {
    override fun <A, B> map(fa: ValidatedKind<E, A>, f: (A) -> B): Validated<E, B> = fa.ev().map(f)
}

@instance(Validated::class)
interface ValidatedApplicativeInstance<E> : ValidatedFunctorInstance<E>, Applicative<ValidatedKindPartial<E>> {

    fun SE(): Semigroup<E>

    override fun <A> pure(a: A): Validated<E, A> = Validated.Valid(a)

    override fun <A, B> map(fa: ValidatedKind<E, A>, f: (A) -> B): Validated<E, B> = fa.ev().map(f)

    override fun <A, B> ap(fa: ValidatedKind<E, A>, ff: HK<ValidatedKindPartial<E>, (A) -> B>): Validated<E, B> = fa.ev().ap(ff.ev(), SE())

}

@instance(Validated::class)
interface ValidatedApplicativeErrorInstance<E> : ValidatedApplicativeInstance<E>, ApplicativeError<ValidatedKindPartial<E>, E> {

    override fun <A> raiseError(e: E): Validated<E, A> = Validated.Invalid(e)

    override fun <A> handleErrorWith(fa: ValidatedKind<E, A>, f: (E) -> ValidatedKind<E, A>): Validated<E, A> =
            fa.ev().handleLeftWith(f)

}

@instance(Validated::class)
interface ValidatedFoldableInstance<E> : Foldable<ValidatedKindPartial<E>> {

    override fun <A, B> foldL(fa: ValidatedKind<E, A>, b: B, f: (B, A) -> B): B =
            fa.ev().foldL(b, f)

    override fun <A, B> foldR(fa: ValidatedKind<E, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
            fa.ev().foldR(lb, f)

}

@instance(Validated::class)
interface ValidatedTraverseInstance<E> : ValidatedFoldableInstance<E>, Traverse<ValidatedKindPartial<E>> {

    override fun <G, A, B> traverse(fa: HK<ValidatedKindPartial<E>, A>, f: (A) -> HK<G, B>, GA: Applicative<G>): HK<G, Validated<E, B>> =
            fa.ev().traverse(f, GA)

}

@instance(Validated::class)
interface ValidatedSemigroupKInstance<E, A> : SemigroupK<ValidatedKindPartial<E>> {

    fun SE(): Semigroup<E>
    fun SA(): Semigroup<A>

    override fun <A> combineK(x: ValidatedKind<E, A>, y: ValidatedKind<E, A>): Validated<E, A> =
            x.ev().combineK(y, SE(), SA() as Semigroup<A>)

}

@instance(Validated::class)
interface ValidatedEqInstance<L, R> : Eq<Validated<L, R>> {

    fun EQL(): Eq<L>

    fun EQR(): Eq<R>

    override fun eqv(a: Validated<L, R>, b: Validated<L, R>): Boolean = when (a) {
        is Validated.Valid -> when (b) {
            is Validated.Invalid -> false
            is Validated.Valid -> EQR().eqv(a.a, b.a)
        }
        is Validated.Invalid -> when (b) {
            is Validated.Invalid -> EQL().eqv(a.e, b.e)
            is Validated.Valid -> false
        }
    }
}

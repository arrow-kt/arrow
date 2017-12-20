package arrow

@instance(Either::class)
interface EitherFunctorInstance<L> : Functor<EitherKindPartial<L>> {
    override fun <A, B> map(fa: EitherKind<L, A>, f: (A) -> B): Either<L, B> = fa.ev().map(f)
}

@instance(Either::class)
interface EitherApplicativeInstance<L> : EitherFunctorInstance<L>, Applicative<EitherKindPartial<L>> {

    override fun <A> pure(a: A): Either<L, A> = Right(a)

    override fun <A, B> map(fa: EitherKind<L, A>, f: (A) -> B): Either<L, B> = fa.ev().map(f)

    override fun <A, B> ap(fa: EitherKind<L, A>, ff: EitherKind<L, (A) -> B>): Either<L, B> =
            fa.ev().ap(ff)
}

@instance(Either::class)
interface EitherMonadInstance<L> : EitherApplicativeInstance<L>, Monad<EitherKindPartial<L>> {

    override fun <A, B> ap(fa: EitherKind<L, A>, ff: EitherKind<L, (A) -> B>): Either<L, B> =
            fa.ev().ap(ff)

    override fun <A, B> flatMap(fa: EitherKind<L, A>, f: (A) -> EitherKind<L, B>): Either<L, B> = fa.ev().flatMap { f(it).ev() }

    override fun <A, B> tailRecM(a: A, f: (A) -> HK<EitherKindPartial<L>, Either<A, B>>): Either<L, B> =
            Either.tailRecM(a, f)
}

@instance(Either::class)
interface EitherMonadErrorInstance<L> : EitherMonadInstance<L>, MonadError<EitherKindPartial<L>, L> {

    override fun <A> raiseError(e: L): Either<L, A> = Left(e)

    override fun <A> handleErrorWith(fa: HK<EitherKindPartial<L>, A>, f: (L) -> HK<EitherKindPartial<L>, A>): Either<L, A> {
        val fea = fa.ev()
        return when (fea) {
            is Left -> f(fea.a).ev()
            is Right -> fea
        }
    }
}

@instance(Either::class)
interface EitherFoldableInstance<L> : Foldable<EitherKindPartial<L>> {

    override fun <A, B> foldLeft(fa: HK<EitherKindPartial<L>, A>, b: B, f: (B, A) -> B): B =
            fa.ev().foldLeft(b, f)

    override fun <A, B> foldRight(fa: HK<EitherKindPartial<L>, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
            fa.ev().foldRight(lb, f)
}

@instance(Either::class)
interface EitherTraverseInstance<L> : EitherFoldableInstance<L>, Traverse<EitherKindPartial<L>> {

    override fun <G, A, B> traverse(fa: HK<EitherKindPartial<L>, A>, f: (A) -> HK<G, B>, GA: Applicative<G>): HK<G, HK<EitherKindPartial<L>, B>> =
            fa.ev().traverse(f, GA)
}

@instance(Either::class)
interface EitherSemigroupKInstance<L> : SemigroupK<EitherKindPartial<L>> {

    override fun <A> combineK(x: EitherKind<L, A>, y: EitherKind<L, A>): Either<L, A> =
            x.ev().combineK(y)
}

@instance(Either::class)
interface EitherEqInstance<L, R> : Eq<Either<L, R>> {

    fun EQL(): Eq<L>

    fun EQR(): Eq<R>

    override fun eqv(a: Either<L, R>, b: Either<L, R>): Boolean = when (a) {
        is Left -> when (b) {
            is Left -> EQL().eqv(a.a, b.a)
            is Right -> false
        }
        is Right -> when (b) {
            is Left -> false
            is Right -> EQR().eqv(a.b, b.b)
        }
    }

}

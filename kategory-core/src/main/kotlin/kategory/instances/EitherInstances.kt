package kategory

interface EitherFunctorInstance<L> : Functor<EitherKindPartial<L>> {
    override fun <A, B> map(fa: EitherKind<L, A>, f: (A) -> B): Either<L, B> = fa.ev().map(f)
}

object EitherFunctorInstanceImplicits {
    @JvmStatic fun <L> instance(): EitherFunctorInstance<L> = object: EitherFunctorInstance<L> {}
}

interface EitherApplicativeInstance<L> : EitherFunctorInstance<L> , Applicative<EitherKindPartial<L>> {
    
    override fun <A> pure(a: A): Either<L, A> = Either.Right(a)
    
    override fun <A, B> map(fa: EitherKind<L, A>, f: (A) -> B): Either<L, B> = fa.ev().map(f)

    override fun <A, B> ap(fa: EitherKind<L, A>, ff: EitherKind<L, (A) -> B>): Either<L, B> =
            fa.ev().ap(ff)
}

object EitherApplicativeInstanceImplicits {
    @JvmStatic fun <L> instance(): EitherApplicativeInstance<L> = object: EitherApplicativeInstance<L> {}
}

interface EitherMonadInstance<L> : EitherApplicativeInstance<L>, Monad<EitherKindPartial<L>> {

    override fun <A, B> ap(fa: EitherKind<L, A>, ff: EitherKind<L, (A) -> B>): Either<L, B> =
            fa.ev().ap(ff)
    
    override fun <A, B> flatMap(fa: EitherKind<L, A>, f: (A) -> EitherKind<L, B>): Either<L, B> = fa.ev().flatMap { f(it).ev() }

    override fun <A, B> tailRecM(a: A, f: (A) -> HK<EitherKindPartial<L>, Either<A, B>>): Either<L, B> =
            Either.tailRecM(a, f)
}

object EitherMonadInstanceImplicits {
    @JvmStatic fun <L> instance(): EitherMonadInstance<L> = object: EitherMonadInstance<L> {}
}

interface EitherMonadErrorInstance<L> : EitherMonadInstance<L>, MonadError<EitherKindPartial<L>, L> {

    override fun <A> raiseError(e: L): Either<L, A> = Either.Left(e)

    override fun <A> handleErrorWith(fa: HK<EitherKindPartial<L>, A>, f: (L) -> HK<EitherKindPartial<L>, A>): Either<L, A> {
        val fea = fa.ev()
        return when (fea) {
            is Either.Left -> f(fea.a).ev()
            is Either.Right -> fea
        }
    }
}

object EitherMonadErrorInstanceImplicits {
    @JvmStatic fun <L> instance(): EitherMonadErrorInstance<L> = object: EitherMonadErrorInstance<L> {}
}

interface EitherFoldableInstance<L> : Foldable<EitherKindPartial<L>> {

    override fun <A, B> foldL(fa: HK<EitherKindPartial<L>, A>, b: B, f: (B, A) -> B): B =
            fa.ev().foldL(b, f)

    override fun <A, B> foldR(fa: HK<EitherKindPartial<L>, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
            fa.ev().foldR(lb, f)
}

object EitherFoldableInstanceImplicits {
    @JvmStatic fun <L> instance(): EitherFoldableInstance<L> = object: EitherFoldableInstance<L> {}
}

interface EitherTraverseInstance<L> : EitherFoldableInstance<L>, Traverse<EitherKindPartial<L>> {

    override fun <G, A, B> traverse(fa: HK<EitherKindPartial<L>, A>, f: (A) -> HK<G, B>, GA: Applicative<G>): HK<G, HK<EitherKindPartial<L>, B>> =
            fa.ev().traverse(f, GA)
}

object EitherTraverseInstanceImplicits {
    @JvmStatic fun <L> instance(): EitherTraverseInstance<L> = object: EitherTraverseInstance<L> {}
}

interface EitherSemigroupKInstance<L> : SemigroupK<EitherKindPartial<L>> {

    override fun <A> combineK(x: EitherKind<L, A>, y: EitherKind<L, A>): Either<L, A> =
            x.ev().combineK(y)
}

object EitherSemigroupKInstanceImplicits {
    @JvmStatic fun <L> instance(): EitherSemigroupKInstance<L> = object: EitherSemigroupKInstance<L> {}
}

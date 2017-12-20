package arrow

interface EitherTFunctorInstance<F, L> : Functor<EitherTKindPartial<F, L>> {

    fun FF(): Functor<F>

    override fun <A, B> map(fa: EitherTKind<F, L, A>, f: (A) -> B): EitherT<F, L, B> = fa.ev().map({ f(it) }, FF())
}

object EitherTFunctorInstanceImplicits {
    fun <F, L> instance(FF: Functor<F>): EitherTFunctorInstance<F, L> = object : EitherTFunctorInstance<F, L> {
        override fun FF(): Functor<F> = FF
    }
}

interface EitherTApplicativeInstance<F, L> : EitherTFunctorInstance<F, L>, Applicative<EitherTKindPartial<F, L>> {

    fun MF(): Monad<F>

    override fun <A> pure(a: A): EitherT<F, L, A> = EitherT.pure(a, MF())

    override fun <A, B> map(fa: EitherTKind<F, L, A>, f: (A) -> B): EitherT<F, L, B> = fa.ev().map({ f(it) }, MF())

    override fun <A, B> ap(fa: EitherTKind<F, L, A>, ff: EitherTKind<F, L, (A) -> B>): EitherT<F, L, B> =
            fa.ev().ap(ff, MF())
}

object EitherTApplicativeInstanceImplicits {
    fun <F, L> instance(MF: Monad<F>): EitherTApplicativeInstance<F, L> = object : EitherTApplicativeInstance<F, L> {
        override fun FF(): Functor<F> = MF

        override fun MF(): Monad<F> = MF
    }
}

interface EitherTMonadInstance<F, L> : EitherTApplicativeInstance<F, L>, Monad<EitherTKindPartial<F, L>> {

    override fun <A, B> ap(fa: EitherTKind<F, L, A>, ff: EitherTKind<F, L, (A) -> B>): EitherT<F, L, B> =
            fa.ev().ap(ff,
                    MF())

    override fun <A, B> flatMap(fa: EitherTKind<F, L, A>, f: (A) -> EitherTKind<F, L, B>): EitherT<F, L, B> = fa.ev().flatMap({ f(it).ev() }, MF())

    override fun <A, B> tailRecM(a: A, f: (A) -> EitherTKind<F, L, Either<A, B>>): EitherT<F, L, B> =
            EitherT.tailRecM(a, f, MF())
}

object EitherTMonadInstanceImplicits {

    fun <F, L> instance(MF: Monad<F>): EitherTMonadInstance<F, L> = object : EitherTMonadInstance<F, L> {
        override fun FF(): Functor<F> = MF

        override fun MF(): Monad<F> = MF
    }
}

interface EitherTMonadErrorInstance<F, L> : EitherTMonadInstance<F, L>, MonadError<EitherTKindPartial<F, L>, L> {

    override fun <A> handleErrorWith(fa: EitherTKind<F, L, A>, f: (L) -> EitherTKind<F, L, A>): EitherT<F, L, A> =
            EitherT(MF().flatMap(fa.ev().value, {
                when (it) {
                    is Left -> f(it.a).ev().value
                    is Right -> MF().pure(it)
                }
            }))

    override fun <A> raiseError(e: L): EitherT<F, L, A> = EitherT(MF().pure(Left(e)))
}

object EitherTMonadErrorInstanceImplicits {

    fun <F, L> instance(MF: Monad<F>): EitherTMonadErrorInstance<F, L> = object : EitherTMonadErrorInstance<F, L> {
        override fun FF(): Functor<F> = MF

        override fun MF(): Monad<F> = MF
    }
}

interface EitherTFoldableInstance<F, L> : Foldable<EitherTKindPartial<F, L>> {

    fun FFF(): Foldable<F>

    override fun <B, C> foldLeft(fa: HK<EitherTKindPartial<F, L>, B>, b: C, f: (C, B) -> C): C = fa.ev().foldLeft(b, f, FFF())

    override fun <B, C> foldRight(fa: HK<EitherTKindPartial<F, L>, B>, lb: Eval<C>, f: (B, Eval<C>) -> Eval<C>): Eval<C> = fa.ev().foldRight(lb, f, FFF())
}

object EitherTFoldableInstanceImplicits {

    fun <F, L> instance(FF: Foldable<F>): EitherTFoldableInstance<F, L> = object : EitherTFoldableInstance<F, L> {
        override fun FFF(): Foldable<F> = FF
    }
}

interface EitherTTraverseInstance<F, L> : EitherTFunctorInstance<F, L>, EitherTFoldableInstance<F, L>, Traverse<EitherTKindPartial<F, L>> {

    fun TF(): Traverse<F>

    override fun <A, B> map(fa: EitherTKind<F, L, A>, f: (A) -> B): EitherT<F, L, B> = fa.ev().map({ f(it) }, TF())

    override fun <G, B, C> traverse(fa: EitherTKind<F, L, B>, f: (B) -> HK<G, C>, GA: Applicative<G>): HK<G, EitherT<F, L, C>> =
            fa.ev().traverse(f, GA, TF())
}

object EitherTTraverseInstanceImplicits {

    fun <F, L> instance(TF: Traverse<F>): EitherTTraverseInstance<F, L> = object : EitherTTraverseInstance<F, L> {
        override fun FFF(): Foldable<F> = TF

        override fun FF(): Functor<F> = TF

        override fun TF(): Traverse<F> = TF
    }
}

interface EitherTSemigroupKInstance<F, L> : SemigroupK<EitherTKindPartial<F, L>> {
    fun MF(): Monad<F>

    override fun <A> combineK(x: EitherTKind<F, L, A>, y: EitherTKind<F, L, A>): EitherT<F, L, A> =
            x.ev().combineK(y, MF())
}

object EitherTSemigroupKInstanceImplicits {

    fun <F, L> instance(MF: Monad<F>): EitherTSemigroupKInstance<F, L> = object : EitherTSemigroupKInstance<F, L> {
        override fun MF(): Monad<F> = MF
    }
}

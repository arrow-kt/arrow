package kategory

interface EitherTInstances<F, L> :
        Functor<EitherTF<F, L>>,
        Applicative<EitherTF<F, L>>,
        Monad<EitherTF<F, L>>,
        MonadError<EitherTF<F, L>, L> {

    fun MF() : Monad<F>

    override fun <A> pure(a: A): EitherT<F, L, A> =
            EitherT(MF(), MF().pure(Either.Right(a)))

    override fun <A, B> map(fa: EitherTKind<F, L, A>, f: (A) -> B): EitherT<F, L, B> =
            fa.ev().map { f(it) }

    override fun <A, B> flatMap(fa: EitherTKind<F, L, A>, f: (A) -> EitherTKind<F, L, B>): EitherT<F, L, B> =
            fa.ev().flatMap { f(it).ev() }

    override fun <A, B> tailRecM(a: A, f: (A) -> HK<EitherTF<F, L>, Either<A, B>>): EitherT<F, L, B> =
            EitherT(MF(), MF().tailRecM(a, {
                MF().map(f(it).ev().value) { recursionControl ->
                    when (recursionControl) {
                        is Either.Left<L> -> Either.Right(Either.Left(recursionControl.a))
                        is Either.Right<Either<A, B>> ->
                            when (recursionControl.b) {
                                is Either.Left<A> -> Either.Left(recursionControl.b.a)
                                is Either.Right<B> -> Either.Right(Either.Right(recursionControl.b.b))
                            }
                    }
                }
            }))

    override fun <A> handleErrorWith(fa: EitherTKind<F, L, A>, f: (L) -> EitherTKind<F, L, A>): EitherT<F, L, A> =
            EitherT(MF(), MF().flatMap(fa.ev().value, {
                when (it) {
                    is Either.Left -> f(it.a).ev().value
                    is Either.Right -> MF().pure(it)
                }
            }))

    override fun <A> raiseError(e: L): EitherT<F, L, A> =
            EitherT(MF(), MF().pure(Either.Left(e)))

}

interface EitherTTraverse<F, A> :
        Foldable<EitherTF<F, A>>,
        Traverse<EitherTF<F, A>> {

    fun FF(): Traverse<F>

    fun MF(): Monad<F>

    override fun <G, B, C> traverse(fa: HK<EitherTF<F, A>, B>, f: (B) -> HK<G, C>, GA: Applicative<G>): HK<G, HK<EitherTF<F, A>, C>> =
            fa.ev().traverse(f, GA, FF(), MF())

    override fun <B, C> foldL(fa: HK<EitherTF<F, A>, B>, b: C, f: (C, B) -> C): C =
            fa.ev().foldL(b, f, FF())

    override fun <B, C> foldR(fa: HK<EitherTF<F, A>, B>, lb: Eval<C>, f: (B, Eval<C>) -> Eval<C>): Eval<C> =
            fa.ev().foldR(lb, f, FF())

}
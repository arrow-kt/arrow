package kategory

@instance(IndexedStateT::class)
interface IndexedStateTFunctorInstance<F, S> : Functor<IndexedStateTKindPartial<F, S, S>> {

    fun FF(): Functor<F>

    override fun <A, B> map(fa: IndexedStateTKind<F, S, S, A>, f: (A) -> B): IndexedStateT<F, S, S, B> =
            fa.ev().map(f, FF())

}

//TODO review since pure only works for IndexedState<F, S, S, A> There can be no applicative for IndexedState<F, SA, SB, A>
@instance(IndexedStateT::class)
interface IndexedStateTApplicativeInstance<F, S> : IndexedStateTFunctorInstance<F, S>, Applicative<IndexedStateTKindPartial<F, S, S>> {

    override fun FF(): Monad<F>

    override fun <A, B> map(fa: IndexedStateTKind<F, S, S, A>, f: (A) -> B): IndexedStateT<F, S, S, B> =
            fa.ev().map(f, FF())

    override fun <A> pure(a: A): IndexedStateT<F, S, S, A> =
            IndexedStateT.pure(FF(), a)

    override fun <A, B> ap(fa: IndexedStateTKind<F, S, S, A>, ff: IndexedStateTKind<F, S, S, (A) -> B>): IndexedStateT<F, S, S, B> =
            fa.ev().ap(ff, FF())

}

@instance(IndexedStateT::class)
interface IndexedStateTMonadInstance<F, S> : IndexedStateTFunctorInstance<F, S>, Monad<IndexedStateTKindPartial<F, S, S>> {

    override fun FF(): Monad<F>

    override fun <A> pure(a: A): IndexedStateT<F, S, S, A> = IndexedStateT.pure(FF(), a)

    override fun <A, B> tailRecM(a: A, f: (A) -> HK<IndexedStateTKindPartial<F, S, S>, Either<A, B>>): IndexedStateT<F, S, S, B> =
            IndexedStateT.tailRecM(a,f, FF())

    override fun <A, B> map(fa: IndexedStateTKind<F, S, S, A>, f: (A) -> B): IndexedStateT<F, S, S, B> =
            fa.ev().map(f, FF())

    override fun <A, B> flatMap(fa: IndexedStateTKind<F, S, S, A>, f: (A) -> IndexedStateTKind<F, S, S, B>): IndexedStateTKind<F, S, S, B> =
            fa.ev().flatMap(f, FF())

}

@instance(IndexedStateT::class)
interface IndexedStateTMonadStateInstance<F, S> : IndexedStateTMonadInstance<F, S>, MonadState<IndexedStateTKindPartial<F, S, S>, S> {

    override fun get(): IndexedStateT<F, S, S, S> = IndexedStateT.get(FF())

    override fun set(s: S): IndexedStateT<F, S, S, Unit> = IndexedStateT.set(FF(), s)

    override fun <A> pure(a: A): IndexedStateT<F, S, S, A> = IndexedStateT.pure(FF(), a)

    override fun <A, B> tailRecM(a: A, f: (A) -> HK<IndexedStateTKindPartial<F, S, S>, Either<A, B>>): IndexedStateT<F, S, S, B> =
            IndexedStateT.tailRecM(a,f, FF())
}

@instance(IndexedStateT::class)
interface IndexedStateTSemigroupKInstance<F, SA, SB> : SemigroupK<IndexedStateTKindPartial<F, SA, SB>> {

    fun MF(): Monad<F>

    fun SS(): SemigroupK<F>

    override fun <A> combineK(x: IndexedStateTKind<F, SA, SB, A>, y: IndexedStateTKind<F, SA, SB, A>): IndexedStateT<F, SA, SB, A> =
            x.ev().combineK(y, MF(), SS())

}

@instance(IndexedStateT::class)
interface IndexedStateTMonadCombineInstance<F, S> : MonadCombine<IndexedStateTKindPartial<F, S, S>>, IndexedStateTMonadInstance<F, S>, IndexedStateTSemigroupKInstance<F, S, S> {

    fun MC(): MonadCombine<F>

    override fun FF(): Monad<F> = MC()

    override fun SS(): SemigroupK<F> = MC()

    override fun <A> empty(): IndexedStateTKind<F, S, S, A> = liftT(MC().empty())

    override fun <A> pure(a: A): IndexedStateT<F, S, S, A> = IndexedStateT.pure(FF(), a)

    override fun <A, B> tailRecM(a: A, f: (A) -> HK<IndexedStateTKindPartial<F, S, S>, Either<A, B>>): IndexedStateT<F, S, S, B> =
            IndexedStateT.tailRecM(a,f, FF())

    fun <A> liftT(ma: HK<F, A>): IndexedStateTKind<F, S, S, A> = IndexedStateT(FF().pure({ s: S -> FF().map(ma) { s toT it } }))
}

@instance(IndexedStateT::class)
interface IndexedStateTMonadErrorInstance<F, S, E> : IndexedStateTMonadInstance<F, S>, MonadError<IndexedStateTKindPartial<F, S, S>, E> {

    override fun FF(): MonadError<F, E>

    override fun <A> pure(a: A): IndexedStateT<F, S, S, A> = IndexedStateT.pure(FF(), a)

    override fun <A, B> tailRecM(a: A, f: (A) -> HK<IndexedStateTKindPartial<F, S, S>, Either<A, B>>): IndexedStateT<F, S, S, B> =
            IndexedStateT.tailRecM(a,f, FF())

    override fun <A> raiseError(e: E): IndexedStateTKind<F, S, S, A> = IndexedStateT.lift(FF(), FF().raiseError(e))

    override fun <A> handleErrorWith(fa: IndexedStateTKind<F, S, S, A>, f: (E) -> IndexedStateTKind<F, S, S, A>): IndexedStateT<F, S, S, A> =
            IndexedStateT(FF().pure({ s: S -> FF().handleErrorWith(fa.runM(FF(), s), { e -> f(e).runM(FF(), s) }) }))
}
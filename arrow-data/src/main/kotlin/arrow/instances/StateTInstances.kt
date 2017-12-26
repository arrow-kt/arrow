package arrow

@instance(StateT::class)
interface StateTFunctorInstance<F, S> : Functor<StateTKindPartial<F, S>> {

    fun FF(): Functor<F>

    override fun <A, B> map(fa: StateTKind<F, S, A>, f: (A) -> B): StateT<F, S, B> = fa.ev().map(f, FF())

}

@instance(StateT::class)
interface StateTApplicativeInstance<F, S> : StateTFunctorInstance<F, S>, Applicative<StateTKindPartial<F, S>> {

    override fun FF(): Monad<F>

    override fun <A, B> map(fa: StateTKind<F, S, A>, f: (A) -> B): StateT<F, S, B> = fa.ev().map(f, FF())

    override fun <A> pure(a: A): StateT<F, S, A> = StateT(FF().pure({ s: S -> FF().pure(Tuple2(s, a)) }))

    override fun <A, B> ap(fa: StateTKind<F, S, A>, ff: StateTKind<F, S, (A) -> B>): StateT<F, S, B> =
            fa.ev().ap(ff, FF())

    override fun <A, B> product(fa: StateTKind<F, S, A>, fb: StateTKind<F, S, B>): StateT<F, S, Tuple2<A, B>> =
            fa.ev().product(fb.ev(), FF())

}

@instance(StateT::class)
interface StateTMonadInstance<F, S> : StateTApplicativeInstance<F, S>, Monad<StateTKindPartial<F, S>> {

    override fun <A, B> flatMap(fa: StateTKind<F, S, A>, f: (A) -> StateTKind<F, S, B>): StateT<F, S, B> =
            fa.ev().flatMap(f, FF())

    override fun <A, B> tailRecM(a: A, f: (A) -> StateTKind<F, S, Either<A, B>>): StateT<F, S, B> =
            StateT.tailRecM(a, f, FF())

    override fun <A, B> ap(fa: StateTKind<F, S, A>, ff: StateTKind<F, S, (A) -> B>): StateT<F, S, B> =
            ff.ev().map2(fa.ev(), { f, a -> f(a) }, FF())

}

@instance(StateT::class)
interface StateTMonadStateInstance<F, S> : StateTMonadInstance<F, S>, MonadState<StateTKindPartial<F, S>, S> {

    override fun get(): StateT<F, S, S> = StateT.get(FF())

    override fun set(s: S): StateT<F, S, Unit> = StateT.set(FF(), s)

}

@instance(StateT::class)
interface StateTSemigroupKInstance<F, S> : SemigroupK<StateTKindPartial<F, S>> {

    fun FF(): Monad<F>

    fun SS(): SemigroupK<F>

    override fun <A> combineK(x: StateTKind<F, S, A>, y: StateTKind<F, S, A>): StateT<F, S, A> =
            x.ev().combineK(y, FF(), SS())

}

@instance(StateT::class)
interface StateTMonadCombineInstance<F, S> : MonadCombine<StateTKindPartial<F, S>>, StateTMonadInstance<F, S>, StateTSemigroupKInstance<F, S> {

    fun MC(): MonadCombine<F>

    override fun FF(): Monad<F> = MC()

    override fun SS(): SemigroupK<F> = MC()

    override fun <A> empty(): HK<StateTKindPartial<F, S>, A> = liftT(MC().empty())

    fun <A> liftT(ma: HK<F, A>): StateT<F, S, A> = StateT(FF().pure({ s: S -> FF().map(ma, { a: A -> s toT a }) }))
}

@instance(StateT::class)
interface StateTMonadErrorInstance<F, S, E> : StateTMonadInstance<F, S>, MonadError<StateTKindPartial<F, S>, E> {
    override fun FF(): MonadError<F, E>

    override fun <A> raiseError(e: E): HK<StateTKindPartial<F, S>, A> = StateT.lift(FF(), FF().raiseError(e))

    override fun <A> handleErrorWith(fa: HK<StateTKindPartial<F, S>, A>, f: (E) -> HK<StateTKindPartial<F, S>, A>): StateT<F, S, A> =
            StateT(FF().pure({ s -> FF().handleErrorWith(fa.runM(FF(), s), { e -> f(e).runM(FF(), s) }) }))
}


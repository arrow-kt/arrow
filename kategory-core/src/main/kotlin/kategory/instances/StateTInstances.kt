package kategory

interface StateTFunctorInstance<F, S> : Functor<StateTKindPartial<F, S>> {

    fun FF(): Functor<F>

    override fun <A, B> map(fa: StateTKind<F, S, A>, f: (A) -> B): StateT<F, S, B> = fa.ev().map(f, FF())

}

object StateTFunctorInstanceImplicits {
    @JvmStatic
    fun <F, S> instance(FF: Functor<F>): StateTFunctorInstance<F, S> = object : StateTFunctorInstance<F, S> {
        override fun FF(): Functor<F> = FF
    }
}

interface StateTApplicativeInstance<F, S> : StateTFunctorInstance<F, S>, Applicative<StateTKindPartial<F, S>> {

    fun MF(): Monad<F>

    override fun <A, B> map(fa: StateTKind<F, S, A>, f: (A) -> B): StateT<F, S, B> = fa.ev().map(f, MF())

    override fun <A> pure(a: A): StateT<F, S, A> = StateT(MF().pure({ s: S -> MF().pure(Tuple2(s, a)) }))

    override fun <A, B> ap(fa: StateTKind<F, S, A>, ff: StateTKind<F, S, (A) -> B>): StateT<F, S, B> =
            fa.ev().ap(ff, MF())

    override fun <A, B> product(fa: StateTKind<F, S, A>, fb: StateTKind<F, S, B>): StateT<F, S, Tuple2<A, B>> =
            fa.ev().product(fb.ev(), MF())

}

object StateTApplicativeInstanceImplicits {
    @JvmStatic
    fun <F, S> instance(MF: Monad<F>): StateTApplicativeInstance<F, S> = object : StateTApplicativeInstance<F, S> {
        override fun FF(): Functor<F> = MF
        override fun MF(): Monad<F> = MF
    }
}

interface StateTMonadInstance<F, S> : StateTApplicativeInstance<F, S>, Monad<StateTKindPartial<F, S>> {

    override fun <A, B> flatMap(fa: StateTKind<F, S, A>, f: (A) -> StateTKind<F, S, B>): StateT<F, S, B> =
            fa.ev().flatMap(f, MF())

    override fun <A, B> tailRecM(a: A, f: (A) -> StateTKind<F, S, Either<A, B>>): StateT<F, S, B> =
            StateT.tailRecM(a, f, MF())

    override fun <A, B> ap(fa: StateTKind<F, S, A>, ff: StateTKind<F, S, (A) -> B>): StateT<F, S, B> =
            ff.ev().map2(fa.ev(), { f, a -> f(a) }, MF())

}

object StateTMonadInstanceImplicits {
    @JvmStatic
    fun <F, S> instance(MF: Monad<F>): StateTMonadInstance<F, S> = object : StateTMonadInstance<F, S> {
        override fun FF(): Functor<F> = MF
        override fun MF(): Monad<F> = MF
    }
}

interface StateTMonadStateInstance<F, S> : StateTMonadInstance<F, S>, MonadState<StateTKindPartial<F, S>, S> {

    override fun get(): StateT<F, S, S> = StateT.get(MF())

    override fun set(s: S): StateT<F, S, Unit> = StateT.set(s, MF())

}

object StateTMonadStateInstanceImplicits {
    @JvmStatic
    fun <F, S> instance(MF: Monad<F>): StateTMonadStateInstance<F, S> = object : StateTMonadStateInstance<F, S> {
        override fun FF(): Functor<F> = MF
        override fun MF(): Monad<F> = MF
    }
}

interface StateTSemigroupKInstance<F, S> : SemigroupK<StateTKindPartial<F, S>> {

    fun MF(): Monad<F>

    fun SF(): SemigroupK<F>

    override fun <A> combineK(x: StateTKind<F, S, A>, y: StateTKind<F, S, A>): StateT<F, S, A> =
            x.ev().combineK(y, MF(), SF())

}

object StateTSemigroupKInstanceImplicits {
    @JvmStatic
    fun <F, S> instance(MF: Monad<F>, SF: SemigroupK<F>): StateTSemigroupKInstance<F, S> = object : StateTSemigroupKInstance<F, S> {
        override fun MF(): Monad<F> = MF
        override fun SF(): SemigroupK<F> = SF
    }
}


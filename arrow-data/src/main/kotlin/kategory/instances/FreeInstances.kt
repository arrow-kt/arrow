package arrow

@instance(Free::class)
interface FreeFunctorInstance<S> : Functor<FreeKindPartial<S>> {
    override fun <A, B> map(fa: FreeKind<S, A>, f: (A) -> B): Free<S, B> = fa.ev().map(f)
}

@instance(Free::class)
interface FreeApplicativeInstance<S> : FreeFunctorInstance<S>, Applicative<FreeKindPartial<S>> {
    override fun <A> pure(a: A): Free<S, A> = Free.pure(a)

    override fun <A, B> map(fa: FreeKind<S, A>, f: (A) -> B): Free<S, B> = fa.ev().map(f)

    override fun <A, B> ap(fa: FreeKind<S, A>, ff: FreeKind<S, (A) -> B>): Free<S, B> =
            fa.ev().ap(ff.ev())

}

@instance(Free::class)
interface FreeMonadInstance<S> : FreeApplicativeInstance<S>, Monad<FreeKindPartial<S>> {

    override fun <A, B> map(fa: FreeKind<S, A>, f: (A) -> B): Free<S, B> = fa.ev().map(f)

    override fun <A, B> ap(fa: FreeKind<S, A>, ff: FreeKind<S, (A) -> B>): Free<S, B> =
            fa.ev().ap(ff.ev())

    override fun <A, B> flatMap(fa: FreeKind<S, A>, f: (A) -> FreeKind<S, B>): Free<S, B> = fa.ev().flatMap { f(it).ev() }

    override fun <A, B> tailRecM(a: A, f: (A) -> FreeKind<S, Either<A, B>>): Free<S, B> = f(a).ev().flatMap {
        when (it) {
            is Left -> tailRecM(it.a, f)
            is Right -> pure(it.b)
        }
    }

}

data class FreeEq<F, G, A>(private val interpreter: FunctionK<F, G>, private val MG: Monad<G>) : Eq<HK<FreeKindPartial<F>, A>> {
    override fun eqv(a: HK<FreeKindPartial<F>, A>, b: HK<FreeKindPartial<F>, A>): Boolean = a.ev().foldMap(interpreter, MG) == b.ev().foldMap(interpreter, MG)

    companion object {
        inline operator fun <F, reified G, A> invoke(interpreter: FunctionK<F, G>, MG: Monad<G> = monad(), dummy: Unit = Unit): FreeEq<F, G, A> =
                FreeEq(interpreter, MG)
    }
}

package kategory

interface FreeApplicativeInstances<S> :
        Functor<FreeApplicativeF<S>>,
        Applicative<FreeApplicativeF<S>> {

    override fun <A> pure(a: A): FreeApplicative<S, A> = FreeApplicative.pure(a)

    override fun <A, B> map(fa: FreeApplicativeKind<S, A>, f: (A) -> B): FreeApplicative<S, B> = fa.ev().map(f)

    override fun <A, B> ap(fa: HK<FreeApplicativeF<S>, A>, ff: HK<FreeApplicativeF<S>, (A) -> B>): FreeApplicative<S, B> = fa.ev().ap(ff.ev())
}

data class FreeApplicativeEq<in F, in G, in A>(private val interpreter: FunctionK<F, G>, private val MG: Monad<G>) : Eq<HK<FreeApplicativeF<F>, A>> {
    override fun eqv(a: HK<FreeApplicativeF<F>, A>, b: HK<FreeApplicativeF<F>, A>): Boolean = a.ev().foldMap(interpreter, MG) == b.ev().foldMap(interpreter, MG)

    companion object {
        inline operator fun <F, reified G, A> invoke(interpreter: FunctionK<F, G>, MG: Monad<G> = monad(), dummy: Unit = Unit): FreeApplicativeEq<F, G, A> = FreeApplicativeEq(interpreter, MG)
    }
}
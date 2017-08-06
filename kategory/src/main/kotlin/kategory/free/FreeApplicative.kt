package kategory

typealias FreeApplicativeKind<S, A> = HK2<FreeApplicative.F, S, A>
typealias FreeApplicativeF<S> = HK<FreeApplicative.F, S>

fun <S, A> FreeApplicativeKind<S, A>.ev(): FreeApplicative<S, A> =
        this as FreeApplicative<S, A>

fun <G, S, A> FreeApplicativeKind<S, A>.foldMapK(f: FunctionK<S, G>, GA: Applicative<G>): HK<G, A> =
        (this as FreeApplicative<S, A>).foldMap(f, GA)

/**
 * See [https://github.com/edmundnoble/cats/blob/6454b4f8b7c5cefd15d8198fa7d52e46e2f45fea/docs/src/main/tut/datatypes/freeapplicative.md]
 */
sealed class FreeApplicative<F, out A> : FreeApplicativeKind<F, A> {

    class F private constructor()

    companion object {
        fun <F, A> pure(a: A): FreeApplicative<F, A> =
                Pure(a)

        fun <F, P, A> ap(fp: FreeApplicative<F, P>, fn: FreeApplicative<F, (P) -> A>): FreeApplicative<F, A> =
                Ap(fn, fp)

        fun <F, A> lift(fa: HK<F, A>): FreeApplicative<F, A> =
                Lift(fa)

        fun <S> functor(): FreeApplicativeInstances<S> = object : FreeApplicativeInstances<S> {}

        fun <S> applicative(): FreeApplicativeInstances<S> = object : FreeApplicativeInstances<S> {}
    }

    fun <B> ap(ap: FreeApplicative<F, (A) -> B>): FreeApplicative<F, B> =
            when (ap) {
                is Pure -> map(ap.value)
                else -> Ap(ap, this)
            }

    fun <C> map(f: (A) -> C): FreeApplicative<F, C> =
            when (this) {
                is Pure -> Pure(f(value))
                else -> Ap(Pure(f), this)
            }

    fun fold(FA: Applicative<F>): HK<F, A> =
            foldMap(FunctionK.id(), FA)

    fun <G> compile(f: FunctionK<F, G>): FreeApplicative<G, A> =
            foldMap(object : FunctionK<F, FreeApplicativeF<G>> {
                override fun <A> invoke(fa: HK<F, A>): FreeApplicative<G, A> =
                        FreeApplicative.lift(f(fa)).ev()

            }, object : Applicative<FreeApplicativeF<G>> {
                override fun <A> pure(a: A): FreeApplicative<G, A> =
                        FreeApplicative.pure(a)

                override fun <A, B> ap(fa: HK<FreeApplicativeF<G>, A>, ff: HK<FreeApplicativeF<G>, (A) -> B>): FreeApplicative<G, B> =
                        FreeApplicative.ap(fa.ev(), ff.ev())
            }).ev()

    fun <G> flatCompile(f: FunctionK<F, FreeApplicativeF<G>>, GFA: Applicative<FreeApplicativeF<G>>): FreeApplicative<G, A> =
            foldMap(f, GFA).ev()

    // TODO(paco): requires Const
    // final def analyze[M: Monoid](f: FunctionK[F, λ[α => M]]): M

    fun monad(): Free<F, A> =
            foldMap(object : FunctionK<F, FreeF<F>> {
                override fun <A> invoke(fa: HK<F, A>): Free<F, A> =
                        Free.liftF(fa)

            }, object : Applicative<FreeF<F>> {
                override fun <A> pure(a: A): Free<F, A> =
                        Free.pure(a)

                override fun <A, B> ap(fa: HK<FreeF<F>, A>, ff: HK<FreeF<F>, (A) -> B>): Free<F, B> =
                        Free.applicative<F>().ap(fa, ff).ev()
            }).ev()

    // Beware: smart code
    fun <G> foldMap(f: FunctionK<F, G>, GA: Applicative<G>): HK<G, A> =
            TODO()

    /** Represents a curried function `F<(A) -> (B) -> (C) -> ...>`
     * that has been constructed with chained `ap` calls.
     * [CurriedFunction.remaining] denotes the amount of curried params remaining.
     */
    private data class CurriedFunction<out G, in A, out B>(val gab: HK<G, (A) -> B>, val remaining: Int)

    private data class Pure<S, out A>(val value: A) : FreeApplicative<S, A>()

    private data class Lift<S, out A>(val fa: HK<S, A>) : FreeApplicative<S, A>()

    private data class Ap<S, P, out A>(val fn: FreeApplicative<S, (P) -> A>, val fp: FreeApplicative<S, P>) : FreeApplicative<S, A>()

    override fun toString(): String {
        return "FreeApplicative(...)"
    }
}

fun <S, A> A.freeAp(): FreeApplicative<S, A> =
        FreeApplicative.pure(this)
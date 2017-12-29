package arrow

import arrow.free.instances.*

inline fun <F, reified G, A> FreeApplicativeKind<F, A>.foldMapK(f: FunctionK<F, G>, GA: Applicative<G> = applicative<G>()): HK<G, A> =
        (this as FreeApplicative<F, A>).foldMap(f, GA)

inline fun <reified F, A> FreeApplicativeKind<F, A>.foldK(FA: Applicative<F> = applicative<F>()): HK<F, A> = (this as FreeApplicative<F, A>).fold(FA)

/**
 * See [https://github.com/edmundnoble/cats/blob/6454b4f8b7c5cefd15d8198fa7d52e46e2f45fea/docs/src/main/tut/datatypes/freeapplicative.md]
 */
@higherkind sealed class FreeApplicative<F, out A> : FreeApplicativeKind<F, A> {

    companion object {
        fun <F, A> pure(a: A): FreeApplicative<F, A> = Pure(a)

        fun <F, P, A> ap(fp: FreeApplicative<F, P>, fn: FreeApplicative<F, (P) -> A>): FreeApplicative<F, A> = Ap(fn, fp)

        fun <F, A> liftF(fa: HK<F, A>): FreeApplicative<F, A> = Lift(fa)

        internal fun <F, G> functionKF(f: FunctionK<F, G>): FunctionK<F, FreeApplicativeKindPartial<G>> =
                object : FunctionK<F, FreeApplicativeKindPartial<G>> {
                    override fun <A> invoke(fa: HK<F, A>): FreeApplicative<G, A> =
                            FreeApplicative.liftF(f(fa))

                }

        internal fun <F> applicativeF(): Applicative<FreeApplicativeKindPartial<F>> = object : Applicative<FreeApplicativeKindPartial<F>> {
            override fun <A> pure(a: A): FreeApplicative<F, A> =
                    FreeApplicative.pure(a)

            override fun <A, B> ap(fa: HK<FreeApplicativeKindPartial<F>, A>, ff: HK<FreeApplicativeKindPartial<F>, (A) -> B>): FreeApplicative<F, B> =
                    FreeApplicative.ap(fa.ev(), ff.ev())
        }
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

    fun fold(FA: Applicative<F>): HK<F, A> = foldMap(FunctionK.id(), FA)

    fun <G> compile(f: FunctionK<F, G>): FreeApplicative<G, A> = foldMap(functionKF(f), applicativeF()).ev()

    fun <G> flatCompile(f: FunctionK<F, FreeApplicativeKindPartial<G>>, GFA: Applicative<FreeApplicativeKindPartial<G>>): FreeApplicative<G, A> =
            foldMap(f, GFA).ev()

    inline fun <reified M> analyze(f: FunctionK<F, ConstKindPartial<M>>, MM: Monoid<M>): M =
            foldMap(object : FunctionK<F, ConstKindPartial<M>> {
                override fun <A> invoke(fa: HK<F, A>): Const<M, A> = f(fa).ev()
            }, Const.applicative(MM)).value()

    fun monad(): Free<F, A> = foldMap(Free.functionKF(), Free.applicativeF()).ev()

    // Beware: smart code
    @Suppress("UNCHECKED_CAST")
    fun <G> foldMap(f: FunctionK<F, G>, GA: Applicative<G>): HK<G, A> {
        var argsF: List<FreeApplicative<F, Any?>> = mutableListOf(this)
        var argsFLength: Int = 1

        var fns: List<CurriedFunction<G, Any?, Any?>> = mutableListOf()
        var fnsLength: Int = 0

        tailrec fun loop(): HK<G, Any?> {
            var argF: FreeApplicative<F, Any?> = argsF.first()
            argsF = argsF.drop(1)
            argsFLength -= 1

            return if (argF is Ap<F, *, *>) {
                val lengthInitial = argsFLength

                do {
                    val ap = argF as Ap<F, Any?, Any?>
                    argsF = listOf(ap.fp) + argsF
                    argsFLength += 1
                    argF = ap.fn
                } while (argF is Ap<F, *, *>)

                val argc = argsFLength - lengthInitial
                fns = listOf(CurriedFunction(foldArg(argF as FreeApplicative<F, (Any?) -> Any?>, f, GA), argc)) + fns
                fnsLength += 1

                loop()
            } else {
                val argT: HK<G, Any?> = foldArg(argF, f, GA)

                if (fns.isNotEmpty()) {

                    var fn = fns.first()
                    fns = fns.drop(1)
                    fnsLength -= 1

                    var res = GA.ap(argT, fn.gab)

                    if (fn.remaining > 1) {
                        fns = listOf(CurriedFunction(res as HK<G, (Any?) -> Any?>, fn.remaining - 1)) + fns
                        fnsLength += 1
                        loop()

                    } else {
                        if (fnsLength > 0) {

                            tailrec fun innerLoop(): Unit {
                                fn = fns.first()
                                fns = fns.drop(1)
                                fnsLength -= 1
                                res = GA.ap(res, fn.gab)

                                if (fn.remaining > 1) {
                                    fns = listOf(CurriedFunction(res as HK<G, (Any?) -> Any?>, fn.remaining - 1)) + fns
                                    fnsLength += 1
                                }

                                if (fn.remaining == 1 && fnsLength > 0) {
                                    innerLoop()
                                }
                            }

                            innerLoop()
                        }

                        if (fnsLength == 0) {
                            res
                        } else {
                            loop()
                        }
                    }
                } else {
                    argT
                }
            }
        }

        return loop() as HK<G, A>
    }

    internal data class CurriedFunction<out G, in A, out B>(val gab: HK<G, (A) -> B>, val remaining: Int)

    internal data class Pure<S, out A>(val value: A) : FreeApplicative<S, A>()

    internal data class Lift<S, out A>(val fa: HK<S, A>) : FreeApplicative<S, A>()

    internal data class Ap<S, P, out A>(val fn: FreeApplicative<S, (P) -> A>, val fp: FreeApplicative<S, P>) : FreeApplicative<S, A>()

    override fun toString(): String = "FreeApplicative(...)"
}

private fun <F, G, A> foldArg(node: FreeApplicative<F, A>, f: FunctionK<F, G>, GA: Applicative<G>): HK<G, A> =
        when (node) {
            is FreeApplicative.Pure<F, A> -> GA.pure(node.value)
            else -> {
                val lift = node as FreeApplicative.Lift<F, A>
                f(lift.fa)
            }
        }

fun <S, A> A.freeAp(): FreeApplicative<S, A> = FreeApplicative.pure(this)

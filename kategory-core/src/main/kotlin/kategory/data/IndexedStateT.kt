package kategory

typealias IndexedStateTFun<F, SA, SB, A> = (SA) -> HK<F, Tuple2<SB, A>>
typealias IndexedStateTFunKind<F, SA, SB, A> = HK<F, IndexedStateTFun<F, SA, SB, A>>

inline fun <reified F, SA, SB, A> IndexedStateTKind<F, SA, SB, A>.runM(initial: SA, MF: Monad<F> = monad()): HK<F, Tuple2<SB, A>> = (this as IndexedStateT<F, SA, SB, A>).run(initial, MF)

fun <F, SA, SB, A> IndexedStateTKind<F, SA, SB, A>.runM(MF: Monad<F>, initial: SA): HK<F, Tuple2<SB, A>> = (this as IndexedStateT<F, SA, SB, A>).run(initial, MF)

@higherkind
class IndexedStateT<F, SA, SB, A>(
        val runF: HK<F, (SA) -> HK<F, Tuple2<SB, A>>>
) : IndexedStateTKind<F, SA, SB, A> {

    companion object {
        inline operator fun <reified F, SA, SB, A> invoke(noinline run: IndexedStateTFun<F, SA, SB, A>, MF: Applicative<F> = applicative<F>()): IndexedStateT<F, SA, SB, A> = IndexedStateT(MF.pure(run))

        operator fun <F, SA, SB, A> invoke(AF: Applicative<F>, run: IndexedStateTFun<F, SA, SB, A>): IndexedStateT<F, SA, SB, A> = IndexedStateT(AF.pure(run))

        fun <F, SA, SB, A> invokeF(runF: IndexedStateTFunKind<F, SA, SB, A>): IndexedStateT<F, SA, SB, A> = IndexedStateT(runF)

        fun <F, S, A> lift(MF: Applicative<F>, fa: HK<F, A>): IndexedStateT<F, S, S, A> = IndexedStateT(MF.pure({ s -> MF.map(fa, { a -> Tuple2(s, a) }) }))

        fun <F, S> get(AF: Applicative<F>): IndexedStateT<F, S, S, S> = IndexedStateT(AF.pure({ s -> AF.pure(Tuple2(s, s)) }))

        fun <F, S, T> gets(AF: Applicative<F>, f: (S) -> T): IndexedStateT<F, S, S, T> = IndexedStateT(AF.pure({ s -> AF.pure(Tuple2(s, f(s))) }))

        fun <F, S> modify(AF: Applicative<F>, f: (S) -> S): IndexedStateT<F, S, S, Unit> = IndexedStateT(AF.pure({ s -> AF.map(AF.pure(f(s))) { it toT Unit } }))

        fun <F, S> modifyF(AF: Applicative<F>, f: (S) -> HK<F, S>): IndexedStateT<F, S, S, Unit> = IndexedStateT(AF.pure({ s -> AF.map(f(s)) { it toT Unit } }))

        fun <F, S> set(AF: Applicative<F>, s: S): IndexedStateT<F, S, S, Unit> = IndexedStateT(AF.pure({ _ -> AF.pure(Tuple2(s, Unit)) }))

        fun <F, S> setF(AF: Applicative<F>, s: HK<F, S>): IndexedStateT<F, S, S, Unit> = IndexedStateT(AF.pure({ _ -> AF.map(s) { Tuple2(it, Unit) } }))

        fun <F, S, A> pure(AF: Applicative<F>, a: A): IndexedStateT<F, S, S, A> = IndexedStateT(AF.pure({ s: S -> AF.pure(Tuple2(s, a)) }))

        fun <F, S, A, B> tailRecM(a: A, f: (A) -> HK<IndexedStateTKindPartial<F, S, S>, Either<A, B>>, MF: Monad<F>): IndexedStateT<F, S, S, B> =
                IndexedStateT(MF, run = { s ->
                    MF.tailRecM(Tuple2(s, a)) { (s, a) ->
                        MF.map(f(a).ev().run(s, MF)) { (s, ab) ->
                            ab.bimap({ s toT it }, { s toT it })
                        }
                    }
                })

    }

    fun <B> map(f: (A) -> B, FF: Functor<F>): IndexedStateT<F, SA, SB, B> = transform(FF, { (s, a) -> Tuple2(s, f(a)) })

    fun <B, SC, Z> map2(sb: IndexedStateTKind<F, SB, SC, B>, fn: (A, B) -> Z, MF: Monad<F>): IndexedStateT<F, SA, SC, Z> =
            invokeF(MF.map2(runF, sb.ev().runF) { (ssa, ssb) ->
                ssa.andThen { fsa ->
                    MF.flatMap(fsa) { (s, a) ->
                        MF.map(ssb(s)) { (s, b) -> Tuple2(s, fn(a, b)) }
                    }
                }
            })

    fun <B, SC, Z> map2Eval(sb: Eval<IndexedStateTKind<F, SB, SC, B>>, fn: (A, B) -> Z, MF: Monad<F>): Eval<IndexedStateT<F, SA, SC, Z>> =
            MF.map2Eval(runF, sb.map { it.ev().runF }) { (ssa, ssb) ->
                ssa.andThen { fsa ->
                    MF.flatMap(fsa) { (s, a) ->
                        MF.map(ssb((s))) { (s, b) -> Tuple2(s, fn(a, b)) }
                    }
                }
            }.map { IndexedStateT.invokeF(it) }

    fun <B, SC> ap(ff: IndexedStateTKind<F, SB, SC, (A) -> B>, MF: Monad<F>): IndexedStateT<F, SA, SC, B> =
            map2(ff, { a, f -> f(a) }, MF)

    fun <B, SC> product(sb: IndexedStateTKind<F, SB, SC, B>, MF: Monad<F>): IndexedStateT<F, SA, SC, Tuple2<A, B>> = map2(sb.ev(), { a, b -> Tuple2(a, b) }, MF)

    fun <B, SC> flatMap(fas: (A) -> IndexedStateTKind<F, SB, SC, B>, MF: Monad<F>): IndexedStateT<F, SA, SC, B> =
            invokeF(
                    MF.map(runF) { safsba ->
                        safsba.andThen { fsba ->
                            MF.flatMap(fsba) {
                                fas(it.b).runM(MF, it.a)
                            }
                        }
                    })

    fun <B> flatMapF(faf: (A) -> HK<F, B>, MF: Monad<F>): IndexedStateT<F, SA, SB, B> =
            invokeF(MF.map(runF) { sfsa ->
                sfsa.andThen { fsa ->
                    MF.flatMap(fsa) { (s, a) ->
                        MF.map(faf(a)) {
                            s toT it
                        }
                    }
                }
            })

    fun <B, SC> bimap(FF: Functor<F>, f: (SB) -> SC, g: (A) -> B): IndexedStateT<F, SA, SC, B> =
            transform(FF, { (sb, a) -> f(sb) toT g(a) })

    /**
     * Like [map], but also allows the state [SB] value to be modified.
     */
    fun <B, SC> transform(FF: Functor<F>, f: (Tuple2<SB, A>) -> Tuple2<SC, B>): IndexedStateT<F, SA, SC, B> =
            invokeF(FF.map(runF) { sfsa ->
                sfsa.andThen { fsa ->
                    FF.map(fsa) { (s, a) -> f(s toT a) }
                }
            })

    /**
     * Like [transform], but allows the context to change from [F] to [G].
     */
    fun <G, B, SC> transformF(MF: Monad<F>, AG: Applicative<G>, f: (HK<F, Tuple2<SB, A>>) -> HK<G, Tuple2<SC, B>>): IndexedStateT<G, SA, SC, B> = IndexedStateT(AG, run = { s ->
        f(this@IndexedStateT.run(s, MF))
    })

    /**
     * Transform the state used.
     */
    fun <R> transformS(FF: Functor<F>, f: (R) -> SA, g: (Tuple2<R, SB>) -> R): IndexedStateT<F, R, R, A> = IndexedStateT.invokeF(FF.map(runF) { sfsa ->
        { r: R ->
            val sa = f(r)
            val fsba = sfsa(sa)
            FF.map(fsba) { (sb, a) ->
                g(Tuple2(r, sb)) toT a
            }
        }
    })

    /**
     * Modify the state [SB] component.
     */
    fun <SC> modify(FF: Functor<F>, f: (SB) -> SC): IndexedStateT<F, SA, SC, A> = transform(FF) { (sb, a) ->
        Tuple2(f(sb), a)
    }

    /**
     * Inspect a value from the input state, without modifying the state.
     */
    fun <B> inspect(FF: Functor<F>, f: (SB) -> B): IndexedStateT<F, SA, SB, B> = transform(FF) { (sb, _) ->
        Tuple2(sb, f(sb))
    }

    /**
     * Get the input state, without modifying the state.
     */
    fun get(FF: Functor<F>): IndexedStateT<F, SA, SB, SB> = inspect(FF, ::identity)

    fun combineK(y: IndexedStateTKind<F, SA, SB, A>, MF: Monad<F>, SF: SemigroupK<F>): IndexedStateT<F, SA, SB, A> =
            IndexedStateT(MF.pure({ s -> SF.combineK(run(s, MF), y.ev().run(s, MF)) }))

    fun run(initial: SA, MF: Monad<F>): HK<F, Tuple2<SB, A>> = MF.flatMap(runF) { f -> f(initial) }

    fun runA(s: SA, MF: Monad<F>): HK<F, A> = MF.map(run(s, MF)) { it.b }

    fun runS(s: SA, MF: Monad<F>): HK<F, SB> = MF.map(run(s, MF)) { it.a }

}

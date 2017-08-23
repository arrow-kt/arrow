package kategory

typealias StateTFun<F, S, A> = (S) -> HK<F, Tuple2<S, A>>
typealias StateTFunKind<F, S, A> = HK<F, StateTFun<F, S, A>>

fun <F, S, A> StateTKind<F, S, A>.runM(initial: S): HK<F, Tuple2<S, A>> = (this as StateT<F, S, A>).run(initial)

@higherkind class StateT<F, S, A>(
        val MF: Monad<F>,
        val runF: StateTFunKind<F, S, A>
) : StateTKind<F, S, A> {

    companion object {
        inline operator fun <reified F, S, A> invoke(noinline run: StateTFun<F, S, A>, MF: Monad<F> = monad<F>()): StateT<F, S, A> = StateT(MF, MF.pure(run))

        fun <F, S, A> invokeF(runF: StateTFunKind<F, S, A>, MF: Monad<F>): StateT<F, S, A> = StateT(MF, runF)

        inline fun <reified F, S> instances(MF: Monad<F> = monad<F>()): StateTInstances<F, S> = object : StateTInstances<F, S> {
            override fun MF(): Monad<F> = MF
        }

        inline fun <reified F, S> functor(MF: Monad<F> = monad<F>()): StateTInstances<F, S> = instances(MF)

        inline fun <reified F, S> applicative(MF: Monad<F> = monad<F>()): StateTInstances<F, S> = instances(MF)

        inline fun <reified F, S> monad(MF: Monad<F> = monad<F>()): StateTInstances<F, S> = instances(MF)

        inline fun <reified F, reified S> monadState(MF: Monad<F> = monad<F>()): StateTInstances<F, S> = instances(MF)

        inline fun <reified F, S> semigroupK(F0: Monad<F> = monad<F>(), G0: SemigroupK<F>): SemigroupK<StateTKindPartial<F, S>> =
                object : StateTSemigroupK<F, S> {
                    override fun F(): Monad<F> = F0
                    override fun G(): SemigroupK<F> = G0
                }
    }

    fun <B> map(f: (A) -> B): StateT<F, S, B> = transform { (s, a) -> Tuple2(s, f(a)) }

    fun <B, Z> map2(sb: StateT<F, S, B>, fn: (A, B) -> Z): StateT<F, S, Z> =
            invokeF(MF.map2(runF, sb.runF) { (ssa, ssb) ->
                ssa.andThen { fsa ->
                    MF.flatMap(fsa) { (s, a) ->
                        MF.map(ssb(s)) { (s, b) -> Tuple2(s, fn(a, b)) }
                    }
                }
            }, MF)

    fun <B, Z> map2Eval(sb: Eval<StateT<F, S, B>>, fn: (A, B) -> Z): Eval<StateT<F, S, Z>> =
            MF.map2Eval(runF, sb.map { it.runF }) { (ssa, ssb) ->
                ssa.andThen { fsa ->
                    MF.flatMap(fsa) { (s, a) ->
                        MF.map(ssb((s))) { (s, b) -> Tuple2(s, fn(a, b)) }
                    }
                }
            }.map { invokeF(it, MF) }

    fun <B> product(sb: StateT<F, S, B>): StateT<F, S, Tuple2<A, B>> = map2(sb) { a, b -> Tuple2(a, b) }

    fun <B> flatMap(fas: (A) -> StateTKind<F, S, B>): StateT<F, S, B> =
            invokeF(
                    MF.map(runF) { sfsa ->
                        sfsa.andThen { fsa ->
                            MF.flatMap(fsa) {
                                fas(it.b).runM(it.a)
                            }
                        }
                    }
                    , MF)

    fun <B> flatMapF(faf: (A) -> HK<F, B>): StateT<F, S, B> =
            invokeF(
                    MF.map(runF) { sfsa ->
                        sfsa.andThen { fsa ->
                            MF.flatMap(fsa) { (s, a) ->
                                MF.map(faf(a)) { b -> Tuple2(s, b) }
                            }
                        }
                    }
                    , MF)

    fun <B> transform(f: (Tuple2<S, A>) -> Tuple2<S, B>): StateT<F, S, B> =
            invokeF(
                    MF.map(runF) { sfsa ->
                        sfsa.andThen { fsa ->
                            MF.map(fsa, f)
                        }
                    }, MF)

    fun run(initial: S): HK<F, Tuple2<S, A>> = MF.flatMap(runF) { f -> f(initial) }

    fun runA(s: S): HK<F, A> = MF.map(run(s)) { it.b }

    fun runS(s: S): HK<F, S> = MF.map(run(s)) { it.a }
}

inline fun <reified F, S, A> StateTFunKind<F, S, A>.stateT(FT: Monad<F> = monad()): StateT<F, S, A> = StateT(FT, this)

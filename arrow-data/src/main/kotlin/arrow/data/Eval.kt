package arrow

/**
 * Eval is a monad which controls evaluation of a value or a computation that produces a value.
 *
 * Three basic evaluation strategies:
 *
 *  - Now:    evaluated immediately
 *  - Later:  evaluated once when value is needed
 *  - Always: evaluated every time value is needed
 *
 * The Later and Always are both lazy strategies while Now is eager.
 * Later and Always are distinguished from each other only by
 * memoization: once evaluated Later will save the value to be returned
 * immediately if it is needed again. Always will run its computation
 * every time.
 *
 * Eval supports stack-safe lazy computation via the .map and .flatMap
 * methods, which use an internal trampoline to avoid stack overflows.
 * Computation done within .map and .flatMap is always done lazily,
 * even when applied to a Now instance.
 *
 * It is not generally good style to pattern-match on Eval instances.
 * Rather, use .map and .flatMap to chain computation, and use .value
 * to get the result when needed. It is also not good style to create
 * Eval instances whose computation involves calling .value on another
 * Eval instance -- this can defeat the trampolining and lead to stack
 * overflows.
 */
@higherkind
@deriving(
        Functor::class,
        Applicative::class,
        Monad::class,
        Comonad::class,
        Bimonad::class)
sealed class Eval<out A> : EvalKind<A> {

    companion object {

        fun <A, B> tailRecM(a: A, f: (A) -> EvalKind<Either<A, B>>): Eval<B> =
                f(a).ev().flatMap { eval: Either<A, B> ->
                    when (eval) {
                        is Left -> tailRecM(eval.a, f)
                        is Right -> pure(eval.b)
                    }
                }

        fun <A> pure(a: A): Eval<A> = Eval.now(a)

        fun <A> now(a: A) = Now(a)

        fun <A> later(f: () -> A) = Later(f)

        fun <A> always(f: () -> A) = Always(f)

        fun <A> defer(f: () -> Eval<A>): Eval<A> = Call(f)

        fun raise(t: Throwable): Eval<Nothing> = defer { throw t }

        val Unit: Eval<Unit> = Now(kotlin.Unit)

        val True: Eval<Boolean> = Now(true)

        val False: Eval<Boolean> = Now(false)

        val Zero: Eval<Int> = Now(0)

        val One: Eval<Int> = Now(1)

        fun <A, B> merge(
                op1: () -> A,
                op2: () -> B): Eval<Tuple2<A, B>> =
                applicative().tupled(
                        later(op1),
                        later(op2)
                ).ev()

        fun <A, B, C> merge(
                op1: () -> A,
                op2: () -> B,
                op3: () -> C): Eval<Tuple3<A, B, C>> =
                applicative().tupled(
                        later(op1),
                        later(op2),
                        later(op3)
                ).ev()

        fun <A, B, C, D> merge(
                op1: () -> A,
                op2: () -> B,
                op3: () -> C,
                op4: () -> D): Eval<Tuple4<A, B, C, D>> =
                applicative().tupled(
                        later(op1),
                        later(op2),
                        later(op3),
                        later(op4)
                ).ev()

        fun <A, B, C, D, E> merge(
                op1: () -> A,
                op2: () -> B,
                op3: () -> C,
                op4: () -> D,
                op5: () -> E): Eval<Tuple5<A, B, C, D, E>> =
                applicative().tupled(
                        later(op1),
                        later(op2),
                        later(op3),
                        later(op4),
                        later(op5)
                ).ev()

        fun <A, B, C, D, E, F> merge(
                op1: () -> A,
                op2: () -> B,
                op3: () -> C,
                op4: () -> D,
                op5: () -> E,
                op6: () -> F): Eval<Tuple6<A, B, C, D, E, F>> =
                applicative().tupled(
                        later(op1),
                        later(op2),
                        later(op3),
                        later(op4),
                        later(op5),
                        later(op6)
                ).ev()

        fun <A, B, C, D, E, F, G> merge(
                op1: () -> A,
                op2: () -> B,
                op3: () -> C,
                op4: () -> D,
                op5: () -> E,
                op6: () -> F,
                op7: () -> G): Eval<Tuple7<A, B, C, D, E, F, G>> =
                applicative().tupled(
                        later(op1),
                        later(op2),
                        later(op3),
                        later(op4),
                        later(op5),
                        later(op6),
                        later(op7)
                ).ev()

        fun <A, B, C, D, E, F, G, H> merge(
                op1: () -> A,
                op2: () -> B,
                op3: () -> C,
                op4: () -> D,
                op5: () -> E,
                op6: () -> F,
                op7: () -> G,
                op8: () -> H): Eval<Tuple8<A, B, C, D, E, F, G, H>> =
                applicative().tupled(
                        later(op1),
                        later(op2),
                        later(op3),
                        later(op4),
                        later(op5),
                        later(op6),
                        later(op7),
                        later(op8)
                ).ev()

        fun <A, B, C, D, E, F, G, H, I> merge(
                op1: () -> A,
                op2: () -> B,
                op3: () -> C,
                op4: () -> D,
                op5: () -> E,
                op6: () -> F,
                op7: () -> G,
                op8: () -> H,
                op9: () -> I): Eval<Tuple9<A, B, C, D, E, F, G, H, I>> =
                applicative().tupled(
                        later(op1),
                        later(op2),
                        later(op3),
                        later(op4),
                        later(op5),
                        later(op6),
                        later(op7),
                        later(op8),
                        later(op9)
                ).ev()

        fun <A, B, C, D, E, F, G, H, I, J> merge(
                op1: () -> A,
                op2: () -> B,
                op3: () -> C,
                op4: () -> D,
                op5: () -> E,
                op6: () -> F,
                op7: () -> G,
                op8: () -> H,
                op9: () -> I,
                op10: () -> J): Eval<Tuple10<A, B, C, D, E, F, G, H, I, J>> =
                applicative().tupled(
                        later(op1),
                        later(op2),
                        later(op3),
                        later(op4),
                        later(op5),
                        later(op6),
                        later(op7),
                        later(op8),
                        later(op9),
                        later(op10)
                ).ev()
    }

    abstract fun value(): A

    abstract fun memoize(): Eval<A>

    fun <B> map(f: (A) -> B): Eval<B> = flatMap { a -> Now(f(a)) }

    fun <B> ap(ff: EvalKind<(A) -> B>): Eval<B> = ff.flatMap { f -> map(f) }.ev()

    fun <B> flatMap(f: (A) -> EvalKind<B>): Eval<B> =
            when (this) {
                is Eval.Compute<A> -> object : Compute<B>() {
                    override fun <S> start(): Eval<S> = (this@Eval).start()
                    override fun <S> run(s: S): Eval<B> =
                            object : Compute<B>() {
                                override fun <S1> start(): Eval<S1> = (this@Eval).run(s) as Eval<S1>
                                override fun <S1> run(s1: S1): Eval<B> = f(s1 as A).ev()
                            }
                }
                is Eval.Call<A> -> object : Eval.Compute<B>() {
                    override fun <S> start(): Eval<S> = this@Eval.thunk() as Eval<S>
                    override fun <S> run(s: S): Eval<B> = f(s as A).ev()
                }
                else -> object : Eval.Compute<B>() {
                    override fun <S> start(): Eval<S> = this@Eval as Eval<S>
                    override fun <S> run(s: S): Eval<B> = f(s as A).ev()
                }
            }

    fun <B> coflatMap(f: (EvalKind<A>) -> B): Eval<B> = Later { f(this) }

    fun extract(): A = value()

    /**
     * Construct an eager Eval<A> instance. In some sense it is equivalent to using a val.
     *
     * This type should be used when an A value is already in hand, or when the computation to produce an A value is
     * pure and very fast.
     */
    data class Now<out A>(val value: A) : Eval<A>() {
        override fun value(): A = value
        override fun memoize(): Eval<A> = this
    }

    /**
     * Construct a lazy Eval<A> instance.
     *
     * This type should be used for most "lazy" values. In some sense it is equivalent to using a lazy val.
     *
     * When caching is not required or desired (e.g. if the value produced may be large) prefer Always. When there
     * is no computation necessary, prefer Now.
     *
     * Once Later has been evaluated, the closure (and any values captured by the closure) will not be retained, and
     * will be available for garbage collection.
     */
    data class Later<out A>(private val f: () -> A) : Eval<A>() {
        val value: A by lazy(f)

        override fun value(): A = value
        override fun memoize(): Eval<A> = this
    }

    /**
     * Construct a lazy Eval<A> instance.
     *
     * This type can be used for "lazy" values. In some sense it is equivalent to using a Function0 value.
     *
     * This type will evaluate the computation every time the value is required. It should be avoided except when
     * laziness is required and caching must be avoided. Generally, prefer Later.
     */
    data class Always<out A>(private val f: () -> A) : Eval<A>() {
        override fun value(): A = f()
        override fun memoize(): Eval<A> = Later(f)
    }

    /**
     * Call is a type of Eval<A> that is used to defer computations which produce Eval<A>.
     *
     * Users should not instantiate Call instances themselves. Instead, they will be automatically created when needed.
     */
    data class Call<out A>(val thunk: () -> Eval<A>) : Eval<A>() {
        override fun memoize(): Eval<A> = Later { value() }
        override fun value(): A = collapse(this).value()

        companion object {
            /**
             * Collapse the call stack for eager evaluations.
             */
            tailrec fun <A> collapse(fa: Eval<A>): Eval<A> =
                    when (fa) {
                        is Call -> collapse(fa.thunk())
                        is Compute ->
                            object : Compute<A>() {
                                override fun <S> start(): Eval<S> = fa.start()
                                override fun <S> run(s: S): Eval<A> = collapse1(fa.run(s))
                            }
                        else -> fa
                    }

            //Enforce tailrec call to collapse inside compute loop
            private inline fun <A> collapse1(fa: Eval<A>): Eval<A> = collapse(fa)
        }
    }

    /**
     * Compute is a type of Eval<A> that is used to chain computations involving .map and .flatMap. Along with
     * Eval#flatMap. It implements the trampoline that guarantees stack-safety.
     *
     * Users should not instantiate Compute instances themselves. Instead, they will be automatically created when
     * needed.
     *
     * Unlike a traditional trampoline, the internal workings of the trampoline are not exposed. This allows a slightly
     * more efficient implementation of the .value method.
     */
    internal abstract class Compute<out A> : Eval<A>() {

        abstract fun <S> start(): Eval<S>

        abstract fun <S> run(s: S): Eval<A>

        override fun memoize(): Eval<A> = Later { value() }

        override fun value(): A {
            var curr: Eval<A> = this
            var fs: List<(Any?) -> Eval<A>> = listOf()

            loop@ while (true) {
                when (curr) {
                    is Compute -> {
                        val currComp: Compute<A> = curr
                        currComp.start<A>().let { cc ->
                            when (cc) {
                                is Compute -> {
                                    val inStartFun: (Any?) -> Eval<A> = { cc.run(it) }
                                    val outStartFun: (Any?) -> Eval<A> = { currComp.run(it) }
                                    curr = cc.start<A>()
                                    fs = listOf(inStartFun, outStartFun) + fs
                                }
                                else -> {
                                    curr = currComp.run(cc.value())
                                }
                            }
                        }
                    }
                    else ->
                        if (fs.isNotEmpty()) {
                            curr = fs[0](curr.value())
                            fs = fs.drop(1)
                        } else {
                            break@loop
                        }
                }
            }
            return curr.value()
        }
    }
}

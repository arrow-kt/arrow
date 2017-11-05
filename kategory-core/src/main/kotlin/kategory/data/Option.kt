package kategory

typealias Some<A> = Option.Some<A>
typealias None = Option.None

/**
 * Port of https://github.com/scala/scala/blob/v2.12.1/src/library/scala/Option.scala
 *
 * Represents optional values. Instances of `Option`
 * are either an instance of $some or the object $none.
 */
@higherkind
@deriving(
        Functor::class,
        Applicative::class,
        Monad::class,
        Foldable::class,
        Traverse::class,
        TraverseFilter::class,
        MonadFilter::class)
sealed class Option<out A> : OptionKind<A> {

    companion object {

        fun <A> pure(a: A): Option<A> = Some(a)

        tailrec fun <A, B> tailRecM(a: A, f: (A) -> OptionKind<Either<A, B>>): Option<B> {
            val option = f(a).ev()
            return when (option) {
                is Some -> {
                    when (option.value) {
                        is Either.Left -> tailRecM(option.value.a, f)
                        is Either.Right -> Some(option.value.b)
                    }
                }
                is None -> None
            }
        }

        @JvmStatic
        fun <A> fromNullable(a: A?): Option<A> = if (a != null) Some(a) else None

        operator fun <A> invoke(a: A): Option<A> = Some(a)

        fun <A> empty(): Option<A> = None

    }

    /**
     * Returns true if the option is [None], false otherwise.
     * @note Used only for performance instead of fold.
     */
    abstract val isEmpty: Boolean

    /**
     * Returns true if the option is an instance of $some, false otherwise.
     * @note Used only for performance instead of fold.
     */
    internal val isDefined: Boolean = !isEmpty

    /**
     * Returns a $some containing the result of applying $f to this $option's
     * value if this $option is nonempty. Otherwise return $none.
     *
     * @note This is similar to `flatMap` except here,
     * $f does not need to wrap its result in an $option.
     *
     * @param f the function to apply
     * @see flatMap
     */
    inline fun <B> map(crossinline f: (A) -> B): Option<B> = fold({ None }, { a -> Some(f(a)) })

    fun <B> ap(ff: OptionKind<(A) -> B>): Option<B> = ff.ev().flatMap { this.ev().map(it) }

    /**
     * Returns the result of applying $f to this $option's value if
     * this $option is nonempty.
     * Returns $none if this $option is empty.
     * Slightly different from `map` in that $f is expected to
     * return an $option (which could be $none).
     *
     * @param f the function to apply
     * @see map
     */
    inline fun <B> flatMap(crossinline f: (A) -> OptionKind<B>): Option<B> = fold({ None }, { a -> f(a) }).ev()

    /**
     * Returns the result of applying $f to this $option's
     * value if the $option is nonempty.  Otherwise, evaluates
     * expression `ifEmpty`.
     *
     * @note This is equivalent to `$option map f getOrElse ifEmpty`.
     *
     * @param ifEmpty the expression to evaluate if empty.
     * @param f the function to apply if nonempty.
     */
    inline fun <B> fold(crossinline ifEmpty: () -> B, crossinline f: (A) -> B): B = when (this) {
        is None -> ifEmpty()
        is Some<A> -> f(value)
    }

    fun <B> foldL(b: B, f: (B, A) -> B): B =
            this.ev().let { option ->
                when (option) {
                    is Some -> f(b, option.value)
                    is None -> b
                }
            }

    fun <B> foldR(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
            this.ev().let { option ->
                when (option) {
                    is Some -> f(option.value, lb)
                    is None -> lb
                }
            }

    fun <G, B> traverse(f: (A) -> HK<G, B>, GA: Applicative<G>): HK<G, Option<B>> =
            this.ev().let { option ->
                when (option) {
                    is Some -> GA.map(f(option.value), { Some(it) })
                    is None -> GA.pure(None)
                }
            }

    fun <G, B> traverseFilter(f: (A) -> HK<G, Option<B>>, GA: Applicative<G>): HK<G, Option<B>> =
            this.ev().let { option ->
                when (option) {
                    is Some -> f(option.value)
                    is None -> GA.pure(None)
                }
            }

    /**
     * Returns this $option if it is nonempty '''and''' applying the predicate $p to
     * this $option's value returns true. Otherwise, return $none.
     *
     *  @param p the predicate used for testing.
     */
    inline fun filter(crossinline p: (A) -> Boolean): Option<A> = fold({ None }, { a -> if (p(a)) Some(a) else None })

    /**
     * Returns this $option if it is nonempty '''and''' applying the predicate $p to
     * this $option's value returns false. Otherwise, return $none.
     *
     * @param p the predicate used for testing.
     */
    inline fun filterNot(crossinline p: (A) -> Boolean): Option<A> = fold({ None }, { a -> if (!p(a)) Some(a) else None })

    /**
     * Returns false if the option is $none, true otherwise.
     * @note Implemented here to avoid the implicit conversion to Iterable.
     * @note Used only for performance instead of fold.
     */
    internal val nonEmpty = isDefined

    /**
     * Returns true if this option is nonempty '''and''' the predicate
     * $p returns true when applied to this $option's value.
     * Otherwise, returns false.
     *
     * @param p the predicate to test
     */
    inline fun exists(crossinline p: (A) -> Boolean): Boolean = fold({ false }, { a -> p(a) })

    /**
     * Returns true if this option is empty '''or''' the predicate
     * $p returns true when applied to this $option's value.
     *
     * @param p the predicate to test
     */
    inline fun forall(crossinline p: (A) -> Boolean): Boolean = exists(p)

    data class Some<out A>(val value: A) : Option<A>() {
        override val isEmpty = false
    }

    object None : Option<Nothing>() {
        override val isEmpty = true
        override fun toString(): String = "None"
    }

}

/**
 * Returns the option's value if the option is nonempty, otherwise
 * return the result of evaluating `default`.
 *
 * @param default the default expression.
 */
fun <B> Option<B>.getOrElse(default: () -> B): B = fold({ default() }, { it })

/**
 * Returns this option's if the option is nonempty, otherwise
 * returns another option provided lazily by `default`.
 *
 * @param default the default option if this is empty.
 */
fun <A, B : A> OptionKind<B>.orElse(alternative: () -> Option<B>): Option<B> = if (ev().isEmpty) alternative() else ev()

fun <A> A.some(): Option<A> = Some(this)

fun <A> none(): Option<A> = None

fun <A, L> Option<A>.toEither(ifEmpty: () -> L): Either<L, A> =
        this.fold({ ifEmpty().left() }, { it.right() })

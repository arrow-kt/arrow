package kategory

typealias OptionKind<A> = HK<Option.F, A>

fun <A> OptionKind<A>.ev(): Option<A> = this as Option<A>

/**
 * Port of https://github.com/scala/scala/blob/v2.12.1/src/library/scala/Option.scala
 *
 * Represents optional values. Instances of `Option`
 * are either an instance of $some or the object $none.
 */
sealed class Option<out A> : OptionKind<A> {

    class F private constructor()

    companion object : OptionInstances, GlobalInstance<Monad<Option.F>>() {
        @JvmStatic fun <A : Any> fromNullable(a: A?): Option<A> = if (a != null) Option.Some(a) else Option.None

        operator fun <A> invoke(a: A): Option<A> = Option.Some(a)

        fun functor(): Functor<Option.F> = this

        fun applicative(): Applicative<Option.F> = this

        fun monad(): Monad<Option.F> = this

        fun monadError(): MonadError<Option.F, Unit> = this

        fun foldable(): Foldable<Option.F> = this

        fun traverse(): Traverse<Option.F> = this

        fun <A> monoid(SG: Semigroup<A>): OptionMonoid<A> = object : OptionMonoid<A> {
            override fun SG(): Semigroup<A> = SG
        }
    }

    /**
     * Returns true if the option is [None], false otherwise.
     * Used only for performance instead of fold.
     */
    internal abstract val isEmpty: Boolean

    /**
     * Returns true if the option is an instance of $some, false otherwise.
     */
    val isDefined: Boolean = !isEmpty

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
    inline fun <B> map(crossinline f: (A) -> B): Option<B> = fold({ Option.None }, { a -> Option.Some(f(a)) })

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
    inline fun <B> flatMap(crossinline f: (A) -> Option<B>): Option<B> = fold({ Option.None }, { a -> f(a) })

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
        is Option.None -> ifEmpty()
        is Option.Some<A> -> f(value)
    }

    /**
     * Returns this $option if it is nonempty '''and''' applying the predicate $p to
     * this $option's value returns true. Otherwise, return $none.
     *
     *  @param p the predicate used for testing.
     */
    inline fun filter(crossinline p: (A) -> Boolean): Option<A> = fold({ Option.None }, { a -> if (p(a)) Option.Some(a) else Option.None })

    /**
     * Returns this $option if it is nonempty '''and''' applying the predicate $p to
     * this $option's value returns false. Otherwise, return $none.
     *
     * @param p the predicate used for testing.
     */
    inline fun filterNot(crossinline p: (A) -> Boolean): Option<A> = fold({ Option.None }, { a -> if (!p(a)) Option.Some(a) else Option.None })

    /**
     * Returns false if the option is $none, true otherwise.
     * @note Implemented here to avoid the implicit conversion to Iterable.
     */
    val nonEmpty = isDefined

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
fun <A, B : A> Option<B>.orElse(alternative: () -> Option<B>): Option<B> = if (isEmpty) alternative() else this

fun <A> A.some(): Option<A> = Option.Some(this)

fun <A> none(): Option<A> = Option.None
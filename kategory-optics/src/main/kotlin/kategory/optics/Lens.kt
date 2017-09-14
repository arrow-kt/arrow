package kategory.optics

import kategory.Either
import kategory.Functor
import kategory.HK
import kategory.Option
import kategory.Tuple2
import kategory.functor
import kategory.identity
import kategory.none
import kategory.right
import kategory.some
import kategory.toT

typealias Lens<S, A> = PLens<S, S, A, A>

/**
 * A [Lens] can be seen as a pair of functions `get: (A) -> B` and `set: (B) -> (A) -> A`
 * - `get: (A) -> B` i.e. from an `A`, we can extract an `B`
 * - `set: (B) -> (A) -> A` i.e. if we replace target value by `B` in an `A`, we obtain another modified `A`
 *
 * @param A the source of a [Lens]
 * @param B the target of a [Lens]
 * @property get from an `A` we can extract a `B`
 * @property set replace the target value by `B` in an `A` so we obtain another modified `A`
 * @constructor Creates a Lens of type `A` with target `B`.
 */
abstract class PLens<S, T, A, B> {

    abstract fun get(s: S): A
    abstract fun set(b: B): (S) -> T

    companion object {

        fun <A> id() = Iso.id<A>().asLens()

        /**
         * [Lens] that takes either A or A and strips the choice of A.
         */
        fun <A> codiagonal(): Lens<Either<A, A>, A> = Lens(
                get = { it.fold(::identity, ::identity) },
                set = { a -> { it.bimap({ a }, { a }) } }
        )

        operator fun <S, T, A, B> invoke(get: (S) -> A, set: (B) -> (S) -> T) = object : PLens<S, T, A, B>() {
            override fun get(s: S): A = get(s)

            override fun set(b: B): (S) -> T = set(b)
        }
    }

    /**
     * Modify the target of a [Lens] using a function `(A) -> B`
     */
    inline fun modify(f: (A) -> B, a: S): T = set(f(get(a)))(a)

    /**
     * Modify the target of a [Lens] using Functor function
     */
    inline fun <reified F> modifyF(FF: Functor<F> = functor(), f: (A) -> HK<F, B>, s: S): HK<F, T> =
            FF.map(f(get(s)), { set(it)(s) })

    /**
     * Find if the target satisfies the predicate
     */
    inline fun find(crossinline p: (A) -> Boolean): (S) -> Option<A> = { s ->
        get(s).let { a ->
            if (p(a)) a.some() else none()
        }
    }

    /**
     * Checks if the target of a [Lens] satisfies the predicate
     */
    inline fun exist(crossinline p: (A) -> Boolean): (S) -> Boolean = { p(get(it)) }

    /**
     * Join two [Lens] with the same target
     */
    fun <S1, T1> choice(other: PLens<S1, T1, A, B>): PLens<Either<S, S1>, Either<T, T1>, A, B> = PLens(
            { ss -> ss.fold(this::get, other::get) },
            { b -> { ss -> ss.bimap(set(b), other.set(b)) } }
    )

    /**
     * Pair two disjoint [Lens]
     */
    fun <S1, T1, A1, B1> split(other: PLens<S1, T1, A1, B1>): PLens<Tuple2<S, S1>, Tuple2<T, T1>, Tuple2<A, A1>, Tuple2<B, B1>> =
            PLens(
                    { (s, c) -> get(s) toT other.get(c) },
                    { (b, b1) -> { (s, s1) -> set(b)(s) toT other.set(b1)(s1) } }
            )

    /**
     * Create a product of the target and a type C
     */
    fun <C> first(): PLens<Tuple2<S, C>, Tuple2<T, C>, Tuple2<A, C>, Tuple2<B, C>> = PLens(
            { (s, c) -> get(s) toT c },
            { (b, c) -> { (a, _) -> set(b)(a) toT c } }
    )

    /**
     * Create a product of a type C and the target
     */
    fun <C> second(): PLens<Tuple2<C, S>, Tuple2<C, T>, Tuple2<C, A>, Tuple2<C, B>> = PLens(
            { (c, s) -> c toT get(s) },
            { (c, b) -> { (_, s) -> c toT set(b)(s) } }
    )

    /**
     * Compose a [Lens] with another [Lens]
     */
    infix fun <C, D> composeLens(l: PLens<A, B, C, D>): PLens<S, T, C, D> = Lens(
            { a -> l.get(get(a)) },
            { c -> { a -> set(l.set(c)(get(a)))(a) } }
    )

    /** compose a [Lens] with a [Optional] */
    infix fun <C, D> composeOptional(other: POptional<A, B, C, D>): POptional<S, T, C, D> =
            asOptional() composeOptional other

    /** compose an [Iso] as an [Prism] */
    fun <C, D> composeIso(other: PIso<A, B, C, D>): PLens<S, T, C, D> = composeLens(other.asLens())

    /**
     * plus operator overload to compose lenses
     */
    operator fun <C, D> plus(other: PLens<A, B, C, D>): PLens<S, T, C, D> = composeLens(other)

    operator fun <C, D> plus(other: POptional<A, B, C, D>): POptional<S, T, C, D> = composeOptional(other)

    operator fun <C, D> plus(other: PIso<A, B, C, D>): PLens<S, T, C, D> = composeIso(other)

    /**
     * View a [Lens] as an [Optional]
     */
    fun asOptional(): POptional<S, T, A, B> = POptional(
            { s -> get(s).right() },
            this::set
    )

}

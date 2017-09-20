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
 * A [PLens] can be seen as a pair of functions `get: (A) -> B` and `set: (B) -> (A) -> A`
 * - `get: (A) -> B` i.e. from an `A`, we can extract an `B`
 * - `set: (B) -> (A) -> A` i.e. if we replace target value by `B` in an `A`, we obtain another modified `A`
 *
 * @param A the source of a [PLens]
 * @param B the target of a [PLens]
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
         * [PLens] that takes either A or A and strips the choice of A.
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
     * Modify the target of s [PLens] using s function `(A) -> B`
     */
    inline fun modify(crossinline f: (A) -> B): (S) -> T = { s -> set(f(get(s)))(s) }

    /**
     * Modify the target of a [PLens] using Functor function
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
     * Checks if the target of a [PLens] satisfies the predicate
     */
    inline fun exist(crossinline p: (A) -> Boolean): (S) -> Boolean = { p(get(it)) }

    /**
     * Join two [PLens] with the same target
     */
    fun <S1, T1> choice(other: PLens<S1, T1, A, B>): PLens<Either<S, S1>, Either<T, T1>, A, B> = PLens(
            { ss -> ss.fold(this::get, other::get) },
            { b -> { ss -> ss.bimap(set(b), other.set(b)) } }
    )

    /**
     * Pair two disjoint [PLens]
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
     * Compose a [PLens] with another [PLens]
     */
    infix fun <C, D> compose(l: PLens<A, B, C, D>): PLens<S, T, C, D> = Lens(
            { a -> l.get(get(a)) },
            { c -> { a -> set(l.set(c)(get(a)))(a) } }
    )

    /**
     * Compose a [PLens] with a [POptional]
     */
    infix fun <C, D> compose(other: POptional<A, B, C, D>): POptional<S, T, C, D> = asOptional() compose other

    /**
     * Compose an [PLens] with a [PIso]
     */
    infix fun <C, D> compose(other: PIso<A, B, C, D>): PLens<S, T, C, D> = compose(other.asLens())

    /**
     * Compose an [PLens] with a [Getter]
     */
    infix fun <C> compose(other: Getter<A, C>): Getter<S, C> = asGetter() composeGetter other

    /**
     * Compose an [PLens] with a [PSetter]
     */
    infix fun <C, D> compose(other: PSetter<A, B, C, D>): PSetter<S, T, C, D> = asSetter() compose other

    /**
     * Compose an [PLens] with a [PPrism]
     */
    infix fun <C, D> compose(other: PPrism<A, B, C, D>): POptional<S, T, C, D> = asOptional() compose other

    /**
     * plus operator overload to compose lenses
     */
    operator fun <C, D> plus(other: PLens<A, B, C, D>): PLens<S, T, C, D> = compose(other)

    operator fun <C, D> plus(other: POptional<A, B, C, D>): POptional<S, T, C, D> = compose(other)

    operator fun <C, D> plus(other: PIso<A, B, C, D>): PLens<S, T, C, D> = compose(other)

    operator fun <C> plus(other: Getter<A, C>): Getter<S, C> = compose(other)

    operator fun <C, D> plus(other: PSetter<A, B, C, D>): PSetter<S, T, C, D> = compose(other)

    operator fun <C, D> plus(other: PPrism<A, B, C, D>): POptional<S, T, C, D> = compose(other)

    /**
     * View [PLens] as a [Getter]
     */
    fun asGetter(): Getter<S, A> = Getter(this::get)

    /**
     * View a [PLens] as a [POptional]
     */
    fun asOptional(): POptional<S, T, A, B> = POptional(
            { s -> get(s).right() },
            this::set
    )

    /**
     * View a [PLens] as a [PSetter]
     */
    fun asSetter(): PSetter<S, T, A, B> = PSetter(this::modify)

}

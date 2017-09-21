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

/**
 * [Lens] is a type alias for [PLens] which fixes the type arguments
 * and restricts the [PLens] to monomorphic updates.
 */
typealias Lens<S, A> = PLens<S, S, A, A>

/**
 * A [Lens] (or Functional Reference) is an optic that allows to see into a structure and
 * getting, setting or modifying the target.
 *
 * A (polymorphic) [PLens] is useful when setting or modifying a value for a constructed type
 * i.e. PLens<Tuple2<Double, Int>, Tuple2<String, Int>, Double, String>
 *
 * A [PLens] can be seen as a pair of functions:
 * - `get: (S) -> A` meaning we can look into an `S` and extract an `A`
 * - `set: (B) -> (S) -> T` meaning we can look into an `S` and set a value `B` for a target `A` and obtain a modified source `T`
 *
 * @param S the source of a [PLens]
 * @param T the modified source of a [PLens]
 * @param A the target of a [PLens]
 * @param B the modified target of a [PLens]
 */
abstract class PLens<S, T, A, B> {

    abstract fun get(s: S): A
    abstract fun set(b: B): (S) -> T

    companion object {

        fun <S> id() = Iso.id<S>().asLens()

        /**
         * [PLens] that takes either [S] or [S] and strips the choice of [S].
         */
        fun <S> codiagonal(): Lens<Either<S, S>, S> = Lens(
                get = { it.fold(::identity, ::identity) },
                set = { a -> { it.bimap({ a }, { a }) } }
        )

        /**
         * Invoke operator overload to create a [PLens] of type `S` with target `A`.
         * Can also be used to construct [Lens]
         */
        operator fun <S, T, A, B> invoke(get: (S) -> A, set: (B) -> (S) -> T) = object : PLens<S, T, A, B>() {
            override fun get(s: S): A = get(s)

            override fun set(b: B): (S) -> T = set(b)
        }
    }

    /**
     * Modify the target of s [PLens] using s function `(A) -> B`
     */
    inline fun modify(s: S, crossinline f: (A) -> B): T = set(f(get(s)))(s)

    /**
     * Modify the target of a [PLens] using Functor function
     */
    inline fun <reified F> modifyF(FF: Functor<F> = functor(), s: S, f: (A) -> HK<F, B>): HK<F, T> =
            FF.map(f(get(s)), { set(it)(s) })

    /**
     * Find if the target satisfies the predicate
     */
    inline fun find(s: S, crossinline p: (A) -> Boolean): Option<A> = get(s).let { a ->
        if (p(a)) a.some() else none()
    }

    /**
     * Checks if the target of a [PLens] satisfies the predicate
     */
    inline fun exist(s: S, crossinline p: (A) -> Boolean): Boolean = p(get(s))

    /**
     * Join two [PLens] with the same target [A]
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
     * Create a product of the [PLens] and a type [C]
     */
    fun <C> first(): PLens<Tuple2<S, C>, Tuple2<T, C>, Tuple2<A, C>, Tuple2<B, C>> = PLens(
            { (s, c) -> get(s) toT c },
            { (b, c) -> { (a, _) -> set(b)(a) toT c } }
    )

    /**
     * Create a product of a type [C] and the [PLens]
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
    fun asSetter(): PSetter<S, T, A, B> = PSetter { f -> { s -> modify(s, f) } }

}

package kategory.optics

import kategory.Applicative
import kategory.Either
import kategory.HK
import kategory.Option
import kategory.Tuple2
import kategory.compose
import kategory.flatMap
import kategory.identity
import kategory.left
import kategory.none
import kategory.right
import kategory.some
import kategory.toT

typealias Optional<S, A> = POptional<S, S, A, A>

/**
 * A [Optional] can be seen as a pair of functions `getOption: (A) -> Option<B>` and `set: (B) -> (A) -> A`
 *
 * An [Optional] can also be defined as a weaker [Lens] and [Prism]
 *
 * @param A the source of a [Optional]
 * @param B the target of a [Optional]
 * @property getOption from an `A` we can extract a `Option<B>`
 * @property set replace the target value by `B` in an `A` so we obtain another modified `A`
 * @constructor Creates a Lens of type `A` with target `B`.
 */
abstract class POptional<S, T, A, B> {

    /**
     * Get the modified source of a [Optional]
     */
    abstract fun set(b: B): (S) -> (T)

    /**
     * Get the target of a [Optional] or return the original value while allowing the type to change if it does not match
     */
    abstract fun getOrModify(s: S): Either<T, A>

    companion object {

        fun <A> id() = Iso.id<A>().asOptional()

        /**
         * [Optional] that takes either A or A and strips the choice of A.
         */
        fun <A> codiagonal(): Optional<Either<A, A>, A> = Optional(
                { it.fold({ it.right() }, { it.right() }) },
                { a -> { aa -> aa.bimap({ a }, { a }) } }
        )

        operator fun <S, T, A, B> invoke(getOrModify: (S) -> Either<T, A>, set: (B) -> (S) -> T): POptional<S, T, A, B> = object : POptional<S, T, A, B>() {
            override fun getOrModify(s: S): Either<T, A> = getOrModify(s)

            override fun set(b: B): (S) -> T = set(b)
        }

        fun <A, B> void(): Optional<A, B> = Optional(
                { it.left() },
                { _ -> ::identity }
        )

    }

    /**
     * Get the target of a [Optional] or [Option.None] if there is no target
     */
    fun getOption(a: S): Option<A> = getOrModify(a).toOption()

    /**
     * Modify polymorphically the target of a [Optional] with a function [f]
     */
    inline fun modify(crossinline f: (A) -> B): (S) -> T = { s ->
        getOrModify(s).fold(::identity, { a -> set(f(a))(s) })
    }

    /**
     * Modify polymorphically the target of a [Optional] with an Applicative function [f]
     */
    inline fun <F> modifyF(FA: Applicative<F>, crossinline f: (A) -> HK<F, B>, s: S): HK<F, T> =
            getOrModify(s).fold(
                    FA::pure,
                    { FA.map(f(it), { set(it)(s) }) }
            )

    /**
     * Modify polymorphically the target of a [Optional] with a function [f]
     * @return [Option.None] if the [Optional] is not matching
     */
    fun modifiyOption(f: (A) -> B): (S) -> Option<T> = { a -> getOption(a).map({ set(f(it))(a) }) }

    /**
     * Set polymorphically the target of a [Optional] with a value.
     * @return [Option.None] if the [Optional] is not matching
     */
    fun setOption(b: B): (S) -> Option<T> = modifiyOption { b }

    /**
     * Check if there is no target
     */
    fun isEmpty(s: S): Boolean = !nonEmpty(s)

    /**
     * Check if there is a target
     */
    fun nonEmpty(s: S): Boolean = getOption(s).fold({ false }, { true })

    /**
     * Find if the target satisfies the predicate [p]
     */
    fun find(p: (A) -> Boolean): (S) -> Option<A> =
            { a -> getOption(a).flatMap { b -> if (p(b)) b.some() else none() } }

    /**
     * Check if there is a target and it satisfies the predicate [p]
     */
    fun exists(p: (A) -> Boolean): (S) -> Boolean =
            { a -> getOption(a).fold({ false }, p) }

    /**
     * check if there is no target or the target satisfies the predicate [p]
     */
    fun all(p: (A) -> Boolean): (S) -> Boolean =
            { a -> getOption(a).fold({ true }, p) }

    /**
     * join two [Optional] with the same target [B]
     */
    fun <S1, T1> choice(other: POptional<S1, T1, A, B>): POptional<Either<S, S1>, Either<T, T1>, A, B> =
            POptional(
                    { ss -> ss.fold({ getOrModify(it).bimap({ it.left() }, ::identity) }, { other.getOrModify(it).bimap({ it.right() }, ::identity) }) },
                    { b -> { it.bimap(this.set(b), other.set(b)) } }
            )

    /**
     * Create a product of the target and a type [C]
     */
    fun <C> first(): POptional<Tuple2<S, C>, Tuple2<T, C>, Tuple2<A, C>, Tuple2<B, C>> =
            POptional(
                    { (s, c) -> getOrModify(s).bimap({ it toT c }, { it toT c }) },
                    { (b, c) -> { (a, c2) -> setOption(b)(a).fold({ set(b)(a) toT c2 }, { it toT c }) } }
            )

    /**
     * Create a product of a type [C] and the target
     */
    fun <C> second(): POptional<Tuple2<C, S>, Tuple2<C, T>, Tuple2<C, A>, Tuple2<C, B>> =
            POptional(
                    { (c, s) -> getOrModify(s).bimap({ c toT it }, { c toT it }) },
                    { (c, b) -> { (c2, a) -> setOption(b)(a).fold({ c2 toT set(b)(a) }, { c toT it }) } }
            )

    /** compose a [Optional] with a [Optional] */
    infix fun <C, D> composeOptional(other: POptional<A, B, C, D>): POptional<S, T, C, D> = POptional(
            { s -> getOrModify(s).flatMap { a -> other.getOrModify(a).bimap({ set(it)(s) }, ::identity) } },
            this::modify compose other::set
    )

    /**
     * Compose a [Optional] with a [Prism]
     */
    infix fun <C, D> composePrism(other: PPrism<A, B, C, D>): POptional<S, T, C, D> = composeOptional(other.asOptional())

    /**
     * Compose a [Optional] with a [Lens]
     */
    infix fun <C, D> composeLens(other: PLens<A, B, C, D>): POptional<S, T, C, D> = composeOptional(other.asOptional())

    /**
     * Plus operator overload to compose optionals
     */
    operator fun <C, D> plus(o: POptional<A, B, C, D>): POptional<S, T, C, D> = composeOptional(o)

    operator fun <C, D> plus(o: PPrism<A, B, C, D>): POptional<S, T, C, D> = composePrism(o)

    operator fun <C, D> plus(o: PLens<A, B, C, D>): POptional<S, T, C, D> = composeLens(o)
}
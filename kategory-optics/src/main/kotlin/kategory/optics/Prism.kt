package kategory.optics

import kategory.Applicative
import kategory.Either
import kategory.Eq
import kategory.HK
import kategory.Option
import kategory.Tuple2
import kategory.compose
import kategory.eq
import kategory.flatMap
import kategory.identity
import kategory.left
import kategory.none
import kategory.right
import kategory.some
import kategory.toT

typealias Prism<S, A> = PPrism<S, S, A, A>

/**
 * A [Prism] can be seen as a pair of functions: `reverseGet : B -> A` and `getOrModify: A -> Either<A,B>`
 *
 * - `reverseGet : B -> A` get the source type of a [Prism]
 * - `getOrModify: A -> Either<A,B>` get the target of a [Prism] or return the original value
 *
 * It encodes the relation between a Sum or CoProduct type (sealed class) and one of its element.
 *
 * @param A the source of a [Prism]
 * @param B the target of a [Prism]
 * @property getOrModify from an `B` we can produce an `A`
 * @property reverseGet get the target of a [Prism] or return the original value
 * @constructor Creates a Lens of type `A` with target `B`
 */
abstract class PPrism<S, T, A, B> {

    abstract fun getOrModify(s: S): Either<T, A>
    abstract fun reverseGet(b: B): T

    companion object {

        fun <A> id() = Iso.id<A>().asPrism()

        operator fun <S, T, A, B> invoke(getOrModify: (S) -> Either<T, A>, reverseGet: (B) -> T) = object : PPrism<S, T, A, B>() {
            override fun getOrModify(s: S): Either<T, A> = getOrModify(s)

            override fun reverseGet(b: B): T = reverseGet(b)
        }

        /**
         * a [Prism] that checks for equality with a given value
         */
        inline fun <reified A> only(a: A, EQA: Eq<A> = eq()): Prism<A, Unit> = Prism(
                getOrModify = { a2 -> (if (EQA.eqv(a, a2)) a.left() else Unit.right()) },
                reverseGet = { a }
        )

    }

    /**
     * Get the target or nothing if `A` does not match the target
     */
    fun getOption(a: S): Option<A> = getOrModify(a).toOption()

    /**
     * Modify the target of a [Prism] with an Applicative function
     */
    inline fun <reified F> modifyF(FA: Applicative<F> = kategory.applicative(), crossinline f: (A) -> HK<F, B>, s: S): HK<F, T> = getOrModify(s).fold(
            FA::pure,
            { FA.map(f(it), this::reverseGet) }
    )

    /**
     * Modify the target of a [Prism] with a function
     */
    inline fun modify(crossinline f: (A) -> B): (S) -> T = { s ->
        getOrModify(s).fold(::identity, { a -> reverseGet(f(a)) })
    }

    /**
     * Modify the target of a [Prism] with a function
     */
    inline fun modifyOption(crossinline f: (A) -> B): (S) -> Option<T> = { getOption(it).map { b -> reverseGet(f(b)) } }

    /**
     * Set the target of a [Prism] with a value
     */
    fun set(b: B): (S) -> T = modify { b }

    /**
     * Set the target of a [Prism] with a value
     */
    fun setOption(b: B): (S) -> Option<T> = modifyOption { b }

    /**
     * Check if there is a target
     */
    fun nonEmpty(s: S): Boolean = getOption(s).fold({ false }, { true })

    /**
     * Check if there is no target
     */
    fun isEmpty(s: S): Boolean = !nonEmpty(s)

    /**
     * Find if the target satisfies the predicate
     */
    inline fun find(crossinline p: (A) -> Boolean): (S) -> Option<A> = { s ->
        getOption(s).flatMap { a -> if (p(a)) a.some() else none() }
    }

    /**
     * Check if there is a target and it satisfies the predicate
     */
    inline fun exist(crossinline p: (A) -> Boolean): (S) -> Boolean = { s ->
        getOption(s).fold({ false }, p)
    }

    /**
     * Check if there is no target or the target satisfies the predicate
     */
    inline fun all(crossinline p: (A) -> Boolean): (S) -> Boolean = { s ->
        getOption(s).fold({ true }, p)
    }

    /**
     * Create a product of the target and a type C
     */
    fun <C> first(): PPrism<Tuple2<S, C>, Tuple2<T, C>, Tuple2<A, C>, Tuple2<B, C>> = PPrism(
            { (s, c) -> getOrModify(s).bimap({ it toT c }, { it toT c }) },
            { (b, c) -> reverseGet(b) toT c }
    )

    /**
     * Create a product of a type C and the target
     */
    fun <C> second(): PPrism<Tuple2<C, S>, Tuple2<C, T>, Tuple2<C, A>, Tuple2<C, B>> = PPrism(
            { (c, s) -> getOrModify(s).bimap({ c toT it }, { c toT it }) },
            { (c, b) -> c toT reverseGet(b) }
    )

    /**
     * Compose a [Prism] with another [Prism]
     */
    infix fun <C, D> composePrism(other: PPrism<A, B, C, D>): PPrism<S, T, C, D> = Prism(
            { s -> getOrModify(s).flatMap { a -> other.getOrModify(a).bimap({ set(it)(s) }, ::identity) } },
            this::reverseGet compose other::reverseGet
    )

    /** compose an [Iso] as an [Prism] */
    fun <C, D> composeIso(other: PIso<A, B, C, D>): PPrism<S, T, C, D> = composePrism(other.asPrism())

    /**
     * View a [Prism] as an [Optional]
     */
    fun asOptional(): POptional<S, T, A, B> = POptional(this::getOrModify, this::set)

    /**
     * Compose a [Prism] with a [Optional]
     */
    infix fun <C, D> composeOptional(other: POptional<A, B, C, D>): POptional<S, T, C, D> =
            asOptional() composeOptional other

    /**
     * Plus operator overload to compose lenses
     */
    operator fun <C, D> plus(other: PPrism<A, B, C, D>): PPrism<S, T, C, D> = composePrism(other)

    operator fun <C, D> plus(other: POptional<A, B, C, D>): POptional<S, T, C, D> = composeOptional(other)

    operator fun <C, D> plus(other: PIso<A, B, C, D>): PPrism<S, T, C, D> = composeIso(other)

}

/**
 * Create a sum of the target and a type C
 */
fun <S, T, A, B, C> PPrism<S, T, A, B>.left(): PPrism<Either<S, C>, Either<T, C>, Either<A, C>, Either<B, C>> = Prism(
        { it.fold({ a -> getOrModify(a).bimap({ it.left() }, { it.left() }) }, { c -> Either.Right(c.right()) }) },
        {
            when (it) {
                is Either.Left<B, C> -> Either.Left(reverseGet(it.a))
                is Either.Right<B, C> -> Either.Right(it.b)
            }
        }
)

/**
 * Create a sum of a type C and the target
 */
fun <S, T, A, B, C> PPrism<S, T, A, B>.right(): PPrism<Either<C, S>, Either<C, T>, Either<C, A>, Either<C, B>> = Prism(
        { it.fold({ c -> Either.Right(c.left()) }, { s -> getOrModify(s).bimap({ it.right() }, { it.right() }) }) },
        { it.map(this::reverseGet) }
)

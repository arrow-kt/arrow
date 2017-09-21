package kategory.optics

import kategory.Applicative
import kategory.Either
import kategory.Eq
import kategory.HK
import kategory.Monoid
import kategory.Option
import kategory.Tuple2
import kategory.compose
import kategory.eq
import kategory.flatMap
import kategory.getOrElse
import kategory.identity
import kategory.left
import kategory.none
import kategory.right
import kategory.some
import kategory.toT

typealias Prism<S, A> = PPrism<S, S, A, A>

/**
 * A [PPrism] can be seen as a pair of functions: `reverseGet : B -> A` and `getOrModify: A -> Either<A,B>`
 *
 * - `reverseGet : B -> A` get the source type of a [PPrism]
 * - `getOrModify: A -> Either<A,B>` get the target of a [PPrism] or return the original value
 *
 * It encodes the relation between a Sum or CoProduct type (sealed class) and one of its element.
 *
 * @param A the source of a [PPrism]
 * @param B the target of a [PPrism]
 * @property getOrModify from an `B` we can produce an `A`
 * @property reverseGet get the target of a [PPrism] or return the original value
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
         * a [PPrism] that checks for equality with a given value
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
     * Modify the target of a [PPrism] with an Applicative function
     */
    inline fun <reified F> modifyF(FA: Applicative<F> = kategory.applicative(), crossinline f: (A) -> HK<F, B>, s: S): HK<F, T> = getOrModify(s).fold(
            FA::pure,
            { FA.map(f(it), this::reverseGet) }
    )

    /**
     * Modify the target of a [PPrism] with a function
     */
    inline fun modify(s: S, crossinline f: (A) -> B): T = getOrModify(s).fold(::identity, { a -> reverseGet(f(a)) })

    /**
     * Modify the target of a [PPrism] with a function
     */
    inline fun modifyOption(s: S, crossinline f: (A) -> B): Option<T> = getOption(s).map { b -> reverseGet(f(b)) }

    /**
     * Set the target of a [PPrism] with a value
     */
    fun set(s: S, b: B): T = modify(s) { b }

    /**
     * Set the target of a [PPrism] with a value
     */
    fun setOption(s: S, b: B): Option<T> = modifyOption(s) { b }

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
    inline fun find(s: S, crossinline p: (A) -> Boolean): Option<A> = getOption(s).flatMap { a -> if (p(a)) a.some() else none() }

    /**
     * Check if there is a target and it satisfies the predicate
     */
    inline fun exist(s: S, crossinline p: (A) -> Boolean): Boolean = getOption(s).fold({ false }, p)

    /**
     * Check if there is no target or the target satisfies the predicate
     */
    inline fun all(s: S, crossinline p: (A) -> Boolean): Boolean = getOption(s).fold({ true }, p)

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
     * Compose a [PPrism] with another [PPrism]
     */
    infix fun <C, D> compose(other: PPrism<A, B, C, D>): PPrism<S, T, C, D> = Prism(
            { s -> getOrModify(s).flatMap { a -> other.getOrModify(a).bimap({ set(s, it) }, ::identity) } },
            this::reverseGet compose other::reverseGet
    )

    /** compose an [Iso] as an [PPrism] */
    fun <C, D> compose(other: PIso<A, B, C, D>): PPrism<S, T, C, D> = compose(other.asPrism())

    /**
     * Compose a [PPrism] with a [POptional]
     */
    infix fun <C, D> compose(other: POptional<A, B, C, D>): POptional<S, T, C, D> = asOptional() compose other

    /**
     * Compose a [PPrism] with a [PSetter]
     */
    infix fun <C, D> compose(other: PSetter<A, B, C, D>): PSetter<S, T, C, D> = asSetter() compose other

    /**
     * Plus operator overload to compose lenses
     */
    operator fun <C, D> plus(other: PPrism<A, B, C, D>): PPrism<S, T, C, D> = compose(other)

    operator fun <C, D> plus(other: POptional<A, B, C, D>): POptional<S, T, C, D> = compose(other)

    operator fun <C, D> plus(other: PIso<A, B, C, D>): PPrism<S, T, C, D> = compose(other)

    operator fun <C, D> plus(other: PSetter<A, B, C, D>): PSetter<S, T, C, D> = compose(other)

    /**
     * View a [PPrism] as an [POptional]
     */
    fun asOptional(): POptional<S, T, A, B> = POptional(
            this::getOrModify,
            { b -> { s -> set(s, b) } }
    )

    /**
     * View a [PPrism] as a [PSetter]
     */
    fun asSetter(): PSetter<S, T, A, B> = PSetter { f -> { s -> modify(s,f) } }

    /**
     * View a [PPrism] as a [Fold]
     */
    fun asFold(): Fold<S, A> = object : Fold<S, A>() {
        override fun <R> foldMap(M: Monoid<R>, s: S, f: (A) -> R): R = getOption(s).map(f).getOrElse(M::empty)
    }

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

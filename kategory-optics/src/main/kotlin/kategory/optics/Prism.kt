package kategory.optics

import kategory.Applicative
import kategory.Either
import kategory.HK
import kategory.Option
import kategory.Tuple2
import kategory.flatMap
import kategory.identity
import kategory.left
import kategory.right
import kategory.toT

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
abstract class Prism<A, B> {

    abstract fun getOrModify(a: A): Either<A, B>
    abstract fun reverseGet(b: B): A

    companion object {
        operator fun <A,B> invoke(getOrModify: (A) -> Either<A, B>, reverseGet: (B) -> A) = object : Prism<A,B>() {
            override fun getOrModify(a: A): Either<A, B> = getOrModify(a)

            override fun reverseGet(b: B): A = reverseGet(b)
        }
    }

    /**
     * Get the target or nothing if `A` does not match the target
     */
    fun getOption(a: A): Option<B> = getOrModify(a).toOption()

    /**
     * Modify the target of a [Prism] with an Applicative function
     */
    inline fun <reified F> modifyF(FA: Applicative<F> = kategory.applicative(), crossinline f: (B) -> HK<F, B>, a: A): HK<F, A> = getOrModify(a).fold(
            { FA.pure(it) },
            { FA.map(f(it), this::reverseGet) }
    )

    /**
     * Modify the target of a [Prism] with a function
     */
    inline fun modify(crossinline f: (B) -> B): (A) -> A = {
        getOrModify(it).fold(::identity, { reverseGet(f(it)) })
    }

    /**
     * Modify the target of a [Prism] with a function
     */
    inline fun modifyOption(crossinline f: (B) -> B): (A) -> Option<A> = { getOption(it).map { b -> reverseGet(f(b)) } }

    /**
     * Set the target of a [Prism] with a value
     */
    fun set(b: B): (A) -> A = modify { b }

    infix fun <C> composePrism(other: Prism<B, C>): Prism<A, C> = Prism(
            { a -> getOrModify(a).flatMap { b: B -> other.getOrModify(b).bimap({ set(it)(a) }, ::identity) } },
            { reverseGet(other.reverseGet(it)) }
    )

    /**
     * Set the target of a [Prism] with a value
     */
    fun setOption(b: B): (A) -> Option<A> = modifyOption { b }

    /**
     * Check if there is a target
     */
    fun isNotEmpty(a: A): Boolean = getOption(a).isDefined

    /**
     * Check if there is no target
     */
    fun isEmpty(a: A): Boolean = !isNotEmpty(a)

    /**
     * Find if the target satisfies the predicate
     */
    inline fun find(crossinline p: (B) -> Boolean): (A) -> Option<B> = { getOption(it).flatMap { if (p(it)) Option.Some(it) else Option.None } }

    /**
     * Check if there is a target and it satisfies the predicate
     */
    inline fun exist(crossinline p: (B) -> Boolean): (A) -> Boolean = { getOption(it).fold({ false }, p) }

    /**
     * Check if there is no target or the target satisfies the predicate
     */
    inline fun all(crossinline p: (B) -> Boolean): (A) -> Boolean = { getOption(it).fold({ true }, p) }

    /**
     * Convenience method to create a product of the target and a type C
     */
    fun <C> first(): Prism<Tuple2<A, C>, Tuple2<B, C>> = Prism(
            { (a, c) -> getOrModify(a).bimap({ it toT c }, { it toT c }) },
            { (b, c) -> reverseGet(b) toT c }
    )

    /**
     * Convenience method to create a product of a type C and the target
     */
    fun <C> second(): Prism<Tuple2<C, A>, Tuple2<C, B>> = Prism(
            { (c, a) -> getOrModify(a).bimap({ c toT it }, { c toT it }) },
            { (c, b) -> c toT reverseGet(b) }
    )

}

/**
 * Convenience method to create a sum of the target and a type C
 */
fun <A, B, C> Prism<A, B>.left(): Prism<Either<A, C>, Either<B, C>> = Prism(
        { it.fold({ a -> getOrModify(a).bimap({ it.left() }, { it.left() }) }, { c -> Either.Right(c.right()) }) },
        {
            when (it) {
                is Either.Left<B, C> -> Either.Left(reverseGet(it.a))
                is Either.Right<B, C> -> Either.Right(it.b)
            }
        }
)

/**
 * Convenience method to create a sum of a type C and the target
 */
fun <A, B, C> Prism<A, B>.right(): Prism<Either<C, A>, Either<C, B>> = Prism(
        { it.fold({ c -> Either.Right(c.left()) }, { a -> getOrModify(a).bimap({ it.right() }, { it.right() }) }) },
        { it.map(this::reverseGet) }
)

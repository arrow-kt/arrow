package kategory.optics

import kategory.Const
import kategory.Either
import kategory.Foldable
import kategory.HK
import kategory.IntMonoid
import kategory.ListKW
import kategory.Monoid
import kategory.Option
import kategory.foldable
import kategory.identity
import kategory.left
import kategory.monoid
import kategory.none
import kategory.right
import kategory.some

/**
 * A [Fold] is an optic that allows to see into structure and get multiple results.
 *
 * [Fold] is a generalisation of [kategory.Foldable] and is implemented in terms of foldMap.
 *
 * @param S the source of a [Fold]
 * @param A the target of a [Fold]
 */
abstract class Fold<S, A> {

    /**
     * Map each target to a type R and use a Monoid to fold the results
     */
    abstract fun <R> foldMap(M: Monoid<R>, s: S, f: (A) -> R): R

    inline fun <reified R> foldMap(s: S, noinline f: (A) -> R): R = foldMap(monoid(), s, f)

    companion object {

        fun <A> id() = Iso.id<A>().asFold()

        /**
         * [Fold] that takes either [S] or [S] and strips the choice of [S].
         */
        inline fun <reified S> codiagonal() = object : Fold<Either<S, S>, S>() {
            override fun <R> foldMap(M: Monoid<R>, s: Either<S, S>, f: (S) -> R): R = s.fold(f, f)
        }

        /**
         * Creates a [Fold] based on a predicate of the source [S]
         */
        fun <S> select(p: (S) -> Boolean): Fold<S, S> = object : Fold<S, S>() {
            override fun <R> foldMap(M: Monoid<R>, s: S, f: (S) -> R): R = if (p(s)) f(s) else M.empty()
        }

        /**
         * [Fold] that points to nothing
         */
        fun <A, B> void() = Optional.void<A, B>().asFold()

        /**
         * Create a [Fold] from a [kategory.Foldable]
         */
        inline fun <reified F, S> fromFoldable(Foldable: Foldable<F> = foldable()) = object : Fold<HK<F, S>, S>() {
            override fun <R> foldMap(M: Monoid<R>, s: HK<F, S>, f: (S) -> R): R = Foldable.foldMap(M, s, f)
        }

    }

    /**
     * Calculate the number of targets
     */
    fun size(s: S) = foldMap(IntMonoid, s = s, f = { _ -> 1 })

    /**
     * Find the first element matching the predicate, if one exists.
     */
    inline fun find(s: S, crossinline p: (A) -> Boolean): Option<A> =
            foldMap(firstOptionMonoid<A>(), s, { b -> (if (p(b)) Const(b.some()) else Const(none())) }).value

    /**
     * Check whether at least one element satisfies the predicate.
     *
     * If there are no elements, the result is false.
     */
    inline fun exists(s: S, crossinline p: (A) -> Boolean): Boolean = find(s, p).fold({ false }, { true })

    /**
     * Check if all targets satisfy the predicate
     */
    fun forall(s: S, p: (A) -> Boolean): Boolean = foldMap(addMonoid, s, p)

    /**
     * Check if there is no target
     */
    fun isEmpty(s: S): Boolean = foldMap(addMonoid, s, { _ -> false })

    /**
     * Check if there is at least one target
     */
    fun nonEmpty(s: S): Boolean = !isEmpty(s)

    /**
     * Get the first target
     */
    fun headOption(s: S): Option<A> = foldMap(firstOptionMonoid<A>(), s, { b -> Const(b.some()) }).value

    /**
     * Get the last target
     */
    fun lastOption(s: S): Option<A> = foldMap(lastOptionMonoid<A>(), s, { b -> Const(b.some()) }).value

    /**
     * Join two [Fold] with the same target
     */
    fun <C> choice(other: Fold<C, A>): Fold<Either<S, C>, A> = object : Fold<Either<S, C>, A>() {
        override fun <R> foldMap(M: Monoid<R>, s: Either<S, C>, f: (A) -> R): R =
                s.fold({ ac -> this@Fold.foldMap(M, ac, f) }, { c -> other.foldMap(M, c, f) })
    }

    /**
     * Create a sum of the [Fold] and a type [C]
     */
    fun <C> left(): Fold<Either<S, C>, Either<A, C>> = object : Fold<Either<S, C>, Either<A, C>>() {
        override fun <R> foldMap(M: Monoid<R>, s: Either<S, C>, f: (Either<A, C>) -> R): R =
                s.fold({ a1: S -> this@Fold.foldMap(M, a1, { b -> f(b.left()) }) }, { c -> f(c.right()) })
    }

    /**
     * Create a sum of a type [C] and the [Fold]
     */
    fun <C> right(): Fold<Either<C, S>, Either<C, A>> = object : Fold<Either<C, S>, Either<C, A>>() {
        override fun <R> foldMap(M: Monoid<R>, s: Either<C, S>, f: (Either<C, A>) -> R): R =
                s.fold({ c -> f(c.left()) }, { a1 -> this@Fold.foldMap(M, a1, { b -> f(b.right()) }) })
    }

    /**
     * Compose a [Fold] with a [Fold]
     */
    infix fun <C> compose(other: Fold<A, C>): Fold<S, C> = object : Fold<S, C>() {
        override fun <R> foldMap(M: Monoid<R>, s: S, f: (C) -> R): R =
                this@Fold.foldMap(M, s, { c -> other.foldMap(M, c, f) })
    }

    /**
     * Compose a [Fold] with a [Getter]
     */
    infix fun <C> compose(other: Getter<A, C>): Fold<S, C> = compose(other.asFold())

    /**
     * Compose a [Fold] with a [Optional]
     */
    infix fun <C> compose(other: Optional<A, C>): Fold<S, C> = compose(other.asFold())

    /**
     * Compose a [[Fold]] with a [Prism]
     */
    infix fun <C> compose(other: Prism<A, C>): Fold<S, C> = compose(other.asFold())

    /**
     * Compose a [Fold] with a [Lens]
     */
    infix fun <C> compose(other: Lens<A, C>): Fold<S, C> = compose(other.asFold())

    /**
     * Compose a [Fold] with a [Iso]
     */
    infix fun <C> compose(other: Iso<A, C>): Fold<S, C> = compose(other.asFold())

    /**
     * Plus operator  overload to compose lenses
     */
    operator fun <C> plus(other: Fold<A, C>): Fold<S, C> = compose(other)

    operator fun <C> plus(other: Optional<A, C>): Fold<S, C> = compose(other)

    operator fun <C> plus(other: Getter<A, C>): Fold<S, C> = compose(other)

    operator fun <C> plus(other: Prism<A, C>): Fold<S, C> = compose(other)

    operator fun <C> plus(other: Lens<A, C>): Fold<S, C> = compose(other)

    operator fun <C> plus(other: Iso<A, C>): Fold<S, C> = compose(other)
}

/**
 * Fold using the given [Monoid] instance.
 */
inline fun <A, reified B> Fold<A, B>.fold(M: Monoid<B> = monoid(), a: A): B = foldMap(M, a, ::identity)

/**
 * Alias for fold.
 */
inline fun <A, reified B> Fold<A, B>.combineAll(M: Monoid<B> = monoid(), a: A): B = foldMap(M, a, ::identity)

/**
 * Get all targets of the [Fold]
 */
inline fun <A, reified B> Fold<A, B>.getAll(M: Monoid<ListKW<B>> = monoid(), a: A): ListKW<B> = foldMap(M, a, { ListKW.pure(it) })

internal val addMonoid = object : Monoid<Boolean> {
    override fun combine(a: Boolean, b: Boolean): Boolean = a && b

    override fun empty(): Boolean = true
}

internal sealed class First
internal sealed class Last

@PublishedApi internal fun <A> firstOptionMonoid() = object : Monoid<Const<Option<A>, First>> {

    override fun empty() = Const<Option<A>, First>(Option.None)

    override fun combine(a: Const<Option<A>, First>, b: Const<Option<A>, First>) =
            if (a.value.fold({ false }, { true })) a else b

}

internal fun <A> lastOptionMonoid() = object : Monoid<Const<Option<A>, Last>> {

    override fun empty() = Const<Option<A>, Last>(Option.None)

    override fun combine(a: Const<Option<A>, Last>, b: Const<Option<A>, Last>) =
            if (b.value.fold({ false }, { true })) b else a

}
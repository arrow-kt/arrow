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
 * [Fold] is a generalisation of [kategory.Foldable] and can be seen as a representation of foldMap,
 * forall methods are defined in terms of foldMap.
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
         * [Fold] that takes either S or S and strips the choice of A.
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
    fun length(s: S) = foldMap(IntMonoid, s = s, f = { _ -> 1 })

    /**
     * Find the first target matching the predicate
     */
    inline fun find(s: S, crossinline p: (A) -> Boolean): Option<A> =
            foldMap(firstOptionMonoid<A>(), s, { b -> (if (p(b)) Const(b.some()) else Const(none())) }).value

    /**
     * Get the first target
     */
    fun headOption(s: S): Option<A> = foldMap(firstOptionMonoid<A>(), s, { b -> Const(b.some()) }).value

    /**
     * Get the last target
     */
    fun lastOption(s: S): Option<A> = foldMap(lastOptionMonoid<A>(), s, { b -> Const(b.some()) }).value

    /**
     * Check if forall targets satisfy the predicate
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
     * Join two [Fold] with the same target
     */
    fun <C> choice(other: Fold<C, A>): Fold<Either<S, C>, A> = object : Fold<Either<S, C>, A>() {
        override fun <R> foldMap(M: Monoid<R>, s: Either<S, C>, f: (A) -> R): R =
                s.fold({ ac -> this@Fold.foldMap(M, ac, f) }, { c -> other.foldMap(M, c, f) })
    }

    fun <C> left(): Fold<Either<S, C>, Either<A, C>> = object : Fold<Either<S, C>, Either<A, C>>() {
        override fun <R> foldMap(M: Monoid<R>, s: Either<S, C>, f: (Either<A, C>) -> R): R =
                s.fold({ a1: S -> this@Fold.foldMap(M, a1, { b -> f(b.left()) }) }, { c -> f(c.right()) })
    }

    fun <C> right(): Fold<Either<C, S>, Either<C, A>> = object : Fold<Either<C, S>, Either<C, A>>() {
        override fun <R> foldMap(M: Monoid<R>, s: Either<C, S>, f: (Either<C, A>) -> R): R =
                s.fold({ c -> f(c.left()) }, { a1 -> this@Fold.foldMap(M, a1, { b -> f(b.right()) }) })
    }

    /**
     * Compose a [Fold] with a [Fold]
     */
    infix fun <C> composeFold(other: Fold<A, C>): Fold<S, C> = object : Fold<S, C>() {
        override fun <R> foldMap(M: Monoid<R>, s: S, f: (C) -> R): R =
                this@Fold.foldMap(M, s, { c -> other.foldMap(M, c, f) })
    }

    /**
     * Compose a [Fold] with a [Getter]
     */
    infix fun <C> composeGetter(other: Getter<A, C>): Fold<S, C> = composeFold(other.asFold())

    /**
     * Compose a [Fold] with a [Optional]
     */
    infix fun <C> composeOptional(other: Optional<A, C>): Fold<S, C> = composeFold(other.asFold())

    /**
     * Compose a [[Fold]] with a [Prism]
     */
    infix fun <C> composePrism(other: Prism<A, C>): Fold<S, C> = composeFold(other.asFold())

    /**
     * Compose a [Fold] with a [Lens]
     */
    infix fun <C> composeLens(other: Lens<A, C>): Fold<S, C> = composeFold(other.asFold())

    /**
     * Compose a [Fold] with a [Iso]
     */
    infix fun <C> composeIso(other: Iso<A, C>): Fold<S, C> = composeFold(other.asFold())

    /**
     * Plus operator  overload to compose lenses
     */
    operator fun <C> plus(other: Fold<A, C>): Fold<S, C> = composeFold(other)

    operator fun <C> plus(other: Optional<A, C>): Fold<S, C> = composeOptional(other)

    operator fun <C> plus(other: Getter<A, C>): Fold<S, C> = composeGetter(other)

    operator fun <C> plus(other: Prism<A, C>): Fold<S, C> = composePrism(other)

    operator fun <C> plus(other: Lens<A, C>): Fold<S, C> = composeLens(other)

    operator fun <C> plus(other: Iso<A, C>): Fold<S, C> = composeIso(other)
}

inline fun <A, reified B> Fold<A, B>.fold(M: Monoid<B> = monoid(), a: A): B = foldMap(M, a, ::identity)

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
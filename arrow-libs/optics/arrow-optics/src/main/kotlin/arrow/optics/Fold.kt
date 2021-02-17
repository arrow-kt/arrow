package arrow.optics

import arrow.core.Either
import arrow.core.boolean
import arrow.core.identity
import arrow.core.int
import arrow.core.list
import arrow.typeclasses.Foldable
import arrow.typeclasses.Monoid

/**
 * A [Fold] is an optic that allows to focus into structure and get multiple results.
 *
 * [Fold] is a generalisation of an instance of [Foldable] and is implemented in terms of foldMap.
 *
 * @param S the source of a [Fold]
 * @param A the target of a [Fold]
 */
interface Fold<S, A> {

  /**
   * Map each target to a type R and use a Monoid to fold the results
   */
  fun <R> foldMap(M: Monoid<R>, source: S, map: (focus: A) -> R): R

  /**
   * Calculate the number of targets
   */
  fun size(source: S) =
    foldMap(Monoid.int(), source) { 1 }

  /**
   * Check if all targets satisfy the predicate
   */
  fun all(source: S, predicate: (focus: A) -> Boolean): Boolean =
    foldMap(Monoid.boolean(), source, predicate)

  fun forall(source: S, predicate: (focus: A) -> Boolean): Boolean =
    all(source, predicate)

  /**
   * Returns `true` if at least one focus matches the given [predicate].
   */
  fun any(source: S, predicate: (focus: A) -> Boolean): Boolean =
    foldMap(Monoid.booleanOr(), source, predicate)

  /**
   * Check if there is no target
   */
  fun isEmpty(source: S): Boolean =
    foldMap(Monoid.boolean(), source) { false }

  /**
   * Check if there is at least one target
   */
  fun isNotEmpty(source: S): Boolean =
    !isEmpty(source)

  /**
   * Get the first target or null
   */
  fun firstOrNull(source: S): A? =
    foldMap(firstOptionMonoid<A>(), source, ::identity)

  /**
   * Get the last target or null
   */ // TODO FIX
  fun lastOrNull(source: S): A? =
    foldMap(lastOptionMonoid<A>(), source, ::identity)

  /**
   * Fold using the given [Monoid] instance.
   */
  fun fold(M: Monoid<A>, source: S): A =
    foldMap(M, source, ::identity)

  /**
   * Alias for fold.
   */
  fun combineAll(M: Monoid<A>, source: S): A =
    foldMap(M, source, ::identity)

  /**
   * Get all targets of the [Fold]
   */
  fun getAll(source: S): List<A> =
    foldMap(Monoid.list(), source, ::listOf)

  /**
   * Find the first element matching the predicate, if one exists.
   */
  fun findOrNull(source: S, predicate: (focus: A) -> Boolean): A? =
    foldMap(firstOptionMonoid<A>(), source) { b -> if (predicate(b)) b else null }

  /**
   * Check whether at least one element satisfies the predicate.
   *
   * If there are no elements, the result is false.
   */
  fun exists(source: S, predicate: (focus: A) -> Boolean): Boolean =
    findOrNull(source, predicate)?.let { true } ?: false

  /**
   * Join two [Fold] with the same target
   */
  infix fun <C> choice(other: Fold<C, A>): Fold<Either<S, C>, A> =
    object : Fold<Either<S, C>, A> {
      override fun <R> foldMap(M: Monoid<R>, source: Either<S, C>, map: (focus: A) -> R): R =
        source.fold({ ac -> this@Fold.foldMap(M, ac, map) }, { c -> other.foldMap(M, c, map) })
    }

  /**
   * Create a sum of the [Fold] and a type [C]
   */
  fun <C> left(): Fold<Either<S, C>, Either<A, C>> =
    object : Fold<Either<S, C>, Either<A, C>> {
      override fun <R> foldMap(M: Monoid<R>, source: Either<S, C>, map: (Either<A, C>) -> R): R =
        source.fold({ a1: S -> this@Fold.foldMap(M, a1) { b -> map(Either.Left(b)) } }, { c -> map(Either.Right(c)) })
    }

  /**
   * Create a sum of a type [C] and the [Fold]
   */
  fun <C> right(): Fold<Either<C, S>, Either<C, A>> =
    object : Fold<Either<C, S>, Either<C, A>> {
      override fun <R> foldMap(M: Monoid<R>, source: Either<C, S>, map: (Either<C, A>) -> R): R =
        source.fold({ c -> map(Either.Left(c)) }, { a1 -> this@Fold.foldMap(M, a1) { b -> map(Either.Right(b)) } })
    }

  /**
   * Compose a [Fold] with a [Fold]
   */
  infix fun <C> compose(other: Fold<A, C>): Fold<S, C> =
    object : Fold<S, C> {
      override fun <R> foldMap(M: Monoid<R>, source: S, map: (focus: C) -> R): R =
        this@Fold.foldMap(M, source) { c -> other.foldMap(M, c, map) }
    }

  operator fun <C> plus(other: Fold<A, C>): Fold<S, C> =
    this compose other

  companion object {

    fun <A> id(): Fold<A, A> =
      PIso.id()

    /**
     * [Fold] that takes either [S] or [S] and strips the choice of [S].
     */
    fun <S> codiagonal() = object : Fold<Either<S, S>, S> {
      override fun <R> foldMap(M: Monoid<R>, source: Either<S, S>, map: (S) -> R): R =
        source.fold(map, map)
    }

    /**
     * Creates a [Fold] based on a predicate of the source [S]
     */
    fun <S> select(p: (S) -> Boolean): Fold<S, S> = object : Fold<S, S> {
      override fun <R> foldMap(M: Monoid<R>, source: S, map: (S) -> R): R =
        if (p(source)) map(source) else M.empty()
    }

    /**
     * [Fold] that points to nothing
     */
    fun <A, B> void(): Fold<A, B> =
      POptional.void()
  }
}

package arrow.optics

import arrow.Kind
import arrow.core.Const
import arrow.core.Either
import arrow.core.ListK
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.extensions.listk.monoid.monoid
import arrow.core.extensions.monoid
import arrow.core.identity
import arrow.higherkind
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
@higherkind
interface Fold<S, A> : FoldOf<S, A> {

  /**
   * Map each target to a type R and use a Monoid to fold the results
   */
  fun <R> foldMap(M: Monoid<R>, s: S, f: (A) -> R): R

  companion object {

    fun <A> id() = PIso.id<A>().asFold()

    /**
     * [Fold] that takes either [S] or [S] and strips the choice of [S].
     */
    fun <S> codiagonal() = object : Fold<Either<S, S>, S> {
      override fun <R> foldMap(M: Monoid<R>, s: Either<S, S>, f: (S) -> R): R = s.fold(f, f)
    }

    /**
     * Creates a [Fold] based on a predicate of the source [S]
     */
    fun <S> select(p: (S) -> Boolean): Fold<S, S> = object : Fold<S, S> {
      override fun <R> foldMap(M: Monoid<R>, s: S, f: (S) -> R): R = if (p(s)) f(s) else M.empty()
    }

    /**
     * [Fold] that points to nothing
     */
    fun <A, B> void() = POptional.void<A, B>().asFold()

    /**
     * Create a [Fold] from a [arrow.Foldable]
     */
    fun <F, S> fromFoldable(foldable: Foldable<F>) = object : Fold<Kind<F, S>, S> {
      override fun <R> foldMap(M: Monoid<R>, s: Kind<F, S>, f: (S) -> R): R = foldable.run { s.foldMap(M, f) }
    }
  }

  /**
   * Calculate the number of targets
   */
  fun size(s: S) = foldMap(Int.monoid(), s = s, f = { _ -> 1 })

  /**
   * Check if all targets satisfy the predicate
   */
  fun forall(s: S, p: (A) -> Boolean): Boolean = foldMap(AndMonoid, s, p)

  /**
   * Check if there is no target
   */
  fun isEmpty(s: S): Boolean = foldMap(AndMonoid, s) { _ -> false }

  /**
   * Check if there is at least one target
   */
  fun nonEmpty(s: S): Boolean = !isEmpty(s)

  /**
   * Get the first target
   */
  fun headOption(s: S): Option<A> = foldMap(firstOptionMonoid<A>(), s) { b -> Const(Some(b)) }.value()

  /**
   * Get the last target
   */
  fun lastOption(s: S): Option<A> = foldMap(lastOptionMonoid<A>(), s) { b -> Const(Some(b)) }.value()

  /**
   * Fold using the given [Monoid] instance.
   */
  fun fold(M: Monoid<A>, s: S): A = foldMap(M, s, ::identity)

  /**
   * Alias for fold.
   */
  fun combineAll(M: Monoid<A>, s: S): A = foldMap(M, s, ::identity)

  /**
   * Get all targets of the [Fold]
   */
  fun getAll(s: S): ListK<A> = foldMap(ListK.monoid(), s) { ListK.just(it) }

  /**
   * Join two [Fold] with the same target
   */
  infix fun <C> choice(other: Fold<C, A>): Fold<Either<S, C>, A> = object : Fold<Either<S, C>, A> {
    override fun <R> foldMap(M: Monoid<R>, s: Either<S, C>, f: (A) -> R): R =
      s.fold({ ac -> this@Fold.foldMap(M, ac, f) }, { c -> other.foldMap(M, c, f) })
  }

  /**
   * Create a sum of the [Fold] and a type [C]
   */
  fun <C> left(): Fold<Either<S, C>, Either<A, C>> = object : Fold<Either<S, C>, Either<A, C>> {
    override fun <R> foldMap(M: Monoid<R>, s: Either<S, C>, f: (Either<A, C>) -> R): R =
      s.fold({ a1: S -> this@Fold.foldMap(M, a1) { b -> f(Either.Left(b)) } }, { c -> f(Either.Right(c)) })
  }

  /**
   * Create a sum of a type [C] and the [Fold]
   */
  fun <C> right(): Fold<Either<C, S>, Either<C, A>> = object : Fold<Either<C, S>, Either<C, A>> {
    override fun <R> foldMap(M: Monoid<R>, s: Either<C, S>, f: (Either<C, A>) -> R): R =
      s.fold({ c -> f(Either.Left(c)) }, { a1 -> this@Fold.foldMap(M, a1) { b -> f(Either.Right(b)) } })
  }

  /**
   * Compose a [Fold] with a [Fold]
   */
  infix fun <C> compose(other: Fold<A, C>): Fold<S, C> = object : Fold<S, C> {
    override fun <R> foldMap(M: Monoid<R>, s: S, f: (C) -> R): R =
      this@Fold.foldMap(M, s) { c -> other.foldMap(M, c, f) }
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
   * Compose a [Fold] with a [Prism]
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
   * Compose a [Fold] with a [Traversal]
   */
  infix fun <C> compose(other: Traversal<A, C>): Fold<S, C> = compose(other.asFold())

  /**
   * Plus operator  overload to compose lenses
   */
  operator fun <C> plus(other: Fold<A, C>): Fold<S, C> = compose(other)

  operator fun <C> plus(other: Optional<A, C>): Fold<S, C> = compose(other)

  operator fun <C> plus(other: Getter<A, C>): Fold<S, C> = compose(other)

  operator fun <C> plus(other: Prism<A, C>): Fold<S, C> = compose(other)

  operator fun <C> plus(other: Lens<A, C>): Fold<S, C> = compose(other)

  operator fun <C> plus(other: Iso<A, C>): Fold<S, C> = compose(other)

  operator fun <C> plus(other: Traversal<A, C>): Fold<S, C> = compose(other)

  /**
   * Find the first element matching the predicate, if one exists.
   */
  fun find(s: S, p: (A) -> Boolean): Option<A> =
    foldMap(firstOptionMonoid<A>(), s) { b -> (if (p(b)) Const(Some(b)) else Const(None)) }.value()

  /**
   * Check whether at least one element satisfies the predicate.
   *
   * If there are no elements, the result is false.
   */
  fun exists(s: S, p: (A) -> Boolean): Boolean = find(s, p).fold({ false }, { true })
}

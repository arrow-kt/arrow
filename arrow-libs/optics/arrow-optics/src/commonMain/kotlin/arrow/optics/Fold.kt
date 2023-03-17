package arrow.optics

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.Option
import arrow.core.Tuple10
import arrow.core.Tuple4
import arrow.core.Tuple5
import arrow.core.Tuple6
import arrow.core.Tuple7
import arrow.core.Tuple8
import arrow.core.Tuple9
import arrow.core.foldMap
import arrow.core.identity
import arrow.typeclasses.Monoid
import arrow.typeclasses.MonoidDeprecation
import kotlin.jvm.JvmStatic

/**
 * A [Fold] is an optic that allows to focus into structure and get multiple results.
 *
 * [Fold] is a generalisation of an instance of [Foldable] and is implemented in terms of foldMap.
 *
 * @param S the source of a [Fold]
 * @param A the target of a [Fold]
 */
public interface Fold<S, A> {

  /**
   * Map each target to a type R and use a Monoid to fold the results
   */
  @Deprecated(MonoidDeprecation, ReplaceWith("foldMap(M.empty(), M::combine, source, map)", "arrow.optics.foldMap", "arrow.typeclasses.combine"))
  public fun <R> foldMap(M: Monoid<R>, source: S, map: (focus: A) -> R): R

  /**
   * Calculate the number of targets
   */
  public fun size(source: S): Int =
    foldMap(0, { x, y -> x + y }, source) { 1 }

  /**
   * Check if all targets satisfy the predicate
   */
  public fun all(source: S, predicate: (focus: A) -> Boolean): Boolean =
    foldMap(true, { x, y -> x && y }, source, predicate)

  /**
   * Returns `true` if at least one focus matches the given [predicate].
   */
  public fun any(source: S, predicate: (focus: A) -> Boolean): Boolean =
    foldMap(false, { x, y -> x || y }, source, predicate)

  /**
   * Check if there is no target
   */
  public fun isEmpty(source: S): Boolean =
    foldMap(true, { _, _ -> false }, source) { false }

  /**
   * Check if there is at least one target
   */
  public fun isNotEmpty(source: S): Boolean =
    !isEmpty(source)

  /**
   * Get the first target or null
   */
  public fun firstOrNull(source: S): A? =
    EMPTY_VALUE.unbox(foldMap(EMPTY_VALUE, { x, y -> if (x === EMPTY_VALUE) y else x }, source, ::identity))

  /**
   * Get the last target or null
   */
  public fun lastOrNull(source: S): A? =
    EMPTY_VALUE.unbox(foldMap(EMPTY_VALUE, { x, y -> if (y != EMPTY_VALUE) y else x }, source, ::identity))

  /**
   * Fold using the given [Monoid] instance.
   */
  @Deprecated(MonoidDeprecation, ReplaceWith("fold(M.empty(), M::combine, source)", "arrow.optics.fold", "arrow.typeclasses.combine"))
  public fun fold(M: Monoid<A>, source: S): A =
    foldMap(M, source, ::identity)

  /**
   * Alias for fold.
   */
  @Deprecated("use fold instead", ReplaceWith("fold(M, source)"))
  public fun combineAll(M: Monoid<A>, source: S): A =
    fold(M, source)

  /**
   * Get all targets of the [Fold]
   */
  public fun getAll(source: S): List<A> =
    foldMap(emptyList(), { x, y -> x + y }, source) { listOf(it) }

  /**
   * Find the first element matching the predicate, if one exists.
   */
  public fun findOrNull(source: S, predicate: (focus: A) -> Boolean): A? =
    EMPTY_VALUE.unbox(
      foldMap<S, A, Any?>(EMPTY_VALUE, { x, y -> if (x == EMPTY_VALUE) y else x }, source) { focus ->
        if (predicate(focus)) focus else EMPTY_VALUE
      }
    )

  /**
   * Check whether at least one element satisfies the predicate.
   *
   * If there are no elements, the result is false.
   */
  public fun exists(source: S, predicate: (focus: A) -> Boolean): Boolean =
    any(source, predicate)

  /**
   * Join two [Fold] with the same target
   */
  public infix fun <C> choice(other: Fold<C, A>): Fold<Either<S, C>, A> =
    object : Fold<Either<S, C>, A> {
      override fun <R> foldMap(M: Monoid<R>, source: Either<S, C>, map: (focus: A) -> R): R =
        source.fold({ ac -> this@Fold.foldMap(M, ac, map) }, { c -> other.foldMap(M, c, map) })
    }

  /**
   * Create a sum of the [Fold] and a type [C]
   */
  public fun <C> left(): Fold<Either<S, C>, Either<A, C>> =
    object : Fold<Either<S, C>, Either<A, C>> {
      override fun <R> foldMap(M: Monoid<R>, source: Either<S, C>, map: (Either<A, C>) -> R): R =
        source.fold(
          { a1: S -> this@Fold.foldMap(M, a1) { b -> map(Either.Left(b)) } },
          { c -> map(Either.Right(c)) })
    }

  /**
   * Create a sum of a type [C] and the [Fold]
   */
  public fun <C> right(): Fold<Either<C, S>, Either<C, A>> =
    object : Fold<Either<C, S>, Either<C, A>> {
      override fun <R> foldMap(M: Monoid<R>, source: Either<C, S>, map: (Either<C, A>) -> R): R =
        source.fold({ c -> map(Either.Left(c)) }, { a1 -> this@Fold.foldMap(M, a1) { b -> map(Either.Right(b)) } })
    }

  /**
   * Compose a [Fold] with a [Fold]
   */
  public infix fun <C> compose(other: Fold<in A, out C>): Fold<S, C> =
    object : Fold<S, C> {
      override fun <R> foldMap(M: Monoid<R>, source: S, map: (focus: C) -> R): R =
        this@Fold.foldMap(M, source) { c -> other.foldMap(M, c, map) }
    }

  public operator fun <C> plus(other: Fold<in A, out C>): Fold<S, C> =
    this compose other

  public companion object {

    public fun <A> id(): Fold<A, A> =
      PIso.id()

    /**
     * [Fold] that takes either [S] or [S] and strips the choice of [S].
     */
    public fun <S> codiagonal(): Fold<Either<S, S>, S> = object : Fold<Either<S, S>, S> {
      override fun <R> foldMap(M: Monoid<R>, source: Either<S, S>, map: (S) -> R): R =
        source.fold(map, map)
    }

    /**
     * Creates a [Fold] based on a predicate of the source [S]
     */
    public fun <S> select(p: (S) -> Boolean): Fold<S, S> = object : Fold<S, S> {
      override fun <R> foldMap(M: Monoid<R>, source: S, map: (S) -> R): R =
        if (p(source)) map(source) else M.empty()
    }

    /**
     * [Fold] that points to nothing
     */
    public fun <A, B> void(): Fold<A, B> =
      POptional.void()

    @JvmStatic
    public fun <A> iterable(): Fold<Iterable<A>, A> =
      object : Fold<Iterable<A>, A> {
        override fun <R> foldMap(M: Monoid<R>, source: Iterable<A>, map: (focus: A) -> R): R =
          source.foldMap(M, map)
      }

    /**
     * [Traversal] for [List] that focuses in each [A] of the source [List].
     */
    @JvmStatic
    public fun <A> list(): Fold<List<A>, A> =
      Every.list()

    /**
     * [Traversal] for [Either] that has focus in each [Either.Right].
     *
     * @return [Traversal] with source [Either] and focus every [Either.Right] of the source.
     */
    @JvmStatic
    public fun <L, R> either(): Fold<Either<L, R>, R> =
      Every.either()

    @JvmStatic
    public fun <K, V> map(): Fold<Map<K, V>, V> =
      Every.map()

    /**
     * [Traversal] for [NonEmptyList] that has focus in each [A].
     *
     * @receiver [PTraversal.Companion] to make it statically available.
     * @return [Traversal] with source [NonEmptyList] and focus every [A] of the source.
     */
    @JvmStatic
    public fun <A> nonEmptyList(): Fold<NonEmptyList<A>, A> =
      Every.nonEmptyList()

    /**
     * [Traversal] for [Option] that has focus in each [arrow.core.Some].
     *
     * @receiver [PTraversal.Companion] to make it statically available.
     * @return [Traversal] with source [Option] and focus in every [arrow.core.Some] of the source.
     */
    @JvmStatic
    public fun <A> option(): Fold<Option<A>, A> =
      Every.option()

    @JvmStatic
    public fun <A> sequence(): Fold<Sequence<A>, A> =
      Every.sequence()

    /**
     * [Traversal] for [String] that focuses in each [Char] of the source [String].
     *
     * @receiver [PTraversal.Companion] to make it statically available.
     * @return [Traversal] with source [String] and foci every [Char] in the source.
     */
    @JvmStatic
    public fun string(): Fold<String, Char> =
      Every.string()

    /**
     * [Traversal] to focus into the first and second value of a [Pair]
     */
    @JvmStatic
    public fun <A> pair(): Fold<Pair<A, A>, A> =
      Every.pair()

    /**
     * [Traversal] to focus into the first, second and third value of a [Triple]
     */
    @JvmStatic
    public fun <A> triple(): Fold<Triple<A, A, A>, A> =
      Every.triple()

    /**
     * [Traversal] to focus into the first, second, third and fourth value of a [arrow.core.Tuple4]
     */
    @JvmStatic
    public fun <A> tuple4(): Fold<Tuple4<A, A, A, A>, A> =
      Every.tuple4()

    /**
     * [PTraversal] to focus into the first, second, third, fourth and fifth value of a [arrow.core.Tuple5]
     */
    @JvmStatic
    public fun <A> tuple5(): Fold<Tuple5<A, A, A, A, A>, A> =
      Every.tuple5()

    /**
     * [Traversal] to focus into the first, second, third, fourth, fifth and sixth value of a [arrow.core.Tuple6]
     */
    @JvmStatic
    public fun <A> tuple6(): Fold<Tuple6<A, A, A, A, A, A>, A> =
      Every.tuple6()

    /**
     * [Traversal] to focus into the first, second, third, fourth, fifth, sixth and seventh value of a [arrow.core.Tuple7]
     */
    @JvmStatic
    public fun <A> tuple7(): Fold<Tuple7<A, A, A, A, A, A, A>, A> =
      Every.tuple7()

    /**
     * [Traversal] to focus into the first, second, third, fourth, fifth, sixth, seventh and eight value of a [arrow.core.Tuple8]
     */
    @JvmStatic
    public fun <A> tuple8(): Fold<Tuple8<A, A, A, A, A, A, A, A>, A> =
      Every.tuple8()

    /**
     * [Traversal] to focus into the first, second, third, fourth, fifth, sixth, seventh, eight and ninth value of a [arrow.core.Tuple9]
     */
    @JvmStatic
    public fun <A> tuple9(): Fold<Tuple9<A, A, A, A, A, A, A, A, A>, A> =
      Every.tuple9()

    /**
     * [Traversal] to focus into the first, second, third, fourth, fifth, sixth, seventh, eight, ninth and tenth value of a [arrow.core.Tuple10]
     */
    @JvmStatic
    public fun <A> tuple10(): Fold<Tuple10<A, A, A, A, A, A, A, A, A, A>, A> =
      Every.tuple10()
  }
}

/**
 * Fold using the given [empty] element and [combine].
 */
public fun <S, A> Fold<S, A>.fold(empty: A, combine: (A, A) -> A, source: S): A =
  foldMap(empty, combine, source, ::identity)

/**
 * Map each target to a type [R] and combine the results as a fold.
 */
public fun <S, A, R> Fold<S, A>.foldMap(empty: R, combine: (R, R) -> R, source: S, map: (focus: A) -> R): R =
  foldMap(object : Monoid<R> {
    override fun empty(): R = empty
    override fun R.combine(b: R): R = combine(this, b)
  }, source, map)

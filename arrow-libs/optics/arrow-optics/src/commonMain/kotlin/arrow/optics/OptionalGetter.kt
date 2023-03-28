package arrow.optics

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.flatMap
import arrow.core.getOrElse
import arrow.typeclasses.Monoid
import arrow.typeclasses.MonoidDeprecation
import kotlin.jvm.JvmStatic

/**
 * [OptionalGetter] is a type alias for [POptionalGetter] which fixes the type arguments
 * and restricts the [POptionalGetter] to monomorphic updates.
 */
public typealias OptionalGetter<S, A> = POptionalGetter<S, S, A>

public fun <S, A> OptionalGetter(getOption: (source: S) -> Option<A>): OptionalGetter<S, A> =
  POptionalGetter { s -> getOption(s).toEither { s } }

/**
 * An [OptionalGetter] is an optic that allows into a structure and querying an optional focus.
 *
 * @param S the source of a [POptional]
 * @param T the modified source of a [POptional]
 * @param A the focus of a [POptional]
 */
public interface POptionalGetter<S, T, A>: Fold<S, A> {
  /**
   * Get the focus of an [OptionalGetter] or return the original value while allowing the type to change if it does not match
   */
  public fun getOrModify(source: S): Either<T, A>

  /**
   * Get the focus of an [OptionalGetter] or `null` if the is not there
   */
  public fun getOrNull(source: S): A? =
    getOrModify(source).getOrNull()

  @Deprecated(MonoidDeprecation, ReplaceWith("foldMap(empty, {r1, r2 -> r1 + r2}, source, map)", "arrow.optics.foldMap"))
  override fun <R> foldMap(M: Monoid<R>, source: S, map: (focus: A) -> R): R =
    getOrModify(source).map(map).getOrElse { M.empty() }

  override fun <R> foldMap(empty: R, combine: (R, R) -> R, source: S, map: (focus: A) -> R): R =
    getOrModify(source).map(map).getOrElse { empty }

  /**
   * Join two [POptionalGetter] with the same focus
   */
  public infix fun <S1, T1> choice(other: POptionalGetter<S1, T1, A>): POptionalGetter<Either<S, S1>, Either<T, T1>, A> =
    POptionalGetter { sources ->
      sources.fold(
        { leftSource ->
          getOrModify(leftSource).mapLeft { Either.Left(it) }
        },
        { rightSource ->
          other.getOrModify(rightSource).mapLeft { Either.Right(it) }
        }
      )
    }

  /**
   * Create a product of the [POptionalGetter] and a type [C]
   */
  public fun <C> first(): POptionalGetter<Pair<S, C>, Pair<T, C>, Pair<A, C>> =
    POptionalGetter { (source, c) -> getOrModify(source).mapLeft { Pair(it, c) }.map { Pair(it, c) } }

  /**
   * Create a product of a type [C] and the [POptionalGetter]
   */
  public fun <C> second(): POptionalGetter<Pair<C, S>, Pair<C, T>, Pair<C, A>> =
    POptionalGetter { (c, s) -> getOrModify(s).mapLeft { c to it }.map { c to it } }

  /**
   * Compose a [POptionalGetter] with a [POptionalGetter]
   */
  public infix fun <C> compose(other: POptionalGetter<in A, T, out C>): POptionalGetter<S, T, C> =
    POptionalGetter { source ->
      getOrModify(source).flatMap { a ->
        other.getOrModify(a)
      }
    }

  public operator fun <C, D> plus(other: POptionalGetter<in A, T, out C>): POptionalGetter<S, T, C> =
    this compose other

  public companion object {
    /**
     * Invoke operator overload to create an [OptionalGetter] of type `S` with focus `A`.
     */
    public operator fun <S, T, A> invoke(
      getOrModify: (source: S) -> Either<T, A>
    ): POptionalGetter<S, T, A> = object : POptionalGetter<S, T, A> {
      override fun getOrModify(source: S): Either<T, A> = getOrModify(source)
    }

    public fun <S> id(): PIso<S, S, S, S> = PIso.id()

    /**
     * [OptionalGetter] to itself if it satisfies the predicate.
     *
     * Select all the elements which satisfy the predicate.
     *
     * ```kotlin
     * import arrow.optics.Traversal
     * import arrow.optics.Optional
     *
     * val positiveNumbers = Traversal.list<Int>() compose OptionalGetter.filter { it >= 0 }
     *
     * positiveNumbers.getAll(listOf(1, 2, -3, 4, -5)) == listOf(1, 2, 4)
     * ```
     */
    @JvmStatic
    public fun <A> filter(predicate: (A) -> Boolean): OptionalGetter<A, A> =
      OptionalGetter(
        getOption = { if (predicate(it)) Some(it) else None }
      )
  }
}


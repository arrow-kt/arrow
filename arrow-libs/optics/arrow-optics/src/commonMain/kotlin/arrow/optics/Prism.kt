package arrow.optics

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.compose
import arrow.core.flatMap
import arrow.core.identity
import arrow.typeclasses.Monoid
import kotlin.jvm.JvmStatic

/**
 * [Prism] is a type alias for [PPrism] which fixes the type arguments
 * and restricts the [PPrism] to monomorphic updates.
 */
public typealias Prism<S, A> = PPrism<S, S, A, A>

/**
 * A [Prism] is a loss less invertible optic that can look into a structure and optionally find its focus.
 * Mostly used for finding a focus that is only present under certain conditions i.e. list head Prism<List<Int>, Int>
 *
 * A (polymorphic) [PPrism] is useful when setting or modifying a value for a polymorphic sum type
 * i.e. PPrism<Option<String>, Option<Int>, String, Int>
 *
 * A [PPrism] gathers the two concepts of pattern matching and constructor and thus can be seen as a pair of functions:
 * - `getOrModify: A -> Either<A, B>` meaning it returns the focus of a [PPrism] OR the original value
 * - `reverseGet : B -> A` meaning we can construct the source type of a [PPrism] from a focus `B`
 *
 * @param S the source of a [PPrism]
 * @param T the modified source of a [PPrism]
 * @param A the focus of a [PPrism]
 * @param B the modified focus of a [PPrism]
 */
public interface PPrism<S, T, A, B> : POptional<S, T, A, B>, PSetter<S, T, A, B>, Fold<S, A>, PTraversal<S, T, A, B>, PEvery<S, T, A, B> {

  override fun getOrModify(source: S): Either<T, A>

  public fun reverseGet(focus: B): T

  override fun <R> foldMap(M: Monoid<R>, source: S, map: (focus: A) -> R): R =
    getOrNull(source)?.let(map) ?: M.empty()

  /**
   * Modify the focus of a [PPrism] with a function
   */
  override fun modify(source: S, map: (A) -> B): T =
    getOrModify(source).fold(::identity) { a -> reverseGet(map(a)) }

  /**
   * Set the focus of a [PPrism] with a value
   */
  override fun set(source: S, focus: B): T =
    modify(source) { focus }

  /**
   * Lift a function [f]: `(A) -> B to the context of `S`: `(S) -> T?`
   */
  public fun liftNullable(f: (focus: A) -> B): (source: S) -> T? =
    { s -> getOrNull(s)?.let { b -> reverseGet(f(b)) } }

  /**
   * Create a product of the [PPrism] and a type [C]
   */
  override fun <C> first(): PPrism<Pair<S, C>, Pair<T, C>, Pair<A, C>, Pair<B, C>> =
    PPrism(
      { (s, c) -> getOrModify(s).bimap({ it to c }, { it to c }) },
      { (b, c) -> reverseGet(b) to c }
    )

  /**
   * Create a product of a type [C] and the [PPrism]
   */
  override fun <C> second(): PPrism<Pair<C, S>, Pair<C, T>, Pair<C, A>, Pair<C, B>> =
    PPrism(
      { (c, s) -> getOrModify(s).bimap({ c to it }, { c to it }) },
      { (c, b) -> c to reverseGet(b) }
    )

  /**
   * Create a sum of the [PPrism] and a type [C]
   */
  override fun <C> left(): PPrism<Either<S, C>, Either<T, C>, Either<A, C>, Either<B, C>> =
    PPrism(
      {
        it.fold(
          { a -> getOrModify(a).bimap({ Either.Left(it) }, { Either.Left(it) }) },
          { c -> Either.Right(Either.Right(c)) })
      },
      {
        when (it) {
          is Either.Left -> Either.Left(reverseGet(it.value))
          is Either.Right -> Either.Right(it.value)
        }
      }
    )

  /**
   * Create a sum of a type [C] and the [PPrism]
   */
  override fun <C> right(): PPrism<Either<C, S>, Either<C, T>, Either<C, A>, Either<C, B>> =
    PPrism(
      {
        it.fold(
          { c -> Either.Right(Either.Left(c)) },
          { s -> getOrModify(s).bimap({ Either.Right(it) }, { Either.Right(it) }) })
      },
      { it.map(this::reverseGet) }
    )

  /**
   * Compose a [PPrism] with another [PPrism]
   */
  public infix fun <C, D> compose(other: PPrism<in A, out B, out C, in D>): PPrism<S, T, C, D> =
    PPrism(
      getOrModify = { s -> getOrModify(s).flatMap { a -> other.getOrModify(a).bimap({ set(s, it) }, ::identity) } },
      reverseGet = this::reverseGet compose other::reverseGet
    )

  public operator fun <C, D> plus(other: PPrism<in A, out B, out C, in D>): PPrism<S, T, C, D> =
    this compose other

  public companion object {

    public fun <S> id(): PIso<S, S, S, S> = PIso.id<S>()

    /**
     * Invoke operator overload to create a [PPrism] of type `S` with focus `A`.
     * Can also be used to construct [Prism]
     */
    public operator fun <S, T, A, B> invoke(
      getOrModify: (S) -> Either<T, A>,
      reverseGet: (B) -> T
    ): PPrism<S, T, A, B> =
      object : PPrism<S, T, A, B> {
        override fun getOrModify(s: S): Either<T, A> = getOrModify(s)

        override fun reverseGet(b: B): T = reverseGet(b)
      }

    /**
     * A [PPrism] that checks for equality with a given value [a]
     */
    public fun <A> only(a: A, eq: (constant: A, other: A) -> Boolean = { aa, b -> aa == b }): Prism<A, Unit> = Prism(
      getOrModify = { a2 -> (if (eq(a, a2)) Either.Left(a) else Either.Right(Unit)) },
      reverseGet = { a }
    )

    /**
     * [PPrism] to focus into an [arrow.core.Some]
     */
    @JvmStatic
    public fun <A, B> pSome(): PPrism<Option<A>, Option<B>, A, B> =
      PPrism(
        getOrModify = { option -> option.fold({ Either.Left(None) }, { Either.Right(it) }) },
        reverseGet = ::Some
      )

    /**
     * [Prism] to focus into an [arrow.core.Some]
     */
    @JvmStatic
    public fun <A> some(): Prism<Option<A>, A> =
      pSome()

    /**
     * [Prism] to focus into an [arrow.core.None]
     */
    @JvmStatic
    public fun <A> none(): Prism<Option<A>, Unit> =
      Prism(
        getOrModify = { option -> option.fold({ Either.Right(Unit) }, { Either.Left(option) }) },
        reverseGet = { _ -> None }
      )
  }
}

/**
 * Invoke operator overload to create a [PPrism] of type `S` with a focus `A` where `A` is a subtype of `S`
 * Can also be used to construct [Prism]
 */
@Suppress("FunctionName")
public fun <S, A> Prism(getOption: (source: S) -> Option<A>, reverseGet: (focus: A) -> S): Prism<S, A> = Prism(
  getOrModify = { getOption(it).toEither { it } },
  reverseGet = { reverseGet(it) }
)

package arrow.optics

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.flatMap
import arrow.core.identity
import arrow.core.prependTo
import arrow.typeclasses.Monoid
import kotlin.jvm.JvmStatic

/**
 * [Optional] is a type alias for [POptional] which fixes the type arguments
 * and restricts the [POptional] to monomorphic updates.
 */
public typealias Optional<S, A> = POptional<S, S, A, A>

@Suppress("FunctionName")
public fun <S, A> Optional(getOption: (source: S) -> Option<A>, set: (source: S, focus: A) -> S): Optional<S, A> =
  POptional({ s -> getOption(s).toEither { s } }, set)

/**
 * [Optional] is an optic that allows to focus into a structure and querying or [copy]'ing an optional focus.
 *
 * ```kotlin
 * import arrow.core.None
 * import arrow.core.Option
 * import arrow.core.Some
 * import arrow.optics.Optional
 *
 * data class User(val username: String, val email: Option<String>) {
 *     companion object {
 *       // can be out generated by @optics
 *       val email: Optional<User, String> = Optional(User::email) { user, email ->
 *         user.copy(email = Some(email))
 *       }
 *   }
 * }
 *
 * fun main(args: Array<String>) {
 *   val original = User("arrow-user", None)
 *   val set = User.email.set(original, "arRoW-UsEr@arrow-Kt.IO")
 *   val modified = User.email.modify(set, String::toLowerCase)
 *   println("original: $original, set: $set, modified: $modified")
 * }
 * ```
 * <!--- KNIT example-optional-01.kt -->
 *
 * A (polymorphic) [POptional] is useful when setting or modifying a value for a type with a optional polymorphic focus
 * i.e. POptional<Either<Int, Double>, Either<String, Double>, Int, String>
 *
 * A [POptional] can be seen as a weaker [Lens] and [Prism] and combines their weakest functions:
 * - `set: (S, B) -> T` meaning we can focus into an `S` and set a value `B` for a target `A` and obtain a modified source `T`
 * - `getOrModify: (S) -> Either<T, A>` meaning it returns the focus of a [POptional] OR the original value
 *
 * @param S the source of a [POptional]
 * @param T the modified source of a [POptional]
 * @param A the focus of a [POptional]
 * @param B the modified focus of a [POptional]
 */
public interface POptional<S, T, A, B> : PSetter<S, T, A, B>, Fold<S, A>, PTraversal<S, T, A, B>, PEvery<S, T, A, B> {

  /**
   * Get the modified source of a [POptional]
   */
  override fun set(source: S, focus: B): T

  /**
   * Get the focus of a [POptional] or return the original value while allowing the type to change if it does not match
   */
  public fun getOrModify(source: S): Either<T, A>

  /**
   * Modify the focus of a [POptional] with a function [map]
   */
  override fun modify(source: S, map: (focus: A) -> B): T =
    getOrModify(source).fold(::identity) { a -> set(source, map(a)) }

  override fun <R> foldMap(M: Monoid<R>, source: S, map: (focus: A) -> R): R =
    getOrModify(source).map(map).fold({ M.empty() }, ::identity)

  /**
   * Get the focus of a [POptional] or `null` if the is not there
   */
  public fun getOrNull(source: S): A? =
    getOrModify(source).orNull()

  /**
   * Set the focus of a [POptional] with a value.
   * @return null if the [POptional] is not matching
   */
  public fun setNullable(source: S, b: B): T? =
    modifyNullable(source) { b }

  /**
   * Modify the focus of a [POptional] with a function [map]
   * @return null if the [POptional] is not matching
   */
  public fun modifyNullable(source: S, map: (focus: A) -> B): T? =
    getOrNull(source)?.let { set(source, map(it)) }

  /**
   * Join two [POptional] with the same focus [B]
   */
  public infix fun <S1, T1> choice(other: POptional<S1, T1, A, B>): POptional<Either<S, S1>, Either<T, T1>, A, B> =
    POptional(
      { sources ->
        sources.fold(
          { leftSource ->
            getOrModify(leftSource).bimap({ Either.Left(it) }, ::identity)
          },
          { rightSource ->
            other.getOrModify(rightSource).bimap({ Either.Right(it) }, ::identity)
          }
        )
      },
      { sources, focus ->
        sources.bimap({ leftSource -> this.set(leftSource, focus) }, { rightSource -> other.set(rightSource, focus) })
      }
    )

  /**
   * Create a product of the [POptional] and a type [C]
   */
  public fun <C> first(): POptional<Pair<S, C>, Pair<T, C>, Pair<A, C>, Pair<B, C>> =
    POptional(
      { (source, c) -> getOrModify(source).bimap({ Pair(it, c) }, { Pair(it, c) }) },
      { (source, c2), (update, c) -> setNullable(source, update)?.let { Pair(it, c) } ?: Pair(set(source, update), c2) }
    )

  /**
   * Create a product of a type [C] and the [POptional]
   */
  public fun <C> second(): POptional<Pair<C, S>, Pair<C, T>, Pair<C, A>, Pair<C, B>> =
    POptional(
      { (c, s) -> getOrModify(s).bimap({ c to it }, { c to it }) },
      { (c2, s), (c, b) -> setNullable(s, b)?.let { c to it } ?: c2 to set(s, b) }
    )

  /**
   * Compose a [POptional] with a [POptional]
   */
  public infix fun <C, D> compose(other: POptional<in A, out B, out C, in D>): POptional<S, T, C, D> =
    POptional(
      { source ->
        getOrModify(source).flatMap { a ->
          other.getOrModify(a).bimap({ b -> set(source, b) }, ::identity)
        }
      },
      { source, d -> modify(source) { a -> other.set(a, d) } }
    )

  public operator fun <C, D> plus(other: POptional<in A, out B, out C, in D>): POptional<S, T, C, D> =
    this compose other

  public companion object {

    public fun <S> id(): PIso<S, S, S, S> = PIso.id<S>()

    /**
     * [POptional] that takes either [S] or [S] and strips the choice of [S].
     */
    public fun <S> codiagonal(): Optional<Either<S, S>, S> = POptional(
      { sources -> sources.fold({ Either.Right(it) }, { Either.Right(it) }) },
      { sources, focus -> sources.bimap({ focus }, { focus }) }
    )

    /**
     * Invoke operator overload to create a [POptional] of type `S` with focus `A`.
     * Can also be used to construct [Optional]
     */
    public operator fun <S, T, A, B> invoke(
      getOrModify: (source: S) -> Either<T, A>,
      set: (source: S, focus: B) -> T
    ): POptional<S, T, A, B> = object : POptional<S, T, A, B> {
      override fun getOrModify(source: S): Either<T, A> = getOrModify(source)

      override fun set(source: S, focus: B): T = set(source, focus)
    }

    /**
     * [POptional] that never sees its focus
     */
    public fun <A, B> void(): Optional<A, B> = POptional(
      { Either.Left(it) },
      { source, _ -> source }
    )

    /**
     * [Optional] to safely operate on the head of a list
     */
    @JvmStatic
    public fun <A> listHead(): Optional<List<A>, A> = Optional(
      getOption = { if (it.isNotEmpty()) Some(it[0]) else None },
      set = { list, newHead -> if (list.isNotEmpty()) newHead prependTo list.drop(1) else emptyList() }
    )

    /**
     * [Optional] to safely operate on the tail of a list
     */
    @JvmStatic
    public fun <A> listTail(): Optional<List<A>, List<A>> = Optional(
      getOption = { if (it.isEmpty()) None else Some(it.drop(1)) },
      set = { list, newTail -> if (list.isNotEmpty()) list[0] prependTo newTail else emptyList() }
    )

    /**
     * [Optional] to itself if it satisfies the predicate.
     * Filter can break the fusion property, if replace or modify do not preserve the predicate.
     */
    @JvmStatic
    public fun <A> filter(predicate: (A) -> Boolean): Optional<A, A> =
      Optional(
        getOption = { if (predicate(it)) Some(it) else None },
        set = { current, newValue -> if (predicate(current)) newValue else current }
      )
  }
}

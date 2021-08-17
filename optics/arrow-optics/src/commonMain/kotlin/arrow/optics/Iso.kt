package arrow.optics

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.None
import arrow.core.Option
import arrow.core.Either.Right
import arrow.core.Some
import arrow.core.Validated
import arrow.core.Validated.Invalid
import arrow.core.Validated.Valid
import arrow.core.compose
import arrow.core.identity
import arrow.typeclasses.Monoid
import kotlin.jvm.JvmStatic

/**
 * [Iso] is a type alias for [PIso] which fixes the type arguments
 * and restricts the [PIso] to monomorphic updates.
 */
public typealias Iso<S, A> = PIso<S, S, A, A>

private val stringToList: Iso<String, List<Char>> =
  Iso(
    get = CharSequence::toList,
    reverseGet = { it.joinToString(separator = "") }
  )

/**
 * An [Iso] is a loss less invertible optic that defines an isomorphism between a type [S] and [A]
 * i.e. a data class and its properties represented by TupleN
 *
 * A (polymorphic) [PIso] is useful when setting or modifying a value for a constructed type
 * i.e. PIso<Option<Int>, Option<String>, Int?, String?>
 *
 * An [PIso] is also a valid [PLens], [PPrism]
 *
 * @param S the source of a [PIso]
 * @param T the modified source of a [PIso]
 * @param A the focus of a [PIso]
 * @param B the modified target of a [PIso]
 */
public interface PIso<S, T, A, B> : PPrism<S, T, A, B>, PLens<S, T, A, B>, Getter<S, A>, POptional<S, T, A, B>,
  PSetter<S, T, A, B>, Fold<S, A>, PTraversal<S, T, A, B>, PEvery<S, T, A, B> {

  /**
   * Get the focus of a [PIso]
   */
  override fun get(source: S): A

  /**
   * Get the modified focus of a [PIso]
   */
  override fun reverseGet(focus: B): T

  override fun getOrModify(source: S): Either<T, A> =
    Either.Right(get(source))

  override fun set(source: S, focus: B): T =
    set(focus)

  /**
   * Modify polymorphically the focus of a [PIso] with a function
   */
  override fun modify(source: S, map: (focus: A) -> B): T =
    reverseGet(map(get(source)))

  override fun <R> foldMap(M: Monoid<R>, source: S, map: (A) -> R): R =
    map(get(source))

  /**
   * Reverse a [PIso]: the source becomes the target and the target becomes the source
   */
  public fun reverse(): PIso<B, A, T, S> =
    PIso(this::reverseGet, this::get)

  /**
   * Set polymorphically the focus of a [PIso] with a value
   */
  public fun set(b: B): T =
    reverseGet(b)

  /**
   * Pair two disjoint [PIso]
   */
  public infix fun <S1, T1, A1, B1> split(other: PIso<S1, T1, A1, B1>): PIso<Pair<S, S1>, Pair<T, T1>, Pair<A, A1>, Pair<B, B1>> =
    PIso(
      { (a, c) -> get(a) to other.get(c) },
      { (b, d) -> reverseGet(b) to other.reverseGet(d) }
    )

  /**
   * Create a pair of the [PIso] and a type [C]
   */
  override fun <C> first(): PIso<Pair<S, C>, Pair<T, C>, Pair<A, C>, Pair<B, C>> = Iso(
    { (a, c) -> get(a) to c },
    { (b, c) -> reverseGet(b) to c }
  )

  /**
   * Create a pair of a type [C] and the [PIso]
   */
  override fun <C> second(): PIso<Pair<C, S>, Pair<C, T>, Pair<C, A>, Pair<C, B>> = PIso(
    { (c, a) -> c to get(a) },
    { (c, b) -> c to reverseGet(b) }
  )

  /**
   * Create a sum of the [PIso] and a type [C]
   */
  override fun <C> left(): PIso<Either<S, C>, Either<T, C>, Either<A, C>, Either<B, C>> = PIso(
    { it.bimap(this::get, ::identity) },
    { it.bimap(this::reverseGet, ::identity) }
  )

  /**
   * Create a sum of a type [C] and the [PIso]
   */
  override fun <C> right(): PIso<Either<C, S>, Either<C, T>, Either<C, A>, Either<C, B>> = PIso(
    { it.bimap(::identity, this::get) },
    { it.bimap(::identity, this::reverseGet) }
  )

  /**
   * Compose a [PIso] with a [PIso]
   */
  public infix fun <C, D> compose(other: PIso<in A, out B, out C, in D>): PIso<S, T, C, D> = PIso(
    other::get compose this::get,
    this::reverseGet compose other::reverseGet
  )

  public operator fun <C, D> plus(other: PIso<in A, out B, out C, in D>): PIso<S, T, C, D> =
    this compose other

  public companion object {

    /**
     * create an [PIso] between any type and itself.
     * Id is the zero element of optics composition, for any optic o of type O (e.g. PLens, Prism, POptional, ...):
     * o compose Iso.id == o
     */
    public fun <S> id(): Iso<S, S> = Iso(::identity, ::identity)

    /**
     * Invoke operator overload to create a [PIso] of type `S` with target `A`.
     * Can also be used to construct [Iso]
     */
    public operator fun <S, T, A, B> invoke(get: (S) -> (A), reverseGet: (B) -> T): PIso<S, T, A, B> =
      object : PIso<S, T, A, B> {
        override fun get(s: S): A = get(s)
        override fun reverseGet(b: B): T = reverseGet(b)
      }

    /**
     * [PIso] that defines equality between a [List] and [Option] [NonEmptyList]
     */
    @JvmStatic
    public fun <A, B> listToPOptionNel(): PIso<List<A>, List<B>, Option<NonEmptyList<A>>, Option<NonEmptyList<B>>> =
      PIso(
        get = { aas -> if (aas.isEmpty()) None else Some(NonEmptyList(aas.first(), aas.drop(1))) },
        reverseGet = { optNel -> optNel.fold({ emptyList() }, NonEmptyList<B>::all) }
      )

    /**
     * [Iso] that defines equality between a [List] and [Option] [NonEmptyList]
     */
    @JvmStatic
    public fun <A> listToOptionNel(): Iso<List<A>, Option<NonEmptyList<A>>> =
      listToPOptionNel()

    /**
     * [PIso] that defines the equality between [Either] and [Validated]
     */
    @JvmStatic
    public fun <A1, A2, B1, B2> eitherToPValidated(): PIso<Either<A1, B1>, Either<A2, B2>, Validated<A1, B1>, Validated<A2, B2>> =
      PIso(
        get = { it.fold(::Invalid, ::Valid) },
        reverseGet = Validated<A2, B2>::toEither
      )

    /**
     * [Iso] that defines the equality between [Either] and [Validated]
     */
    @JvmStatic
    public fun <A, B> eitherToValidated(): Iso<Either<A, B>, Validated<A, B>> =
      eitherToPValidated()

    /**
     * [Iso] that defines the equality between a Unit value [Map] and a [Set] with its keys
     */
    @JvmStatic
    public fun <K> mapToSet(): Iso<Map<K, Unit>, Set<K>> =
      Iso(
        get = { it.keys },
        reverseGet = { keys -> keys.map { it to Unit }.toMap() }
      )

    /**
     * [PIso] that defines the equality between the nullable platform type and [Option].
     */
    @JvmStatic
    public fun <A, B> nullableToPOption(): PIso<A?, B?, Option<A>, Option<B>> =
      PIso(
        get = Option.Companion::fromNullable,
        reverseGet = { it.fold({ null }, ::identity) }
      )

    @JvmStatic
    public fun <A, B> nullableToOption(): PIso<A?, B?, Option<A>, Option<B>> =
      nullableToPOption()

    /**
     * [PIso] that defines the equality between [Option] and the nullable platform type.
     */
    @JvmStatic
    public fun <A, B> optionToPNullable(): PIso<Option<A>, Option<B>, A?, B?> =
      PIso(
        get = { it.fold({ null }, ::identity) },
        reverseGet = Option.Companion::fromNullable
      )

    /**
     * [PIso] that defines the isomorphic relationship between [Option] and the nullable platform type.
     */
    @JvmStatic
    public fun <A> optionToNullable(): Iso<Option<A>, A?> = optionToPNullable()

    /**
     * [Iso] that defines the equality between and [arrow.core.Option] and [arrow.core.Either]
     */
    @JvmStatic
    public fun <A, B> optionToPEither(): PIso<Option<A>, Option<B>, Either<Unit, A>, Either<Unit, B>> =
      PIso(
        get = { opt -> opt.fold({ Either.Left(Unit) }, ::Right) },
        reverseGet = { either -> either.fold({ None }, ::Some) }
      )

    /**
     * [Iso] that defines the equality between and [arrow.core.Option] and [arrow.core.Either]
     */
    @JvmStatic
    public fun <A> optionToEither(): Iso<Option<A>, Either<Unit, A>> =
      optionToPEither()

    /**
     * [Iso] that defines equality between String and [List] of [Char]
     */
    @JvmStatic
    public fun stringToList(): Iso<String, List<Char>> =
      stringToList

    /**
     * [PIso] that defines equality between [Validated] and [Either]
     */
    @JvmStatic
    public fun <A1, A2, B1, B2> validatedToPEither(): PIso<Validated<A1, B1>, Validated<A2, B2>, Either<A1, B1>, Either<A2, B2>> =
      PIso(
        get = Validated<A1, B1>::toEither,
        reverseGet = Validated.Companion::fromEither
      )

    /**
     * [Iso] that defines equality between [Validated] and [Either]
     */
    @JvmStatic
    public fun <A, B> validatedToEither(): Iso<Validated<A, B>, Either<A, B>> =
      validatedToPEither()
  }
}

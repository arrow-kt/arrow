package arrow.optics

import arrow.core.Either
import arrow.core.compose
import arrow.core.identity
import arrow.typeclasses.Monoid

public typealias Getter<S, A> = PGetter<S, S, A>

/**
 * A [Getter] is an optic that allows to see into a structure and getting a focus.
 *
 * A [Getter] can be seen as a get function:
 * - `get: (S) -> A` meaning we can look into an `S` and get an `A`
 *
 * @param S the source of a [Getter]
 * @param A the focus of a [Getter]
 */
public fun interface PGetter<in S, out T, out A> : POptionalGetter<S, T, A> {

  /**
   * Get the focus of a [Getter]
   */
  public fun get(source: S): A

  override fun getOrModify(source: S): Either<T, A> =
    Either.Right(get(source))

  override fun <R> foldMap(M: Monoid<R>, source: S, map: (A) -> R): R =
    map(get(source))

  /**
   * Create a product of the [Getter] and a type [C]
   */
  public override fun <C> first(): PGetter<Pair<S, C>, Pair<T, C>, Pair<A, C>> =
    PGetter { (s, c) -> get(s) to c }

  /**
   * Create a product of type [C] and the [Getter]
   */
  public override fun <C> second(): PGetter<Pair<C, S>, Pair<C, T>, Pair<C, A>> =
    PGetter { (c, s) -> c to get(s) }

  /**
   * Create a sum of the [Getter] and type [C]
   */
  override fun <C> left(): PGetter<Either<S, C>, Either<T, C>, Either<A, C>> =
    PGetter { sc -> sc.bimap(this::get, ::identity) }

  /**
   * Create a sum of type [C] and the [Getter]
   */
  override fun <C> right(): PGetter<Either<C, S>, Either<C, T>, Either<C, A>> =
    PGetter { cs -> cs.map(this::get) }

  /**
   * Join two [Getter] with the same focus
   */
  public infix fun <C, D> choice(other: PGetter<C, D, @UnsafeVariance A>): PGetter<Either<S, C>, Either<T, D>, A> =
    PGetter { s -> s.fold(this::get, other::get) }

  /**
   * Pair two disjoint [Getter]
   */
  public infix fun <C, E, D> split(other: PGetter<C, E, D>): PGetter<Pair<S, C>, Pair<T, E>, Pair<A, D>> =
    PGetter { (s, c) -> get(s) to other.get(c) }

  /**
   * Zip two [Getter] optics with the same source [S]
   */
  public infix fun <C> zip(other: PGetter<@UnsafeVariance S, @UnsafeVariance T, C>): PGetter<S, T, Pair<A, C>> =
    PGetter { s -> get(s) to other.get(s) }

  /**
   * Compose a [Getter] with a [Getter]
   */
  public infix fun <E, C> compose(other: PGetter<A, E, C>): PGetter<S, T, C> =
    PGetter(other::get compose this::get)

  public operator fun <E, C> plus(other: PGetter<A, E, C>): PGetter<S, T, C> =
    this compose other

  public companion object {

    public fun <S> id(): Getter<S, S> =
      PIso.id()

    /**
     * [Getter] that takes either [S] or [S] and strips the choice of [S].
     */
    public fun <S> codiagonal(): Getter<Either<S, S>, S> =
      Getter { aa -> aa.fold(::identity, ::identity) }
  }
}

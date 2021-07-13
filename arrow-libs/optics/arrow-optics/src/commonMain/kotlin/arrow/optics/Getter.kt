package arrow.optics

import arrow.core.Either
import arrow.core.compose
import arrow.core.identity
import arrow.typeclasses.Monoid

/**
 * A [Getter] is an optic that allows to see into a structure and getting a focus.
 *
 * A [Getter] can be seen as a get function:
 * - `get: (S) -> A` meaning we can look into an `S` and get an `A`
 *
 * @param S the source of a [Getter]
 * @param A the focus of a [Getter]
 */
public fun interface Getter<S, A> : Fold<S, A> {

  /**
   * Get the focus of a [Getter]
   */
  public fun get(source: S): A

  override fun <R> foldMap(M: Monoid<R>, source: S, map: (A) -> R): R =
    map(get(source))

  /**
   * Create a product of the [Getter] and a type [C]
   */
  public fun <C> first(): Getter<Pair<S, C>, Pair<A, C>> =
    Getter { (s, c) -> get(s) to c }

  /**
   * Create a product of type [C] and the [Getter]
   */
  public fun <C> second(): Getter<Pair<C, S>, Pair<C, A>> =
    Getter { (c, s) -> c to get(s) }

  /**
   * Create a sum of the [Getter] and type [C]
   */
  override fun <C> left(): Getter<Either<S, C>, Either<A, C>> =
    Getter { sc -> sc.bimap(this::get, ::identity) }

  /**
   * Create a sum of type [C] and the [Getter]
   */
  override fun <C> right(): Getter<Either<C, S>, Either<C, A>> =
    Getter { cs -> cs.map(this::get) }

  /**
   * Join two [Getter] with the same focus
   */
  public infix fun <C> choice(other: Getter<C, A>): Getter<Either<S, C>, A> =
    Getter { s -> s.fold(this::get, other::get) }

  /**
   * Pair two disjoint [Getter]
   */
  public infix fun <C, D> split(other: Getter<C, D>): Getter<Pair<S, C>, Pair<A, D>> =
    Getter { (s, c) -> get(s) to other.get(c) }

  /**
   * Zip two [Getter] optics with the same source [S]
   */
  public infix fun <C> zip(other: Getter<S, C>): Getter<S, Pair<A, C>> =
    Getter { s -> get(s) to other.get(s) }

  /**
   * Compose a [Getter] with a [Getter]
   */
  public infix fun <C> compose(other: Getter<in A, out C>): Getter<S, C> =
    Getter(other::get compose this::get)

  public operator fun <C> plus(other: Getter<in A, out C>): Getter<S, C> =
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

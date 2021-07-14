package arrow.optics

import arrow.core.Either

/**
 * [Setter] is a type alias for [PSetter] which fixes the type arguments
 * and restricts the [PSetter] to monomorphic updates.
 */
public typealias Setter<S, A> = PSetter<S, S, A, A>

/**
 * A [Setter] is an optic that allows to see into a structure and set or modify its focus.
 *
 * A (polymorphic) [PSetter] is useful when setting or modifying a value for a constructed type
 * i.e. PSetter<List<Int>, List<String>, Int, String>
 *
 * A [PSetter] is a generalisation of a [arrow.Functor].
 * Functor::map   (fa: Kind<F, A>, f: (A) -> B): Kind<F, B>
 * PSetter::modify(s: S,         f: (A) -> B): T
 *
 * @param S the source of a [PSetter]
 * @param T the modified source of a [PSetter]
 * @param A the focus of a [PSetter]
 * @param B the modified focus of a [PSetter]
 */
public fun interface PSetter<S, T, A, B> {

  /**
   * Modify polymorphically the focus of a [PSetter] with a function [map].
   */
  public fun modify(source: S, map: (focus: A) -> B): T

  /**
   * Set polymorphically the focus of a [PSetter] with a value [b].
   */
  public fun set(source: S, focus: B): T =
    modify(source) { focus }

  /**
   * Lift a function [map]: `(A) -> B to the context of `S`: `(S) -> T`
   */
  public fun lift(map: (focus: A) -> B): (source: S) -> T =
    { s -> modify(s) { map(it) } }

  /**
   * Join two [PSetter] with the same target
   */
  public infix fun <U, V> choice(other: PSetter<U, V, A, B>): PSetter<Either<S, U>, Either<T, V>, A, B> =
    PSetter { su, f ->
      su.bimap({ s -> modify(s, f) }, { u -> other.modify(u, f) })
    }

  /**
   * Compose a [PSetter] with a [PSetter]
   */
  public infix fun <C, D> compose(other: PSetter<in A, out B, out C, in D>): PSetter<S, T, C, D> =
    PSetter { s, fb -> modify(s) { a -> other.modify(a, fb) } }

  public operator fun <C, D> plus(other: PSetter<in A, out B, out C, in D>): PSetter<S, T, C, D> =
    this compose other

  public companion object {

    public fun <S> id(): PSetter<S, S, S, S> =
      PIso.id()

    /**
     * [PSetter] that takes either S or S and strips the choice of S.
     */
    public fun <S> codiagonal(): Setter<Either<S, S>, S> =
      Setter { aa, f -> aa.bimap(f, f) }
  }
}

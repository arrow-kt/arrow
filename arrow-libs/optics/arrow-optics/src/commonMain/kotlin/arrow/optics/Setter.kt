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
public fun interface PSetter<in S, out T, out A, in B> {

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
  public infix fun <U, V> choice(other: PSetter<U, V, @UnsafeVariance A, @UnsafeVariance B>): PSetter<Either<S, U>, Either<T, V>, A, B> =
    PSetter { su, f ->
      su.bimap({ s -> modify(s, f) }, { u -> other.modify(u, f) })
    }

  /**
   * Compose a [PSetter] with a [PSetter]
   */
  public infix fun <C, D> compose(other: PSetter<A, B, C, D>): PSetter<S, T, C, D> =
    PSetter { s, fb -> modify(s) { a -> other.modify(a, fb) } }

  public operator fun <C, D> plus(other: PSetter<A, B, C, D>): PSetter<S, T, C, D> =
    this compose other

  public companion object {

    public fun <S> id(): PSetter<S, S, S, S> =
      PIso.id()

    /**
     * [PSetter] that takes either S or S and strips the choice of S.
     */
    public fun <S> codiagonal(): Setter<Either<S, S>, S> =
      Setter { aa, f -> aa.bimap(f, f) }

    /**
     * [PTraversal] constructor from multiple getters of the same source.
     */
    public operator fun <S, T, A, B> invoke(get1: (S) -> A, get2: (S) -> A, set: (B, B, S) -> T): PSetter<S, T, A, B> =
      PSetter { s, f -> set(f(get1(s)), f(get2(s)), s) }

    public operator fun <S, T, A, B> invoke(
      get1: (S) -> A,
      get2: (S) -> A,
      get3: (S) -> A,
      set: (B, B, B, S) -> T
    ): PSetter<S, T, A, B> =
      PSetter { s, f -> set(f(get1(s)), f(get2(s)), f(get3(s)), s) }

    public operator fun <S, T, A, B> invoke(
      get1: (S) -> A,
      get2: (S) -> A,
      get3: (S) -> A,
      get4: (S) -> A,
      set: (B, B, B, B, S) -> T
    ): PSetter<S, T, A, B> =
      PSetter { s, f -> set(f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)), s) }

    public operator fun <S, T, A, B> invoke(
      get1: (S) -> A,
      get2: (S) -> A,
      get3: (S) -> A,
      get4: (S) -> A,
      get5: (S) -> A,
      set: (B, B, B, B, B, S) -> T
    ): PSetter<S, T, A, B> =
      PSetter { s, f -> set(f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)), f(get5(s)), s) }

    public operator fun <S, T, A, B> invoke(
      get1: (S) -> A,
      get2: (S) -> A,
      get3: (S) -> A,
      get4: (S) -> A,
      get5: (S) -> A,
      get6: (S) -> A,
      set: (B, B, B, B, B, B, S) -> T
    ): PSetter<S, T, A, B> =
      PSetter { s, f -> set(f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)), f(get5(s)), f(get6(s)), s) }

    public operator fun <S, T, A, B> invoke(
      get1: (S) -> A,
      get2: (S) -> A,
      get3: (S) -> A,
      get4: (S) -> A,
      get5: (S) -> A,
      get6: (S) -> A,
      get7: (S) -> A,
      set: (B, B, B, B, B, B, B, S) -> T
    ): PSetter<S, T, A, B> =
      PSetter { s, f -> set(f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)), f(get5(s)), f(get6(s)), f(get7(s)), s) }

    public operator fun <S, T, A, B> invoke(
      get1: (S) -> A,
      get2: (S) -> A,
      get3: (S) -> A,
      get4: (S) -> A,
      get5: (S) -> A,
      get6: (S) -> A,
      get7: (S) -> A,
      get8: (S) -> A,
      set: (B, B, B, B, B, B, B, B, S) -> T
    ): PSetter<S, T, A, B> =
      PSetter { s, f ->
        set(
          f(get1(s)),
          f(get2(s)),
          f(get3(s)),
          f(get4(s)),
          f(get5(s)),
          f(get6(s)),
          f(get7(s)),
          f(get8(s)),
          s
        )
      }

    public operator fun <S, T, A, B> invoke(
      get1: (S) -> A,
      get2: (S) -> A,
      get3: (S) -> A,
      get4: (S) -> A,
      get5: (S) -> A,
      get6: (S) -> A,
      get7: (S) -> A,
      get8: (S) -> A,
      get9: (S) -> A,
      set: (B, B, B, B, B, B, B, B, B, S) -> T
    ): PSetter<S, T, A, B> =
      PSetter { s, f ->
        set(
          f(get1(s)),
          f(get2(s)),
          f(get3(s)),
          f(get4(s)),
          f(get5(s)),
          f(get6(s)),
          f(get7(s)),
          f(get8(s)),
          f(get9(s)),
          s
        )
      }

    public operator fun <S, T, A, B> invoke(
      get1: (S) -> A,
      get2: (S) -> A,
      get3: (S) -> A,
      get4: (S) -> A,
      get5: (S) -> A,
      get6: (S) -> A,
      get7: (S) -> A,
      get8: (S) -> A,
      get9: (S) -> A,
      get10: (S) -> A,
      set: (B, B, B, B, B, B, B, B, B, B, S) -> T
    ): PSetter<S, T, A, B> =
      PSetter { s, f ->
        set(
          f(get1(s)),
          f(get2(s)),
          f(get3(s)),
          f(get4(s)),
          f(get5(s)),
          f(get6(s)),
          f(get7(s)),
          f(get8(s)),
          f(get9(s)),
          f(get10(s)),
          s
        )
      }

  }
}

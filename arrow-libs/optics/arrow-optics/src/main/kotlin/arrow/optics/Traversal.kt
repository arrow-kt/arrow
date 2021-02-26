package arrow.optics

import arrow.core.Either

/**
 * [Traversal] is a type alias for [PTraversal] which fixes the type arguments
 * and restricts the [PTraversal] to monomorphic updates.
 */
typealias Traversal<S, A> = PTraversal<S, S, A, A>

/**
 * A [Traversal] is an optic that allows to see into a structure with 0 to N foci.
 *
 * [Traversal] is a generalisation of [kotlin.collections.map] and can be seen as a representation of modify.
 * all methods are written in terms of modify
 *
 * @param S the source of a [PTraversal]
 * @param T the modified source of a [PTraversal]
 * @param A the target of a [PTraversal]
 * @param B the modified target of a [PTraversal]
 */
fun interface PTraversal<S, T, A, B> : PSetter<S, T, A, B> {

  override fun modify(source: S, map: (focus: A) -> B): T

  fun <U, V> choice(other: PTraversal<U, V, A, B>): PTraversal<Either<S, U>, Either<T, V>, A, B> =
    PTraversal { s, f ->
      s.fold(
        { a -> Either.Left(this@PTraversal.modify(a, f)) },
        { u -> Either.Right(other.modify(u, f)) }
      )
    }

  /**
   * Compose a [PTraversal] with a [PTraversal]
   */
  infix fun <C, D> compose(other: PTraversal<A, B, C, D>): PTraversal<S, T, C, D> =
    PTraversal { s, f -> this@PTraversal.modify(s) { b -> other.modify(b, f) } }

  operator fun <C, D> plus(other: PTraversal<A, B, C, D>): PTraversal<S, T, C, D> =
    this compose other

  companion object {
    fun <S> id(): PTraversal<S, S, S, S> =
      PIso.id()

    fun <S> codiagonal(): Traversal<Either<S, S>, S> =
      Traversal { s, f -> s.bimap(f, f) }

    /**
     * [PTraversal] that points to nothing
     */
    fun <S, A> void(): Traversal<S, A> =
      POptional.void()

    /**
     * [PTraversal] constructor from multiple getters of the same source.
     */
    operator fun <S, T, A, B> invoke(get1: (S) -> A, get2: (S) -> A, set: (B, B, S) -> T): PTraversal<S, T, A, B> =
      PTraversal { s, f -> set(f(get1(s)), f(get2(s)), s) }

    operator fun <S, T, A, B> invoke(
      get1: (S) -> A,
      get2: (S) -> A,
      get3: (S) -> A,
      set: (B, B, B, S) -> T
    ): PTraversal<S, T, A, B> =
      PTraversal { s, f -> set(f(get1(s)), f(get2(s)), f(get3(s)), s) }

    operator fun <S, T, A, B> invoke(
      get1: (S) -> A,
      get2: (S) -> A,
      get3: (S) -> A,
      get4: (S) -> A,
      set: (B, B, B, B, S) -> T
    ): PTraversal<S, T, A, B> =
      PTraversal { s, f -> set(f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)), s) }

    operator fun <S, T, A, B> invoke(
      get1: (S) -> A,
      get2: (S) -> A,
      get3: (S) -> A,
      get4: (S) -> A,
      get5: (S) -> A,
      set: (B, B, B, B, B, S) -> T
    ): PTraversal<S, T, A, B> =
      PTraversal { s, f -> set(f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)), f(get5(s)), s) }

    operator fun <S, T, A, B> invoke(
      get1: (S) -> A,
      get2: (S) -> A,
      get3: (S) -> A,
      get4: (S) -> A,
      get5: (S) -> A,
      get6: (S) -> A,
      set: (B, B, B, B, B, B, S) -> T
    ): PTraversal<S, T, A, B> =
      PTraversal { s, f -> set(f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)), f(get5(s)), f(get6(s)), s) }

    operator fun <S, T, A, B> invoke(
      get1: (S) -> A,
      get2: (S) -> A,
      get3: (S) -> A,
      get4: (S) -> A,
      get5: (S) -> A,
      get6: (S) -> A,
      get7: (S) -> A,
      set: (B, B, B, B, B, B, B, S) -> T
    ): PTraversal<S, T, A, B> =
      PTraversal { s, f -> set(f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)), f(get5(s)), f(get6(s)), f(get7(s)), s) }

    operator fun <S, T, A, B> invoke(
      get1: (S) -> A,
      get2: (S) -> A,
      get3: (S) -> A,
      get4: (S) -> A,
      get5: (S) -> A,
      get6: (S) -> A,
      get7: (S) -> A,
      get8: (S) -> A,
      set: (B, B, B, B, B, B, B, B, S) -> T
    ): PTraversal<S, T, A, B> =
      PTraversal { s, f -> set(f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)), f(get5(s)), f(get6(s)), f(get7(s)), f(get8(s)), s) }

    operator fun <S, T, A, B> invoke(
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
    ): PTraversal<S, T, A, B> =
      PTraversal { s, f -> set(f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)), f(get5(s)), f(get6(s)), f(get7(s)), f(get8(s)), f(get9(s)), s) }

    operator fun <S, T, A, B> invoke(
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
    ): PTraversal<S, T, A, B> =
      PTraversal { s, f -> set(f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)), f(get5(s)), f(get6(s)), f(get7(s)), f(get8(s)), f(get9(s)), f(get10(s)), s) }
  }
}

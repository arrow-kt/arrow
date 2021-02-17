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

  /**
   * Compose a [PTraversal] with a [PSetter]
   */
  infix fun <C, D> compose(other: PSetter<A, B, C, D>): PSetter<S, T, C, D> = asSetter() compose other

  /**
   * Compose a [PTraversal] with a [POptional]
   */
  infix fun <C, D> compose(other: POptional<A, B, C, D>): PTraversal<S, T, C, D> = compose(other.asTraversal())

  /**
   * Compose a [PTraversal] with a [PLens]
   */
  infix fun <C, D> compose(other: PLens<A, B, C, D>): PTraversal<S, T, C, D> = compose(other.asTraversal())

  /**
   * Compose a [PTraversal] with a [PPrism]
   */
  infix fun <C, D> compose(other: PPrism<A, B, C, D>): PTraversal<S, T, C, D> = compose(other.asTraversal())

  /**
   * Compose a [PTraversal] with a [PIso]
   */
  infix fun <C, D> compose(other: PIso<A, B, C, D>): PTraversal<S, T, C, D> = compose(other.asTraversal())

  /**
   * Compose a [PTraversal] with a [Fold]
   */
  infix fun <C> compose(other: Fold<A, C>): Fold<S, C> = asFold() compose other

  /**
   * Plus operator overload to compose [PTraversal] with other optics
   */
  operator fun <C, D> plus(other: PTraversal<A, B, C, D>): PTraversal<S, T, C, D> = compose(other)

  operator fun <C, D> plus(other: PSetter<A, B, C, D>): PSetter<S, T, C, D> = compose(other)

  operator fun <C, D> plus(other: POptional<A, B, C, D>): PTraversal<S, T, C, D> = compose(other)

  operator fun <C, D> plus(other: PLens<A, B, C, D>): PTraversal<S, T, C, D> = compose(other)

  operator fun <C, D> plus(other: PPrism<A, B, C, D>): PTraversal<S, T, C, D> = compose(other)

  operator fun <C, D> plus(other: PIso<A, B, C, D>): PTraversal<S, T, C, D> = compose(other)

  operator fun <C> plus(other: Fold<A, C>): Fold<S, C> = compose(other)

  fun asSetter(): PSetter<S, T, A, B> = PSetter { s, f -> modify(s, f) }

  fun asFold(): Fold<S, A> = object : Fold<S, A> {
    override fun <R> foldMap(M: Monoid<R>, s: S, f: (A) -> R): R =
      this@PTraversal.foldMap(M, s, f)
  }

  /**
   * Find the first target matching the predicate
   */
  fun find(s: S, p: (A) -> Boolean): Option<A> = foldMap(firstOptionMonoid<A>(), s) { a ->
    if (p(a)) Const(Some(a))
    else Const(None)
  }.value()

  /**
   * Map each target to a Monoid and combine the results
   */
  fun <R> foldMap(s: S, f: (A) -> R, M: Monoid<R>): R =
    modifyF(Const.applicative(M), s) { b -> Const(f(b)) }.value()

  /**
   * Modify polymorphically the target of a [PTraversal] with a function [f]
   */
  fun modify(s: S, f: (A) -> B): T = modifyF(idApplicative, s) { b -> Id(f(b)) }.fix().value

  /**
   * Check whether at least one element satisfies the predicate.
   *
   * If there are no elements, the result is false.
   */
  fun exist(s: S, p: (A) -> Boolean): Boolean = find(s, p).fold({ false }, { true })

  /**
   * Check if forall targets satisfy the predicate
   */
  fun forall(s: S, p: (A) -> Boolean): Boolean = foldMap(s, p, AndMonoid)

  /**
   * DSL to compose [Traversal] with a [Lens] for a structure [S] to see all its foci [A]
   *
   * @receiver [Lens] with a focus in [S]
   * @return [Traversal] with a focus in [A]
   */
  val <U, V> PLens<U, V, S, T>.every: PTraversal<U, V, A, B> get() =
    this@every.compose(this@PTraversal)

  /**
   * DSL to compose [Traversal] with a [Iso] for a structure [S] to see all its foci [A]
   *
   * @receiver [Iso] with a focus in [S]
   * @return [Traversal] with a focus in [A]
   */
  val <U, V> PIso<U, V, S, T>.every: PTraversal<U, V, A, B> get() =
    this@every.compose(this@PTraversal)

  /**
   * DSL to compose [Traversal] with a [Prism] for a structure [S] to see all its foci [A]
   *
   * @receiver [Prism] with a focus in [S]
   * @return [Traversal] with a focus in [A]
   */
  val <U, V> PPrism<U, V, S, T>.every: PTraversal<U, V, A, B> get() =
    this.compose(this@PTraversal)

  /**
   * DSL to compose [Traversal] with a [Optional] for a structure [S] to see all its foci [A]
   *
   * @receiver [Optional] with a focus in [S]
   * @return [Traversal] with a focus in [A]
   */
  val <U, V> POptional<U, V, S, T>.every: PTraversal<U, V, A, B> get() =
    this.compose(this@PTraversal)

  /**
   * DSL to compose [Traversal] with a [Setter] for a structure [S] to see all its foci [A]
   *
   * @receiver [Setter] with a focus in [S]
   * @return [Setter] with a focus in [A]
   */
  val <U, V> PSetter<U, V, S, T>.every: PSetter<U, V, A, B> get() =
    this.compose(this@PTraversal)

  /**
   * DSL to compose [Traversal] with a [Traversal] for a structure [S] to see all its foci [A]
   *
   * @receiver [Traversal] with a focus in [S]
   * @return [Traversal] with a focus in [A]
   */
  val <U, V> PTraversal<U, V, S, T>.every: PTraversal<U, V, A, B> get() =
    this.compose(this@PTraversal)

  /**
   * DSL to compose [Traversal] with a [Fold] for a structure [S] to see all its foci [A]
   *
   * @receiver [Fold] with a focus in [S]
   * @return [Fold] with a focus in [A]
   */
  val <U> Fold<U, S>.every: Fold<U, A> get() = this.compose(this@PTraversal.asFold())
}

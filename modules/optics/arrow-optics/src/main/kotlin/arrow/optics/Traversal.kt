package arrow.optics

import arrow.Kind
import arrow.core.Const
import arrow.core.Either
import arrow.core.Id
import arrow.core.ListK
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.extensions.const.applicative.applicative
import arrow.core.extensions.id.applicative.applicative
import arrow.core.extensions.listk.monoid.monoid
import arrow.core.extensions.monoid
import arrow.core.identity
import arrow.core.value
import arrow.higherkind
import arrow.typeclasses.Applicative
import arrow.typeclasses.Monoid
import arrow.typeclasses.Traverse

/**
 * [Traversal] is a type alias for [PTraversal] which fixes the type arguments
 * and restricts the [PTraversal] to monomorphic updates.
 */
typealias Traversal<S, A> = PTraversal<S, S, A, A>

typealias ForTraversal = ForPTraversal
typealias TraversalOf<S, A> = PTraversalOf<S, S, A, A>
typealias TraversalPartialOf<S> = Kind<ForTraversal, S>
typealias TraversalKindedJ<S, A> = PTraversalKindedJ<S, S, A, A>

/**
 * A [Traversal] is an optic that allows to see into a structure with 0 to N foci.
 *
 * [Traversal] is a generalisation of [arrow.Traverse] and can be seen as a representation of modifyF.
 * all methods are written in terms of modifyF
 *
 * @param S the source of a [PTraversal]
 * @param T the modified source of a [PTraversal]
 * @param A the target of a [PTraversal]
 * @param B the modified target of a [PTraversal]
 */
@higherkind
interface PTraversal<S, T, A, B> : PTraversalOf<S, T, A, B> {

  fun <F> modifyF(FA: Applicative<F>, s: S, f: (A) -> Kind<F, B>): Kind<F, T>

  companion object {
    fun <S> id() = PIso.id<S>().asTraversal()

    fun <S> codiagonal(): Traversal<Either<S, S>, S> = object : Traversal<Either<S, S>, S> {
      override fun <F> modifyF(FA: Applicative<F>, s: Either<S, S>, f: (S) -> Kind<F, S>): Kind<F, Either<S, S>> = FA.run {
        s.bimap(f, f).fold({ fa -> fa.map { a -> Either.Left(a) } }, { fa -> fa.map { a -> Either.Right(a) } })
      }
    }

    /**
     * Construct a [PTraversal] from a [Traverse] instance.
     */
    fun <T, A, B> fromTraversable(TT: Traverse<T>) = object : PTraversal<Kind<T, A>, Kind<T, B>, A, B> {
      override fun <F> modifyF(FA: Applicative<F>, s: Kind<T, A>, f: (A) -> Kind<F, B>): Kind<F, Kind<T, B>> =
        TT.run { s.traverse(FA, f) }
    }

    /**
     * [PTraversal] that points to nothing
     */
    fun <S, A> void() = POptional.void<S, A>().asTraversal()

    /**
     * [PTraversal] constructor from multiple getters of the same source.
     */
    operator fun <S, T, A, B> invoke(get1: (S) -> A, get2: (S) -> A, set: (B, B, S) -> T): PTraversal<S, T, A, B> = object : PTraversal<S, T, A, B> {
      override fun <F> modifyF(FA: Applicative<F>, s: S, f: (A) -> Kind<F, B>): Kind<F, T> =
        FA.map(
          f(get1(s)), f(get2(s))
        ) { (b1, b2) -> set(b1, b2, s) }
    }

    operator fun <S, T, A, B> invoke(
      get1: (S) -> A,
      get2: (S) -> A,
      get3: (S) -> A,
      set: (B, B, B, S) -> T
    ): PTraversal<S, T, A, B> = object : PTraversal<S, T, A, B> {
      override fun <F> modifyF(FA: Applicative<F>, s: S, f: (A) -> Kind<F, B>): Kind<F, T> =
        FA.map(
          f(get1(s)), f(get2(s)), f(get3(s))
        ) { (b1, b2, b3) -> set(b1, b2, b3, s) }
    }

    operator fun <S, T, A, B> invoke(
      get1: (S) -> A,
      get2: (S) -> A,
      get3: (S) -> A,
      get4: (S) -> A,
      set: (B, B, B, B, S) -> T
    ): PTraversal<S, T, A, B> = object : PTraversal<S, T, A, B> {
      override fun <F> modifyF(FA: Applicative<F>, s: S, f: (A) -> Kind<F, B>): Kind<F, T> =
        FA.map(
          f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s))
        ) { (b1, b2, b3, b4) -> set(b1, b2, b3, b4, s) }
    }

    operator fun <S, T, A, B> invoke(
      get1: (S) -> A,
      get2: (S) -> A,
      get3: (S) -> A,
      get4: (S) -> A,
      get5: (S) -> A,
      set: (B, B, B, B, B, S) -> T
    ): PTraversal<S, T, A, B> = object : PTraversal<S, T, A, B> {
      override fun <F> modifyF(FA: Applicative<F>, s: S, f: (A) -> Kind<F, B>): Kind<F, T> =
        FA.map(
          f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)), f(get5(s))
        ) { (b1, b2, b3, b4, b5) -> set(b1, b2, b3, b4, b5, s) }
    }

    operator fun <S, T, A, B> invoke(
      get1: (S) -> A,
      get2: (S) -> A,
      get3: (S) -> A,
      get4: (S) -> A,
      get5: (S) -> A,
      get6: (S) -> A,
      set: (B, B, B, B, B, B, S) -> T
    ): PTraversal<S, T, A, B> = object : PTraversal<S, T, A, B> {
      override fun <F> modifyF(FA: Applicative<F>, s: S, f: (A) -> Kind<F, B>): Kind<F, T> =
        FA.map(
          f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)), f(get5(s)), f(get6(s))
        ) { (b1, b2, b3, b4, b5, b6) -> set(b1, b2, b3, b4, b5, b6, s) }
    }

    operator fun <S, T, A, B> invoke(
      get1: (S) -> A,
      get2: (S) -> A,
      get3: (S) -> A,
      get4: (S) -> A,
      get5: (S) -> A,
      get6: (S) -> A,
      get7: (S) -> A,
      set: (B, B, B, B, B, B, B, S) -> T
    ): PTraversal<S, T, A, B> = object : PTraversal<S, T, A, B> {
      override fun <F> modifyF(FA: Applicative<F>, s: S, f: (A) -> Kind<F, B>): Kind<F, T> =
        FA.map(
          f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)), f(get5(s)), f(get6(s)), f(get7(s))
        ) { (b1, b2, b3, b4, b5, b6, b7) -> set(b1, b2, b3, b4, b5, b6, b7, s) }
    }

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
    ): PTraversal<S, T, A, B> = object : PTraversal<S, T, A, B> {
      override fun <F> modifyF(FA: Applicative<F>, s: S, f: (A) -> Kind<F, B>): Kind<F, T> =
        FA.map(
          f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)), f(get5(s)), f(get6(s)), f(get7(s)), f(get8(s))
        ) { (b1, b2, b3, b4, b5, b6, b7, b8) -> set(b1, b2, b3, b4, b5, b6, b7, b8, s) }
    }

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
    ): PTraversal<S, T, A, B> = object : PTraversal<S, T, A, B> {
      override fun <F> modifyF(FA: Applicative<F>, s: S, f: (A) -> Kind<F, B>): Kind<F, T> =
        FA.map(
          f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)), f(get5(s)), f(get6(s)), f(get7(s)), f(get8(s)), f(get9(s))
        ) { (b1, b2, b3, b4, b5, b6, b7, b8, b9) -> set(b1, b2, b3, b4, b5, b6, b7, b8, b9, s) }
    }

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
    ): PTraversal<S, T, A, B> = object : PTraversal<S, T, A, B> {
      override fun <F> modifyF(FA: Applicative<F>, s: S, f: (A) -> Kind<F, B>): Kind<F, T> =
        FA.map(
          f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)), f(get5(s)), f(get6(s)), f(get7(s)), f(get8(s)), f(get9(s)), f(get10(s))
        ) { (b1, b2, b3, b4, b5, b6, b7, b8, b9, b10) -> set(b1, b2, b3, b4, b5, b6, b7, b8, b9, b10, s) }
    }
  }

  /**
   * Map each target to a Monoid and combine the results
   */
  fun <R> foldMap(M: Monoid<R>, s: S, f: (A) -> R): R =
    modifyF(Const.applicative(M), s) { b -> Const(f(b)) }.value()

  /**
   * Fold using the given [Monoid] instance.
   */
  fun fold(M: Monoid<A>, s: S): A = foldMap(M, s, ::identity)

  /**
   * Alias for fold.
   */
  fun combineAll(M: Monoid<A>, s: S): A = fold(M, s)

  /**
   * Get all foci of the [PTraversal]
   */
  fun getAll(s: S): ListK<A> = foldMap(ListK.monoid(), s) { ListK(listOf(it)) }

  /**
   * Set polymorphically the target of a [PTraversal] with a value
   */
  fun set(s: S, b: B): T = modify(s) { b }

  /**
   * Calculate the number of targets in the [PTraversal]
   */
  fun size(s: S): Int = foldMap(Int.monoid(), s) { 1 }

  /**
   * Check if there is no target
   */
  fun isEmpty(s: S): Boolean = foldMap(AndMonoid, s) { _ -> false }

  /**
   * Check if there is at least one target
   */
  fun nonEmpty(s: S): Boolean = !isEmpty(s)

  /**
   * Find the first target or [Option.None] if no targets
   */
  fun headOption(s: S): Option<A> = foldMap(firstOptionMonoid<A>(), s) { b -> Const(Some(b)) }.value()

  /**
   * Find the first target or [Option.None] if no targets
   */
  fun lastOption(s: S): Option<A> = foldMap(lastOptionMonoid<A>(), s) { b -> Const(Some(b)) }.value()

  fun <U, V> choice(other: PTraversal<U, V, A, B>): PTraversal<Either<S, U>, Either<T, V>, A, B> = object : PTraversal<Either<S, U>, Either<T, V>, A, B> {
    override fun <F> modifyF(FA: Applicative<F>, s: Either<S, U>, f: (A) -> Kind<F, B>): Kind<F, Either<T, V>> = FA.run {
      s.fold(
        { a -> this@PTraversal.modifyF(FA, a, f).map { Either.Left(it) } },
        { u -> other.modifyF(FA, u, f).map { Either.Right(it) } }
      )
    }
  }

  /**
   * Compose a [PTraversal] with a [PTraversal]
   */
  infix fun <C, D> compose(other: PTraversal<A, B, C, D>): PTraversal<S, T, C, D> = object : PTraversal<S, T, C, D> {
    override fun <F> modifyF(FA: Applicative<F>, s: S, f: (C) -> Kind<F, D>): Kind<F, T> =
      this@PTraversal.modifyF(FA, s) { b -> other.modifyF(FA, b, f) }
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
  fun modify(s: S, f: (A) -> B): T = modifyF(Id.applicative(), s) { b -> Id(f(b)) }.value()

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
}

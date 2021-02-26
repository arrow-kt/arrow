package arrow.optics

import arrow.typeclasses.Monoid

typealias Every<S, A> = PEvery<S, S, A, A>

/**
 * Composition of Fold and Traversal
 * It combines their powers
 */
interface PEvery<S, T, A, B> : PTraversal<S, T, A, B>, Fold<S, A>, PSetter<S, T, A, B> {

  /**
   * Map each target to a type R and use a Monoid to fold the results
   */
  override fun <R> foldMap(M: Monoid<R>, source: S, map: (focus: A) -> R): R

  override fun modify(source: S, map: (focus: A) -> B): T

  /**
   * Compose a [PEvery] with a [PEvery]
   */
  infix fun <C, D> compose(other: PEvery<A, B, C, D>): PEvery<S, T, C, D> =
    object : PEvery<S, T, C, D> {
      override fun <R> foldMap(M: Monoid<R>, source: S, map: (C) -> R): R =
        this@PEvery.foldMap(M, source) { c -> other.foldMap(M, c, map) }

      override fun modify(source: S, map: (focus: C) -> D): T =
        this@PEvery.modify(source) { b -> other.modify(b, map) }
    }

  operator fun <C, D> plus(other: PEvery<A, B, C, D>): PEvery<S, T, C, D> =
    this compose other

  companion object {
    fun <S, A> from(T: Traversal<S, A>, F: Fold<S, A>): Every<S, A> =
      object : Every<S, A> {
        override fun <R> foldMap(M: Monoid<R>, source: S, map: (A) -> R): R = F.foldMap(M, source, map)
        override fun modify(source: S, map: (focus: A) -> A): S = T.modify(source, map)
      }
  }

  /**
   * DSL to compose [Every] with a [Lens] for a structure [S] to see all its foci [A]
   *
   * @receiver [Lens] with a focus in [S]
   * @return [Every] with a focus in [A]
   */
  val <U, V> PLens<U, V, S, T>.every: PEvery<U, V, A, B>
    get() = this@every.compose(this@PEvery)

  /**
   * DSL to compose [Every] with a [Iso] for a structure [S] to see all its foci [A]
   *
   * @receiver [Iso] with a focus in [S]
   * @return [Every] with a focus in [A]
   */
  val <U, V> PIso<U, V, S, T>.every: PEvery<U, V, A, B>
    get() = this@every.compose(this@PEvery)

  /**
   * DSL to compose [Every] with a [Prism] for a structure [S] to see all its foci [A]
   *
   * @receiver [Prism] with a focus in [S]
   * @return [Every] with a focus in [A]
   */
  val <U, V> PPrism<U, V, S, T>.every: PEvery<U, V, A, B>
    get() = this.compose(this@PEvery)

  /**
   * DSL to compose [Every] with a [Optional] for a structure [S] to see all its foci [A]
   *
   * @receiver [Optional] with a focus in [S]
   * @return [Every] with a focus in [A]
   */
  val <U, V> POptional<U, V, S, T>.every: PEvery<U, V, A, B>
    get() = this.compose(this@PEvery)

  /**
   * DSL to compose [Every] with a [Setter] for a structure [S] to see all its foci [A]
   *
   * @receiver [Setter] with a focus in [S]
   * @return [Setter] with a focus in [A]
   */
  val <U, V> PSetter<U, V, S, T>.every: PSetter<U, V, A, B>
    get() = this.compose(this@PEvery)

  /**
   * DSL to compose [Every] with a [Traversal] for a structure [S] to see all its foci [A]
   *
   * @receiver [Traversal] with a focus in [S]
   * @return [Traversal] with a focus in [A]
   */
  val <U, V> PTraversal<U, V, S, T>.every: PTraversal<U, V, A, B>
    get() = this.compose(this@PEvery)
}

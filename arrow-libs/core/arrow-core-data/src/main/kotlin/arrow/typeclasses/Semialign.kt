package arrow.typeclasses

import arrow.Kind
import arrow.KindDeprecation
import arrow.core.Ior
import arrow.core.Option
import arrow.core.Tuple2
import arrow.core.identity
import arrow.core.some
import arrow.core.toT

@Deprecated(KindDeprecation)
/**
 * A type class used for aligning of functors with non-uniform shapes.
 *
 * Note: Instances need to override either one of align/unlign here, otherwise a Stackoverflow exception will occur at runtime!
 */
interface Semialign<F> : Functor<F> {
  /**
   * Combines two structures by taking the union of their shapes and using Ior to hold the elements.
   *
   *  {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.core.extensions.*
   * import arrow.core.extensions.listk.semialign.semialign
   * import arrow.core.*
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val result = ListK.semialign().run {
   *    align(listOf("A", "B").k(), listOf(1, 2, 3).k())
   *   }
   *   //sampleEnd
   *   println(result)
   * }
   * ```
   */
  fun <A, B> align(a: Kind<F, A>, b: Kind<F, B>): Kind<F, Ior<A, B>> = alignWith(a, b, ::identity)

  /**
   * Combines two structures by taking the union of their shapes and combining the elements with the given function.
   *
   * {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.core.extensions.*
   * import arrow.core.extensions.listk.semialign.semialign
   * import arrow.core.*
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val result = ListK.semialign().run {
   *    alignWith(listOf("A", "B").k(), listOf(1, 2, 3).k()) {
   *      "$it"
   *    }
   *   }
   *   //sampleEnd
   *   println(result)
   * }
   * ```
   */
  fun <A, B, C> alignWith(a: Kind<F, A>, b: Kind<F, B>, fa: (Ior<A, B>) -> C): Kind<F, C> = align(a, b).map(fa)

  /**
   * aligns two structures and combine them with the given Semigroups '+'
   */
  fun <A> Kind<F, A>.salign(
    SG: Semigroup<A>,
    other: Kind<F, A>
  ): Kind<F, A> =
    alignWith(this, other) {
      it.fold(::identity, ::identity) { a, b ->
        SG.run { a.combine(b) }
      }
    }

  /**
   * Align two structures as in zip, but filling in blanks with None.
   */
  fun <A, B> Kind<F, A>.padZip(
    other: Kind<F, B>
  ): Kind<F, Tuple2<Option<A>, Option<B>>> =
    alignWith(this, other) { ior ->
      ior.fold(
        { it.some() toT Option.empty<B>() },
        { Option.empty<A>() toT it.some() },
        { a, b -> a.some() toT b.some() }
      )
    }

  /**
   * Align two structures as in zipWith, but filling in blanks with None.
   */
  fun <A, B, C> Kind<F, A>.padZipWith(
    other: Kind<F, B>,
    fa: (Option<A>, Option<B>) -> C
  ): Kind<F, C> =
    padZip(other).map { fa(it.a, it.b) }
}

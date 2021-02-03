package arrow.typeclasses

import arrow.Kind
import arrow.Kind2
import arrow.KindDeprecation
import arrow.core.identity

@Deprecated(KindDeprecation)
interface Bicrosswalk<T> : Bifunctor<T>, Bifoldable<T> {

  /**
   * {: data-executable='true'}
   *
   * ```kotlin:ank
   *
   * import arrow.core.extensions.*
   * import arrow.core.extensions.either.bicrosswalk.bicrosswalk
   * import arrow.core.extensions.listk.align.align
   * import arrow.core.*
   *
   * Either.bicrosswalk().run {
   *   val either = Either.Right("arrow")
   *   bicrosswalk(ListK.align(), either, {ListK.just("fa($it)")}) {ListK.just("fb($it)")}
   * }
   * ```
   */
  fun <F, A, B, C, D> bicrosswalk(
    ALIGN: Align<F>,
    tab: Kind2<T, A, B>,
    fa: (A) -> Kind<F, C>,
    fb: (B) -> Kind<F, D>
  ): Kind<F, Kind2<T, C, D>> =
    bisequenceL(ALIGN, tab.bimap(fa, fb))

  /**
   * {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.core.extensions.*
   * import arrow.core.extensions.either.bicrosswalk.bicrosswalk
   * import arrow.core.extensions.listk.align.align
   * import arrow.core.*
   *
   * Either.bicrosswalk().run {
   *   val either: Either<ListK<Int>, ListK<String>> = Either.Right(listOf("hello", "arrow").k())
   *   bisequenceL(ListK.align(), either)
   * }
   * ```
   */
  fun <F, A, B> bisequenceL(
    ALIGN: Align<F>,
    tab: Kind2<T, Kind<F, A>, Kind<F, B>>
  ): Kind<F, Kind2<T, A, B>> =
    bicrosswalk(ALIGN, tab, ::identity, ::identity)
}

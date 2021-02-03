package arrow.typeclasses

import arrow.Kind
import arrow.KindDeprecation
import arrow.core.Tuple2
import arrow.core.identity

@Deprecated(KindDeprecation)
/**
 * The `Unzip` typeclass extends `Zip` by providing an inverse operation to zip.
 */
interface Unzip<F> : Zip<F> {
  /**
   * unzips the structure holding the resulting elements in an `Tuple2`
   *
   * {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.core.extensions.*
   * import arrow.core.extensions.listk.unzip.unzip
   * import arrow.core.*
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val result = ListK.unzip().run {
   *      listOf("A" toT 1, "B" toT 2).k().unzip()
   *   }
   *   //sampleEnd
   *   println(result)
   * }
   * ```
   */
  fun <A, B> Kind<F, Tuple2<A, B>>.unzip(): Tuple2<Kind<F, A>, Kind<F, B>> =
    unzipWith(::identity)

  /**
   * after applying the given function unzip the resulting structure into its elements.
   *
   * {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.core.extensions.*
   * import arrow.core.extensions.listk.unzip.unzip
   * import arrow.core.*
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val result = ListK.unzip().run {
   *    listOf("A:1", "B:2", "C:3").k().unzipWith { e ->
   *      e.split(":").let {
   *        it.first() toT it.last()
   *      }
   *    }
   *   }
   *   //sampleEnd
   *   println(result)
   * }
   * ```
   */
  fun <A, B, C> Kind<F, C>.unzipWith(fc: (C) -> Tuple2<A, B>): Tuple2<Kind<F, A>, Kind<F, B>> =
    map(fc).unzip()
}

package arrow.mtl.typeclasses

import arrow.Kind
import arrow.Kind2
import arrow.typeclasses.Monad

/**
 * MonadTrans is a typeclass that abstracts lifting arbitray monadic computations in another context.
 */
interface MonadTrans<F> {
  /**
   * Transforms a given monad `Kind<G, A>` to `Kind2<F, G, A>`
   *
   * {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.mtl.extensions.optiont.monadTrans.monadTrans
   * import arrow.core.extensions.id.monad.monad
   * import arrow.core.extensions.*
   * import arrow.core.*
   * import arrow.mtl.*
   *
   * fun main(args: Array<String>) {
   *    // sampleStart
   *    val result = OptionT.monadTrans().run {
   *      Id.just("hello").liftT(Id.monad())
   *    }
   *    // sampleEnd
   *    println(result)
   * }
   * ```
   */
  fun <G, A> Kind<G, A>.liftT(MG: Monad<G>): Kind2<F, G, A>
}

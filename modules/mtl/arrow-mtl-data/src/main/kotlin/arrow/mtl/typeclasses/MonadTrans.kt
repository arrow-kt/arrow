package arrow.mtl.typeclasses

import arrow.Kind
import arrow.Kind2
import arrow.typeclasses.Monad

/**
 * MonadTrans is a typeclass that abstracts lifting arbitray monadic computations in another context.
 */
interface MonadTrans<T> {
  /**
   * transforms a given monad `Kind<F, A>` to `Kind2<T, F, A>`
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
   *      Id.just("hello").lift(Id.monad())
   *    }
   *    // sampleEnd
   *    println(result)
   * }
   * ```
   */
  fun <F, A> Kind<F, A>.lift(MF: Monad<F>): Kind2<T, F, A>
}

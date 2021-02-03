package arrow.typeclasses

import arrow.Kind
import arrow.KindDeprecation

@Deprecated(KindDeprecation)
/**
 * Repeat extends Zip by providing a repeat structure.
 */
interface Repeat<F> : Zip<F> {

  /**
   * a (potentially infinite) repeat structure that contains the given value
   *
   * {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.core.extensions.*
   * import arrow.core.extensions.sequencek.repeat.repeat
   * import arrow.core.*
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val seq = generateSequence(0) { it + 1 }.k()
   *   val result = SequenceK.repeat().run {
   *    repeat("Item").zip(seq).fix().take(5).toList()
   *   }
   *   //sampleEnd
   *   println(result)
   * }
   * ```
   */
  fun <A> repeat(a: A): Kind<F, A>
}

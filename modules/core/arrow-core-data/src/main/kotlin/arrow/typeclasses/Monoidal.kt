package arrow.typeclasses

import arrow.Kind
import arrow.core.Option

/**
 * ank_macro_hierarchy(arrow.typeclasses.Monoidal)
 *
 * The [Monoidal] type class adds an identity element to the [Semigroupal] type class by defining the function [identity].
 *
 * [identity] returns a specific identity `Kind<F, A>` value for a given type [F] and [A].
 *
 * This type class complies with the following law:
 *
 * ```kotlin
 * fa.product(identity) == identity.product(fa) == identity
 * ```
 *
 * In addition, the laws of the [Semigroupal] type class also apply.
 *
 * Currently, [Monoidal] instances are defined for [Option], [ListK], [SequenceK] and [SetK].
 *
 * ### Examples
 *
 * ```kotlin:ank:playground:extension
 * _imports_
 *
 * fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   _extensionFactory_
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 *
 * The following examples show the identity elements for the already defined [Monoidal] instances:
 *
 * ```kotlin:ank:playground
 * import arrow.core.Option
 * import arrow.core.extensions.option.monoidal.monoidal
 *
 * fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   Option.monoidal().run {
 *     identity<Any>()
 *   }
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 *
 * ```kotlin:ank:playground
 * import arrow.core.ListK
 * import arrow.core.extensions.listk.monoidal.monoidal
 *
 * fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   ListK.monoidal().run {
 *     identity<Any>()
 *   }
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 *
 * ```kotlin:ank:playground
 * import arrow.core.SetK
 * import arrow.core.extensions.setk.monoidal.monoidal
 *
 * fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   SetK.monoidal().run {
 *     identity<Any>()
 *   }
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 *
 * ```kotlin:ank:playground
 * import arrow.core.SequenceK
 * import arrow.core.extensions.sequencek.monoidal.monoidal
 *
 * fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   SequenceK.monoidal().run {
 *     identity<Any>()
 *   }
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 */
interface Monoidal<F> : Semigroupal<F> {

  /**
   * Given a type [A], create an "identity" for a F<A> value.
   */
  fun <A> identity(): Kind<F, A>

  companion object
}

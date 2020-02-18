package arrow.typeclasses

import arrow.core.Option
import arrow.documented

/**
 * ank_macro_hierarchy(arrow.typeclasses.Semiring)
 *
 * The [Semiring] type class for a given type `A` combines both a commutative additive [Monoid] and a multiplicative [Monoid].
 *  It requires the multiplicative [Monoid] to distribute over the additive one. The operations of the multiplicative [Monoid] have been renamed to
 *  [one] and [combineMultiplicate] for easier use.
 *
 * ```kotlin
 * (a.combineMultiplicate(b)).combineMultiplicate(c) == a.combineMultiplicate(b.combineMultiplicate(c))
 * ```
 *
 * The [one] value serves exactly like the [empty] function for an additive [Monoid], just adapted for the multiplicative
 * version. This forms the following law:
 *
 * ```kotlin
 * combineMultiplicate(x, one) == combineMultiplicate(one, x) == x
 * ```
 *
 * Please note that the empty function has been renamed to [zero] to get a consistent naming style inside the semiring.
 *
 * Currently, [Semiring] instances are defined for all available number types.
 *
 * ### Examples
 *
 * Here a some examples:
 *
 * ```kotlin:ank:playground
 * import arrow.core.extensions.*
 *
 * fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   Int.semiring().run { 1.combine(2) }
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 *
 * ```kotlin:ank:playground
 * import arrow.core.extensions.*
 *
 * fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   Int.semiring().run { 2.combineMultiplicate(3) }
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 *
 * ```kotlin:ank:playground
 * import arrow.core.Option
 * import arrow.core.extensions.*
 * import arrow.core.extensions.option.semiring.semiring
 *
 * fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   Option.semiring(Int.semiring()).run {
 *      Option(1).combine(Option(2))
 *   }
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 *
 * ```kotlin:ank:playground
 * import arrow.core.Option
 * import arrow.core.extensions.*
 * import arrow.core.extensions.option.semiring.semiring
 *
 * fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   Option.semiring(Int.semiring()).run {
 *      Option(2).combineMultiplicate(Option(3))
 *   }
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 *
 * ```kotlin:ank:playground
 * import arrow.core.Option
 * import arrow.core.None
 * import arrow.core.extensions.*
 * import arrow.core.extensions.option.semiring.semiring
 *
 * fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   Option.semiring(Int.semiring()).run {
 *      Option(1).combine(None)
 *   }
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 *
 * The type class `Semiring` also has support for the `+` `*` syntax:
 *
 * ```kotlin:ank:playground
 * import arrow.core.Option
 * import arrow.core.extensions.*
 * import arrow.core.extensions.option.semiring.semiring
 *
 * fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   Option.semiring(Int.semiring()).run {
 *      Option(1) + Option(2)
 *   }
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 *
 * ```kotlin:ank:playground
 * import arrow.core.Option
 * import arrow.core.extensions.*
 * import arrow.core.extensions.option.semiring.semiring
 *
 * fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   Option.semiring(Int.semiring()).run {
 *      Option(2) * Option(3)
 *   }
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 */
@documented
interface Semiring<A> {

  /**
   * A zero value for this A
   */
  fun zero(): A

  /**
   * A one value for this A
   */
  fun one(): A

  fun A.combine(b: A): A

  operator fun A.plus(b: A): A = combine(b)

  /**
   * Multiplicatively combine two [A] values.
   */
  fun A.combineMultiplicate(b: A): A

  operator fun A.times(b: A): A = this.combineMultiplicate(b)

  /**
   * Maybe additively combine two [A] values.
   */
  fun A?.maybeCombineAddition(b: A?): A {
    if (this == null) return zero()
    return Option.fromNullable(b).fold({ this }, { combine(it) })
  }

  /**
   * Maybe multiplicatively combine two [A] values.
   */
  fun A?.maybeCombineMultiplicate(b: A?): A {
    if (this == null) return one()
    return Option.fromNullable(b).fold({ this }, { combineMultiplicate(it) })
  }
}

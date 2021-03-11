package arrow.typeclasses

import arrow.documented

/**
 * The [Semiring] type class for a given type `A` combines both a commutative additive [Monoid] and a multiplicative [Monoid].
 *  It requires the multiplicative [Monoid] to distribute over the additive one. The operations of the multiplicative [Monoid] have been renamed to
 *  [one] and [combineMultiplicate] for easier use.
 *
 * ```kotlin
 * (a.combineMultiplicate(b)).combineMultiplicate(c) == a.combineMultiplicate(b.combineMultiplicate(c))
 * ```
 *
 * The [one] function serves exactly like the [empty] function for an additive [Monoid], just adapted for the multiplicative
 * version. This forms the following law:
 *
 * ```kotlin
 * a.combineMultiplicate(one()) == one().combineMultiplicate(a) == a
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
 * import arrow.core.int
 * import arrow.typeclasses.Semiring
 *
 * fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   Semiring.int().run { 1.combine(2) }
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 *
 * ```kotlin:ank:playground
 * import arrow.core.int
 * import arrow.typeclasses.Semiring
 *
 * fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   Semiring.int().run { 2.combineMultiplicate(3) }
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 *
 * The type class `Semiring` also has support for the `+` `*` syntax:
 *
 * ```kotlin:ank:playground
 * import arrow.core.int
 * import arrow.typeclasses.Semiring
 *
 * fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   Semiring.int().run {
 *      1 + 2 * 3
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

  operator fun A.plus(b: A): A =
    combine(b)

  /**
   * Multiplicatively combine two [A] values.
   */
  fun A.combineMultiplicate(b: A): A

  operator fun A.times(b: A): A =
    this.combineMultiplicate(b)

  /**
   * Maybe additively combine two [A] values.
   */
  fun A?.maybeCombineAddition(b: A?): A =
    if (this == null) zero()
    else b?.let { combine(it) } ?: this

  /**
   * Maybe multiplicatively combine two [A] values.
   */
  fun A?.maybeCombineMultiplicate(b: A?): A =
    if (this == null) one()
    else b?.let { combineMultiplicate(it) } ?: this

  companion object
}

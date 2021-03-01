package arrow.typeclasses

import arrow.Kind
import arrow.KindDeprecation
import arrow.core.Option

@Deprecated(KindDeprecation)
/**
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
 */
interface Monoidal<F> : Semigroupal<F> {

  /**
   * Given a type [A], create an "identity" for a F<A> value.
   */
  fun <A> identity(): Kind<F, A>

  companion object
}

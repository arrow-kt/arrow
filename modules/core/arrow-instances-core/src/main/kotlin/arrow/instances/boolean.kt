package arrow.instances

import arrow.core.BooleanInstances
import arrow.core.DeprecatedAmbiguity
import arrow.typeclasses.Eq
import arrow.typeclasses.Show

interface BooleanShowInstance : Show<Boolean> {
  override fun Boolean.show(): String =
    this.toString()
}

interface BooleanEqInstance : Eq<Boolean> {
  override fun Boolean.eqv(b: Boolean): Boolean = this == b
}

// FIXME
//fun Boolean.Companion.show(): Show<Boolean> =
//  object : BooleanShowInstance {}
//
//fun Boolean.Companion.eq(): Eq<Boolean> =
//  object : BooleanEqInstance {}

@Deprecated(DeprecatedAmbiguity, ReplaceWith("Boolean.show()"))
fun BooleanInstances.show(): Show<Boolean> =
  object : BooleanShowInstance {}

@Deprecated(DeprecatedAmbiguity, ReplaceWith("Boolean.eq()"))
fun BooleanInstances.eq(): Eq<Boolean> =
  object : BooleanEqInstance {}

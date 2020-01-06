package `arrow`.`ap`.`objects`.`generic`

import arrow.typeclasses.*
import arrow.core.extensions.*
import arrow.core.extensions.option.semigroup.semigroup
import arrow.core.extensions.option.monoid.monoid

fun `arrow`.`ap`.`objects`.`generic`.`Monoid`.combine(other: `arrow`.`ap`.`objects`.`generic`.`Monoid`): `arrow`.`ap`.`objects`.`generic`.`Monoid` =
  this + other

fun List<`arrow`.`ap`.`objects`.`generic`.`Monoid`>.combineAll(): `arrow`.`ap`.`objects`.`generic`.`Monoid` =
  this.reduce { a, b -> a + b }

operator fun `arrow`.`ap`.`objects`.`generic`.`Monoid`.plus(other: `arrow`.`ap`.`objects`.`generic`.`Monoid`): `arrow`.`ap`.`objects`.`generic`.`Monoid` =
  with(`arrow`.`ap`.`objects`.`generic`.`Monoid`.semigroup()) { this@plus.combine(other) }

fun emptyMonoid(): `arrow`.`ap`.`objects`.`generic`.`Monoid` =
  `arrow`.`ap`.`objects`.`generic`.`Monoid`.monoid().empty()

interface MonoidSemigroup : arrow.typeclasses.Semigroup<`arrow`.`ap`.`objects`.`generic`.`Monoid`> {
  override fun `arrow`.`ap`.`objects`.`generic`.`Monoid`.combine(b: `arrow`.`ap`.`objects`.`generic`.`Monoid`): `arrow`.`ap`.`objects`.`generic`.`Monoid` {
    val (xA, xB) = this
    val (yA, yB) = b
    return `arrow`.`ap`.`objects`.`generic`.`Monoid`(with(`kotlin`.`String`.semigroup()){ xA.combine(yA) }, with(`arrow`.`core`.`Option`.semigroup<`kotlin`.`String`>(`kotlin`.`String`.semigroup())){ xB.combine(yB) })
  }

  companion object {
    val defaultInstance : arrow.typeclasses.Semigroup<`arrow`.`ap`.`objects`.`generic`.`Monoid`> =
      object : MonoidSemigroup{}
  }
}

fun `arrow`.`ap`.`objects`.`generic`.`Monoid`.Companion.semigroup(): arrow.typeclasses.Semigroup<`arrow`.`ap`.`objects`.`generic`.`Monoid`> =
  MonoidSemigroup.defaultInstance

interface MonoidMonoid: arrow.typeclasses.Monoid<`arrow`.`ap`.`objects`.`generic`.`Monoid`>, MonoidSemigroup {
  override fun empty(): `arrow`.`ap`.`objects`.`generic`.`Monoid` =
    `arrow`.`ap`.`objects`.`generic`.`Monoid`(with(`kotlin`.`String`.monoid()){ empty() }, with(`arrow`.`core`.`Option`.monoid<`kotlin`.`String`>(`kotlin`.`String`.monoid())){ empty() })

  companion object {
    val defaultInstance : arrow.typeclasses.Monoid<`arrow`.`ap`.`objects`.`generic`.`Monoid`> =
      object : MonoidMonoid{}
  }
}

fun `arrow`.`ap`.`objects`.`generic`.`Monoid`.Companion.monoid(): arrow.typeclasses.Monoid<`arrow`.`ap`.`objects`.`generic`.`Monoid`> =
  MonoidMonoid.defaultInstance


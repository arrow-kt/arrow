package `arrow`.`ap`.`objects`.`generic`

import arrow.typeclasses.*
import arrow.core.extensions.*
import arrow.core.extensions.option.semigroup.semigroup

fun `arrow`.`ap`.`objects`.`generic`.`Semigroup`.combine(other: `arrow`.`ap`.`objects`.`generic`.`Semigroup`): `arrow`.`ap`.`objects`.`generic`.`Semigroup` =
  this + other

fun List<`arrow`.`ap`.`objects`.`generic`.`Semigroup`>.combineAll(): `arrow`.`ap`.`objects`.`generic`.`Semigroup` =
  this.reduce { a, b -> a + b }

operator fun `arrow`.`ap`.`objects`.`generic`.`Semigroup`.plus(other: `arrow`.`ap`.`objects`.`generic`.`Semigroup`): `arrow`.`ap`.`objects`.`generic`.`Semigroup` =
  with(`arrow`.`ap`.`objects`.`generic`.`Semigroup`.semigroup()) { this@plus.combine(other) }

interface SemigroupSemigroup : arrow.typeclasses.Semigroup<`arrow`.`ap`.`objects`.`generic`.`Semigroup`> {
  override fun `arrow`.`ap`.`objects`.`generic`.`Semigroup`.combine(b: `arrow`.`ap`.`objects`.`generic`.`Semigroup`): `arrow`.`ap`.`objects`.`generic`.`Semigroup` {
    val (xA, xB) = this
    val (yA, yB) = b
    return `arrow`.`ap`.`objects`.`generic`.`Semigroup`(with(`kotlin`.`String`.semigroup()){ xA.combine(yA) }, with(`arrow`.`core`.`Option`.semigroup<`kotlin`.`String`>(`kotlin`.`String`.semigroup())){ xB.combine(yB) })
  }

  companion object {
    val defaultInstance : arrow.typeclasses.Semigroup<`arrow`.`ap`.`objects`.`generic`.`Semigroup`> =
      object : SemigroupSemigroup{}
  }
}

fun `arrow`.`ap`.`objects`.`generic`.`Semigroup`.Companion.semigroup(): arrow.typeclasses.Semigroup<`arrow`.`ap`.`objects`.`generic`.`Semigroup`> =
  SemigroupSemigroup.defaultInstance


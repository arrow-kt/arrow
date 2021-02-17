package `arrow`.`ap`.`objects`.`generic`

import arrow.typeclasses.*

interface EqEq : arrow.typeclasses.Eq<`arrow`.`ap`.`objects`.`generic`.`Eq`> {
  override fun `arrow`.`ap`.`objects`.`generic`.`Eq`.eqv(b: `arrow`.`ap`.`objects`.`generic`.`Eq`): Boolean =
    this == b

  companion object {
    val defaultInstance: arrow.typeclasses.Eq<`arrow`.`ap`.`objects`.`generic`.`Eq`> =
      object : EqEq{}
  }
}

fun `arrow`.`ap`.`objects`.`generic`.`Eq`.Companion.eq(): arrow.typeclasses.Eq<`arrow`.`ap`.`objects`.`generic`.`Eq`> =
  EqEq.defaultInstance

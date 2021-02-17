package `arrow`.`ap`.`objects`.`generic`

import arrow.typeclasses.*

interface ShowShow : arrow.typeclasses.Show<`arrow`.`ap`.`objects`.`generic`.`Show`> {
  override fun `arrow`.`ap`.`objects`.`generic`.`Show`.show(): String =
    this.toString()
}

fun `arrow`.`ap`.`objects`.`generic`.`Show`.Companion.show(): arrow.typeclasses.Show<`arrow`.`ap`.`objects`.`generic`.`Show`> =
  object : ShowShow{}

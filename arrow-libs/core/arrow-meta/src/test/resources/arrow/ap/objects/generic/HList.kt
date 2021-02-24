package `arrow`.`ap`.`objects`.`generic`

import arrow.core.*
import arrow.core.extensions.*

fun `arrow`.`ap`.`objects`.`generic`.`HList`.toHList(): arrow.generic.HList2<`kotlin`.`String`, `arrow`.`core`.`Option`<`kotlin`.`String`>> =
  arrow.generic.hListOf(this.`field`, this.`option`)

fun arrow.generic.HList2<`kotlin`.`String`, `arrow`.`core`.`Option`<`kotlin`.`String`>>.toHList(): `arrow`.`ap`.`objects`.`generic`.`HList` =
  `arrow`.`ap`.`objects`.`generic`.`HList`(this.head, this.tail.head)

fun `arrow`.`ap`.`objects`.`generic`.`HList`.toHListLabeled(): arrow.generic.HList2<arrow.core.Tuple2<String, `kotlin`.`String`>, arrow.core.Tuple2<String, `arrow`.`core`.`Option`<`kotlin`.`String`>>> =
  arrow.generic.hListOf(("field" toT field), ("option" toT option))

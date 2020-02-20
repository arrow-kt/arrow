package `arrow`.`ap`.`objects`.`generic`

import arrow.core.*
import arrow.core.extensions.*

fun `arrow`.`ap`.`objects`.`generic`.`Tupled`.tupled(): arrow.core.Tuple2<`kotlin`.`String`, `arrow`.`core`.`Option`<`kotlin`.`String`>> =
  arrow.core.Tuple2(this.`field`, this.`option`)

fun `arrow`.`ap`.`objects`.`generic`.`Tupled`.tupledLabeled(): arrow.core.Tuple2<arrow.core.Tuple2<String, `kotlin`.`String`>, arrow.core.Tuple2<String, `arrow`.`core`.`Option`<`kotlin`.`String`>>> =
  arrow.core.Tuple2(("field" toT field), ("option" toT option))

fun <B> `arrow`.`ap`.`objects`.`generic`.`Tupled`.foldLabeled(f: (arrow.core.Tuple2<kotlin.String, `kotlin`.`String`>, arrow.core.Tuple2<kotlin.String, `arrow`.`core`.`Option`<`kotlin`.`String`>>) -> B): B {
  val t = tupledLabeled()
  return f(t.a, t.b)
}

fun arrow.core.Tuple2<`kotlin`.`String`, `arrow`.`core`.`Option`<`kotlin`.`String`>>.toTupled(): `arrow`.`ap`.`objects`.`generic`.`Tupled` =
  `arrow`.`ap`.`objects`.`generic`.`Tupled`(this.a, this.b)


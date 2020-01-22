package `arrow`.`ap`.`objects`.`generic`

import arrow.core.*
import arrow.core.extensions.*
import arrow.typeclasses.*

fun `arrow`.`ap`.`objects`.`generic`.`Applicative`.tupled(): arrow.core.Tuple2<`kotlin`.`String`, `arrow`.`core`.`Option`<`kotlin`.`String`>> =
  arrow.core.Tuple2(this.`field`, this.`option`)

fun `arrow`.`ap`.`objects`.`generic`.`Applicative`.tupledLabeled(): arrow.core.Tuple2<arrow.core.Tuple2<String, `kotlin`.`String`>, arrow.core.Tuple2<String, `arrow`.`core`.`Option`<`kotlin`.`String`>>> =
  arrow.core.Tuple2(("field" toT field), ("option" toT option))

fun <B> `arrow`.`ap`.`objects`.`generic`.`Applicative`.foldLabeled(f: (arrow.core.Tuple2<kotlin.String, `kotlin`.`String`>, arrow.core.Tuple2<kotlin.String, `arrow`.`core`.`Option`<`kotlin`.`String`>>) -> B): B {
  val t = tupledLabeled()
  return f(t.a, t.b)
}

fun arrow.core.Tuple2<`kotlin`.`String`, `arrow`.`core`.`Option`<`kotlin`.`String`>>.toApplicative(): `arrow`.`ap`.`objects`.`generic`.`Applicative` =
  `arrow`.`ap`.`objects`.`generic`.`Applicative`(this.a, this.b)

fun <F> arrow.typeclasses.Applicative<F>.mapToApplicative(field: arrow.Kind<F, `kotlin`.`String`>, option: arrow.Kind<F, `arrow`.`core`.`Option`<`kotlin`.`String`>>): arrow.Kind<F, `arrow`.`ap`.`objects`.`generic`.`Applicative`> =
  this.map(field, option) { it.toApplicative() }


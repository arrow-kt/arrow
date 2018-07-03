package `arrow`.`ap`.`objects`.`iso`



inline val `arrow`.`ap`.`objects`.`iso`.`Iso`.Companion.iso: arrow.optics.Iso<`arrow`.`ap`.`objects`.`iso`.`Iso`, arrow.core.Tuple3<`kotlin`.`String`, `kotlin`.`String`?, `arrow`.`core`.`Option`<`kotlin`.`String`>>> inline get()= arrow.optics.Iso(
  get = { iso: `arrow`.`ap`.`objects`.`iso`.`Iso` -> arrow.core.Tuple3(iso.`field`, iso.`nullable`, iso.`option`) },
  reverseGet = { tuple: arrow.core.Tuple3<`kotlin`.`String`, `kotlin`.`String`?, `arrow`.`core`.`Option`<`kotlin`.`String`>> -> `arrow`.`ap`.`objects`.`iso`.`Iso`(tuple.a, tuple.b, tuple.c) }
)

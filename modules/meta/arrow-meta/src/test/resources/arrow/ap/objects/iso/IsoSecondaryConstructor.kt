package `arrow`.`ap`.`objects`.`iso`



inline val `arrow`.`ap`.`objects`.`iso`.`IsoSecondaryConstructor`.Companion.iso: arrow.optics.Iso<`arrow`.`ap`.`objects`.`iso`.`IsoSecondaryConstructor`, arrow.core.Tuple2<`kotlin`.`Int`, `kotlin`.`String`>> inline get()= arrow.optics.Iso(
  get = { isoSecondaryConstructor: `arrow`.`ap`.`objects`.`iso`.`IsoSecondaryConstructor` -> arrow.core.Tuple2(isoSecondaryConstructor.`fieldNumber`, isoSecondaryConstructor.`fieldString`) },
  reverseGet = { tuple: arrow.core.Tuple2<`kotlin`.`Int`, `kotlin`.`String`> -> `arrow`.`ap`.`objects`.`iso`.`IsoSecondaryConstructor`(tuple.a, tuple.b) }
)

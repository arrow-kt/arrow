package `arrow`.`ap`.`objects`.`iso`

import arrow.core.Tuple2
import arrow.optics.Iso

inline val `arrow`.`ap`.`objects`.`iso`.`IsoSecondaryConstructor`.Companion.iso: Iso<`IsoSecondaryConstructor`, Tuple2<`Int`, `String`>>
  inline get()= Iso(
    get = { isoSecondaryConstructor: `IsoSecondaryConstructor` -> Tuple2(isoSecondaryConstructor.`fieldNumber`, isoSecondaryConstructor.`fieldString`) },
    reverseGet = { tuple: Tuple2<`Int`, `String`> -> `IsoSecondaryConstructor`(tuple.a, tuple.b) }
  )

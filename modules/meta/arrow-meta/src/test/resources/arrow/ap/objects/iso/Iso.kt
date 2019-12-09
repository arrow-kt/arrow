package `arrow`.`ap`.`objects`.`iso`

import arrow.core.Option
import arrow.core.Tuple3
import arrow.optics.Iso

inline val `arrow`.`ap`.`objects`.`iso`.`Iso`.Companion.iso: Iso<`Iso`, Tuple3<`String`, `String`?, `Option`<`String`>>>
  inline get()= Iso(
    get = { iso: `Iso` -> Tuple3(iso.`field`, iso.`nullable`, iso.`option`) },
    reverseGet = { tuple: Tuple3<`String`, `String`?, `Option`<`String`>> -> `Iso`(tuple.a, tuple.b, tuple.c) }
  )

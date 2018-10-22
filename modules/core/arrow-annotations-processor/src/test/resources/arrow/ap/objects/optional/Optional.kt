package `arrow`.`ap`.`objects`.`optional`

import arrow.core.left
import arrow.core.right
import arrow.core.toOption


inline val `arrow`.`ap`.`objects`.`optional`.`Optional`.Companion.nullable: arrow.optics.Optional<`arrow`.`ap`.`objects`.`optional`.`Optional`, `kotlin`.`String`> inline get()= arrow.optics.Optional(
  getOrModify = { optional: `arrow`.`ap`.`objects`.`optional`.`Optional` -> optional.`nullable`?.right() ?: optional.left() },
  set = { optional: `arrow`.`ap`.`objects`.`optional`.`Optional`, value: `kotlin`.`String` -> optional.copy(`nullable` = value) }
)

inline val `arrow`.`ap`.`objects`.`optional`.`Optional`.Companion.option: arrow.optics.Optional<`arrow`.`ap`.`objects`.`optional`.`Optional`, `kotlin`.`String`> inline get()= arrow.optics.Optional(
  getOrModify = { optional: `arrow`.`ap`.`objects`.`optional`.`Optional` -> optional.`option`.orNull()?.right() ?: optional.left() },
  set = { optional: `arrow`.`ap`.`objects`.`optional`.`Optional`, value: `kotlin`.`String` -> optional.copy(`option` = value.toOption()) }
)

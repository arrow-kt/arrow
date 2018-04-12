package `arrow`.`ap`.`objects`.`optional`

import arrow.core.left
import arrow.core.right
import arrow.core.toOption

fun optionalNullable(): arrow.optics.Optional<`arrow`.`ap`.`objects`.`optional`.`Optional`, `kotlin`.`String`> = arrow.optics.Optional(
  getOrModify = { optional: `arrow`.`ap`.`objects`.`optional`.`Optional` -> optional.`nullable`?.right() ?: optional.left() },
  set = { value: `kotlin`.`String` ->
    { optional: `arrow`.`ap`.`objects`.`optional`.`Optional` ->
      optional.copy(`nullable` = value)
    }
  }
)
fun optionalOption(): arrow.optics.Optional<`arrow`.`ap`.`objects`.`optional`.`Optional`, `kotlin`.`String`> = arrow.optics.Optional(
  getOrModify = { optional: `arrow`.`ap`.`objects`.`optional`.`Optional` -> optional.`option`.orNull()?.right() ?: optional.left() },
  set = { value: `kotlin`.`String` ->
    { optional: `arrow`.`ap`.`objects`.`optional`.`Optional` ->
      optional.copy(`option` = value.toOption())
    }
  }
)
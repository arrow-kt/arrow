package `arrow`.`ap`.`objects`.`optional`

import arrow.core.left
import arrow.core.right
import arrow.core.toOption


/**
 * [arrow.optics.Optional] that can see into arrow.ap.objects.optional.Optional and focus in its property nullable [kotlin.String]
 */
inline val `arrow`.`ap`.`objects`.`optional`.`Optional`.Companion.nullable: arrow.optics.Optional<`arrow`.`ap`.`objects`.`optional`.`Optional`, `kotlin`.`String`> inline get()= arrow.optics.Optional(
  getOrModify = { optional: `arrow`.`ap`.`objects`.`optional`.`Optional` -> optional.`nullable`?.right() ?: optional.left() },
  set = { value: `kotlin`.`String` ->
    { optional: `arrow`.`ap`.`objects`.`optional`.`Optional` ->
      optional.copy(`nullable` = value)
    }
  }
)

/**
 * [arrow.optics.Optional] that can see into arrow.ap.objects.optional.Optional and focus in its property option [kotlin.String]
 */
inline val `arrow`.`ap`.`objects`.`optional`.`Optional`.Companion.option: arrow.optics.Optional<`arrow`.`ap`.`objects`.`optional`.`Optional`, `kotlin`.`String`> inline get()= arrow.optics.Optional(
  getOrModify = { optional: `arrow`.`ap`.`objects`.`optional`.`Optional` -> optional.`option`.orNull()?.right() ?: optional.left() },
  set = { value: `kotlin`.`String` ->
    { optional: `arrow`.`ap`.`objects`.`optional`.`Optional` ->
      optional.copy(`option` = value.toOption())
    }
  }
)

package `arrow`.`ap`.`objects`.`optional`

import arrow.core.left
import arrow.core.right
import arrow.core.toOption

inline val `arrow`.`ap`.`objects`.`optional`.`OptionalSecondaryConstructor`.Companion.field: arrow.optics.Optional<`arrow`.`ap`.`objects`.`optional`.`OptionalSecondaryConstructor`, `kotlin`.`String`> inline get()= arrow.optics.Optional(
  getOrModify = { optionalSecondaryConstructor: `arrow`.`ap`.`objects`.`optional`.`OptionalSecondaryConstructor` -> optionalSecondaryConstructor.`field`?.right() ?: optionalSecondaryConstructor.left() },
  set = { optionalSecondaryConstructor: `arrow`.`ap`.`objects`.`optional`.`OptionalSecondaryConstructor`, value: `kotlin`.`String` -> optionalSecondaryConstructor.copy(`field` = value) }
)

package `arrow`.`ap`.`objects`.`prism`

import arrow.core.left
import arrow.core.right

fun prismPrismSealed1(): arrow.optics.Prism<`arrow`.`ap`.`objects`.`prism`.`Prism`, `arrow`.`ap`.`objects`.`prism`.`Prism`.`PrismSealed1`> = arrow.optics.Prism(
  getOrModify = { prism: `arrow`.`ap`.`objects`.`prism`.`Prism` ->
    when (prism) {
      is `arrow`.`ap`.`objects`.`prism`.`Prism`.`PrismSealed1` -> prism.right()
      else -> prism.left()
    }
  },
  reverseGet = { it }
)

fun prismPrismSealed2(): arrow.optics.Prism<`arrow`.`ap`.`objects`.`prism`.`Prism`, `arrow`.`ap`.`objects`.`prism`.`Prism`.`PrismSealed2`> = arrow.optics.Prism(
  getOrModify = { prism: `arrow`.`ap`.`objects`.`prism`.`Prism` ->
    when (prism) {
      is `arrow`.`ap`.`objects`.`prism`.`Prism`.`PrismSealed2` -> prism.right()
      else -> prism.left()
    }
  },
  reverseGet = { it }
)
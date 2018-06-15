package `arrow`.`ap`.`objects`.`prism`

import arrow.core.left
import arrow.core.right
import arrow.core.identity

inline val `arrow`.`ap`.`objects`.`prism`.`Prism`.Companion.prismSealed1: arrow.optics.Prism<`arrow`.`ap`.`objects`.`prism`.`Prism`, arrow.ap.objects.prism.Prism.PrismSealed1> inline get()= arrow.optics.Prism(
  getOrModify = { prism: `arrow`.`ap`.`objects`.`prism`.`Prism` ->
    when (prism) {
      is arrow.ap.objects.prism.Prism.PrismSealed1 -> prism.right()
      else -> prism.left()
    }
  },
  reverseGet = ::identity
)


inline val `arrow`.`ap`.`objects`.`prism`.`Prism`.Companion.prismSealed2: arrow.optics.Prism<`arrow`.`ap`.`objects`.`prism`.`Prism`, arrow.ap.objects.prism.Prism.PrismSealed2> inline get()= arrow.optics.Prism(
  getOrModify = { prism: `arrow`.`ap`.`objects`.`prism`.`Prism` ->
    when (prism) {
      is arrow.ap.objects.prism.Prism.PrismSealed2 -> prism.right()
      else -> prism.left()
    }
  },
  reverseGet = ::identity
)

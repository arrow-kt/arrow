package `arrow`.`ap`.`objects`.`prism`

import arrow.core.left
import arrow.core.right
import arrow.core.identity

/**
 * [arrow.optics.Prism] that can see into arrow.ap.objects.prism.Prism and focus in its property prismSealed1 arrow.ap.objects.prism.Prism.PrismSealed1
 */
inline val `arrow`.`ap`.`objects`.`prism`.`Prism`.Companion.prismSealed1: arrow.optics.Prism<`arrow`.`ap`.`objects`.`prism`.`Prism`, arrow.ap.objects.prism.Prism.PrismSealed1> inline get()= arrow.optics.Prism(
  getOrModify = { prism: `arrow`.`ap`.`objects`.`prism`.`Prism` ->
    when (prism) {
      is arrow.ap.objects.prism.Prism.PrismSealed1 -> prism.right()
      else -> prism.left()
    }
  },
  reverseGet = ::identity
)


/**
 * [arrow.optics.Prism] that can see into arrow.ap.objects.prism.Prism and focus in its property prismSealed2 arrow.ap.objects.prism.Prism.PrismSealed2
 */
inline val `arrow`.`ap`.`objects`.`prism`.`Prism`.Companion.prismSealed2: arrow.optics.Prism<`arrow`.`ap`.`objects`.`prism`.`Prism`, arrow.ap.objects.prism.Prism.PrismSealed2> inline get()= arrow.optics.Prism(
  getOrModify = { prism: `arrow`.`ap`.`objects`.`prism`.`Prism` ->
    when (prism) {
      is arrow.ap.objects.prism.Prism.PrismSealed2 -> prism.right()
      else -> prism.left()
    }
  },
  reverseGet = ::identity
)

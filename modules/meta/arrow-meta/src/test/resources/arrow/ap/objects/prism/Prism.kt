package `arrow`.`ap`.`objects`.`prism`

import arrow.core.identity
import arrow.optics.Prism

inline val `arrow`.`ap`.`objects`.`prism`.`Prism`.Companion.prismSealed1: Prism<`Prism`, Prism.PrismSealed1>
  inline get()= Prism(
    getOrModify = { prism: `Prism` ->
      when (prism) {
        is Prism.PrismSealed1 -> prism.right()
        else -> prism.left()
      }
    },
    reverseGet = ::identity
  )


inline val `arrow`.`ap`.`objects`.`prism`.`Prism`.Companion.prismSealed2: Prism<`Prism`, Prism.PrismSealed2>
  inline get()= Prism(
    getOrModify = { prism: `Prism` ->
      when (prism) {
        is Prism.PrismSealed2 -> prism.right()
        else -> prism.left()
      }
    },
    reverseGet = ::identity
  )

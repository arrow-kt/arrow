package arrow.kindedj

import arrow.Kind as HK_K

interface ArrowShow<in F> {
  fun <A> show(hk: HK_K<F, A>): String
}

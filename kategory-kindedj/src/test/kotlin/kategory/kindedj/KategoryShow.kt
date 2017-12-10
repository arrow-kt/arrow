package kategory.kindedj

import kategory.HK as HK_K

interface KategoryShow<in F> {
    fun <A> show(hk: HK_K<F, A>): String
}

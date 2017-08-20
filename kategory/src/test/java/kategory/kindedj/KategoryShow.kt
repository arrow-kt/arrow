package kategory

import kategory.HK as HK_K

interface KategoryShow<F> {
    fun <A> show(hk: HK_K<F, A>): String
}
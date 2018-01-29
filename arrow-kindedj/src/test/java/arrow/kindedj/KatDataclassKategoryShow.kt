package arrow.kindedj

import arrow.HK

object KatDataclassArrowShow : ArrowShow<KatDataclassHK> {
    override fun <A> show(hk: HK<KatDataclassHK, A>): String =
            hk.show()
}

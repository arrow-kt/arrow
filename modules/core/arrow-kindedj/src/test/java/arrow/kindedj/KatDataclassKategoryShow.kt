package arrow.kindedj

import arrow.HK

object KatDataclassArrowShow : ArrowShow<ForKatDataclass> {
    override fun <A> show(hk: HK<ForKatDataclass, A>): String =
            hk.show()
}

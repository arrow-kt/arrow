package arrow.kindedj

import arrow.HK
import arrow.kindedj.fromkindedj.KJDataclassHK

object KJDataclassArrowShow : ArrowShow<HK<ConvertHK, KJDataclassHK>> {
    override fun <A> show(hk: HK<HK<ConvertHK, KJDataclassHK>, A>): String =
            KJDataclassHK.show(hk.fromArrow())
}

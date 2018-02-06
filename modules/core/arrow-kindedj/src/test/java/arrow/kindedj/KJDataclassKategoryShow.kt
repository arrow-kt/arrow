package arrow.kindedj

import arrow.HK
import arrow.kindedj.fromkindedj.KJDataclassHK

object KJDataclassArrowShow : ArrowShow<HK<ForConvert, KJDataclassHK>> {
    override fun <A> show(hk: HK<HK<ForConvert, KJDataclassHK>, A>): String =
            KJDataclassHK.show(hk.fromArrow())
}

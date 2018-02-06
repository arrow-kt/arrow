package arrow.kindedj

import arrow.HK
import arrow.kindedj.fromkindedj.ForKJDataclass

object KJDataclassArrowShow : ArrowShow<HK<ForConvert, ForKJDataclass>> {
    override fun <A> show(hk: HK<HK<ForConvert, ForKJDataclass>, A>): String =
            ForKJDataclass.show(hk.fromArrow())
}

package arrow.kindedj

import arrow.Kind
import arrow.kindedj.fromkindedj.ForKJDataclass

object KJDataclassArrowShow : ArrowShow<Kind<ForConvert, ForKJDataclass>> {
  override fun <A> show(hk: Kind<Kind<ForConvert, ForKJDataclass>, A>): String =
    ForKJDataclass.show(hk.fromArrow())
}

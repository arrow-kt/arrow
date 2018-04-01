package arrow.kindedj

import arrow.Kind

object KatDataclassArrowShow : ArrowShow<ForKatDataclass> {
  override fun <A> show(hk: Kind<ForKatDataclass, A>): String =
    hk.show()
}

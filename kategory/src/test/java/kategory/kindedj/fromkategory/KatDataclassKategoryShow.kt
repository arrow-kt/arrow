package kategory.fromkotlin

import kategory.HK
import kategory.KategoryShow

object KatDataclassKategoryShow : KategoryShow<KatDataclassHK> {
    override fun <A> show(hk: HK<KatDataclassHK, A>): String =
            hk.value().toString()
}
package kategory.kindedj

import kategory.HK

object KatDataclassKategoryShow : KategoryShow<KatDataclassHK> {
    override fun <A> show(hk: HK<KatDataclassHK, A>): String =
            hk.show()
}
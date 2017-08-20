package kategory.fromkotlin

import kategory.ConvertHK
import kategory.HK
import kategory.KategoryShow
import kategory.convert
import kategory.kindedj.fromkindedj.KJDataclassHK

object KJDataclassKategoryShow : KategoryShow<HK<ConvertHK, KJDataclassHK>> {
    override fun <A> show(hk: HK<HK<ConvertHK, KJDataclassHK>, A>): String =
            KJDataclassHK.show(hk.convert())
}
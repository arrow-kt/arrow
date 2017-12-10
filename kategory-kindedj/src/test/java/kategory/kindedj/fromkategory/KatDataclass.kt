package kategory.kindedj

import kategory.HK as HK_K

class KatDataclassHK private constructor()

fun <A> HK_K<KatDataclassHK, A>.show(): String = (this as KatDataclass1<A>).a.toString()

data class KatDataclass1<out A>(val a: A) : HK_K<KatDataclassHK, A>

data class KatDataclass2<A, out B>(val a: A) : HK_K<HK_K<KatDataclassHK, A>, B>

data class KatDataclass3<A, B, out C>(val a: A) : HK_K<HK_K<HK_K<KatDataclassHK, A>, B>, C>

data class KatDataclass4<A, B, C, out D>(val a: A) : HK_K<HK_K<HK_K<HK_K<KatDataclassHK, A>, B>, C>, D>

data class KatDataclass5<A, B, C, D, out E>(val a: A) : HK_K<HK_K<HK_K<HK_K<HK_K<KatDataclassHK, A>, B>, C>, D>, E>
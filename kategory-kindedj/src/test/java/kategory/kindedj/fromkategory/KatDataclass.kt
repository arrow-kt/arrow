package kategory.kindedj

import kategory.HK as HK_K

class KatDataclassHK private constructor()

fun <A> HK_K<KatDataclassHK, A>.show(): String = (this as KatDataclass1<A>).a.toString()

data class KatDataclass1<out A>(val a: A) : HK_K<KatDataclassHK, A>

package arrow.kindedj

import arrow.HK as HK_K

class ForKatDataclass private constructor()

fun <A> HK_K<ForKatDataclass, A>.show(): String = (this as KatDataclass1<A>).a.toString()

data class KatDataclass1<out A>(val a: A) : HK_K<ForKatDataclass, A>

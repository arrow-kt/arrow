package arrow.kindedj

import io.kindedj.HkJ2
import arrow.HK
import arrow.HK2
import io.kindedj.Hk as HK_J

class ConvertHK private constructor()

@Suppress("UNCHECKED_CAST")
fun <F, A> HK2<ForConvert, F, A>.fromArrow(): HK_J<F, A> = (this as Convert.FromKindedJToArrow<F, A>).toKindedJ()

fun <F, A> HkJ2<ForConvert, F, A>.toArrow(): HK<F, A> = (this as Convert.FromArrowToKindedJ<F, A>).toArrow()

fun <F, A> HK_J<F, A>.fromKindedJ(): HK2<ForConvert, F, A> = Convert.fromKindedJ(this)

fun <F, A> HK<F, A>.toKindedJ(): HkJ2<ForConvert, F, A> = Convert.toKindedJ(this)

package arrow.kindedj

import arrow.Kind
import arrow.Kind2
import arrow.HkJ2
import io.kindedj.Hk as HK_J

class ForConvert private constructor()

@Suppress("UNCHECKED_CAST")
fun <F, A> Kind2<ForConvert, F, A>.fromArrow(): HK_J<F, A> = (this as Convert.FromKindedJToArrow<F, A>).toKindedJ()

fun <F, A> HkJ2<ForConvert, F, A>.toArrow(): Kind<F, A> = (this as Convert.FromArrowToKindedJ<F, A>).toArrow()

fun <F, A> HK_J<F, A>.fromKindedJ(): Kind2<ForConvert, F, A> = Convert.fromKindedJ(this)

fun <F, A> Kind<F, A>.toKindedJ(): HkJ2<ForConvert, F, A> = Convert.toKindedJ(this)

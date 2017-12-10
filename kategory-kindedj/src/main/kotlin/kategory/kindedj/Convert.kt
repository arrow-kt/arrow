package kategory.kindedj

import io.kindedj.HkJ2
import kategory.HK
import kategory.HK2
import io.kindedj.Hk as HK_J

class ConvertHK private constructor()

@Suppress("UNCHECKED_CAST")
fun <F, A> HK2<ConvertHK, F, A>.fromKategory(): HK_J<F, A> = (this as Convert.FromKindedJToKategory<F, A>).toKindedJ()

fun <F, A> HkJ2<ConvertHK, F, A>.toKategory(): HK<F, A> = (this as Convert.FromKategoryToKindedJ<F, A>).toKategory()

fun <F, A> HK_J<F, A>.fromKindedJ(): HK2<ConvertHK, F, A> = Convert.fromKindedJ(this)

fun <F, A> HK<F, A>.toKindedJ(): HkJ2<ConvertHK, F, A> = Convert.toKindedJ(this)

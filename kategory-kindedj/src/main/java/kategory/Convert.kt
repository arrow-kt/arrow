package kategory

import io.kindedj.HkJ2
import io.kindedj.Hk as HK_J

class ConvertHK private constructor()

@Suppress("UNCHECKED_CAST")
fun <F, A> HK2<ConvertHK, F, A>.convert(): HK_J<F, A> = (this as Convert.FromKindedJToKategory<F, A>).asKindedJ()

fun <F, A> HkJ2<ConvertHK, F, A>.convert(): HK<F, A> = (this as Convert.FromKategoryToKindedJ<F, A>).asKategory()

fun <F, A> HK_J<F, A>.fromKindedJ(): HK2<ConvertHK, F, A> = Convert.fromKindedJ(this)

fun <F, A> HK<F, A>.toKindedJ(): HkJ2<ConvertHK, F, A> = Convert.toKindedJ(this)

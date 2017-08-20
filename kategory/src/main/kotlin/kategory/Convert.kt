package kategory

import io.kindedj.HK as HK_J

class ConvertHK private constructor()

fun <F, A> HK<HK<ConvertHK, F>, A>.convert(): HK_J<F, A> =
        (this as Convert.FromKindedJToKategory<F, A>).asKindedJ()

fun <F, A> HK_J<HK_J<ConvertHK, F>, A>.convert(): HK<F, A> =
        (this as Convert.FromKategoryToKindedJ<F, A>).asKategory()

fun <F, A> HK_J<F, A>.fromKindedJ(): Convert.FromKindedJToKategory<F, A> = Convert.fromKindedJ(this)

fun <F, A> HK<F, A>.toKindedJ(): Convert.FromKategoryToKindedJ<F, A> = Convert.toKindedJ(this)
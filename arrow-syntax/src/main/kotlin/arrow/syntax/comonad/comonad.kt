package arrow.syntax.comonad

import arrow.*

inline fun <reified F, A, B> HK<F, A>.coflatMap(FT: Comonad<F> = comonad(), noinline f: (HK<F, A>) -> B): HK<F, B> = FT.coflatMap(this, f)

inline fun <reified F, A> HK<F, A>.extract(FT: Comonad<F> = comonad()): A = FT.extract(this)

inline fun <reified F, A> HK<F, A>.duplicate(FT: Comonad<F> = comonad()): HK<F, HK<F, A>> = FT.duplicate(this)
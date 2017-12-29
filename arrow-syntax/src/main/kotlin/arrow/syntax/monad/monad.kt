package arrow.syntax.monad

import arrow.*
import arrow.core.Tuple2

inline fun <F, A, B> Monad<F>.mproduct(fa: HK<F, A>, crossinline f: (A) -> HK<F, B>): HK<F, Tuple2<A, B>> =
        flatMap(fa, { a -> map(f(a), { Tuple2(a, it) }) })

inline fun <F, B> Monad<F>.ifM(fa: HK<F, Boolean>, crossinline ifTrue: () -> HK<F, B>, crossinline ifFalse: () -> HK<F, B>): HK<F, B> =
        flatMap(fa, { if (it) ifTrue() else ifFalse() })

inline fun <reified F, A, B> HK<F, A>.flatMap(FT: Monad<F> = monad(), noinline f: (A) -> HK<F, B>): HK<F, B> = FT.flatMap(this, f)

inline fun <reified F, A> HK<F, HK<F, A>>.flatten(FT: Monad<F> = monad()): HK<F, A> = FT.flatten(this)
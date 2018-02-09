package arrow.syntax.monad

import arrow.*
import arrow.core.Tuple2
import arrow.typeclasses.Monad
import arrow.typeclasses.monad

inline fun <F, A, B> Monad<F>.mproduct(fa: Kind<F, A>, crossinline f: (A) -> Kind<F, B>): Kind<F, Tuple2<A, B>> =
        flatMap(fa, { a -> map(f(a), { Tuple2(a, it) }) })

inline fun <F, B> Monad<F>.ifM(fa: Kind<F, Boolean>, crossinline ifTrue: () -> Kind<F, B>, crossinline ifFalse: () -> Kind<F, B>): Kind<F, B> =
        flatMap(fa, { if (it) ifTrue() else ifFalse() })

inline fun <reified F, A, B> Kind<F, A>.flatMap(FT: Monad<F> = monad(), noinline f: (A) -> Kind<F, B>): Kind<F, B> = FT.flatMap(this, f)

inline fun <reified F, A> Kind<F, Kind<F, A>>.flatten(FT: Monad<F> = monad()): Kind<F, A> = FT.flatten(this)
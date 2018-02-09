package arrow.syntax.comonad

import arrow.Kind
import arrow.typeclasses.Comonad
import arrow.typeclasses.comonad

inline fun <reified F, A, B> Kind<F, A>.coflatMap(FT: Comonad<F> = comonad(), noinline f: (Kind<F, A>) -> B): Kind<F, B> = FT.coflatMap(this, f)

inline fun <reified F, A> Kind<F, A>.extractM(FT: Comonad<F> = comonad()): A = FT.extractM(this)

inline fun <reified F, A> Kind<F, A>.duplicate(FT: Comonad<F> = comonad()): Kind<F, Kind<F, A>> = FT.duplicate(this)
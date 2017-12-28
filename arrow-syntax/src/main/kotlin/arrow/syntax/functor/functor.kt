package arrow.syntax.functor

import arrow.*

inline fun <reified F, A, B> HK<F, A>.map(FT : Functor<F> = functor(), noinline f: (A) -> B): HK<F, B> = FT.map(this, f)

inline fun <reified F, A, B> ((A) -> B).lift(FT : Functor<F> = functor()): (HK<F, A>) -> HK<F, B> = FT.lift(this)
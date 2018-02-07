package arrow.syntax.functor

import arrow.*
import arrow.typeclasses.Functor
import arrow.typeclasses.functor

inline fun <reified F, A, B> Kind<F, A>.map(FT : Functor<F> = functor(), noinline f: (A) -> B): Kind<F, B> = FT.map(this, f)

inline fun <reified F, A, B> ((A) -> B).lift(FT : Functor<F> = functor()): (Kind<F, A>) -> Kind<F, B> = FT.lift(this)
package arrow.instances

import arrow.Kind
import arrow.data.Day
import arrow.data.DayOf
import arrow.data.DayPartialOf
import arrow.data.fix
import arrow.instance
import arrow.typeclasses.Comonad
import arrow.typeclasses.Functor

@instance(Day::class)
interface ComonadDayInstance<F, G, X, Y> : Comonad<DayPartialOf<F, G, X, Y>> {
  fun CF(): Comonad<F>

  fun CG(): Comonad<G>

  override fun <A, B> DayOf<F, G, X, Y, A>.coflatMap(f: (DayOf<F, G, X, Y, A>) -> B): Day<F, G, X, Y, B> =
    fix().coflatMap(f)

  override fun <A> DayOf<F, G, X, Y, A>.extract(): A =
    fix().extract(CF(), CG())

  override fun <A, B> DayOf<F, G, X, Y, A>.map(f: (A) -> B): Day<F, G, X, Y, B> =
    fix().map(f)
}

@instance(Day::class)
interface FunctorDayInstance<F, G, X, Y> : Functor<DayPartialOf<F, G, X, Y>> {

  override fun <A, B> DayOf<F, G, X, Y, A>.map(f: (A) -> B): Kind<DayPartialOf<F, G, X, Y>, B> =
    fix().map(f)
}

class DayContext<F, G, X, Y>(val CF: Comonad<F>, val CG: Comonad<G>) : ComonadDayInstance<F, G, X, Y> {

  override fun CF(): Comonad<F> = CF
  override fun CG(): Comonad<G> = CG
}

class DayContextPartiallyApplied<F, G, X, Y>(val CF: Comonad<F>, val CG: Comonad<G>) {
  infix fun <A> extensions(f: DayContext<F, G, X, Y>.() -> A): A =
    f(DayContext(CF, CG))
}

fun <F, G, X, Y> ForDay(CF: Comonad<F>, CG: Comonad<G>): DayContextPartiallyApplied<F, G, X, Y> =
  DayContextPartiallyApplied(CF, CG)

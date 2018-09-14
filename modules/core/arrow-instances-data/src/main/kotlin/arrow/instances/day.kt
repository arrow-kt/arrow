package arrow.instances

import arrow.data.Day
import arrow.data.DayOf
import arrow.data.DayPartialOf
import arrow.data.fix
import arrow.instance
import arrow.typeclasses.Comonad
import arrow.typeclasses.Functor

@instance(Day::class)
interface ComonadDayInstance<F, G> : Comonad<DayPartialOf<F, G>> {
  fun CF(): Comonad<F>

  fun CG(): Comonad<G>

  override fun <A, B> DayOf<F, G, A>.coflatMap(f: (DayOf<F, G, A>) -> B): Day<F, G, B> =
    fix().coflatMap(f)

  override fun <A> DayOf<F, G, A>.extract(): A =
    fix().extract(CF(), CG())

  override fun <A, B> DayOf<F, G, A>.map(f: (A) -> B): Day<F, G, B> =
    fix().map(f)
}

@instance(Day::class)
interface FunctorDayInstance<F, G> : Functor<DayPartialOf<F, G>> {

  override fun <A, B> DayOf<F, G, A>.map(f: (A) -> B): Day<F, G, B> =
    fix().map(f)
}

class DayContext<F, G>(val CF: Comonad<F>, val CG: Comonad<G>) : ComonadDayInstance<F, G> {

  override fun CF(): Comonad<F> = CF
  override fun CG(): Comonad<G> = CG
}

class DayContextPartiallyApplied<F, G>(val CF: Comonad<F>, val CG: Comonad<G>) {
  infix fun <A> extensions(f: DayContext<F, G>.() -> A): A =
    f(DayContext(CF, CG))
}

fun <F, G> ForDay(CF: Comonad<F>, CG: Comonad<G>): DayContextPartiallyApplied<F, G> =
  DayContextPartiallyApplied(CF, CG)

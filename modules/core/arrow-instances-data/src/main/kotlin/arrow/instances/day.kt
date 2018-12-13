package arrow.instances

import arrow.Kind
import arrow.data.Day
import arrow.data.DayOf
import arrow.data.DayPartialOf
import arrow.data.fix
import arrow.deprecation.ExtensionsDSLDeprecated
import arrow.extension
import arrow.typeclasses.Applicative
import arrow.typeclasses.Comonad
import arrow.typeclasses.Functor

@extension
interface ComonadDayInstance<F, G> : Comonad<DayPartialOf<F, G>> {
  fun CF(): Comonad<F>

  fun CG(): Comonad<G>

  override fun <A, B> DayOf<F, G, A>.coflatMap(f: (DayOf<F, G, A>) -> B): Day<F, G, B> =
    fix().coflatMapLazy(CF(), CG(), f)

  override fun <A> DayOf<F, G, A>.extract(): A =
    fix().extract(CF(), CG())

  override fun <A, B> DayOf<F, G, A>.map(f: (A) -> B): Day<F, G, B> =
    fix().mapLazy(f)
}

@extension
interface FunctorDayInstance<F, G> : Functor<DayPartialOf<F, G>> {

  override fun <A, B> DayOf<F, G, A>.map(f: (A) -> B): Day<F, G, B> =
    fix().mapLazy(f)
}

@extension
interface ApplicativeDayInstance<F, G> : Applicative<DayPartialOf<F, G>> {
  fun AF(): Applicative<F>

  fun AG(): Applicative<G>

  override fun <A, B> DayOf<F, G, A>.map(f: (A) -> B): Day<F, G, B> =
    fix().mapLazy(f)

  override fun <A> just(a: A): Day<F, G, A> =
    Day.just(AF(), AG(), a)

  override fun <A, B> Kind<DayPartialOf<F, G>, A>.ap(ff: Kind<DayPartialOf<F, G>, (A) -> B>): Day<F, G, B> =
    fix().ap(AF(), AG(), ff)
}

class DayContext<F, G>(val AF: Applicative<F>, val AG: Applicative<G>, val CF: Comonad<F>, val CG: Comonad<G>) : ComonadDayInstance<F, G>, ApplicativeDayInstance<F, G> {
  override fun <A, B> DayOf<F, G, A>.map(f: (A) -> B): Day<F, G, B> =
    fix().mapLazy(f)

  override fun AF(): Applicative<F> = AF
  override fun AG(): Applicative<G> = AG

  override fun CF(): Comonad<F> = CF
  override fun CG(): Comonad<G> = CG
}

class DayContextPartiallyApplied<F, G>(val AF: Applicative<F>, val AG: Applicative<G>, val CF: Comonad<F>, val CG: Comonad<G>) {
  @Deprecated(ExtensionsDSLDeprecated)
  infix fun <A> extensions(f: DayContext<F, G>.() -> A): A =
    f(DayContext(AF, AG, CF, CG))
}

fun <F, G> ForDay(AF: Applicative<F>, AG: Applicative<G>, CF: Comonad<F>, CG: Comonad<G>): DayContextPartiallyApplied<F, G> =
  DayContextPartiallyApplied(AF, AG, CF, CG)

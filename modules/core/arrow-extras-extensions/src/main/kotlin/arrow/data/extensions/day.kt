package arrow.data.extensions

import arrow.Kind
import arrow.data.Day
import arrow.data.DayOf
import arrow.data.DayPartialOf
import arrow.data.fix
import arrow.extension
import arrow.typeclasses.Applicative
import arrow.typeclasses.Comonad
import arrow.typeclasses.Functor
import arrow.undocumented

@extension
@undocumented
interface DayComonad<F, G> : Comonad<DayPartialOf<F, G>> {
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
@undocumented
interface DayFunctor<F, G> : Functor<DayPartialOf<F, G>> {

  override fun <A, B> DayOf<F, G, A>.map(f: (A) -> B): Day<F, G, B> =
    fix().mapLazy(f)
}

@extension
@undocumented
interface DayApplicative<F, G> : Applicative<DayPartialOf<F, G>> {
  fun AF(): Applicative<F>

  fun AG(): Applicative<G>

  override fun <A, B> DayOf<F, G, A>.map(f: (A) -> B): Day<F, G, B> =
    fix().mapLazy(f)

  override fun <A> just(a: A): Day<F, G, A> =
    Day.just(AF(), AG(), a)

  override fun <A, B> Kind<DayPartialOf<F, G>, A>.ap(ff: Kind<DayPartialOf<F, G>, (A) -> B>): Day<F, G, B> =
    fix().ap(AF(), AG(), ff)
}

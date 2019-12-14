package arrow.fx.extensions

import arrow.Kind
import arrow.Kind2
import arrow.extension
import arrow.fx.ForSchedule
import arrow.fx.Schedule
import arrow.fx.SchedulePartialOf
import arrow.fx.fix
import arrow.typeclasses.Category
import arrow.typeclasses.Conested
import arrow.typeclasses.Contravariant
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import arrow.typeclasses.Profunctor
import arrow.typeclasses.conest
import arrow.typeclasses.counnest

@extension
interface ScheduleFunctor<F, Input> : Functor<SchedulePartialOf<F, Input>> {
  override fun <A, B> Kind<SchedulePartialOf<F, Input>, A>.map(f: (A) -> B): Kind<SchedulePartialOf<F, Input>, B> =
    fix().map(f)
}

@extension
interface ScheduleContravariant<F, Output> : Contravariant<Conested<Kind<ForSchedule, F>, Output>> {
  override fun <A, B> Kind<Conested<Kind<ForSchedule, F>, Output>, A>.contramap(f: (B) -> A): Kind<Conested<Kind<ForSchedule, F>, Output>, B> =
    counnest().fix().contramap(f).conest()

  fun <A, B> Kind<SchedulePartialOf<F, A>, Output>.contramapC(f: (B) -> A): Kind<SchedulePartialOf<F, B>, Output> =
    fix().contramap(f)
}

@extension
interface ScheduleProfunctor<F> : Profunctor<Kind<ForSchedule, F>> {
  override fun <A, B, C, D> Kind2<Kind<ForSchedule, F>, A, B>.dimap(fl: (C) -> A, fr: (B) -> D): Kind2<Kind<ForSchedule, F>, C, D> =
    fix().dimap(fl, fr)
}

@extension
interface ScheduleCategory<F> : Category<Kind<ForSchedule, F>> {
  fun MM(): Monad<F>

  override fun <A> id(): Kind2<Kind<ForSchedule, F>, A, A> = Schedule.identity(MM())

  override fun <A, B, C> Kind2<Kind<ForSchedule, F>, B, C>.compose(arr: Kind2<Kind<ForSchedule, F>, A, B>): Kind2<Kind<ForSchedule, F>, A, C> =
    fix().compose(arr.fix())
}

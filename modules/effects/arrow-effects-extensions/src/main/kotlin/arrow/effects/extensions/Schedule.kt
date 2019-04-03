package arrow.effects.extensions

import arrow.Kind
import arrow.Kind2
import arrow.effects.ForSchedule
import arrow.effects.SchedulePartialOf
import arrow.effects.fix
import arrow.extension
import arrow.typeclasses.*

@extension
interface ScheduleFunctor<F, State, Input> : Functor<SchedulePartialOf<F, State, Input>> {
  override fun <A, B> Kind<SchedulePartialOf<F, State, Input>, A>.map(f: (A) -> B): Kind<SchedulePartialOf<F, State, Input>, B> =
    fix().map(f)
}

@extension
interface ScheduleContravariant<F, State, Output> : Contravariant<Conested<Kind2<ForSchedule, F, State>, Output>> {
  override fun <A, B> Kind<Conested<Kind2<ForSchedule, F, State>, Output>, A>.contramap(f: (B) -> A): Kind<Conested<Kind2<ForSchedule, F, State>, Output>, B> =
    counnest().fix().contramap(f).conest()

  fun <A, B> Kind<SchedulePartialOf<F, State, A>, Output>.contramapC(f: (B) -> A): Kind<SchedulePartialOf<F, State, B>, Output> =
    fix().contramap(f)
}

@extension
interface ScheduleProfunctor<F, State> : Profunctor<Kind2<ForSchedule, F, State>> {
  override fun <A, B, C, D> Kind2<Kind2<ForSchedule, F, State>, A, B>.dimap(fl: (C) -> A, fr: (B) -> D): Kind2<Kind2<ForSchedule, F, State>, C, D> =
    fix().dimap(fl, fr)
}
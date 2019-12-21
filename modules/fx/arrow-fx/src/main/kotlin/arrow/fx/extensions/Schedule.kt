package arrow.fx.extensions

import arrow.Kind
import arrow.Kind2
import arrow.core.identity
import arrow.extension
import arrow.fx.ForSchedule
import arrow.fx.Schedule
import arrow.fx.SchedulePartialOf
import arrow.fx.fix
import arrow.typeclasses.Alternative
import arrow.typeclasses.Applicative
import arrow.typeclasses.Apply
import arrow.typeclasses.Category
import arrow.typeclasses.Conested
import arrow.typeclasses.Contravariant
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import arrow.typeclasses.Monoid
import arrow.typeclasses.MonoidK
import arrow.typeclasses.Profunctor
import arrow.typeclasses.Semigroup
import arrow.typeclasses.SemigroupK
import arrow.typeclasses.Semiring
import arrow.typeclasses.conest
import arrow.typeclasses.counnest

@extension
interface ScheduleFunctor<F, Input> : Functor<SchedulePartialOf<F, Input>> {
  override fun <A, B> Kind<SchedulePartialOf<F, Input>, A>.map(f: (A) -> B): Kind<SchedulePartialOf<F, Input>, B> =
    fix().map(f)
}

@extension
interface ScheduleAppy<F, Input> : Apply<SchedulePartialOf<F, Input>>, ScheduleFunctor<F, Input> {
  override fun <A, B> Kind<SchedulePartialOf<F, Input>, A>.ap(ff: Kind<SchedulePartialOf<F, Input>, (A) -> B>): Kind<SchedulePartialOf<F, Input>, B> =
    fix().and(ff.fix()).map { (a, f) -> f(a) }
}

@extension
interface ScheduleApplicative<F, Input> : Applicative<SchedulePartialOf<F, Input>>, ScheduleAppy<F, Input> {

  fun MF(): Monad<F>

  override fun <A> just(a: A): Kind<SchedulePartialOf<F, Input>, A> =
    Schedule.forever<F, A>(MF()).const(a) as Schedule<F, Input, A>

  override fun <A, B> Kind<SchedulePartialOf<F, Input>, A>.map(f: (A) -> B): Kind<SchedulePartialOf<F, Input>, B> =
    fix().map(f)
}

@extension
interface ScheduleSemigroup<F, Input, Output> : Semigroup<Schedule<F, Input, Output>> {
  fun SA(): Semigroup<Output>

  override fun Schedule<F, Input, Output>.combine(b: Schedule<F, Input, Output>): Schedule<F, Input, Output> =
    and(b).map { (a, b) -> SA().run { a + b } }
}

@extension
interface ScheduleMonoid<F, Input, Output> : Monoid<Schedule<F, Input, Output>>, ScheduleSemigroup<F, Input, Output> {
  override fun SA(): Semigroup<Output> = MA()
  fun MA(): Monoid<Output>
  fun MF(): Monad<F>

  override fun empty(): Schedule<F, Input, Output> =
    Schedule.forever<F, Input>(MF()).const(MA().empty())
}

@extension
interface ScheduleSemiring<F, Input, Output> : Semiring<Schedule<F, Input, Output>>, ScheduleMonoid<F, Input, Output> {
  override fun MA(): Monoid<Output>
  override fun MF(): Monad<F>

  override fun one(): Schedule<F, Input, Output> = empty()

  override fun empty(): Schedule<F, Input, Output> =
    Schedule.forever<F, Input>(MF()).const(MA().empty())

  override fun zero(): Schedule<F, Input, Output> =
    Schedule.never<F, Input>(MF()).const(MA().empty())

  override fun Schedule<F, Input, Output>.combineMultiplicate(b: Schedule<F, Input, Output>): Schedule<F, Input, Output> =
    andThen(b).map { it.fold(::identity, ::identity) }
}

@extension
interface ScheduleSemigroupK<F, Input> : SemigroupK<SchedulePartialOf<F, Input>> {
  override fun <A> Kind<SchedulePartialOf<F, Input>, A>.combineK(y: Kind<SchedulePartialOf<F, Input>, A>): Kind<SchedulePartialOf<F, Input>, A> =
    fix().andThen(y.fix()).map { it.fold(::identity, ::identity) }
}

@extension
interface ScheduleMonoidK<F, Input> : MonoidK<SchedulePartialOf<F, Input>>, ScheduleSemigroupK<F, Input> {
  fun MF(): Monad<F>

  override fun <A> empty(): Kind<SchedulePartialOf<F, Input>, A> =
    Schedule.never(MF())
}

@extension
interface ScheduleAlternative<F, Input> : Alternative<SchedulePartialOf<F, Input>>, ScheduleApplicative<F, Input>, ScheduleMonoidK<F, Input> {
  override fun MF(): Monad<F>

  override fun <A> Kind<SchedulePartialOf<F, Input>, A>.orElse(b: Kind<SchedulePartialOf<F, Input>, A>): Kind<SchedulePartialOf<F, Input>, A> =
    fix().andThen(b.fix()).map { it.fold(::identity, ::identity) }

  override fun <A> Kind<SchedulePartialOf<F, Input>, A>.combineK(y: Kind<SchedulePartialOf<F, Input>, A>): Kind<SchedulePartialOf<F, Input>, A> =
    orElse(y)
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
